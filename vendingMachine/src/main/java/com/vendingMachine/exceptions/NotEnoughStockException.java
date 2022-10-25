package com.vendingMachine.exceptions;

public class NotEnoughStockException extends Exception {
    public NotEnoughStockException(String message){
        super(message);
    }
}
