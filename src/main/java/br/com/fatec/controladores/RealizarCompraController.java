package br.com.fatec.controladores;

import br.com.fatec.DAO.ClienteDAO;
import br.com.fatec.DAO.PedidoDAO;
import br.com.fatec.DAO.ProdutoDAO;
import br.com.fatec.model.Cliente;
import br.com.fatec.model.Pedido;
import br.com.fatec.model.Produto;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RealizarCompraController implements Initializable {

    // ── Componentes do FXML ──────────────────────────────────────────────────
    @FXML private Button    btnRealizarCompra;

    // Dados do cliente
    @FXML private TextField txtCPF;
    @FXML private TextField txtCEP;
    @FXML private TextField txtNum;
    @FXML private TextField txtEnd;
    @FXML private TextField txtUF;
    @FXML private TextField txtCidade;
    @FXML private TextField txtBairro;

    // Dados do produto/pedido
    @FXML private TextField txtProduto;
    @FXML private TextField txtID;
    @FXML private TextField txtPrescricao;
    @FXML private TextField txtValorUni;
    @FXML private TextField txtQuantidade;
    @FXML private TextField txtValor;

    // ── DAOs ──────────────────────────────────────────────────────────────────
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final PedidoDAO  pedidoDAO  = new PedidoDAO();

    // ── Estado ────────────────────────────────────────────────────────────────
    private Cliente clienteAtual;
    private Produto produtoAtual;
    private static int proximoIdPedido = 1;

    // ─────────────────────────────────────────────────────────────────────────
    // INICIALIZAÇÃO
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCamposReadOnly();

        // ── Máscaras nos campos editáveis pelo usuário ────────────────────────
        aplicarMascaraCPF(txtCPF);           // 000.000.000-00
        aplicarMascaraSomenteNumeros(txtQuantidade); // apenas dígitos

        configurarListenerCPF();
        configurarListenerProduto();
        configurarListenerQuantidade();

        btnRealizarCompra.setOnAction(e -> realizarCompra());

        // Intercepta o X vermelho e volta para a tela principal
        btnRealizarCompra.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
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
    // CONFIGURAÇÕES INICIAIS
    // ─────────────────────────────────────────────────────────────────────────

    // Campos preenchidos automaticamente ficam somente leitura
    private void configurarCamposReadOnly() {
        txtCEP.setEditable(false);
        txtEnd.setEditable(false);
        txtUF.setEditable(false);
        txtCidade.setEditable(false);
        txtBairro.setEditable(false);
        txtNum.setEditable(false);
        txtPrescricao.setEditable(false);
        txtValorUni.setEditable(false);
        txtValor.setEditable(false);
        txtID.setEditable(false);
    }

    // Busca cliente ao perder foco no CPF
    private void configurarListenerCPF() {
        txtCPF.focusedProperty().addListener((obs, focoAntigo, focoNovo) -> {
            if (!focoNovo && !txtCPF.getText().trim().isEmpty()) {
                buscarClientePorCPF(txtCPF.getText().trim());
            }
        });
    }

    // Busca produto ao perder foco no nome
    private void configurarListenerProduto() {
        txtProduto.focusedProperty().addListener((obs, focoAntigo, focoNovo) -> {
            if (!focoNovo && !txtProduto.getText().trim().isEmpty()) {
                buscarProdutoPorNome(txtProduto.getText().trim());
            }
        });
    }

    // Recalcula o total ao alterar a quantidade
    private void configurarListenerQuantidade() {
        txtQuantidade.textProperty().addListener((obs, antigo, novo) -> calcularTotal());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BUSCAS NO BANCO
    // ─────────────────────────────────────────────────────────────────────────

    private void buscarClientePorCPF(String cpf) {
        try {
            Collection<Cliente> resultado = clienteDAO.listar("cpf = '" + cpf + "'");

            if (resultado.isEmpty()) {
                exibirAlerta(Alert.AlertType.WARNING,
                    "Cliente não encontrado", "Nenhum cliente com o CPF: " + cpf);
                limparDadosCliente();
                return;
            }

            clienteAtual = resultado.iterator().next();
            preencherDadosCliente(clienteAtual);

        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro no banco", e.getMessage());
        }
    }

    private void buscarProdutoPorNome(String nome) {
        try {
            Collection<Produto> resultado = produtoDAO.listar("nome LIKE '%" + nome + "%'");

            if (resultado.isEmpty()) {
                exibirAlerta(Alert.AlertType.WARNING,
                    "Produto não encontrado", "Nenhum produto com o nome: " + nome);
                limparDadosProduto();
                return;
            }

            produtoAtual = resultado.iterator().next();
            preencherDadosProduto(produtoAtual);

        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro no banco", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PREENCHIMENTO DOS CAMPOS
    // ─────────────────────────────────────────────────────────────────────────

    private void preencherDadosCliente(Cliente c) {
        txtCEP.setText(c.getCep());
        txtNum.setText(c.getNum_comp());
        txtEnd.setText(c.getEndereco());
        txtUF.setText(c.getUf());
        txtCidade.setText(c.getCidade());
        txtBairro.setText(c.getBairro());
    }

    private void preencherDadosProduto(Produto p) {
        txtProduto.setText(p.getNome());
        txtID.setText(String.valueOf(p.getId()));
        txtPrescricao.setText(p.getPrescricao() ? "Sim" : "Não");
        txtValorUni.setText(formatarReal((long)(p.getPreco() * 100)));
        calcularTotal();
    }

    private void calcularTotal() {
        if (produtoAtual == null || txtQuantidade.getText().trim().isEmpty()) {
            txtValor.clear();
            return;
        }
        try {
            int qtd       = Integer.parseInt(txtQuantidade.getText().trim());
            float total   = produtoAtual.getPreco() * qtd;
            txtValor.setText(formatarReal((long)(total * 100)));
        } catch (NumberFormatException e) {
            txtValor.clear();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AÇÃO DO BOTÃO
    // ─────────────────────────────────────────────────────────────────────────

    private void realizarCompra() {
        if (!validarCampos()) return;

        try {
            int qtd     = Integer.parseInt(txtQuantidade.getText().trim());
            float total = produtoAtual.getPreco() * qtd;

            Pedido pedido = new Pedido();
            pedido.setId(proximoIdPedido++);
            pedido.setCliente(clienteAtual);
            pedido.setProduto(produtoAtual);
            pedido.setData(LocalDate.now().toString());
            pedido.setQuantidade(qtd);
            pedido.setPrecoTotal(total);

            if (pedidoDAO.insere(pedido)) {
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Compra realizada com sucesso!");
                limparTudo();
            } else {
                exibirAlerta(Alert.AlertType.ERROR, "Falha", "Não foi possível registrar a compra.");
            }

        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Quantidade inválida", "Digite um número inteiro válido.");
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro no banco", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VALIDAÇÃO
    // ─────────────────────────────────────────────────────────────────────────

    private boolean validarCampos() {
        if (clienteAtual == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Cliente não carregado",
                "Preencha o CPF para carregar os dados do cliente.");
            return false;
        }
        if (produtoAtual == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Produto não carregado",
                "Preencha o nome do produto para carregar os dados.");
            return false;
        }
        if (txtQuantidade.getText().trim().isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Quantidade obrigatória",
                "Informe a quantidade do produto.");
            return false;
        }
        try {
            int qtd = Integer.parseInt(txtQuantidade.getText().trim());
            if (qtd <= 0) {
                exibirAlerta(Alert.AlertType.WARNING, "Quantidade inválida",
                    "A quantidade deve ser maior que zero.");
                return false;
            }
        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.WARNING, "Quantidade inválida",
                "Digite apenas números inteiros na quantidade.");
            return false;
        }
        return true;
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

    // Quantidade → somente dígitos, sem formatação especial
    private void aplicarMascaraSomenteNumeros(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String digits = novo.replaceAll("[^\\d]", "");
            if (!novo.equals(digits)) {
                campo.setText(digits);
                campo.positionCaret(digits.length());
            }
        });
    }

    // Preço → R$ 1.234,56 (usado nos campos read-only de valor)
    private String formatarReal(long centavos) {
        long reais = centavos / 100;
        long cents = centavos % 100;
        NumberFormat nf = NumberFormat.getIntegerInstance(new Locale("pt", "BR"));
        return String.format("R$ %s,%02d", nf.format(reais), cents);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LIMPEZA
    // ─────────────────────────────────────────────────────────────────────────

    private void limparDadosCliente() {
        clienteAtual = null;
        txtCEP.clear(); txtNum.clear(); txtEnd.clear();
        txtUF.clear();  txtCidade.clear(); txtBairro.clear();
    }

    private void limparDadosProduto() {
        produtoAtual = null;
        txtID.clear(); txtPrescricao.clear();
        txtValorUni.clear(); txtValor.clear();
    }

    private void limparTudo() {
        txtCPF.clear();
        txtProduto.clear();
        txtQuantidade.clear();
        limparDadosCliente();
        limparDadosProduto();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────────────────────────────────────

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}