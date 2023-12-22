package com.example.layeredarchitecture.bo;

import com.example.layeredarchitecture.bo.impl.CustomerBoImpl;
import com.example.layeredarchitecture.bo.impl.ItemBoImpl;
import com.example.layeredarchitecture.bo.impl.PlaceOrderBoImpl;

public class BOFactory {

    private static BOFactory boFactory;

    private BOFactory() {
    }

    public static BOFactory getBoFactory() {
        return boFactory == null ? boFactory = new BOFactory() : boFactory;
    }

    public enum BOTypes {
        CUSTOMER, ITEM, PLACE_ORDER
    }

    public SuperBo getBOObjects(BOTypes boTypes) {
        switch (boTypes) {
            case CUSTOMER:
                return new CustomerBoImpl();
            case ITEM:
                return new ItemBoImpl();
            case PLACE_ORDER:
                return new PlaceOrderBoImpl();
            default:
                return null;
        }
    }
}
