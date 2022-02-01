package com.my.wallet.env;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class category {
    private int logo_category;
    private Map<String, Integer> sub_category;
}
