package com.andriusdgt.thedotsbackend.exception;

public class DuplicatePointException extends RuntimeException {

    public DuplicatePointException(){
        super("Duplicate Point was provided");
    }

}
