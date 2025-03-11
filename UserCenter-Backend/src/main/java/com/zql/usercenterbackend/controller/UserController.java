package com.zql.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zql.usercenterbackend.model.domain.User;
import com.zql.usercenterbackend.model.domain.request.UserLoginRequest;
import com.zql.usercenterbackend.model.domain.request.UserRegisterRequest;
import com.zql.usercenterbackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.zql.usercenterbackend.constant.userConstant.ADMIN_ROLE;
import static com.zql.usercenterbackend.constant.userConstant.USER_LOGIN_STATE;


/**
 * 用户接口
 *
 * @author tuba
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @author tuba
     */
    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    /**
     * 用户登录
     * @author tuba
     */
    @PostMapping("/login")
    public User userRegister(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest== null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        return userService.userLogin(userAccount, userPassword,request);
    }

    /**
     * 获取当前用户
     * @author tuba
     */
    @PostMapping("current")
    public User currentUser(HttpServletRequest request) {
        Object userObj=request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser=(User) userObj;
        if (currentUser==null){
            return null;
        }
        long userId = currentUser.getId();
        //todo 校验用户是否合格
        User user = userService.getById(userId);
        return userService.getSafeUser(user);
    }

    /**
     * 用户查询
     * @author tuba
     */
    @GetMapping("/search")
    public List<User> searchUsers(String username,HttpServletRequest request) {
        if(isAdmin(request)){
            return new ArrayList<>();
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        return userList.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
    }

    /**
     * 用户删除
     * @author tuba
     */
    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id,HttpServletRequest request) {
        if(isAdmin(request)){
            return false;
        }

        if (id<=0){
            return false;
        }
        return userService.removeById(id);
    }

    /**
     * 是否为管理员
     * @author tuba
     */
    private  boolean isAdmin(HttpServletRequest request){
        // 仅有管理员可以查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User  user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
