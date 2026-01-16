package com.ht.feignapi.auth.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AddRoleMenuList implements Serializable {

    private String roleCode;

    private List<String> menuCodeList;
}
