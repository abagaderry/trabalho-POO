package br.com.fatec.controladores;

import br.com.fatec.DAO.ClienteDAO;
import br.com.fatec.DAO.PedidoDAO;
import br.com.fatec.DAO.ProdutoDAO;
import br.com.fatec.model.Cliente;
import br.com.fatec.model.Pedido;
import br.com.fatec.model.Produto;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RelatorioController implements Initializable {

    // ── Componentes do FXML ──────────────────────────────────────────────────
    @FXML private Button           btnRelatorio;
    @FXML private ComboBox<String> cmbRelatorio;
    @FXML private ComboBox<String> cmbFiltro;

    // ── DAOs ─────────────────────────────────────────────────────────────────
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final PedidoDAO  pedidoDAO  = new PedidoDAO();

    // ─────────────────────────────────────────────────────────────────────────
    // INICIALIZAÇÃO
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbRelatorio.setItems(FXCollections.observableArrayList("Produtos", "Clientes", "Pedidos"));

        // Atualiza o cmbFiltro sempre que o tipo de relatório muda
        cmbRelatorio.valueProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) atualizarFiltros(novo);
        });

        btnRelatorio.setOnAction(e -> gerarRelatorio());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ATUALIZA OS FILTROS CONFORME O RELATÓRIO SELECIONADO
    // ─────────────────────────────────────────────────────────────────────────
    private void atualizarFiltros(String relatorio) {
        cmbFiltro.getItems().clear();
        cmbFiltro.setValue(null);

        switch (relatorio) {
            case "Produtos":
                cmbFiltro.setItems(FXCollections.observableArrayList(
                    "Sem filtro", "Com prescrição", "Sem prescrição"
                ));
                break;
            case "Clientes":
                cmbFiltro.setItems(FXCollections.observableArrayList(
                    "Sem filtro", "Por cidade"
                ));
                break;
            case "Pedidos":
                cmbFiltro.setItems(FXCollections.observableArrayList(
                    "Sem filtro"
                ));
                break;
        }
        cmbFiltro.setValue("Sem filtro");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // AÇÃO DO BOTÃO
    // ─────────────────────────────────────────────────────────────────────────
    private void gerarRelatorio() {
        String relatorio = cmbRelatorio.getValue();
        String filtro    = cmbFiltro.getValue();

        if (relatorio == null) {
            exibirAlerta(Alert.AlertType.WARNING,
                "Seleção obrigatória", "Selecione um tipo de relatório.");
            return;
        }

        try {
            switch (relatorio) {
                case "Produtos":
                    gerarRelatorioProdutos(filtro);
                    break;
                case "Clientes":
                    gerarRelatorioClientes(filtro);
                    break;
                case "Pedidos":
                    gerarRelatorioPedidos();
                    break;
            }
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro no banco de dados", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RELATÓRIO DE PRODUTOS
    // ─────────────────────────────────────────────────────────────────────────
    private void gerarRelatorioProdutos(String filtro) throws SQLException {
        String criterio;
        if ("Com prescrição".equals(filtro)) {
            criterio = "prescricao = true";
        } else if ("Sem prescrição".equals(filtro)) {
            criterio = "prescricao = false";
        } else {
            criterio = "";
        }

        Collection<Produto> lista = produtoDAO.listar(criterio);

        if (lista.isEmpty()) {
            exibirAlerta(Alert.AlertType.INFORMATION, "Relatório", "Nenhum produto encontrado.");
            return;
        }

        // Monta o conteúdo do relatório
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s %-30s %-14s %s%n", "ID", "Nome", "Prescrição", "Preço"));
        sb.append("─".repeat(62)).append("\n");

        for (Produto p : lista) {
            sb.append(String.format("%-6d %-30s %-14s R$ %.2f%n",
                p.getId(),
                p.getNome(),
                p.getPrescricao() ? "Sim" : "Não",
                p.getPreco()
            ));
        }

        String titulo = "Relatório de Produtos"
            + (criterio.isEmpty() ? "" : "  [" + filtro + "]");

        exibirJanelaRelatorio(titulo, sb.toString(), lista.size());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RELATÓRIO DE CLIENTES
    // ─────────────────────────────────────────────────────────────────────────
    private void gerarRelatorioClientes(String filtro) throws SQLException {
        String criterio = "";
        String cidadeEscolhida = "";

        if ("Por cidade".equals(filtro)) {
            // Pop-up para o usuário digitar a cidade
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Filtro por Cidade");
            dialog.setHeaderText(null);
            dialog.setContentText("Digite o nome da cidade:");

            Optional<String> resultado = dialog.showAndWait();

            // Cancelou ou deixou em branco → aborta
            if (resultado.isEmpty() || resultado.get().trim().isEmpty()) {
                exibirAlerta(Alert.AlertType.WARNING,
                    "Filtro cancelado", "Nenhuma cidade informada.");
                return;
            }

            cidadeEscolhida = resultado.get().trim();
            criterio = "cidade = '" + cidadeEscolhida + "'";
        }

        Collection<Cliente> lista = clienteDAO.listar(criterio);

        if (lista.isEmpty()) {
            exibirAlerta(Alert.AlertType.INFORMATION, "Relatório",
                "Nenhum cliente encontrado" +
                (cidadeEscolhida.isEmpty() ? "." : " para a cidade: " + cidadeEscolhida + "."));
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-25s %-16s %-20s %-4s%n",
            "ID", "Nome", "CPF", "Cidade", "UF"));
        sb.append("─".repeat(72)).append("\n");

        for (Cliente c : lista) {
            sb.append(String.format("%-5d %-25s %-16s %-20s %-4s%n",
                c.getId(),
                c.getNome(),
                c.getCpf(),
                c.getCidade(),
                c.getUf()
            ));
        }

        String titulo = "Relatório de Clientes"
            + (cidadeEscolhida.isEmpty() ? "" : "  [Cidade: " + cidadeEscolhida + "]");

        exibirJanelaRelatorio(titulo, sb.toString(), lista.size());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RELATÓRIO DE PEDIDOS
    // ─────────────────────────────────────────────────────────────────────────
    private void gerarRelatorioPedidos() throws SQLException {
        Collection<Pedido> lista = pedidoDAO.listar("");

        if (lista.isEmpty()) {
            exibirAlerta(Alert.AlertType.INFORMATION, "Relatório", "Nenhum pedido encontrado.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-20s %-22s %-12s %-5s %s%n",
            "ID", "Cliente", "Produto", "Data", "Qtd", "Total"));
        sb.append("─".repeat(78)).append("\n");

        for (Pedido p : lista) {
            sb.append(String.format("%-5d %-20s %-22s %-12s %-5d R$ %.2f%n",
                p.getId(),
                p.getCliente() != null ? p.getCliente().getNome() : "N/A",
                p.getProduto() != null ? p.getProduto().getNome() : "N/A",
                p.getData(),
                p.getQuantidade(),
                p.getPrecoTotal()
            ));
        }

        exibirJanelaRelatorio("Relatório de Pedidos", sb.toString(), lista.size());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // JANELA DO RELATÓRIO
    // ─────────────────────────────────────────────────────────────────────────
    private void exibirJanelaRelatorio(String titulo, String conteudo, int total) {
        TextArea textArea = new TextArea(conteudo + "\nTotal de registros: " + total);
        textArea.setEditable(false);
        // Fonte monoespaçada para o alinhamento das colunas ficar correto
        textArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");
        textArea.setPrefSize(780, 480);

        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(new VBox(textArea)));
        stage.show();
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