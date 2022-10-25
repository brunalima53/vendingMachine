package com.vendingMachine.exceptions;

public class NotAValidProductCostException extends Exception {
    public NotAValidProductCostException(String message){
        super(message);
    }
}
