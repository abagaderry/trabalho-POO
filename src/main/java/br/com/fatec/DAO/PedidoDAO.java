package br.com.fatec.DAO;

import br.com.fatec.model.Cliente;
import br.com.fatec.model.Pedido;
import br.com.fatec.model.Produto;
import br.com.fatec.persistencia.Banco;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class PedidoDAO implements DAO<Pedido> {

    private PreparedStatement pst;
    private ResultSet rs;

    // ── DAOs auxiliares para buscar os objetos de composição ──────────────
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    // ─────────────────────────────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────────────────────────────
    @Override
    public boolean insere(Pedido model) throws SQLException {
        String sql = "INSERT INTO pedido (idCliente, idProduto, CPF, Data, Quantidade, PrecoTotal) "
                   + "VALUES (?, ?, ?, ?, ?, ?);";

        Banco.conectar();
        pst = Banco.obterConexao().prepareStatement(sql);

        pst.setInt(1, model.getCliente().getId());   // ✅ getId() pelo getter
        pst.setInt(2, model.getProduto().getId());   // ✅ getId() pelo getter
        pst.setString(3, model.getCliente().getCpf()); // ✅ getCpf() pelo getter
        pst.setString(4, model.getData());
        pst.setInt(5, model.getQuantidade());
        pst.setFloat(6, model.getPrecoTotal());

        if (pst.executeUpdate() >= 1) {
            Banco.desconectar();
            return true;
        }
        Banco.desconectar();
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────
    // DELETE — ✅ corrigido para tabela "pedido"
    // ─────────────────────────────────────────────────────────────────────
    @Override
    public boolean remove(Pedido model) throws SQLException {
        String sql = "DELETE FROM pedido WHERE id = ?;"; // ✅ era "produto"

        Banco.conectar();
        pst = Banco.obterConexao().prepareStatement(sql);
        pst.setInt(1, model.getId());

        if (pst.executeUpdate() >= 1) {
            Banco.desconectar();
            return true;
        }
        Banco.desconectar();
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────────────────────────────
    @Override
    public boolean altera(Pedido model) throws SQLException {
        String sql = "UPDATE pedido SET Quantidade = ?, PrecoTotal = ? WHERE id = ?;";

        Banco.conectar();
        pst = Banco.obterConexao().prepareStatement(sql);
        pst.setInt(1, model.getQuantidade());
        pst.setFloat(2, model.getPrecoTotal());
        pst.setInt(3, model.getId());

        if (pst.executeUpdate() >= 1) {
            Banco.desconectar();
            return true;
        }
        Banco.desconectar();
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────
    // READ — buscar por ID  ✅ corrigido
    // ─────────────────────────────────────────────────────────────────────
    @Override
    public Pedido buscarID(Pedido model) throws SQLException {
        Pedido pedido = null; // ✅ variável local, não conflita com a classe

        String sql = "SELECT * FROM pedido WHERE id = ?;"; // ✅ era "Produto"

        Banco.conectar();
        pst = Banco.obterConexao().prepareStatement(sql);
        pst.setInt(1, model.getId());
        rs = pst.executeQuery();

        if (rs.next()) {
            pedido = new Pedido(); // ✅ instancia o objeto (estava comentado!)

            pedido.setId(rs.getInt("id"));
            pedido.setData(rs.getString("Data"));
            pedido.setQuantidade(rs.getInt("Quantidade"));
            pedido.setPrecoTotal(rs.getFloat("PrecoTotal"));

            // ✅ COMPOSIÇÃO — busca Cliente e Produto pelos IDs gravados no banco
            Cliente clienteAux = new Cliente();
            clienteAux.setId(rs.getInt("idCliente"));
            pedido.setCliente(clienteDAO.buscarID(clienteAux));

            Produto produtoAux = new Produto();
            produtoAux.setId(rs.getInt("idProduto"));
            pedido.setProduto(produtoDAO.buscarID(produtoAux));
        }

        rs.close();
        Banco.desconectar();

        return pedido;
    }

    // ─────────────────────────────────────────────────────────────────────
    // READ — listar  ✅ corrigido
    // ─────────────────────────────────────────────────────────────────────
    @Override
    public Collection<Pedido> listar(String criterio) throws SQLException {
        Collection<Pedido> listagem = new ArrayList<>();

        String sql = "SELECT * FROM pedido "; // ✅ era "Produto"
        if (criterio.length() != 0) {
            sql += "WHERE " + criterio;
        }

        Banco.conectar();
        pst = Banco.obterConexao().prepareStatement(sql);
        rs = pst.executeQuery();

        while (rs.next()) {
            Pedido pedido = new Pedido(); // ✅ instancia dentro do loop

            pedido.setId(rs.getInt("id"));
            pedido.setData(rs.getString("Data"));
            pedido.setQuantidade(rs.getInt("Quantidade"));
            pedido.setPrecoTotal(rs.getFloat("PrecoTotal"));

            // ✅ COMPOSIÇÃO — mesma lógica do buscarID
            Cliente clienteAux = new Cliente();
            clienteAux.setId(rs.getInt("idCliente"));
            pedido.setCliente(clienteDAO.buscarID(clienteAux));

            Produto produtoAux = new Produto();
            produtoAux.setId(rs.getInt("idProduto"));
            pedido.setProduto(produtoDAO.buscarID(produtoAux));

            listagem.add(pedido);
        }

        rs.close();
        Banco.desconectar();

        return listagem;
    }
}