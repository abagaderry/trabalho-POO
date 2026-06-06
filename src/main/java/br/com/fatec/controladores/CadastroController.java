package br.com.fatec.controladores;

import br.com.fatec.DAO.ClienteDAO;
import br.com.fatec.model.Cliente;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastroController implements Initializable {

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

    // ── Botões @FXML ──────────────────────────────────────────────────────
    @FXML private Button btnGravar;
    @FXML private Button btnBuscar;   // ✅ adicionado
    @FXML private Button btnExcluir;
    @FXML private Button btnEncerrar;

    // ── DAO ───────────────────────────────────────────────────────────────
    // ✅ apenas UM dao — removido o duplicado
    private final ClienteDAO clienteDAO = new ClienteDAO();

    // ── Variáveis auxiliares ──────────────────────────────────────────────
    private boolean incluindo = true;
    private Cliente cliente;

    // ─────────────────────────────────────────────────────────────────────
    // INICIALIZAÇÃO
    // ─────────────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limparCampos();
    }

    // ─────────────────────────────────────────────────────────────────────
    // GRAVAR / ALTERAR
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void Gravar(ActionEvent event) throws IOException {

        if (!validarDados()) return;

        cliente = carregar_Model();

        try {
            if (incluindo) {
                // ✅ usa clienteDAO (único DAO agora)
                if (clienteDAO.insere(cliente)) {
                    alerta(AlertType.INFORMATION, "Sucesso", "Inclusão feita com sucesso!");
                    limparCampos();
                } else {
                    alerta(AlertType.WARNING, "Falha", "Falha na inclusão.");
                }
            } else {
                if (clienteDAO.altera(cliente)) {
                    alerta(AlertType.INFORMATION, "Sucesso", "Alteração feita com sucesso!");
                    limparCampos();
                } else {
                    alerta(AlertType.WARNING, "Falha", "Falha na alteração.");
                    limparCampos();
                }
            }

        } catch (SQLException e) {
            alerta(AlertType.ERROR, "Erro no Banco", "Erro: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // BUSCAR
    // ─────────────────────────────────────────────────────────────────────
    @FXML
    private void Buscar(ActionEvent event) {

        if (txtCPF.getText().isBlank()) {
            alerta(AlertType.WARNING, "Atenção", "Informe o CPF para buscar.");
            return;
        }

        try {
            // ✅ usa clienteDAO (único DAO agora)
            String criterio = "cpf = '" + txtCPF.getText() + "'";
            var lista = clienteDAO.listar(criterio);

            if (!lista.isEmpty()) {
                Cliente encontrado = lista.iterator().next();

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

                habilitarBotoes(false);
                incluindo = false;

            } else {
                alerta(AlertType.WARNING, "Não encontrado", "Nenhum cliente com esse CPF.");
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
            String criterio = "cpf = '" + txtCPF.getText() + "'";
            var lista = clienteDAO.listar(criterio);

            if (!lista.isEmpty()) {
                Cliente c = lista.iterator().next();

                if (clienteDAO.remove(c)) {
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

    // ── Métodos de controle ───────────────────────────────────────────────
    private Cliente carregar_Model() {
        Cliente model = new Cliente();
        model.setCpf(txtCPF.getText());
        model.setNome(txtNome.getText());
        model.setDatanasc(txtData.getText());
        model.setCep(txtCEP.getText());
        model.setEndereco(txtEnd.getText());
        model.setNum_comp(txtNum.getText());
        model.setBairro(txtBairro.getText());
        model.setCidade(txtCidade.getText());
        model.setUf(txtUF.getText());
        model.setFone(txtFone.getText());
        model.setEmail(txtEmail.getText());
        model.setUsuario(txtUsuario.getText());
        model.setSenha(txtSenha.getText());
        return model;
    }

    /**
     * TRUE  → modo inclusão  → Excluir desabilitado
     * FALSE → modo alteração → Excluir habilitado
     */
    public void habilitarBotoes(boolean inclusao) {
        btnExcluir.setDisable(inclusao);  // desabilita ao incluir, habilita ao buscar
        txtCPF.setEditable(inclusao);     // CPF só editável no modo inclusão
    }

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

        incluindo = true;          // ✅ volta ao modo inclusão
        habilitarBotoes(true);
        txtCPF.requestFocus();
    }

    private boolean validarDados() {
        if (txtCPF.getText().isBlank()     || txtNome.getText().isBlank()    ||
            txtData.getText().isBlank()    || txtCEP.getText().isBlank()     ||
            txtEnd.getText().isBlank()     || txtNum.getText().isBlank()     ||
            txtBairro.getText().isBlank()  || txtCidade.getText().isBlank()  ||
            txtUF.getText().isBlank()      || txtFone.getText().isBlank()    ||
            txtEmail.getText().isBlank()   || txtUsuario.getText().isBlank() ||
            txtSenha.getText().isBlank()) {

            alerta(AlertType.WARNING, "Atenção", "Preencha todos os campos!");
            return false;
        }
        return true;
    }

    private void alerta(AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}