/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.fatec.model;

/**
 *
 * @author biel2
 */
public class Cliente {
    public int id;
    public String cpf, nome, datanasc, cep, num_comp, bairro, cidade, uf, fone, email, usuario, senha, endereco;

    public Cliente(int id, String cpf, String nome, String datanasc, String cep, String num_comp, String bairro, String cidade, String uf, String fone, String email, String usuario, String senha, String endereco) {
        this.id = id;
        this.cpf = cpf;
        this.nome = nome;
        this.datanasc = datanasc;
        this.cep = cep;
        this.num_comp = num_comp;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.fone = fone;
        this.email = email;
        this.usuario = usuario;
        this.senha = senha;
        this.endereco = endereco;
    }

    public Cliente() {
    }

    public Cliente(int id) {
        this.id = id;
    }

    

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDatanasc() {
        return datanasc;
    }

    public void setDatanasc(String datanasc) {
        this.datanasc = datanasc;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNum_comp() {
        return num_comp;
    }

    public void setNum_comp(String num_comp) {
        this.num_comp = num_comp;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getFone() {
        return fone;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    
}
