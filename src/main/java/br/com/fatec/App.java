package br.com.fatec;

import br.com.fatec.DAO.ProdutoDAO;
import br.com.fatec.model.Produto;
import br.com.fatec.persistencia.Banco;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("???????????????"));
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        //launch();
        try
        {
            //criar uma DAO
            ProdutoDAO dao = new ProdutoDAO();
            
            /**buscar um proprietario
            Produto p = dao.buscarID(new Produto(21, null));
            
            if(p == null) 
                System.out.println("Proprietario não encontrado...");
            else 
            {
                System.out.println("Código: " + p.getCodProprietario());
                System.out.println("Nome: " + p.getNome());
            }
            */
            //vamos inserir um dado
            if(dao.insere(new Produto(1, "Dipirona", false, "toma ai", 2)))
              System.out.println("Incluido com sucesso!!");
            else
              System.out.println("Erro na Inclusão");
        }
        catch (SQLException ex) 
        {
            System.out.println("Erro: " + ex.getMessage());
        }
    }

}