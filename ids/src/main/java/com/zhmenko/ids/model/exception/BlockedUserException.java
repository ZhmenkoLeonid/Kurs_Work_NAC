package com.zhmenko.ids.model.exception;

public class BlockedUserException extends Exception {
    private String ipAddress;
    public BlockedUserException(String ipAddress){
        this.ipAddress = ipAddress;
    }
    @Override
    public void printStackTrace() {
        System.err.print("Следующий пользователь находится в чёрном списке: "+ipAddress);
    }
}
