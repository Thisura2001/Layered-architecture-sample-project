package com.example.layeredarchitecture.bo.Custom;

import com.example.layeredarchitecture.Entity.Customer;
import com.example.layeredarchitecture.Entity.Item;
import com.example.layeredarchitecture.bo.SuperBo;
import com.example.layeredarchitecture.dto.CustomerDTO;
import com.example.layeredarchitecture.dto.ItemDTO;
import com.example.layeredarchitecture.dto.OrderDetailDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface PlaceOrderBo extends SuperBo {

     boolean placeOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) throws SQLException, ClassNotFoundException;
     ItemDTO findItem(String code) throws SQLException, ClassNotFoundException;

    CustomerDTO searchCustomer(String s) throws SQLException, ClassNotFoundException;

    ItemDTO searchItem(String s) throws SQLException, ClassNotFoundException;

    boolean existItem(String code) throws SQLException, ClassNotFoundException;

    boolean existCustomer(String id) throws SQLException, ClassNotFoundException;

    String generateNewOrderId() throws SQLException, ClassNotFoundException;

    ArrayList<CustomerDTO> getAllCustomerId() throws SQLException, ClassNotFoundException;

    ArrayList<ItemDTO> getAllItemCode() throws SQLException, ClassNotFoundException;
}