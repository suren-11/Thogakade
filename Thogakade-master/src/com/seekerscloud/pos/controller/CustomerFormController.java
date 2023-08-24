package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.Customer;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();
        for (Customer c:Database.customerTable
             ) {
            if (c.getName().contains(text) || c.getAddress().contains(text)){

                Button btn = new Button("Delete");
                CustomerTm tm = new CustomerTm(c.getId(),c.getName(),c.getAddress(),c.getSalary(),btn);
                tmList.add(tm);

                btn.setOnAction(event -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to delete?",ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get()==ButtonType.YES){
                        boolean isDeleted = Database.customerTable.remove(c);

                        if(isDeleted){
                            searchCustomers(searchText);
                            new Alert(Alert.AlertType.INFORMATION,"Customer Deleted!").show();
                        }else {
                            new Alert(Alert.AlertType.WARNING,"Try Again").show();
                        }
                    }
                });
            }

        }
        tblCustomer.setItems(tmList);
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) customerFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"))));
    }

    public void newCustomerOnAction(ActionEvent actionEvent) {
        btnSaveCustomer.setText("Save Customer");
    }

    public void saveCustomerOnAction(ActionEvent actionEvent) {
        Customer c1 = new Customer(txtId.getText(),txtName.getText(),txtAddress.getText(),Double.parseDouble(txtSalary.getText()));

        if(btnSaveCustomer.getText().equalsIgnoreCase("Save Customer")){

            try{
                //DRIVER LOAD RAM
                Class.forName("com.mysql.cj.jdbc.Driver");
                //Create connection
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade","root","1992");
                //Create Statement
                Statement statement = connection.createStatement();
                //Create Query
                String sql = "INSERT INTO Customer VALUES('"+c1.getId()+"','"+
                        c1.getName()+"','"+c1.getAddress()+"','"+c1.getSalary()+"')";
                //Statement execute
                int isSaved = statement.executeUpdate(sql);

                if(isSaved > 0){
                    searchCustomers(searchText);
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION,"Customer Saved!").show();
                }else {
                    new Alert(Alert.AlertType.WARNING,"Try Again").show();
                }
            }catch (ClassNotFoundException |SQLException e){
                e.printStackTrace();
            }



        }else {
            for (int i = 0; i < Database.customerTable.size(); i++) {
                if (txtId.getText().equalsIgnoreCase(Database.customerTable.get(i).getId())){
                    Database.customerTable.get(i).setName(txtName.getText());
                    Database.customerTable.get(i).setAddress(txtAddress.getText());
                    Database.customerTable.get(i).setSalary(Double.parseDouble(txtSalary.getText()));
                    searchCustomers(searchText);
                    clearFields();
                    new Alert(Alert.AlertType.INFORMATION,"Customer Updated").show();
                }
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
