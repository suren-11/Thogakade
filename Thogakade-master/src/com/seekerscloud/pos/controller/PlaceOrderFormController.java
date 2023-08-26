package com.seekerscloud.pos.controller;

import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.Customer;
import com.seekerscloud.pos.modal.Item;
import com.seekerscloud.pos.modal.ItemDetails;
import com.seekerscloud.pos.modal.Order;
import com.seekerscloud.pos.view.tm.CartTm;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class PlaceOrderFormController {
    public AnchorPane placeOrderFormContext;
    public TextField txtOrderId;
    public TextField txtDate;
    public ComboBox<String> cmbCustomerIds;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtSalary;
    public ComboBox<String> cmbItemCodes;
    public TextField txtDescription;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public TextField txtQty;
    public TableView<CartTm> tblCart;
    public TableColumn colCode;
    public TableColumn colDescription;
    public TableColumn colUnitPrice;
    public TableColumn colQty;
    public TableColumn colTotal;
    public TableColumn colOption;
    public Label lblTotal;

    public void initialize(){
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));

       setDateAndOrderId();
       loadAllCustomerIds();
       loadAllItemCodes();
       setOrderId();

       cmbCustomerIds.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           if(newValue!=null) {
               setCustomerDetails();
           }
       });

       cmbItemCodes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           if (newValue!=null){
               setItemDetails();
           }
       });
    }

    private void setOrderId() {
        if (Database.orderTable.isEmpty()){
            txtOrderId.setText("D-1");
            return;
        }
        String tempOrderId = Database.orderTable.get(Database.orderTable.size() - 1).getOrderId();
        String[] array = tempOrderId.split("-");
        int tempNumber = Integer.parseInt(array[1]);
        int finalizeOrderId = tempNumber + 1;
        txtOrderId.setText("D-" + finalizeOrderId);
    }

    private void setItemDetails() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1992");

            String sql = "SELECT * FROM Item WHERE code = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,cmbItemCodes.getValue());
            ResultSet set = statement.executeQuery();
            if (set.next()){
                txtDescription.setText(set.getString(2));
                txtUnitPrice.setText(String.valueOf(set.getDouble(3)));
                txtQtyOnHand.setText(String.valueOf(set.getInt(4)));
            }

        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    private void setCustomerDetails() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1992");

            String sql = "SELECT * FROM Customer WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,cmbCustomerIds.getValue());
            ResultSet set = statement.executeQuery();
            if (set.next()){
                txtName.setText(set.getString(2));
                txtAddress.setText(set.getString(3));
                txtSalary.setText(String.valueOf(set.getDouble(4)));
            }

        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }

    }

    private void loadAllItemCodes() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1992");

            String sql = "SELECT code FROM Item";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet set = statement.executeQuery();
            ArrayList<String> idList = new ArrayList<>();
            while (set.next()){
                idList.add(set.getString(1));
            }
            ObservableList<String> obList = FXCollections.observableArrayList(idList);
            cmbItemCodes.setItems(obList);
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    private void loadAllCustomerIds() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1992");

            String sql = "SELECT id FROM Customer";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet set = statement.executeQuery();
            ArrayList<String> idList = new ArrayList<>();
            while (set.next()){
                idList.add(set.getString(1));
            }
            ObservableList<String> obList = FXCollections.observableArrayList(idList);
            cmbCustomerIds.setItems(obList);
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    public void placeOrderOnAction(ActionEvent actionEvent) {
        if (obList.isEmpty()) return;
        ArrayList<ItemDetails> details = new ArrayList<>();

        for (CartTm tm: obList
             ) {
            details.add(new ItemDetails(tm.getCode(),tm.getUnitPrice(), tm.getQty()));
        }

        Order order = new Order(txtOrderId.getText(),new Date(),Double.parseDouble(lblTotal.getText()),cmbCustomerIds.getValue(),details);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1992");

            String sql = "INSERT INTO `Order` VALUES (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,order.getOrderId());
            statement.setString(2,txtDate.getText());
            statement.setDouble(3,order.getTotalCost());
            statement.setString(4,order.getCustomer());

            if (statement.executeUpdate()>0){
                manageQty();
                clearAll();
            }else {
                new Alert(Alert.AlertType.WARNING,"Try Again").show();
            }
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }

    }

    private void manageQty() {
        for (CartTm tm: obList
             ) {
            for (Item i: Database.itemTable
                 ) {
                if (i.getCode().equals(tm.getCode())){
                    i.setQtyOnHand(i.getQtyOnHand() - tm.getQty());
                    break;
                }
            }
        }
    }

    private void clearAll() {
        obList.clear();
        calculateTotal();
        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();

        cmbCustomerIds.setValue(null);
        cmbItemCodes.setValue(null);

        clearFields();
        cmbCustomerIds.requestFocus();
        setOrderId();
    }

    ObservableList<CartTm> obList = FXCollections.observableArrayList();

    private boolean checkQty(String code,int qty){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1992");

            String sql = "SELECT qtyOnHand FROM Item WHERE code = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,code);
            ResultSet set = statement.executeQuery();

            if (set.next()){
                int tempQty = set.getInt(1);
                if (tempQty>=qty){
                    return true;
                }else {
                    return false;
                }

            }else {
                return false;
            }
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public void addToCartOnAction(ActionEvent actionEvent) {
        if (!checkQty(cmbItemCodes.getValue(),Integer.parseInt(txtQty.getText()))){
            new Alert(Alert.AlertType.WARNING,"Invalid Qty").show();
            return;
        }

        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qty = Integer.parseInt(txtQty.getText());
        double total = unitPrice * qty;
        Button btn = new Button("Delete");

        int row = isAlreadyExists(cmbItemCodes.getValue());


        if (row==-1){
            CartTm tm = new CartTm(cmbItemCodes.getValue(),
                    txtDescription.getText(),unitPrice,qty,total,btn);
            obList.add(tm);
            tblCart.setItems(obList);
        }else {
            int tempQty = obList.get(row).getQty() + qty;
            double tempTotal = unitPrice * tempQty;
            if (!checkQty(cmbItemCodes.getValue(),tempQty)){
                new Alert(Alert.AlertType.WARNING,"Invalid Qty").show();
                return;
            }
            obList.get(row).setQty(tempQty);
            obList.get(row).setTotal(tempTotal);
            tblCart.refresh();
        }

        calculateTotal();
        clearFields();
        cmbItemCodes.requestFocus();

        btn.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?",
                    ButtonType.YES,ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.get()== ButtonType.YES){
                for (CartTm tm: obList
                     ) {
                        obList.remove(tm);
                        calculateTotal();
                        tblCart.refresh();
                        return;
                }
            }
        });
    }

    private void clearFields() {
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQty.clear();
        txtQtyOnHand.clear();
    }

    private int isAlreadyExists(String code){
        for (int i = 0; i < obList.size(); i++) {
            if (obList.get(i).getCode().equals(code)){
                return i;
            }
        }
        return -1;
        }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) placeOrderFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"))));
    }
    private void setDateAndOrderId(){
         /*Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String d = df.format(date);
        txtDate.setText(d);*/
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    }
    private void calculateTotal(){
        double total = 0.00;
        for (CartTm tm: obList
             ) {
            total += tm.getTotal();
        }
        lblTotal.setText(String.valueOf(total));
    }
}
