package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.seekerscloud.pos.dao.DatabaseAccessCode;
import com.seekerscloud.pos.db.DBConnection;
import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.entity.Customer;
import com.seekerscloud.pos.view.tm.CustomerTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Optional;

public class CustomerFormController {
    public AnchorPane customerFormContext;
    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtSalary;
    public JFXButton btnSaveCustomer;
    public TextField txtSearch;
    public TableView tblCustomer;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colSalary;
    public TableColumn colOption;

    private String searchText = "";

    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));

        searchCustomers(searchText);

        tblCustomer.getSelectionModel()
                .selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    if(newValue!=null){
                        setData((CustomerTm) newValue);
                    }
                }));

        txtSearch.textProperty().addListener(((observable, oldValue, newValue) -> {
            searchText=newValue;
            searchCustomers(searchText);
        }));
    }

    private void setData(CustomerTm tm) {
        txtId.setText(tm.getId());
        txtName.setText(tm.getName());
        txtAddress.setText(tm.getAddress());
        txtSalary.setText(String.valueOf(tm.getSalary()));
        btnSaveCustomer.setText("Update Customer");
    }

    private void searchCustomers(String text) {
        String searchText = "%"+text+"%";

        try {
            ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();

            ArrayList<Customer> customerList = new DatabaseAccessCode().searchCustomers(searchText);

            for (Customer c : customerList){
                    Button btn = new Button("Delete");
                    CustomerTm tm = new CustomerTm(
                            c.getId(),
                            c.getName(),
                            c.getAddress(),
                            c.getSalary(),
                            btn);
                    tmList.add(tm);
                    btn.setOnAction(event -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to delete?",ButtonType.YES, ButtonType.NO);
                        Optional<ButtonType> buttonType = alert.showAndWait();
                        if (buttonType.get()==ButtonType.YES){
                            try {
                                if(new DatabaseAccessCode().deleteCustomer(tm.getId())){
                                    searchCustomers(searchText);
                                    new Alert(Alert.AlertType.INFORMATION,"Customer Deleted!").show();
                                }else {
                                    new Alert(Alert.AlertType.WARNING,"Try Again").show();
                                }
                            }catch (ClassNotFoundException | SQLException e){
                                e.printStackTrace();
                            }
                        }
                    });
            }
            tblCustomer.setItems(tmList);

        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) customerFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"))));
    }

    public void newCustomerOnAction(ActionEvent actionEvent) {
        btnSaveCustomer.setText("Save Customer");
    }

    public void saveCustomerOnAction(ActionEvent actionEvent) {

        if(btnSaveCustomer.getText().equalsIgnoreCase("Save Customer")){
            try {
               boolean isCustomerSaved = new DatabaseAccessCode().saveCustomer(new Customer(
                        txtId.getText(),
                        txtName.getText(),
                        txtAddress.getText(),
                        Double.parseDouble(txtSalary.getText())
                ));
               if (isCustomerSaved){
                   searchCustomers(searchText);
                   clearFields();
                   new Alert(Alert.AlertType.INFORMATION,"Customer Saved!").show();
               }else {
                   new Alert(Alert.AlertType.WARNING,"Try Again").show();
               }
            }catch (ClassNotFoundException | SQLException e){
                e.printStackTrace();
            }
        }else {
            try {
                boolean isCustomerUpdated = new DatabaseAccessCode().updateCustomer(new Customer(
                        txtId.getText(),
                        txtName.getText(),
                        txtAddress.getText(),
                        Double.parseDouble(txtSalary.getText())
                ));
                if(isCustomerUpdated){
                    searchCustomers(searchText);
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION,"Customer Updated!").show();
                }else {
                    new Alert(Alert.AlertType.WARNING,"Try Again").show();
                }
            }catch (ClassNotFoundException | SQLException e){
                e.printStackTrace();
            }

        }

    }

    private void clearFields(){
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();
    }
}
