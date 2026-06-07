package br.com.fatec.controladores;

import br.com.fatec.DAO.ClienteDAO;
import br.com.fatec.model.Cliente;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadastroController implements Initializable {

    // ── Componentes do FXML ──────────────────────────────────────────────────
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

    @FXML private Button btnGravar;
    @FXML private Button btnBuscar;
    @FXML private Button btnExcluir;
    @FXML private Button btnEncerrar;

    // ── DAO e estado ──────────────────────────────────────────────────────────
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private boolean incluindo = true;
    private Cliente cliente;

    // ─────────────────────────────────────────────────────────────────────────
    // INICIALIZAÇÃO
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ── Máscaras ──────────────────────────────────────────────────────────
        aplicarMascaraCPF(txtCPF);         // 000.000.000-00
        aplicarMascaraData(txtData);       // 00/00/0000
        aplicarMascaraCEP(txtCEP);         // 00000-000
        aplicarMascaraTelefone(txtFone);   // (00) 00000-0000
        aplicarMascaraUF(txtUF);           // 2 letras maiúsculas
        aplicarMascaraSomenteNumeros(txtNum); // apenas dígitos

        limparCampos();

        // Intercepta o X vermelho e volta para a tela inicial
        txtCPF.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
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

    // ─────────────────────────────────────────────────────────────────────────
    // AÇÕES DOS BOTÕES
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    private void Gravar(ActionEvent event) throws IOException {
        if (!validarDados()) return;

        cliente = carregarModel();

        try {
            if (incluindo) {
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

    @FXML
    private void Buscar(ActionEvent event) {
        if (txtCPF.getText().isBlank()) {
            alerta(AlertType.WARNING, "Atenção", "Informe o CPF para buscar.");
            return;
        }

        try {
            String criterio = "cpf = '" + txtCPF.getText() + "'";
            var lista = clienteDAO.listar(criterio);

            if (!lista.isEmpty()) {
                Cliente encontrado = lista.iterator().next();

                // Os campos com máscara recebem o valor do banco normalmente
                // — o listener reformata automaticamente ao setar o texto
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

    @FXML
    private void Encerrar(ActionEvent event) {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MÁSCARAS
    // ─────────────────────────────────────────────────────────────────────────

    // CPF → 000.000.000-00
    private void aplicarMascaraCPF(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String digits = novo.replaceAll("[^\\d]", "");
            if (digits.length() > 11) digits = digits.substring(0, 11);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 3 || i == 6) sb.append('.');
                if (i == 9)           sb.append('-');
                sb.append(digits.charAt(i));
            }

            String formatado = sb.toString();
            if (!novo.equals(formatado)) {
                campo.setText(formatado);
                campo.positionCaret(formatado.length());
            }
        });
    }

    // Data → 00/00/0000
    private void aplicarMascaraData(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String digits = novo.replaceAll("[^\\d]", "");
            if (digits.length() > 8) digits = digits.substring(0, 8);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 2 || i == 4) sb.append('/');
                sb.append(digits.charAt(i));
            }

            String formatado = sb.toString();
            if (!novo.equals(formatado)) {
                campo.setText(formatado);
                campo.positionCaret(formatado.length());
            }
        });
    }

    // CEP → 00000-000
    private void aplicarMascaraCEP(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String digits = novo.replaceAll("[^\\d]", "");
            if (digits.length() > 8) digits = digits.substring(0, 8);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 5) sb.append('-');
                sb.append(digits.charAt(i));
            }

            String formatado = sb.toString();
            if (!novo.equals(formatado)) {
                campo.setText(formatado);
                campo.positionCaret(formatado.length());
            }
        });
    }

    // Telefone → (00) 00000-0000
    private void aplicarMascaraTelefone(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String digits = novo.replaceAll("[^\\d]", "");
            if (digits.length() > 11) digits = digits.substring(0, 11);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i == 0) sb.append('(');
                if (i == 2) sb.append(") ");
                if (i == 7) sb.append('-');
                sb.append(digits.charAt(i));
            }

            String formatado = sb.toString();
            if (!novo.equals(formatado)) {
                campo.setText(formatado);
                campo.positionCaret(formatado.length());
            }
        });
    }

    // UF → 2 letras maiúsculas
    private void aplicarMascaraUF(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String letras = novo.replaceAll("[^a-zA-Z]", "").toUpperCase();
            if (letras.length() > 2) letras = letras.substring(0, 2);

            if (!novo.equals(letras)) {
                campo.setText(letras);
                campo.positionCaret(letras.length());
            }
        });
    }

    // Número/Comp → somente dígitos
    private void aplicarMascaraSomenteNumeros(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String digits = novo.replaceAll("[^\\d]", "");
            if (!novo.equals(digits)) {
                campo.setText(digits);
                campo.positionCaret(digits.length());
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private Cliente carregarModel() {
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

    public void habilitarBotoes(boolean inclusao) {
        btnExcluir.setDisable(inclusao);
        txtCPF.setEditable(inclusao);
    }

    private void limparCampos() {
        txtCPF.clear();    txtNome.clear();    txtData.clear();
        txtCEP.clear();    txtEnd.clear();     txtNum.clear();
        txtBairro.clear(); txtCidade.clear();  txtUF.clear();
        txtFone.clear();   txtEmail.clear();   txtUsuario.clear();
        txtSenha.clear();

        incluindo = true;
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