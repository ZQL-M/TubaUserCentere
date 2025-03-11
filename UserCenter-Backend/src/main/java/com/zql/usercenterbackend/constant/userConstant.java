package com.zql.usercenterbackend.constant;

/**
 * 用户常量
 *
 * @author tuba
 */
public interface userConstant {

    /**
     * 用户登录状态量
     */
    String USER_LOGIN_STATE = "userLoginState";

    // ------ 权限 ------
    /**
     * 默认权限，普通用户权限
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;

    /**
     * 返回的脱敏信息
     */
    String SHOW_TUBA = "tuba-TUBA-图八";

}
