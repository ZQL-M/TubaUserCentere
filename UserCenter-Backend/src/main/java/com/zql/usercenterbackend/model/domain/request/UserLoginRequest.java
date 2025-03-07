package com.zql.usercenterbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求实体
 *
 * @author tuba
 */

@Data
public class UserLoginRequest implements Serializable {
    private static final  long serialVersionUID = 1345099986181355631L;

    private String userAccount;

    private String userPassword;
}
