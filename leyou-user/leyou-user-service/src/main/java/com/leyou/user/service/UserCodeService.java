package com.leyou.user.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.commom.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;


import com.leyou.user.propertties.JwtProperties;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserCodeService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    JwtProperties jwtProperties;



    static final String KEY_PREFIX = "user:code:phone:";


    public Boolean checkData(String data, Integer type) {
        User record = new User();
        if(type==1){
            record.setUsername(data);
        }else if(type==2){
            record.setPhone(data);
        }else{
            return null;
        }
        return this.userMapper.selectCount(record)==0;
    }

    public Boolean createAndSend(String phone) {
        //手机号是否合法
        if(phone==null){
            return false;
        }
        //创建验证码
        String code = NumberUtils.generateCode(6);
        //把codec存起来用于核对用户输入的 存在redis里

        try {
            this.redisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
            //发送验证码
            Map<String,String> msg =new HashMap<>();
            msg.put("phone",phone);
            msg.put("code",code);
            this.amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE","sms.vcode",msg);

            return  true;
        } catch (AmqpException e) {
            e.printStackTrace();
            return false;
        }

    }

    public Boolean userRegidter(User user, String code) {
        //校验验证码
        String pCode = this.redisTemplate.opsForValue().get(KEY_PREFIX+user.getPhone());
        if(!StringUtils.equals(pCode,code)){
            return false;
        }
        //生成salt
        String salt = CodecUtils.generateSalt();
        //校验密码
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        user.setId(null);
        user.setCreated(new Date());
        user.setSalt(salt);

        Boolean bool=this.userMapper.insertSelective(user)== 1;
        if(bool){
            this.redisTemplate.delete(KEY_PREFIX+user.getPhone());
        }
        return bool;


    }

    public User queryByUser(String username, String password) {
        User user1 = new User();
        user1.setUsername(username);
        User user = this.userMapper.selectOne(user1);
        if(user==null){
            return null;
        }
        if(!StringUtils.equals(user.getPassword(),CodecUtils.md5Hex(password, user.getSalt()))){
            return null;
        }
        return user;
    }


//    public String thCreat(String username, String password) {
//        User user = this.queryByUser(username,password);
//        if(user==null){
//            return null;
//        }
//        try {
//            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()),
//                    jwtProperties.getPrivateKey(), jwtProperties.getExpire());
//
//            return token;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
