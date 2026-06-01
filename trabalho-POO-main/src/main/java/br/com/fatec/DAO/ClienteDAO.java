/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.fatec.DAO;

import br.com.fatec.model.Cliente;
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

//CREATE TABLE `loja`.`cliente` (`id` INT NOT NULL , `cpf` VARCHAR(255) UNiQUE NOT NULL , `nome` VARCHAR(255) NOT NULL , `datanasc` VARCHAR(255) NOT NULL , `cep` VARCHAR(255) NOT NULL , `endereco` VARCHAR(255) NOT NULL , `num_comp` VARCHAR(255) NOT NULL , `bairro` VARCHAR(255) NOT NULL , `cidade` VARCHAR(255) NOT NULL , `uf` VARCHAR(255) NOT NULL , `fone` VARCHAR(255) NOT NULL , `email` VARCHAR(255) UNIQUE NOT NULL , `usuario` VARCHAR(255) NOT NULL , `senha` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;
public class ClienteDAO implements DAO <Cliente>{
//variaveis auxiliares
    private Cliente cliente;
    //auxiliares para acesso aos dados
    
    //para conter os comandos DML
    private PreparedStatement pst; //pacote java.sql
    //para conter os dados vindos do BD
    private ResultSet rs; //pacote java.sql

    @Override
    public boolean insere(Cliente model) throws SQLException {
        String sql = "INSERT INTO cliente (id, cpf, nome, datanasc, cep, endereco, num_comp, bairro, cidade, uf, fone, email, usuario, senha) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        
        //Abre a conexao
        Banco.conectar();
        
        //cria o comando preparado
        pst = Banco.obterConexao().prepareStatement(sql);
        
        //coloca os valores dentro do comando
        //substitui as '?' por dados
        pst.setInt(1, model.getId());
        pst.setString(2, model.getCpf());
        pst.setString(3, model.getNome());
        pst.setString(4, model.getDatanasc());
        pst.setString(5, model.getCep());
        pst.setString(6, model.getEndereco());
        pst.setString(7, model.getNum_comp());
        pst.setString(8, model.getBairro());
        pst.setString(9, model.getCidade());
        pst.setString(10, model.getUf());
        pst.setString(11, model.getFone());
        pst.setString(12, model.getEmail());
        pst.setString(13, model.getUsuario());
        pst.setString(14, model.getSenha());
         
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
    public boolean remove(Cliente model) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?;";
        
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
    public boolean altera(Cliente model) throws SQLException {
        String sql = "UPDATE cliente SET nome = ? "
        + "WHERE id = ?;";
        
        //Abre a conexao
        Banco.conectar();
        
        //cria o comando preparado
        pst = Banco.obterConexao().prepareStatement(sql);
        
        //coloca os valores dentro do comando
        //substitui as '?' por dados
        
        pst.setString(1, model.getNome());
        pst.setInt(2, model.getId());
        
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
    public Cliente buscarID(Cliente model) throws SQLException {
        cliente = null;
        
        //Comando SELECT
        String sql = "SELECT * FROM cliente WHERE id = ?;";
        
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
            cliente = new Cliente();
            //move os dados do resultSet para o objeto proprietario
            cliente.setId(rs.getInt("id"));
            cliente.setCpf(rs.getString("cpf"));
            cliente.setNome(rs.getString("nome"));
            cliente.setDatanasc(rs.getString("datanasc"));
            cliente.setCep(rs.getString("cep"));
            cliente.setEndereco(rs.getString("endereco"));
            cliente.setNum_comp(rs.getString("num_comp"));
            cliente.setBairro(rs.getString("bairro"));
            cliente.setCidade(rs.getString("cidade"));
            cliente.setUf(rs.getString("uf"));
            cliente.setFone(rs.getString("fone"));
            cliente.setEmail(rs.getString("email"));
            cliente.setUsuario(rs.getString("usuario"));
            cliente.setSenha(rs.getString("senha"));
            
        }
        
        Banco.desconectar();
        //fechar o resultSet
        rs.close();
        
        return cliente;
    }

    @Override
    public Collection<Cliente> listar(String criterio) throws SQLException {
        Collection<Cliente> listagem = new ArrayList<>();
        
        cliente = null;
        //Comando SELECT
        String sql = "SELECT * FROM cliente ";
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
            cliente = new Cliente();
            //move os dados do resultSet para o objeto veiculo
            cliente.setId(rs.getInt("id"));
            cliente.setCpf(rs.getString("cpf"));
            cliente.setNome(rs.getString("nome"));
            cliente.setDatanasc(rs.getString("datanasc"));
            cliente.setCep(rs.getString("cep"));
            cliente.setEndereco(rs.getString("endereco"));
            cliente.setNum_comp(rs.getString("num_comp"));
            cliente.setBairro(rs.getString("bairro"));
            cliente.setCidade(rs.getString("cidade"));
            cliente.setUf(rs.getString("uf"));
            cliente.setFone(rs.getString("fone"));
            cliente.setEmail(rs.getString("email"));
            cliente.setUsuario(rs.getString("usuario"));
            cliente.setSenha(rs.getString("senha"));
            
            //adicionar na coleção
            listagem.add(cliente);
        }
        
        Banco.desconectar();
        rs.close();
        
        
        return listagem;
    }
}
