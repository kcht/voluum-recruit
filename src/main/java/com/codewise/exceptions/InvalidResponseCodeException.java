package com.codewise.exceptions;

import java.security.InvalidParameterException;

/**
 * Created by kchachlo on 2016-11-01.
 */
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
