package br.com.fatec.controladores;

import br.com.fatec.util.Sessao;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PrincipalController implements Initializable {

    @FXML private Button Pedido;
    @FXML private Button Lista;
    @FXML private Button Relatorio;
    @FXML private Button Cadastro;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // ── Esconde botões conforme o perfil ──────────────────────────────────
        boolean isAdmin = Sessao.isAdmin();
        Relatorio.setVisible(isAdmin);
        Cadastro.setVisible(isAdmin);

        // ── Handler do X vermelho ─────────────────────────────────────────────
        Pedido.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        Platform.runLater(() -> {
                            Stage stage = (Stage) newWindow;
                            stage.setOnCloseRequest(event -> {
                                event.consume();
                                Sessao.encerrar(); // limpa a sessão ao sair
                                try {
                                    Parent root = FXMLLoader.load(
                                        getClass().getResource("/fxml/tela_inicial.fxml")
                                    );
                                    stage.setScene(new Scene(root));
                                    stage.setTitle("Tela Inicial");
                                    stage.show();
                                    stage.setOnCloseRequest(null);
                                } catch (IOException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Erro");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Não foi possível abrir a tela inicial.");
                                    alert.showAndWait();
                                }
                            });
                        });
                    }
                });
            }
        });
    }

    @FXML
    private void Pedido(ActionEvent event) throws IOException {
        trocarTela(event, "tela_compra");
    }

    @FXML
    private void Lista(ActionEvent event) throws IOException {
        trocarTela(event, "consulta");
    }

    @FXML
    private void Relatorio(ActionEvent event) throws IOException {
        trocarTela(event, "tela_relatorios");
    }

    @FXML
    private void Cadastro(ActionEvent event) throws IOException {
        trocarTela(event, "cadastrar_produtos");
    }

    private void trocarTela(ActionEvent event, String nomeFxml) throws IOException {
        Parent root = FXMLLoader.load(
            getClass().getResource("/fxml/" + nomeFxml + ".fxml")
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(nomeFxml);
        stage.show();
    }
}