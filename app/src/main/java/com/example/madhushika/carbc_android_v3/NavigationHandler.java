package com.example.madhushika.carbc_android_v3;

import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import chainUtil.KeyGenerator;
import core.connection.VehicleJDBCDAO;

public class NavigationHandler {
    private static FragmentManager manager;

    public static void navigateTo(String fragment) {

        FragmentTransaction transaction = manager.beginTransaction();
    switch (fragment) {
            case "addtransactionFragment":
                if (MainActivity.vehicle_numbers.size() == 0) {
                    if (manager.findFragmentByTag("addUnregisteredNewTransactionFragment") == null) {
                        transaction.add(R.id.contentLayout, new NewTransactionFragment(), "addUnregisteredNewTransactionFragment");
                    } else {
                        transaction.show(manager.findFragmentByTag("addUnregisteredNewTransactionFragment"));
                        }
                    if (manager.findFragmentByTag("addtransactionFragment") != null) {
                        transaction.hide(manager.findFragmentByTag("addtransactionFragment"));
                    }
                } else {
                   // if (manager.findFragmentByTag("addtransactionFragment") == null) {
                        transaction.add(R.id.contentLayout, new NewTransactionFragment(), "addtransactionFragment");
//                    } else {
//                        transaction.show(manager.findFragmentByTag("addtransactionFragment"));
//                    }
                    if (manager.findFragmentByTag("addUnregisteredNewTransactionFragment") != null) {
                        transaction.hide(manager.findFragmentByTag("addUnregisteredNewTransactionFragment"));
                    }
                }

                if (manager.findFragmentByTag("SearchVehicleFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("SearchVehicleFragment"));
                }
                if (manager.findFragmentByTag("NotificationFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("NotificationFragment"));
                }
                if (manager.findFragmentByTag("StatusFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("StatusFragment"));
                }
                if (manager.findFragmentByTag("RemindersFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("RemindersFragment"));
                }
                if (manager.findFragmentByTag("InfoFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("InfoFragment"));
                }

                break;

            case "UnregisteredNewTransactionFragment":
                if (manager.findFragmentByTag("UnregisteredNewTransactionFragment") == null) {
                    transaction.add(R.id.contentLayout, new RemindersFragment(), "UnregisteredNewTransactionFragment");
                } else {
                    transaction.show(manager.findFragmentByTag("UnregisteredNewTransactionFragment"));
                }
                if (manager.findFragmentByTag("addtransactionFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("addtransactionFragment"));
                }
                if (manager.findFragmentByTag("NotificationFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("NotificationFragment"));
                }
                if (manager.findFragmentByTag("StatusFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("StatusFragment"));
                }
                if (manager.findFragmentByTag("SearchVehicleFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("SearchVehicleFragment"));
                }
                if (manager.findFragmentByTag("InfoFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("InfoFragment"));
                }
                if (manager.findFragmentByTag("addUnregisteredNewTransactionFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("addUnregisteredNewTransactionFragment"));
                }
                if (manager.findFragmentByTag("RemindersFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("RemindersFragment"));
                }

                break;
//            case "SearchVehicleFragment":
//                if (manager.findFragmentByTag("SearchVehicleFragment") == null) {
//                    transaction.add(R.id.contentLayout, new SearchVehicleFragment(), "SearchVehicleFragment");
//                } else {
//                    transaction.show(manager.findFragmentByTag("SearchVehicleFragment"));
//                }
//                if (manager.findFragmentByTag("addtransactionFragment") != null) {
//                    transaction.hide(manager.findFragmentByTag("addtransactionFragment"));
//                }
//                if (manager.findFragmentByTag("NotificationFragment") != null) {
//                    transaction.hide(manager.findFragmentByTag("NotificationFragment"));
//                }
//                if (manager.findFragmentByTag("StatusFragment") != null) {
//                    transaction.hide(manager.findFragmentByTag("StatusFragment"));
//                }
//                if (manager.findFragmentByTag("RemindersFragment") != null) {
//                    transaction.hide(manager.findFragmentByTag("RemindersFragment"));
//                }
//                if (manager.findFragmentByTag("InfoFragment") != null) {
//                    transaction.hide(manager.findFragmentByTag("InfoFragment"));
//                }
//                if (manager.findFragmentByTag("UnregisteredNewTransactionFragment") != null) {
//                    transaction.hide(manager.findFragmentByTag("UnregisteredNewTransactionFragment"));
//                }
//                break;
//            case "NotificationFragment":
//                if (manager.findFragmentByTag("NotificationFragment") == null) {
//                    transaction.add(R.id.contentLayout, new NotificationFragment(), "NotificationFragment");
//                } else {
//                    transaction.show(manager.findFragmentByTag("NotificationFragment"));
//                }
//                if (manager.findFragmentByTag("addtransactionFragment") != null) {
//                    transaction.hide(manager.findFragmentByTag("addtransactionFragment"));
//                }
//                if (manager.findFragmentByTag("SearchVehicleFragment") != null) {
//                    transaction.hide(manager.findFragmentByTag("SearchVehicleFragment"));
//                }
//                if (manager.findFragmentByTag("StatusFragment") != null) {
//                    transaction.hide(manager.findFragmentByTag("StatusFragment"));
//                }
//                if (manager.findFragmentByTag("RemindersFragment") != null) {
//                    transaction.hide(manager.findFragmentByTag("RemindersFragment"));
//                }
//                if (manager.findFragmentByTag("InfoFragment") != null) {
//                    transaction.hide(manager.findFragmentByTag("InfoFragment"));
//                }
//                if (manager.findFragmentByTag("UnregisteredNewTransactionFragment") != null) {
//                    transaction.hide(manager.findFragmentByTag("UnregisteredNewTransactionFragment"));
//                }
//                break;
            case "StatusFragment":
                if (manager.findFragmentByTag("StatusFragment") == null) {
                    transaction.add(R.id.contentLayout, new StatusFragment(), "StatusFragment");
                } else {
                    transaction.show(manager.findFragmentByTag("StatusFragment"));
                }
                if (manager.findFragmentByTag("addtransactionFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("addtransactionFragment"));
                }
                if (manager.findFragmentByTag("NotificationFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("NotificationFragment"));
                }
                if (manager.findFragmentByTag("SearchVehicleFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("SearchVehicleFragment"));
                }
                if (manager.findFragmentByTag("RemindersFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("RemindersFragment"));
                }
                if (manager.findFragmentByTag("InfoFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("InfoFragment"));
                }
                if (manager.findFragmentByTag("addUnregisteredNewTransactionFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("addUnregisteredNewTransactionFragment"));
                }
                break;
            case "RemindersFragment":
                if (manager.findFragmentByTag("RemindersFragment") == null) {
                    transaction.add(R.id.contentLayout, new RemindersFragment(), "RemindersFragment");
                } else {
                    transaction.show(manager.findFragmentByTag("RemindersFragment"));
                }
                if (manager.findFragmentByTag("addtransactionFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("addtransactionFragment"));
                }
                if (manager.findFragmentByTag("NotificationFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("NotificationFragment"));
                }
                if (manager.findFragmentByTag("StatusFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("StatusFragment"));
                }
                if (manager.findFragmentByTag("SearchVehicleFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("SearchVehicleFragment"));
                }
                if (manager.findFragmentByTag("InfoFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("InfoFragment"));
                }
                if (manager.findFragmentByTag("addUnregisteredNewTransactionFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("addUnregisteredNewTransactionFragment"));
                }


                break;

            default:
                if (manager.findFragmentByTag("InfoFragment") == null) {
                    transaction.add(R.id.contentLayout, new InfoFragment(), "InfoFragment");
                } else {
                    transaction.show(manager.findFragmentByTag("InfoFragment"));
                }

                if (manager.findFragmentByTag("addtransactionFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("addtransactionFragment"));
                }
                if (manager.findFragmentByTag("NotificationFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("NotificationFragment"));
                }
                if (manager.findFragmentByTag("StatusFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("StatusFragment"));
                }
                if (manager.findFragmentByTag("RemindersFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("RemindersFragment"));
                }
                if (manager.findFragmentByTag("SearchVehicleFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("SearchVehicleFragment"));
                }
                if (manager.findFragmentByTag("addUnregisteredNewTransactionFragment") != null) {
                    transaction.hide(manager.findFragmentByTag("addUnregisteredNewTransactionFragment"));
                }

                break;
        }
        transaction.commit();
    }

    public static void setManager(FragmentManager manager) {
        NavigationHandler.manager = manager;
    }

}
