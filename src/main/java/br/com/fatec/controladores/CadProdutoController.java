package br.com.fatec.controladores;

import br.com.fatec.DAO.ProdutoDAO;
import br.com.fatec.model.Produto;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.text.NumberFormat;
import java.util.Locale;

public class CadProdutoController implements Initializable {

    // ── Componentes do FXML ──────────────────────────────────────────────────
    @FXML private Button      btnCadastro;
    @FXML private TextField   txtNome;
    @FXML private TextField   txtDescricao;
    @FXML private TextField   txtPreco;
    @FXML private ComboBox<String> cmbPrescricao;

    // ── DAO ──────────────────────────────────────────────────────────────────
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    // ── Controle simples de ID (veja a observação no final) ──────────────────
    private static int proximoId = 1;

    // ── Inicialização ─────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Popula a ComboBox com as opções de prescrição
        cmbPrescricao.setItems(FXCollections.observableArrayList("Sim", "Não"));
        aplicarMascaraReal();
    }

    // ── Ação do botão Cadastrar ───────────────────────────────────────────────
    @FXML
    private void Cadastro() {

        // 1. Validação: todos os campos devem estar preenchidos
        if (txtNome.getText().trim().isEmpty()
                || txtDescricao.getText().trim().isEmpty()
                || txtPreco.getText().trim().isEmpty()
                || cmbPrescricao.getValue() == null) {

            exibirAlerta(Alert.AlertType.WARNING,
                    "Campos obrigatórios",
                    "Preencha todos os campos antes de cadastrar.");
            return;
        }

        try {
            // 2. Converte o preço (aceita vírgula ou ponto)
            String precoRaw = txtPreco.getText()
                .replace("R$ ", "")   // remove o prefixo
                .replace(".", "")     // remove separador de milhar
                .replace(",", ".");   // troca decimal BR → EN

            float preco = Float.parseFloat(precoRaw);

            // 3. Monta o objeto Produto
            Produto produto = new Produto();
            produto.setId(proximoId++);
            produto.setNome(txtNome.getText().trim());
            produto.setDescricao(txtDescricao.getText().trim());
            produto.setPrescricao(cmbPrescricao.getValue().equals("Sim"));
            produto.setPreco(preco);

            // 4. Chama o DAO para persistir
            boolean sucesso = produtoDAO.insere(produto);

            if (sucesso) {
                exibirAlerta(Alert.AlertType.INFORMATION,
                        "Sucesso",
                        "Produto cadastrado com sucesso!");
                limparCampos();
            } else {
                exibirAlerta(Alert.AlertType.ERROR,
                        "Falha",
                        "Não foi possível cadastrar o produto.");
            }

        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.ERROR,
                    "Preço inválido",
                    "Digite um valor numérico válido para o preço.\nExemplo: 19.90");

        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR,
                    "Erro no banco de dados",
                    "Detalhe: " + e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void limparCampos() {
        txtNome.clear();
        txtDescricao.clear();
        txtPreco.clear();
        cmbPrescricao.setValue(null);
    }
    // Aplica a máscara de Real no TextField de preço
    private void aplicarMascaraReal() {
        txtPreco.textProperty().addListener((obs, valorAntigo, valorNovo) -> {

            // Extrai somente os dígitos digitados
            String apenasDigitos = valorNovo.replaceAll("[^\\d]", "");

            if (apenasDigitos.isEmpty()) {
                return;
            }

            // Limita a 10 dígitos → máximo R$ 99.999.999,99
            if (apenasDigitos.length() > 10) {
                apenasDigitos = apenasDigitos.substring(0, 10);
            }

            long centavos = Long.parseLong(apenasDigitos);
            String formatado = formatarReal(centavos);

            // Atualiza somente se mudou (evita loop infinito)
            if (!valorNovo.equals(formatado)) {
                txtPreco.setText(formatado);
                txtPreco.positionCaret(formatado.length()); // cursor sempre no fim
            }
        });
    }

    // Formata long de centavos → "R$ 1.234,56"
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

    // ── Métodos onAction dos TextFields (obrigatórios pelo FXML) ─────────────
    @FXML private void Nome() {}
    @FXML private void Descricao() {}
    @FXML private void Preco() {}
    @FXML private void Prescricao() {}
}