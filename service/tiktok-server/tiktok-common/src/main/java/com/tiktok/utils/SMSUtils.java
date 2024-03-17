package com.tiktok.utils;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * 短信发送工具类.
 *
 * @author lzc
 * @version 1.0
 */
@Component
@Slf4j
public class SMSUtils {
    @Resource
    private TencentCloudProperties tencentCloudProperties;

    /**
     * 接入腾讯云过程.
     *
     * @param phone 手机号
     * @param code 验证码
     */
    public void sendSMS(String phone, String code) throws TencentCloudSDKException {

        // 实例化一个认证对象, 入参需要传入腾讯云账户密钥对secretId, secretKey
        Credential cred = new Credential(tencentCloudProperties.getSecretId(), tencentCloudProperties.getSecretKey());

        // 实例化一个http选项, 可选的, 没有特殊需求可以跳过, 默认为POST
        HttpProfile httpProfile = new HttpProfile();

        /*
         * SDK会自动指定域名, 通常是不需要特地指定域名的, 但是如果你访问的是金融区的服务
         * 则必须手动指定域名, 例如sms的上海金融区域名: sms.ap-shanghai-fsi.tencentcloudapi.com
         */
        httpProfile.setEndpoint("sms.tencentcloudapi.com");

        // 实例化一个clientProfile选项
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        // 实例化要请求产品的client对象, clientProfile是可选的
        SmsClient client = new SmsClient(cred, "ap-nanjing", clientProfile);

        // 实例化一个请求对象, 每个接口都会对应一个request对象
        SendSmsRequest req = new SendSmsRequest();

        String[] phoneNumberSet = {"+86" + phone};  //电话号码
        req.setPhoneNumberSet(phoneNumberSet);
        req.setSmsSdkAppId("");   // 短信应用id
        req.setSignName("");     // 签名
        req.setTemplateId("");       // 模板id

        // 模板参数, 若无模板参数, 则设置为空
        String[] templateParamSet = {code};
        req.setTemplateParamSet(templateParamSet);

        // 返回的resp是一个SendSmsResponse的实例, 与请求对象对应
        SendSmsResponse resp = client.SendSms(req);

        // 输出json格式的字符串回包
        // log.info(SendSmsResponse.toJsonString(resp));
    }
}