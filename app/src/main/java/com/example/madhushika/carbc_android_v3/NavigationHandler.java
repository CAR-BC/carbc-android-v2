package com.example.madhushika.carbc_android_v3;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.io.IOException;

import chainUtil.KeyGenerator;
import core.connection.VehicleJDBCDAO;

public class NavigationHandler {
    private static FragmentManager manager;

    public static void navigateTo(String fragment) {

        FragmentTransaction transaction = manager.beginTransaction();
        if (fragment.equals("addtransaction")){
            transaction.replace(R.id.contentLayout, new TransactionNew(), "addtransaction");
        }

        transaction.commit();
    }

    public static void setManager(FragmentManager manager) {
        NavigationHandler.manager = manager;
    }
}
