package br.com.fatec.controladores;

import java.io.IOException;
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

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtSenha;

    @FXML
    private void Logar(ActionEvent event) throws IOException {

        String usuario = txtUsuario.getText();
        String senha   = txtSenha.getText();

        // ── Validação básica ──────────────────────────────────────────
        if (usuario.isBlank() || senha.isBlank()) {
            alerta(AlertType.WARNING, "Atenção", "Preencha usuário e senha!");
            return;
        }

        // ── Verifica credenciais ──────────────────────────────────────
        if (usuario.equals("admin") && senha.equals("admin")) {

            // ✅ Login correto — abre a próxima tela
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/tela_principal.fxml") // ← nome do seu fxml
            );
            Parent root = loader.load();

            // Pega o Stage atual pela cena do botão
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tela Principal");
            stage.show();

        } else {
            // ❌ Login incorreto
            alerta(AlertType.ERROR, "Acesso negado", "Usuário ou senha incorretos!");
            txtSenha.clear();
            txtUsuario.requestFocus();
        }
    }

    private void alerta(AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}