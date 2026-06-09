/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.fatec.model;

/**
 *
 * @author biel2
 */
public class Produto {
    public int id;
    public String nome;
    public boolean prescricao;
    public String descricao;
    public float preco;

    public Produto(int id, String nome, boolean prescricao, String descricao, float preco) {
        this.id = id;
        this.nome = nome;
        this.prescricao = prescricao;
        this.descricao = descricao;
        this.preco = preco;
    }

    public Produto(String nome, boolean prescricao, String descricao, float preco) {
        this.nome = nome;
        this.prescricao = prescricao;
        this.descricao = descricao;
        this.preco = preco;
    }

    
    
    public Produto() {
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean getPrescricao() {
        return prescricao;
    }

    public void setPrescricao(boolean prescricao) {
        this.prescricao = prescricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public float getPreco() {
        return preco;
    }

    public void setPreco(float preco) {
        this.preco = preco;
    }
    
}
