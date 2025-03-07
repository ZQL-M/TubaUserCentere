package com.zql.usercenterbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求实体
 *
 * @author tuba
 */

@Data
public class UserRegisterRequest implements Serializable {
    private static final  long serialVersionUID = 5553872418030603690L;

    private String userAccount;

    private String checkPassword;

    private String userPassword;
}
