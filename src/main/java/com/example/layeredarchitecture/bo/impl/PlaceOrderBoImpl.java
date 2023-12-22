package com.example.layeredarchitecture.bo.impl;

import com.example.layeredarchitecture.bo.Custom.PlaceOrderBo;
import com.example.layeredarchitecture.dao.DaoFactory;
import com.example.layeredarchitecture.dao.Impl.*;
import com.example.layeredarchitecture.dao.custom.CustomerDAO;
import com.example.layeredarchitecture.dao.custom.ItemDAO;
import com.example.layeredarchitecture.dao.custom.OrderDAO;
import com.example.layeredarchitecture.dao.custom.OrderDetailDAO;
import com.example.layeredarchitecture.db.DBConnection;
import com.example.layeredarchitecture.model.CustomerDTO;
import com.example.layeredarchitecture.model.ItemDTO;
import com.example.layeredarchitecture.model.OrderDTO;
import com.example.layeredarchitecture.model.OrderDetailDTO;

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
            boolean b2 = orderDAO.save(new OrderDTO(orderId, orderDate, customerId));


            if (!b2) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }
            // add data to the Order Details table


            for (OrderDetailDTO detail : orderDetails) {
                boolean b3 = orderDetailsDAO.save(detail);
                if (!b3) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }


                //Search & Update Item
                ItemDTO item = findItem(detail.getItemCode());
                item.setQtyOnHand(item.getQtyOnHand() - detail.getQty());


                //update item
                boolean b = itemDAO.update(new ItemDTO(item.getCode(), item.getDescription(), item.getUnitPrice(), item.getQtyOnHand()));


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
        public ItemDTO findItem(String code) {
            try {
                return itemDAO.search(code);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to find the Item " + code, e);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

    @Override
    public CustomerDTO searchCustomer(String s) throws SQLException, ClassNotFoundException {
        return customerDAO.search(s);
    }

    @Override
    public ItemDTO searchItem(String s) throws SQLException, ClassNotFoundException {
        return itemDAO.search(s);
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
        return customerDAO.getAll();
    }

    @Override
    public ArrayList<ItemDTO> getAllItemCode() throws SQLException, ClassNotFoundException {
        return itemDAO.getAll();
    }
}
