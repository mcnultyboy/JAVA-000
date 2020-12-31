package io.kimmking.dubbo.demo.api;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Account implements Serializable{
    private static final long serialVersionUID = -3477604253711430698L;
    private String id;
    private String name;
    private int dollarAmt;
    private int rmbAmt;
    private int dollarFrz;
    private int rmbFrz;
    private int version;
}
