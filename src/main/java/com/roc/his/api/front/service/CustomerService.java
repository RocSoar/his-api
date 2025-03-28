package com.roc.his.api.front.service;

import java.util.HashMap;
import java.util.Map;

public interface CustomerService {
    boolean sendSmsCode(String tel);

    HashMap login(String tel, String code);

    HashMap searchSummary(int id);

    boolean update(Map param);
}
