package com.example.layeredarchitecture.dao;

import com.example.layeredarchitecture.dao.Impl.*;

import java.io.Serializable;

public class DaoFactory {
    private static DaoFactory daoFactory;
    private DaoFactory(){

    }
    public static DaoFactory getDaoFactory(){
        return (daoFactory==null)?daoFactory=new DaoFactory():daoFactory;
    }
    public enum DAOType{
        CUSTOMER,ITEM,ORDER,ORDER_DETAIL,QUARY
    }
    public SuperDao getDAO(DAOType daoType){
        switch (daoType){
            case CUSTOMER:
                return new CustomerDAOImpl();
            case  ITEM:
                return new ItemDAOImpl();
            case ORDER:
                return new OrderDAOImpl();
            case ORDER_DETAIL:
                return new OrderDetailDAOImpl();
            case QUARY:
                return new QuaryDaoImpl();
            default:
                return null;
        }
    }
}
