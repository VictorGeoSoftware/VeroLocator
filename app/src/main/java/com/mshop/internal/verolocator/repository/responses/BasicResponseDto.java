package com.mshop.internal.verolocator.repository.responses;

import com.mshop.internal.verolocator.data.MessageCode;

import java.util.ArrayList;

/**
 * Created by victor on 8/6/18.
 * Mshop Spain.
 */
public class BasicResponseDto {
    private String code;
    private ArrayList<MessageCode> errors = new ArrayList<MessageCode>();

    public String getCode() {
        return code;
    }

    public ArrayList<MessageCode> getErrors() {
        return errors;
    }
}
