package com.leyou.sms.listener;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.leyou.sms.properties.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {
    @Autowired
    private SmsUtils smsUtils;
    @Autowired
    private SmsProperties sms;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "leyou.sms.user.queue",durable = "true"),
                    exchange = @Exchange(value = "LEYOU.SMS.EXCHANGE",
                    ignoreDeclarationExceptions = "true",
                    durable = "true",
                    type = ExchangeTypes.TOPIC),
                    key = {"sms.vcode"}
    ))
    public void smsListener(Map<String, String> msg) throws ClientException {
        if(msg==null||msg.size()<=0){
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");
        if(phone==null||code==null){
            return;
        }
        //发送验证码
        SendSmsResponse response = this.smsUtils.sendSms(phone, code, sms.getSignName(), sms.getVerifyCodeTemplate());



    }
}
