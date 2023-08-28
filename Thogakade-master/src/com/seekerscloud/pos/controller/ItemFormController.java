package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.seekerscloud.pos.dao.DatabaseAccessCode;
import com.seekerscloud.pos.db.DBConnection;
import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.entity.Item;
import com.seekerscloud.pos.modal.Customer;
import com.seekerscloud.pos.view.tm.CustomerTm;
import com.seekerscloud.pos.view.tm.ItemTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class ItemFormController {
    public AnchorPane itemFormContext;
    public TextField txtCode;
    public TextField txtDescription;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public JFXButton btnSaveItem;
    public TextField txtSearch;
    public TableView tblItem;
    public TableColumn colCode;
    public TableColumn colDescription;
    public TableColumn colUnitPrice;
    public TableColumn colQtyOnHand;
    public TableColumn colOption;
    private String searchText = "";
    public void initialize(){
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));

        searchItem(searchText);

        tblItem.getSelectionModel()
                .selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    if(newValue!=null){
                        setData((ItemTm) newValue);
                    }
                }));

        txtSearch.textProperty().addListener(((observable, oldValue, newValue) -> {
            searchText=newValue;
            searchItem(searchText);
        }));
    }

    private void setData(ItemTm tm) {
        txtCode.setText(tm.getCode());
        txtDescription.setText(tm.getDescription());
        txtUnitPrice.setText(String.valueOf(tm.getUnitPrice()));
        txtQtyOnHand.setText(String.valueOf(tm.getQtyOnHand()));
        btnSaveItem.setText("Update Item");
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) itemFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"))));
    }

    public void newItemOnAction(ActionEvent actionEvent) {
        btnSaveItem.setText("Save Item");
    }

    public void saveItemOnAction(ActionEvent actionEvent) {
        Item i1 = new Item();

        if(btnSaveItem.getText().equalsIgnoreCase("Save Item")){

            try{
                boolean isItemSaved = new DatabaseAccessCode().saveItem(new Item(
                        txtCode.getText(),
                        txtDescription.getText(),
                        Double.parseDouble(txtUnitPrice.getText()),
                        Integer.parseInt(txtQtyOnHand.getText())
                ));
                if(isItemSaved){
                    searchItem(searchText);
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION,"Item Saved!").show();
                }else {
                    new Alert(Alert.AlertType.WARNING,"Try Again").show();
                }
            }catch (ClassNotFoundException | SQLException e){
                e.printStackTrace();
            }
        }else {
            try {
                boolean isItemUpdated = new DatabaseAccessCode().updateItem(
                        new Item(
                                txtCode.getText(),
                                txtDescription.getText(),
                                Double.parseDouble(txtUnitPrice.getText()),
                                Integer.parseInt(txtQtyOnHand.getText()))
                        );
                if(isItemUpdated){
                    searchItem(searchText);
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION,"Item Updated!").show();
                }else {
                    new Alert(Alert.AlertType.WARNING,"Try Again").show();
                }
            }catch (ClassNotFoundException | SQLException e){
                e.printStackTrace();
            }
        }
    }
    private void searchItem(String text) {
        String searchText = "%"+text+"%";

        try {
            ObservableList<ItemTm> tmList = FXCollections.observableArrayList();
            ArrayList<Item> itemList = new DatabaseAccessCode().searchItems(searchText);
            for (Item i : itemList){
                Button btn = new Button("Delete");
                ItemTm tm = new ItemTm(
                        i.getCode(),
                        i.getDescription(),
                        i.getUnitPrice(),
                        i.getQtyOnHand(),btn);
                tmList.add(tm);
                btn.setOnAction(event -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to delete?",ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get()==ButtonType.YES){

                        try {
                            if(new DatabaseAccessCode().deleteItem(tm.getCode())){
                                searchItem(searchText);
                                new Alert(Alert.AlertType.INFORMATION,"Item Deleted!").show();
                            }else {
                                new Alert(Alert.AlertType.WARNING,"Try Again").show();
                            }
                        }catch (ClassNotFoundException | SQLException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
            tblItem.setItems(tmList);
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }
    private void clearFields(){
        txtCode.clear();
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
    }
}
