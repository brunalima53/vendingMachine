package com.vendingMachine.exceptions;

public class NotAValidCoinException extends Exception {
    public NotAValidCoinException(String message){
        super(message);
    }
}
