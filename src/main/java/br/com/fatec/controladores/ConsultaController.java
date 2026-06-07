package br.com.fatec.controladores;

import br.com.fatec.DAO.ProdutoDAO;
import br.com.fatec.model.Produto;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsultaController implements Initializable {

    @FXML private Button           btnConsulta;
    @FXML private ComboBox<String> cmbConsulta;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private static final String PRECO_CRESCENTE   = "Preço crescente";
    private static final String PRECO_DECRESCENTE = "Preço decrescente";
    private static final String ORDEM_ALFABETICA  = "Ordem alfabética";
    private static final String POR_PRESCRICAO    = "Por prescrição";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbConsulta.setItems(FXCollections.observableArrayList(
            PRECO_CRESCENTE, PRECO_DECRESCENTE, ORDEM_ALFABETICA, POR_PRESCRICAO
        ));

        btnConsulta.setOnAction(e -> consultar());

        // ✅ Intercepta o X vermelho e volta para a tela principal
        btnConsulta.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
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
                                stage.setOnCloseRequest(null); // ✅ limpa o handler
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

    private void consultar() {
        String opcao = cmbConsulta.getValue();

        if (opcao == null) {
            exibirAlerta(Alert.AlertType.WARNING,
                "Seleção obrigatória", "Selecione uma opção de consulta.");
            return;
        }

        try {
            if (POR_PRESCRICAO.equals(opcao)) {
                consultarPorPrescricao();
            } else {
                consultarComOrdenacao(opcao);
            }
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro no banco de dados", e.getMessage());
        }
    }

    private void consultarComOrdenacao(String opcao) throws SQLException {
        List<Produto> lista = new ArrayList<>(produtoDAO.listar(""));

        if (lista.isEmpty()) {
            exibirAlerta(Alert.AlertType.INFORMATION, "Consulta", "Nenhum produto cadastrado.");
            return;
        }

        if (PRECO_CRESCENTE.equals(opcao)) {
            lista.sort((p1, p2) -> Float.compare(p1.getPreco(), p2.getPreco()));
        } else if (PRECO_DECRESCENTE.equals(opcao)) {
            lista.sort((p1, p2) -> Float.compare(p2.getPreco(), p1.getPreco()));
        } else if (ORDEM_ALFABETICA.equals(opcao)) {
            lista.sort(Comparator.comparing(Produto::getNome));
        }

        exibirJanelaResultado("Consulta — " + opcao, lista);
    }

    private void consultarPorPrescricao() throws SQLException {
        ButtonType comPrescricao = new ButtonType("Com prescrição");
        ButtonType semPrescricao = new ButtonType("Sem prescrição");
        ButtonType cancelar      = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Filtro de Prescrição");
        dialog.setHeaderText(null);
        dialog.setContentText("Selecione o tipo de prescrição desejado:");
        dialog.getButtonTypes().setAll(comPrescricao, semPrescricao, cancelar);

        Optional<ButtonType> resultado = dialog.showAndWait();

        if (!resultado.isPresent() || resultado.get() == cancelar) {
            return;
        }

        boolean querComPrescricao = resultado.get() == comPrescricao;
        String criterio = "prescricao = " + querComPrescricao;
        String titulo   = "Consulta — " + (querComPrescricao ? "Com prescrição" : "Sem prescrição");

        List<Produto> lista = new ArrayList<>(produtoDAO.listar(criterio));

        if (lista.isEmpty()) {
            exibirAlerta(Alert.AlertType.INFORMATION, "Consulta",
                "Nenhum produto " + (querComPrescricao ? "com" : "sem") + " prescrição encontrado.");
            return;
        }

        exibirJanelaResultado(titulo, lista);
    }

    private void exibirJanelaResultado(String titulo, List<Produto> lista) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-6s %-30s %-15s %s%n",
            "ID", "Nome", "Prescrição", "Preço"));
        sb.append("─".repeat(65)).append("\n");

        for (Produto p : lista) {
            sb.append(String.format("%-6d %-30s %-15s R$ %.2f%n",
                p.getId(),
                p.getNome(),
                p.getPrescricao() ? "Sim" : "Não",
                p.getPreco()
            ));
        }

        sb.append("\nTotal de produtos: ").append(lista.size());

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");
        textArea.setPrefSize(620, 420);

        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(new VBox(textArea)));
        stage.show();
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}