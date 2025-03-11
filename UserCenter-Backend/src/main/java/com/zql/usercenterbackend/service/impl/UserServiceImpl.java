package com.zql.usercenterbackend.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zql.usercenterbackend.model.domain.User;
import com.zql.usercenterbackend.service.UserService;
import com.zql.usercenterbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zql.usercenterbackend.constant.userConstant.SHOW_TUBA;
import static com.zql.usercenterbackend.constant.userConstant.USER_LOGIN_STATE;

/**
* @author tuba
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-03-07 14:12:30
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Resource
    private UserMapper userMapper;

    /**
     * 加密盐值，避免密码验证出错
     */
    public static final String SALT = "tuba";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
//        1.校验
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
    //            todo 后期对返回值进行优化
            return -1;
        }
        if (userAccount.length()<4){
            return -1;
        }
        if (userPassword.length()<8 || checkPassword.length()<8){
            return -1;
        }

    //          密码与校验密码是否一致
        if (!checkPassword.equals(userPassword)){
            return -1;
        }

    //          账号是否有特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘； ：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            return -1;
        }

    //          账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count =  userMapper.selectCount(queryWrapper);
        if (count>0){
            return -1;
        }

//        2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userAccount).getBytes());

//        3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult){
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        /**
         *  1.对用户信息进行校验
         */
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        if (userAccount.length()<4){
            return null;
        }
        if (userPassword.length()<8 ){
            return null;
        }

        //          账号是否有特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘； ：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            return null;
        }

        /**
         * 2.用户密码加密，去数据库中比对
         */
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userAccount).getBytes());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info("登录失败！账号或者密码有误");
            log.info("user login failed!userAccount Cannont match password");
            return null;
        }

        /**
         * 3.对用户信息进行脱敏处理
         * todo 可以根据后续建构将这些放在DTO类中
         */
        User safetyUser = getSafeUser(user);

        /**
         * 4.记录用户登录态（session）
         */
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

        return safetyUser;
    }

    @Override
    public User getSafeUser(User originalUser){
        if(originalUser==null){
            return null;
        }

        User safetyUser = new User();
        safetyUser.setId(originalUser.getId());
        safetyUser.setUsername(originalUser.getUsername());
        safetyUser.setUserAccount(originalUser.getUserAccount());
        safetyUser.setAvatarUrl(originalUser.getAvatarUrl());
        safetyUser.setGender(originalUser.getGender());
        safetyUser.setUserPassword(SHOW_TUBA);
        safetyUser.setEmail(originalUser.getEmail());
        safetyUser.setUserRole(originalUser.getUserRole());
        safetyUser.setPhone(originalUser.getPhone());
        safetyUser.setCreateTime(new Date());
        safetyUser.setUpdateTime(new Date());

        return safetyUser;
    }

}


