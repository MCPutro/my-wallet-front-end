package com.my.wallet.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Wallet implements Serializable {
    private String id;
    private String backgroundColor;
    private String textColor;
    private String name;
    private Double nominal;
    private String type;

    public Wallet(String id, String backgroundColor, String name, Double nominal, String type) {
        this.id = id;
        this.backgroundColor = backgroundColor;
        this.name = name;
        this.nominal = nominal;
        this.textColor = "#FFFFFFFF";
        this.type = type;
    }

    public Wallet(String id, String backgroundColor, String name, Double nominal, String textColor, String type) {
        this.id = id;
        this.backgroundColor = backgroundColor;
        this.name = name;
        this.nominal = nominal;
        this.textColor = textColor;
        this.type = type;
    }

}
