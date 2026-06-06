package br.com.fatec.controladores;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Node;

public class PrincipalController {

    // ── Botões @FXML ──────────────────────────────────────────────────────
    @FXML private Button Pedido;
    @FXML private Button Lista;
    @FXML private Button Relatorio;
    @FXML private Button Cadastro;

    // ─────────────────────────────────────────────────────────────────────
    // REALIZAR PEDIDO → tela_compra.fxml
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void Pedido(ActionEvent event) throws IOException {
        trocarTela(event, "tela_compra");
    }

    // ─────────────────────────────────────────────────────────────────────
    // LISTA PRODUTOS → consulta.fxml
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void Lista(ActionEvent event) throws IOException {
        trocarTela(event, "consulta");
    }

    // ─────────────────────────────────────────────────────────────────────
    // RELATÓRIO → tela_relatorios.fxml
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void Relatorio(ActionEvent event) throws IOException {
        trocarTela(event, "tela_relatorios");
    }

    // ─────────────────────────────────────────────────────────────────────
    // CADASTRO PRODUTO → cadastrar_produtos.fxml
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void Cadastro(ActionEvent event) throws IOException {
        trocarTela(event, "cadastrar_produtos");
    }

    // ── Método auxiliar — evita repetição de código ───────────────────────
    private void trocarTela(ActionEvent event, String nomeFxml) throws IOException {
        Parent root = FXMLLoader.load(
            getClass().getResource("/fxml/" + nomeFxml + ".fxml")
        );
        Stage stage = (Stage) ((Node) event.getSource())
            .getScene()
            .getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(nomeFxml);
        stage.show();
    }
}