
package com.example.nutzdemo.Util;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailService {
    public static void sendMail(String targetMail,String code,int type) throws Exception {
        Properties props = new Properties();
        // 开启debug调试
        props.setProperty("mail.debug", "true");
        // 发送服务器需要身份验证
        props.setProperty("mail.smtp.auth", "true");
        // 设置邮件服务器主机名
        props.setProperty("mail.host", "smtp.qq.com");
        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);

        Session session = Session.getInstance(props);

        Message msg = new MimeMessage(session);
        msg.setSubject("韩驭洲的杂货铺");
        StringBuilder builder = new StringBuilder();
        if (type==1){
            builder.append("下载链接为："+ code);
        }
        else{
            builder.append("你的验证码是："+code);
        }
        msg.setText(builder.toString());
        msg.setFrom(new InternetAddress("your mail"));

        Transport transport = session.getTransport();
        transport.connect("smtp.qq.com", "1455723823@qq.com", "Boyhood1996");

        transport.sendMessage(msg, new Address[] { new InternetAddress(targetMail) });
        transport.close();
    }
}
