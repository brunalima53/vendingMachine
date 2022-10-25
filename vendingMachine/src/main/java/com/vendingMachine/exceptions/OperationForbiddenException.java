package com.vendingMachine.exceptions;

public class OperationForbiddenException extends Exception {
    public OperationForbiddenException(String message) {
        super(message);
    }
}
