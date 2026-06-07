package br.com.fatec.controladores;

import br.com.fatec.DAO.ProdutoDAO;
import br.com.fatec.model.Produto;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CadProdutoController implements Initializable {

    // ── Componentes do FXML ──────────────────────────────────────────────────
    @FXML private Button           btnCadastro;
    @FXML private TextField        txtNome;
    @FXML private TextField        txtDescricao;
    @FXML private TextField        txtPreco;
    @FXML private ComboBox<String> cmbPrescricao;

    // ── DAO e controle de estado ──────────────────────────────────────────────
    private final ProdutoDAO produtoDAO   = new ProdutoDAO();
    private static int       proximoId    = 1;
    private Produto          produtoAtual = null;

    // ─────────────────────────────────────────────────────────────────────────
    // INICIALIZAÇÃO
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbPrescricao.setItems(FXCollections.observableArrayList("Sim", "Não"));

        // Máscaras aplicadas aos campos deste controller
        aplicarMascaraReal(txtPreco);

        // Ao fechar a janela, volta para a tela principal
        txtNome.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        Stage stage = (Stage) newWindow;
                        stage.setOnCloseRequest(event -> {
                            event.consume();
                            try {
                                Parent root = FXMLLoader.load(
                                    getClass().getResource("/fxml/tela_principal.fxml")
                                );
                                stage.setScene(new Scene(root));
                                stage.setTitle("Tela Principal");
                                stage.setOnCloseRequest(null);
                                stage.show();
                            } catch (IOException e) {
                                exibirAlerta(Alert.AlertType.ERROR,
                                    "Erro", "Não foi possível abrir a tela principal.");
                            }
                        });
                    }
                });
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PESQUISAR
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    private void Pesquisar() {
        String nome = txtNome.getText().trim();

        if (nome.isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING,
                "Campo obrigatório", "Digite o nome do produto para pesquisar.");
            return;
        }

        try {
            Collection<Produto> resultado = produtoDAO.listar("nome LIKE '%" + nome + "%'");

            if (resultado.isEmpty()) {
                exibirAlerta(Alert.AlertType.WARNING,
                    "Não encontrado", "Nenhum produto encontrado com o nome: " + nome);
                produtoAtual = null;
                return;
            }

            produtoAtual = resultado.iterator().next();
            preencherCampos(produtoAtual);

        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro no banco", "Detalhe: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CADASTRAR / GRAVAR
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    private void Cadastro() {
        if (txtNome.getText().trim().isEmpty()
                || txtDescricao.getText().trim().isEmpty()
                || txtPreco.getText().trim().isEmpty()
                || cmbPrescricao.getValue() == null) {

            exibirAlerta(Alert.AlertType.WARNING,
                "Campos obrigatórios", "Preencha todos os campos antes de cadastrar.");
            return;
        }

        try {
            String precoRaw = txtPreco.getText()
                .replace("R$ ", "")
                .replace(".", "")
                .replace(",", ".");

            float preco = Float.parseFloat(precoRaw);

            Produto produto = new Produto();
            produto.setId(proximoId++);
            produto.setNome(txtNome.getText().trim());
            produto.setDescricao(txtDescricao.getText().trim());
            produto.setPrescricao(cmbPrescricao.getValue().equals("Sim"));
            produto.setPreco(preco);

            if (produtoDAO.insere(produto)) {
                exibirAlerta(Alert.AlertType.INFORMATION,
                    "Sucesso", "Produto cadastrado com sucesso!");
                limparCampos();
            } else {
                exibirAlerta(Alert.AlertType.ERROR,
                    "Falha", "Não foi possível cadastrar o produto.");
            }

        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.ERROR,
                "Preço inválido", "Digite um valor numérico válido.");
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR,
                "Erro no banco de dados", "Detalhe: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EXCLUIR
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    private void Excluir() {
        if (produtoAtual == null) {
            exibirAlerta(Alert.AlertType.WARNING,
                "Produto não carregado",
                "Pesquise um produto pelo nome antes de excluir.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText(
            "Deseja excluir o produto: \"" + produtoAtual.getNome() + "\"?");

        Optional<ButtonType> resposta = confirmacao.showAndWait();

        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            try {
                if (produtoDAO.remove(produtoAtual)) {
                    exibirAlerta(Alert.AlertType.INFORMATION,
                        "Sucesso", "Produto excluído com sucesso!");
                    limparCampos();
                } else {
                    exibirAlerta(Alert.AlertType.ERROR,
                        "Falha", "Não foi possível excluir o produto.");
                }
            } catch (SQLException e) {
                exibirAlerta(Alert.AlertType.ERROR,
                    "Erro no banco", "Detalhe: " + e.getMessage());
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MÁSCARAS — reutilizáveis em qualquer controller
    // ─────────────────────────────────────────────────────────────────────────

    // Preço → R$ 1.234,56  (entrada da direita para a esquerda)
    private void aplicarMascaraReal(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String digits = novo.replaceAll("[^\\d]", "");
            if (digits.isEmpty()) return;
            if (digits.length() > 10) digits = digits.substring(0, 10);
            long centavos = Long.parseLong(digits);
            String formatado = formatarReal(centavos);
            if (!novo.equals(formatado)) {
                campo.setText(formatado);
                campo.positionCaret(formatado.length());
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────
    private void preencherCampos(Produto p) {
        txtNome.setText(p.getNome());
        txtDescricao.setText(p.getDescricao());
        txtPreco.setText(formatarReal((long)(p.getPreco() * 100)));
        cmbPrescricao.setValue(p.getPrescricao() ? "Sim" : "Não");
    }

    private void limparCampos() {
        txtNome.clear();
        txtDescricao.clear();
        txtPreco.clear();
        cmbPrescricao.setValue(null);
        produtoAtual = null;
    }

    private String formatarReal(long centavos) {
        long reais = centavos / 100;
        long cents = centavos % 100;
        NumberFormat nf = NumberFormat.getIntegerInstance(new Locale("pt", "BR"));
        return String.format("R$ %s,%02d", nf.format(reais), cents);
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML private void Nome() {}
    @FXML private void Descricao() {}
    @FXML private void Preco() {}
    @FXML private void Prescricao() {}
}