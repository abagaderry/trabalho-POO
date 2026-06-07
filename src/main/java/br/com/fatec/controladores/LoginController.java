package br.com.fatec.controladores;

import br.com.fatec.DAO.ClienteDAO;
import br.com.fatec.util.Sessao;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField     txtUsuario;
    @FXML private PasswordField txtSenha;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @FXML
    public void initialize() {
        txtUsuario.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        Stage stage = (Stage) newWindow;
                        stage.setOnCloseRequest(event -> {
                            event.consume();
                            try {
                                Parent root = FXMLLoader.load(
                                    getClass().getResource("/fxml/tela_inicial.fxml")
                                );
                                stage.setScene(new Scene(root));
                                stage.setTitle("Tela Inicial");
                                stage.show();
                                stage.setOnCloseRequest(null);
                            } catch (IOException e) {
                                alerta(AlertType.ERROR, "Erro", "Não foi possível abrir a tela inicial.");
                            }
                        });
                    }
                });
            }
        });
    }

    @FXML
    private void Logar(ActionEvent event) throws IOException {
        String usuario = txtUsuario.getText().trim();
        String senha   = txtSenha.getText().trim();

        if (usuario.isBlank() || senha.isBlank()) {
            alerta(AlertType.WARNING, "Atenção", "Preencha usuário e senha!");
            return;
        }

        // ── Verifica se é admin ───────────────────────────────────────────────
        if (usuario.equals("admin") && senha.equals("admin")) {
            Sessao.iniciar(Sessao.Perfil.ADMIN);
            abrirTelaPrincipal();
            return;
        }

        // ── Verifica se é cliente cadastrado no banco ─────────────────────────
        try {
            String criterio = "usuario = '" + usuario + "' AND senha = '" + senha + "'";
            Collection resultado = clienteDAO.listar(criterio);

            if (!resultado.isEmpty()) {
                Sessao.iniciar(Sessao.Perfil.CLIENTE);
                abrirTelaPrincipal();
            } else {
                alerta(AlertType.ERROR, "Acesso negado", "Usuário ou senha incorretos!");
                txtSenha.clear();
                txtUsuario.requestFocus();
            }

        } catch (SQLException e) {
            alerta(AlertType.ERROR, "Erro no banco", "Detalhe: " + e.getMessage());
        }
    }

    private void abrirTelaPrincipal() throws IOException {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/fxml/tela_principal.fxml")
        );
        Parent root = loader.load();
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Tela Principal");
        stage.show();
    }

    private void alerta(AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}