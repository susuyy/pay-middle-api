package com.ht.auth2.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AddRoleMenuList implements Serializable {

    private String roleCode;

    private List<String> menuCodeList;
}
