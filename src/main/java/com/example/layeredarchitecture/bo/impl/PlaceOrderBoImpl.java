package com.example.layeredarchitecture.bo.impl;

import com.example.layeredarchitecture.Entity.Customer;
import com.example.layeredarchitecture.Entity.Item;
import com.example.layeredarchitecture.Entity.Order;
import com.example.layeredarchitecture.Entity.OrderDetail;
import com.example.layeredarchitecture.bo.Custom.PlaceOrderBo;
import com.example.layeredarchitecture.dao.DaoFactory;
import com.example.layeredarchitecture.dao.Impl.*;
import com.example.layeredarchitecture.dao.custom.CustomerDAO;
import com.example.layeredarchitecture.dao.custom.ItemDAO;
import com.example.layeredarchitecture.dao.custom.OrderDAO;
import com.example.layeredarchitecture.dao.custom.OrderDetailDAO;
import com.example.layeredarchitecture.db.DBConnection;
import com.example.layeredarchitecture.dto.CustomerDTO;
import com.example.layeredarchitecture.dto.ItemDTO;
import com.example.layeredarchitecture.dto.OrderDTO;
import com.example.layeredarchitecture.dto.OrderDetailDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderBoImpl implements PlaceOrderBo {
    OrderDAO orderDAO = (OrderDAO) DaoFactory.getDaoFactory().getDAO(DaoFactory.DAOType.ORDER);
    CustomerDAO customerDAO = (CustomerDAO) DaoFactory.getDaoFactory().getDAO(DaoFactory.DAOType.CUSTOMER);
    ItemDAO itemDAO = (ItemDAO) DaoFactory.getDaoFactory().getDAO(DaoFactory.DAOType.ITEM);
    OrderDetailDAO orderDetailsDAO = (OrderDetailDAO) DaoFactory.getDaoFactory().getDAO(DaoFactory.DAOType.ORDER_DETAIL);
    QuaryDaoImpl quaryDao = (QuaryDaoImpl) DaoFactory.getDaoFactory().getDAO(DaoFactory.DAOType.QUARY);
    @Override
    public boolean placeOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) throws SQLException, ClassNotFoundException {
        Connection connection = null;

            connection= DBConnection.getDbConnection().getConnection();


            //Check order id already exist or not


            boolean b1 = orderDAO.exist(orderId);
            /*if order id already exist*/
            if (b1) {
                return false;
            }


            connection.setAutoCommit(false);


            //Save the Order to the order table
            boolean isSaved = orderDAO.save(new Order(orderId,orderDate,customerId));


            if (!isSaved) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }
            // add data to the Order Details table



        for (OrderDetailDTO detail : orderDetails) {

            boolean isOk = orderDetailsDAO.save(new OrderDetail(orderId, detail.getItemCode(), detail.getQty(), detail.getUnitPrice()));

            if (!isOk) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }


                //Search & Update Item
            ItemDTO item = findItem(detail.getItemCode());
            item.setQtyOnHand(item.getQtyOnHand() - detail.getQty());


                //update item
                boolean b = itemDAO.update(new Item(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand()));


                if (!b) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }
            }

            connection.commit();
            connection.setAutoCommit(true);
            return true;
        }
        @Override
        public ItemDTO findItem(String itemCode) throws SQLException, ClassNotFoundException {
            Item item = itemDAO.search(itemCode);
            return new ItemDTO(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand());
        }

    @Override
    public CustomerDTO searchCustomer(String s) throws SQLException, ClassNotFoundException {
        Customer customer = customerDAO.search(s);
        return new CustomerDTO(customer.getId(), customer.getName(), customer.getAddress());
    }

    @Override
    public ItemDTO searchItem(String s) throws SQLException, ClassNotFoundException {
        Item item = itemDAO.search(s);
        return new ItemDTO(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand());
    }

    @Override
    public boolean existItem(String code) throws SQLException, ClassNotFoundException {
        return itemDAO.exist(code);
    }

    @Override
    public boolean existCustomer(String id) throws SQLException, ClassNotFoundException {
        return customerDAO.exist(id);
    }

    @Override
    public String generateNewOrderId() throws SQLException, ClassNotFoundException {
        return orderDAO.generateNewId();
    }

    @Override
    public ArrayList<CustomerDTO> getAllCustomerId() throws SQLException, ClassNotFoundException {
        ArrayList<Customer> allCustomers = customerDAO.getAll();
        ArrayList<CustomerDTO> customerDTOS = new ArrayList<>();

        for (Customer customer : allCustomers) {
            customerDTOS.add(new CustomerDTO(customer.getId(), customer.getName(), customer.getAddress()));
        }

        return customerDTOS;
    }
    @Override
    public ArrayList<ItemDTO> getAllItemCode() throws SQLException, ClassNotFoundException {
        ArrayList<Item> allitems = itemDAO.getAll();
        ArrayList<ItemDTO> itemDTOS = new ArrayList<>();

        for (Item item : allitems) {
            itemDTOS.add(new ItemDTO(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand()));
        }

        return itemDTOS;
    }
}
