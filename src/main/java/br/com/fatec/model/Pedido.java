/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.fatec.model;

/**
 *
 * @author biel2
 */
public class Pedido {
    private int Id;
    private Cliente Cliente;
    private Produto Produto;
    private String Data;
    private int Quantidade;
    private float PrecoTotal;
    
    public Pedido() {}

    public Pedido(int Id, Cliente Cliente, Produto Produto, String Data, int Quantidade, float PrecoTotal) {
        this.Id = Id;
        this.Cliente = Cliente;
        this.Produto = Produto;
        this.Data = Data;
        this.Quantidade = Quantidade;
        this.PrecoTotal = PrecoTotal;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public Cliente getCliente() {
        return Cliente;
    }

    public void setCliente(Cliente Cliente) {
        this.Cliente = Cliente;
    }

    public Produto getProduto() {
        return Produto;
    }

    public void setProduto(Produto Produto) {
        this.Produto = Produto;
    }

    public String getData() {
        return Data;
    }

    public void setData(String Data) {
        this.Data = Data;
    }

    public int getQuantidade() {
        return Quantidade;
    }

    public void setQuantidade(int Quantidade) {
        this.Quantidade = Quantidade;
    }

    public float getPrecoTotal() {
        return PrecoTotal;
    }

    public void setPrecoTotal(float PrecoTotal) {
        this.PrecoTotal = PrecoTotal;
    }

}