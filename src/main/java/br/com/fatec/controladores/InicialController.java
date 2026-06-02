/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.fatec.controladores;


import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

/**
 *
 * @author bruno
 */
public class InicialController {
        @FXML
    private void abrirLogin(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(
            getClass().getResource("/fxml/login.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource())
            .getScene()
            .getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void abrirCadastro(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(
            getClass().getResource("/fxml/cadastro.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource())
            .getScene()
            .getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }
}
