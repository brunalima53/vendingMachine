package com.vendingMachine.exceptions;

public class NotExistentProductException extends Exception {
    public NotExistentProductException(String message){
        super(message);
    }
}