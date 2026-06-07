package br.com.fatec.util;

public class Sessao {

    public enum Perfil { ADMIN, CLIENTE }

    private static Perfil perfilAtual = null;

    public static void iniciar(Perfil perfil) {
        perfilAtual = perfil;
    }

    public static boolean isAdmin() {
        return perfilAtual == Perfil.ADMIN;
    }

    public static void encerrar() {
        perfilAtual = null;
    }
}