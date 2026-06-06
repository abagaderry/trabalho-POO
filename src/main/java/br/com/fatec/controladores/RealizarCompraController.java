package br.com.fatec.controladores;

import br.com.fatec.DAO.ClienteDAO;
import br.com.fatec.DAO.PedidoDAO;
import br.com.fatec.DAO.ProdutoDAO;
import br.com.fatec.model.Cliente;
import br.com.fatec.model.Pedido;
import br.com.fatec.model.Produto;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RealizarCompraController implements Initializable {

    // ── Componentes do FXML ──────────────────────────────────────────────────
    @FXML private Button    btnRealizarCompra;

    // Dados do cliente
    @FXML private TextField txtCPF;
    @FXML private TextField txtCEP;
    @FXML private TextField txtNum;
    @FXML private TextField txtEnd;
    @FXML private TextField txtUF;
    @FXML private TextField txtCidade;
    @FXML private TextField txtBairro;

    // Dados do produto
    @FXML private TextField txtProduto;
    @FXML private TextField txtID;
    @FXML private TextField txtPrescricao;
    @FXML private TextField txtValorUni;
    @FXML private TextField txtQuantidade;
    @FXML private TextField txtValor;

    // ── DAOs ──────────────────────────────────────────────────────────────────
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final PedidoDAO  pedidoDAO  = new PedidoDAO();

    // ── Objetos carregados em memória ─────────────────────────────────────────
    private Cliente clienteAtual;
    private Produto produtoAtual;

    // ── Controle de ID ─────────────────────────────────────────────────────────
    private static int proximoIdPedido = 1;

    // ─────────────────────────────────────────────────────────────────────────
    // INICIALIZAÇÃO
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCamposReadOnly();
        configurarListenerCPF();
        configurarListenerProduto();
        configurarListenerQuantidade();

        // Liga o botão ao método (pois o FXML não tem onAction definido)
        btnRealizarCompra.setOnAction(e -> realizarCompra());
    }

    // Campos preenchidos automaticamente ficam somente leitura
    private void configurarCamposReadOnly() {
        txtCEP.setEditable(false);
        txtEnd.setEditable(false);
        txtUF.setEditable(false);
        txtCidade.setEditable(false);
        txtBairro.setEditable(false);
        txtNum.setEditable(false);
        txtPrescricao.setEditable(false);
        txtValorUni.setEditable(false);
        txtValor.setEditable(false);
        txtID.setEditable(false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LISTENERS
    // ─────────────────────────────────────────────────────────────────────────

    // Busca o cliente ao perder foco no campo de CPF
    private void configurarListenerCPF() {
        txtCPF.focusedProperty().addListener((obs, focoAntigo, focoNovo) -> {
            if (!focoNovo && !txtCPF.getText().trim().isEmpty()) {
                buscarClientePorCPF(txtCPF.getText().trim());
            }
        });
    }

    // Busca o produto ao perder foco no campo de nome
    private void configurarListenerProduto() {
        txtProduto.focusedProperty().addListener((obs, focoAntigo, focoNovo) -> {
            if (!focoNovo && !txtProduto.getText().trim().isEmpty()) {
                buscarProdutoPorNome(txtProduto.getText().trim());
            }
        });
    }

    // Recalcula o total automaticamente ao alterar a quantidade
    private void configurarListenerQuantidade() {
        txtQuantidade.textProperty().addListener((obs, antigo, novo) -> calcularTotal());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BUSCAS NO BANCO
    // ─────────────────────────────────────────────────────────────────────────

    private void buscarClientePorCPF(String cpf) {
        try {
            // Usa listar() com critério, pois não há busca direta por CPF no DAO
            Collection<Cliente> resultado = clienteDAO.listar("cpf = '" + cpf + "'");

            if (resultado.isEmpty()) {
                exibirAlerta(Alert.AlertType.WARNING,
                        "Cliente não encontrado",
                        "Nenhum cliente com o CPF: " + cpf);
                limparDadosCliente();
                return;
            }

            clienteAtual = resultado.iterator().next(); // CPF é único → sempre 1 resultado
            preencherDadosCliente(clienteAtual);

        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro no banco", e.getMessage());
        }
    }

    private void buscarProdutoPorNome(String nome) {
        try {
            // Busca por nome parcial (LIKE)
            Collection<Produto> resultado = produtoDAO.listar("nome LIKE '%" + nome + "%'");

            if (resultado.isEmpty()) {
                exibirAlerta(Alert.AlertType.WARNING,
                        "Produto não encontrado",
                        "Nenhum produto com o nome: " + nome);
                limparDadosProduto();
                return;
            }

            produtoAtual = resultado.iterator().next(); // pega o primeiro da lista
            preencherDadosProduto(produtoAtual);

        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro no banco", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PREENCHIMENTO DOS CAMPOS
    // ─────────────────────────────────────────────────────────────────────────

    private void preencherDadosCliente(Cliente c) {
        txtCEP.setText(c.getCep());
        txtNum.setText(c.getNum_comp());
        txtEnd.setText(c.getEndereco());
        txtUF.setText(c.getUf());
        txtCidade.setText(c.getCidade());
        txtBairro.setText(c.getBairro());
    }

    private void preencherDadosProduto(Produto p) {
        txtProduto.setText(p.getNome());
        txtID.setText(String.valueOf(p.getId()));
        txtPrescricao.setText(p.getPrescricao() ? "Sim" : "Não");
        txtValorUni.setText(String.format("R$ %.2f", p.getPreco()).replace(".", ","));
        calcularTotal();
    }

    private void calcularTotal() {
        if (produtoAtual == null || txtQuantidade.getText().trim().isEmpty()) {
            txtValor.clear();
            return;
        }
        try {
            int qtd = Integer.parseInt(txtQuantidade.getText().trim());
            float total = produtoAtual.getPreco() * qtd;
            txtValor.setText(String.format("R$ %.2f", total).replace(".", ","));
        } catch (NumberFormatException e) {
            txtValor.clear();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AÇÃO DO BOTÃO
    // ─────────────────────────────────────────────────────────────────────────

    private void realizarCompra() {
        if (!validarCampos()) return;

        try {
            int qtd     = Integer.parseInt(txtQuantidade.getText().trim());
            float total = produtoAtual.getPreco() * qtd;

            Pedido pedido = new Pedido();
            pedido.setId(proximoIdPedido++);
            pedido.setCliente(clienteAtual);
            pedido.setProduto(produtoAtual);
            pedido.setData(LocalDate.now().toString()); // "YYYY-MM-DD"
            pedido.setQuantidade(qtd);
            pedido.setPrecoTotal(total);

            if (pedidoDAO.insere(pedido)) {
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Compra realizada com sucesso!");
                limparTudo();
            } else {
                exibirAlerta(Alert.AlertType.ERROR, "Falha", "Não foi possível registrar a compra.");
            }

        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Quantidade inválida", "Digite um número inteiro válido.");
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro no banco", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VALIDAÇÃO
    // ─────────────────────────────────────────────────────────────────────────

    private boolean validarCampos() {
        if (clienteAtual == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Cliente não carregado",
                    "Preencha o CPF para carregar os dados do cliente.");
            return false;
        }
        if (produtoAtual == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Produto não carregado",
                    "Preencha o nome do produto para carregar os dados.");
            return false;
        }
        if (txtQuantidade.getText().trim().isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Quantidade obrigatória",
                    "Informe a quantidade do produto.");
            return false;
        }
        try {
            int qtd = Integer.parseInt(txtQuantidade.getText().trim());
            if (qtd <= 0) {
                exibirAlerta(Alert.AlertType.WARNING, "Quantidade inválida",
                        "A quantidade deve ser maior que zero.");
                return false;
            }
        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.WARNING, "Quantidade inválida",
                    "Digite apenas números inteiros na quantidade.");
            return false;
        }
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LIMPEZA
    // ─────────────────────────────────────────────────────────────────────────

    private void limparDadosCliente() {
        clienteAtual = null;
        txtCEP.clear(); txtNum.clear(); txtEnd.clear();
        txtUF.clear();  txtCidade.clear(); txtBairro.clear();
    }

    private void limparDadosProduto() {
        produtoAtual = null;
        txtID.clear(); txtPrescricao.clear();
        txtValorUni.clear(); txtValor.clear();
    }

    private void limparTudo() {
        txtCPF.clear();
        txtProduto.clear();
        txtQuantidade.clear();
        limparDadosCliente();
        limparDadosProduto();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────────────────────────────────────

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}