package br.com.fatec.controladores;

import br.com.fatec.DAO.ClienteDAO;
import br.com.fatec.model.Cliente;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastroController {

    // ── Campos @FXML ──────────────────────────────────────────────────────
    @FXML private TextField txtCPF;
    @FXML private TextField txtNome;
    @FXML private TextField txtData;
    @FXML private TextField txtCEP;
    @FXML private TextField txtEnd;
    @FXML private TextField txtNum;
    @FXML private TextField txtBairro;
    @FXML private TextField txtCidade;
    @FXML private TextField txtUF;
    @FXML private TextField txtFone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtSenha;

    // ── DAO ───────────────────────────────────────────────────────────────
    private final ClienteDAO dao = new ClienteDAO();

    // ─────────────────────────────────────────────────────────────────────
    // GRAVAR
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void Gravar(ActionEvent event) throws IOException {

        if (txtNome.getText().isBlank() || txtCPF.getText().isBlank()) {
            alerta(AlertType.WARNING, "Atenção", "Nome e CPF são obrigatórios!");
            return;
        }

        try {
            Cliente c = new Cliente();
            c.setCpf(txtCPF.getText());
            c.setNome(txtNome.getText());
            c.setDatanasc(txtData.getText());
            c.setCep(txtCEP.getText());
            c.setEndereco(txtEnd.getText());
            c.setNum_comp(txtNum.getText());
            c.setBairro(txtBairro.getText());
            c.setCidade(txtCidade.getText());
            c.setUf(txtUF.getText());
            c.setFone(txtFone.getText());
            c.setEmail(txtEmail.getText());
            c.setUsuario(txtUsuario.getText());
            c.setSenha(txtSenha.getText());

            boolean ok = dao.insere(c);

            if (ok) {
                alerta(AlertType.INFORMATION, "Sucesso", "Cliente gravado com sucesso!");
                limparCampos();
            } else {
                alerta(AlertType.ERROR, "Erro", "Não foi possível gravar o cliente.");
            }

        } catch (SQLException e) {
            alerta(AlertType.ERROR, "Erro no Banco", "Erro: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // BUSCAR — pelo CPF
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void Buscar(ActionEvent event) {

        if (txtCPF.getText().isBlank()) {
            alerta(AlertType.WARNING, "Atenção", "Informe o CPF para buscar.");
            return;
        }

        try {
            // busca por CPF usando listar() com critério
            String criterio = "cpf = '" + txtCPF.getText() + "'";
            var lista = dao.listar(criterio);

            if (!lista.isEmpty()) {
                Cliente encontrado = lista.iterator().next();

                // Preenche os campos com os dados respectivos
                txtCPF.setText(encontrado.getCpf());
                txtNome.setText(encontrado.getNome());
                txtData.setText(encontrado.getDatanasc());
                txtCEP.setText(encontrado.getCep());
                txtEnd.setText(encontrado.getEndereco());
                txtNum.setText(encontrado.getNum_comp());
                txtBairro.setText(encontrado.getBairro());
                txtCidade.setText(encontrado.getCidade());
                txtUF.setText(encontrado.getUf());
                txtFone.setText(encontrado.getFone());
                txtEmail.setText(encontrado.getEmail());
                txtUsuario.setText(encontrado.getUsuario());
                txtSenha.setText(encontrado.getSenha());

            } else {
                alerta(AlertType.WARNING, "Não encontrado", "Nenhum cliente com esse CPF.");
            }

        } catch (SQLException e) {
            alerta(AlertType.ERROR, "Erro no Banco", "Erro: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // ALTERAR
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void Alterar(ActionEvent event) {

        if (txtCPF.getText().isBlank()) {
            alerta(AlertType.WARNING, "Atenção", "Informe o CPF para alterar.");
            return;
        }

        try {
            Cliente c = new Cliente();
            c.setCpf(txtCPF.getText());
            c.setNome(txtNome.getText());
            c.setDatanasc(txtData.getText());
            c.setCep(txtCEP.getText());
            c.setEndereco(txtEnd.getText());
            c.setNum_comp(txtNum.getText());
            c.setBairro(txtBairro.getText());
            c.setCidade(txtCidade.getText());
            c.setUf(txtUF.getText());
            c.setFone(txtFone.getText());
            c.setEmail(txtEmail.getText());
            c.setUsuario(txtUsuario.getText());
            c.setSenha(txtSenha.getText());

            boolean ok = dao.altera(c);

            if (ok) {
                alerta(AlertType.INFORMATION, "Sucesso", "Cliente alterado!");
                limparCampos();
            } else {
                alerta(AlertType.ERROR, "Erro", "Não foi possível alterar.");
            }

        } catch (SQLException e) {
            alerta(AlertType.ERROR, "Erro no Banco", "Erro: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // EXCLUIR
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void Excluir(ActionEvent event) throws IOException {

        if (txtCPF.getText().isBlank()) {
            alerta(AlertType.WARNING, "Atenção", "Informe o CPF para excluir.");
            return;
        }

        try {
            //  busca o cliente pelo CPF primeiro para pegar o ID
            String criterio = "cpf = '" + txtCPF.getText() + "'";
            var lista = dao.listar(criterio);

            if (!lista.isEmpty()) {
                Cliente c = lista.iterator().next();
                boolean ok = dao.remove(c); 

                if (ok) {
                    alerta(AlertType.INFORMATION, "Sucesso", "Cliente excluído!");
                    limparCampos();
                } else {
                    alerta(AlertType.ERROR, "Erro", "Não foi possível excluir.");
                }
            } else {
                alerta(AlertType.WARNING, "Não encontrado", "Nenhum cliente com esse CPF.");
            }

        } catch (SQLException e) {
            alerta(AlertType.ERROR, "Erro no Banco", "Erro: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // ENCERRAR
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void Encerrar(ActionEvent event) throws IOException {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    // ── Métodos auxiliares ────────────────────────────────────────────────
    private void limparCampos() {
      
        txtCPF.clear();
        txtNome.clear();
        txtData.clear();
        txtCEP.clear();
        txtEnd.clear();
        txtNum.clear();
        txtBairro.clear();
        txtCidade.clear();
        txtUF.clear();
        txtFone.clear();
        txtEmail.clear();
        txtUsuario.clear();
        txtSenha.clear();
    }

    private void alerta(AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

}