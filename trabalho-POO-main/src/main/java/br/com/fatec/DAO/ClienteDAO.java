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
import java.util.Collection;
/**
 *
 * @author biel2
 */

//CREATE TABLE `loja`.`cliente` (`id` INT NOT NULL , `cpf` VARCHAR(255) NOT NULL , `nome` VARCHAR(255) NOT NULL , `datanasc` VARCHAR(255) NOT NULL , `cep` VARCHAR(255) NOT NULL , `endereco` VARCHAR(255) NOT NULL , `num_comp` VARCHAR(255) NOT NULL , `bairro` VARCHAR(255) NOT NULL , `cidade` VARCHAR(255) NOT NULL , `uf` VARCHAR(255) NOT NULL , `fone` VARCHAR(255) NOT NULL , `email` VARCHAR(255) NOT NULL , `usuario` VARCHAR(255) NOT NULL , `senha` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;
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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean altera(Cliente model) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Cliente buscarID(Cliente model) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Collection<Cliente> listar(String criterio) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
