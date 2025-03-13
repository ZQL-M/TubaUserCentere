package com.zql.usercenterbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zql.usercenterbackend.common.BaseResponse;
import com.zql.usercenterbackend.common.ErrorCode;
import com.zql.usercenterbackend.common.ResultUtils;
import com.zql.usercenterbackend.exception.BusinessException;
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
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     * @author tuba
     */
    @PostMapping("/login")
    public BaseResponse<User> userRegister(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest== null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User resultUser = userService.userLogin(userAccount, userPassword,request);
        return ResultUtils.success(resultUser);
    }

    /**
     * 用户注销，退出登录
     * @author tuba
     */
    @PostMapping("/outLogin")
    public BaseResponse<Integer> userOutLogin(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userOutLogin(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户
     * @author tuba
     */
    @GetMapping("current")
    public BaseResponse<User> currentUser(HttpServletRequest request) {
        Object userObj=request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser=(User) userObj;
        if (currentUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        //todo 校验用户是否合格
        User user = userService.getById(userId);
        User resultUser = userService.getSafeUser(user);
        return ResultUtils.success(resultUser);
    }

    /**
     * 用户查询
     * @author tuba
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request) {
        if(!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> resultList = userList.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
        return ResultUtils.success(resultList);
    }

    /**
     * 用户删除
     * @author tuba
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean resultBoolean= userService.removeById(id);
        return ResultUtils.success(resultBoolean);
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
