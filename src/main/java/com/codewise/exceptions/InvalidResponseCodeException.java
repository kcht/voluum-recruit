package com.codewise.exceptions;

public class InvalidResponseCodeException extends RuntimeException
{
    private int errorCode;

    public InvalidResponseCodeException(int errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode(){
        return errorCode;
    }
}
