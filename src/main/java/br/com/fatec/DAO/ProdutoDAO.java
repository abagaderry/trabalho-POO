/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.fatec.DAO;

import br.com.fatec.model.Produto;
import br.com.fatec.persistencia.Banco;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;


/**
 *
 * @author biel2
 */

    //Create table
    //CREATE TABLE `loja`.`produto` (`id` INT NOT NULL , `nome` VARCHAR(100) NOT NULL , `prescricao` BOOLEAN NOT NULL , `descricao` VARCHAR(255) NOT NULL , `preco` FLOAT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;
public class ProdutoDAO implements DAO <Produto>{
//variaveis auxiliares
    private Produto produto;
    //auxiliares para acesso aos dados
    
    //para conter os comandos DML
    private PreparedStatement pst; //pacote java.sql
    //para conter os dados vindos do BD
    private ResultSet rs; //pacote java.sql
     
    @Override
    public boolean insere(Produto model) throws SQLException {
        
        String sql = "INSERT INTO produto (nome, prescricao, descricao, preco) "
        + "VALUES (?, ?, ?, ?);";
        
        //Abre a conexao
        Banco.conectar();
        
        //cria o comando preparado
        pst = Banco.obterConexao().prepareStatement(sql);
        
        //coloca os valores dentro do comando
        //substitui as '?' por dados
        pst.setString(1, model.getNome());
        pst.setBoolean(2, model.getPrescricao());
        pst.setString(3, model.getDescricao());
        pst.setFloat(4, model.getPreco());
        
        //executa o comando
        if(pst.executeUpdate() >= 1) { //tudo certo
            Banco.desconectar();
            return true;
        }
        else {
            Banco.desconectar();
            return false;
        }
    }

    @Override
    public boolean remove(Produto model) throws SQLException {
        String sql = "DELETE FROM produto WHERE id = ?;";
        
        //Abre a conexao
        Banco.conectar();
        
        //cria o comando preparado
        pst = Banco.obterConexao().prepareStatement(sql);
        
        //coloca os valores dentro do comando
        //substitui as '?' por dados
        pst.setInt(1, model.getId());
        
        //executa o comando
        if(pst.executeUpdate() >= 1) { //tudo certo
            Banco.desconectar();
            return true;
        }
        else {
            Banco.desconectar();
            return false;
        }
    }

    @Override
    public boolean altera(Produto model) throws SQLException {
        String sql = "UPDATE produto SET nome = ?, prescricao = ?, descricao = ?, preco = ? "
        + "WHERE nome = ?;";
        
        //Abre a conexao
        Banco.conectar();
        
        //cria o comando preparado
        pst = Banco.obterConexao().prepareStatement(sql);
        
        //coloca os valores dentro do comando
        //substitui as '?' por dados
        
        pst.setString(1, model.getNome());
        pst.setBoolean(2, model.getPrescricao());
        pst.setString(3, model.getDescricao());
        pst.setFloat(4, model.getPreco());
        pst.setString(5, model.getNome());
        
        //executa o comando
        if(pst.executeUpdate() >= 1) { //tudo certo
            Banco.desconectar();
            return true;
        }
        else {
            Banco.desconectar();
            return false;
        }
    }

    @Override
    public Produto buscarID(Produto model) throws SQLException {
        produto = null;
        
        //Comando SELECT
        String sql = "SELECT * FROM Produto WHERE id = ?;";
        
        //conecta ao banco
        Banco.conectar();
        
        //cria o comando preparado
        pst = Banco.obterConexao().prepareStatement(sql);
        
        //troca a ?
        pst.setInt(1, model.getId());
        
        //Executa o comando SELECT
        rs = pst.executeQuery();
        
        //le o próximo regitro
        if(rs.next()) { //achou 1 registro
            //cria o objeto proprietario
            produto = new Produto();
            //move os dados do resultSet para o objeto proprietario
            produto.setId(rs.getInt("id"));
            produto.setNome(rs.getString("nome"));
            produto.setPrescricao(rs.getBoolean("prescricao"));
            produto.setDescricao(rs.getString("descricao"));
            produto.setPreco(rs.getFloat("preco"));       //Não funciona ainda
        }
        
        Banco.desconectar();
        //fechar o resultSet
        rs.close();
        
        return produto;
    }

    @Override
    public Collection<Produto> listar(String criterio) throws SQLException {
        Collection<Produto> listagem = new ArrayList<>();
        
        produto = null;
        //Comando SELECT
        String sql = "SELECT * FROM Produto ";
        //colocar filtro ou nao
        if(criterio.length() != 0) {
            sql += "WHERE " + criterio;
        }
        
        //conecta ao banco
        Banco.conectar();
        
        pst = Banco.obterConexao().prepareStatement(sql);
        
        //Executa o comando SELECT
        rs = pst.executeQuery();
        
        //le o próximo regitro
        while(rs.next()) { //achou 1 registro
            //cria o objeto veiculo
            produto = new Produto();
            //move os dados do resultSet para o objeto veiculo
            produto.setId(rs.getInt("id"));
            produto.setNome(rs.getString("nome"));
            produto.setPrescricao(rs.getBoolean("prescricao"));
            produto.setDescricao(rs.getString("descricao"));
            produto.setPreco(rs.getFloat("preco"));       
            
            //adicionar na coleção
            listagem.add(produto);
        }
        
        Banco.desconectar();
        rs.close();
        
        
        return listagem;
    }
    
}
