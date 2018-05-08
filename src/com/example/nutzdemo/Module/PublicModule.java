package com.example.nutzdemo.Module;

import com.example.nutzdemo.Bean.Product;
import com.example.nutzdemo.Bean.User;
import com.example.nutzdemo.Bean.VerificationCode;
import com.example.nutzdemo.Util.MD5Utils;
import com.example.nutzdemo.Util.TestMail;
import com.example.nutzdemo.Util.Toolkit;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@IocBean
@Fail("http:500")
@Filters(@By(type = CrossOriginFilter.class))
public class PublicModule {

    @Inject
    Dao dao;

    //登录
    @At("/login")
    @Ok("json")
    @Fail("http:403")
    @POST
    @Filters(@By(type = CrossOriginFilter.class))
    public Object login(@Param("email") String email,
                        @Param("password") String password,
                        HttpServletRequest request) {
        // -1 结构验证错误
        // -2 用户不存在
        // -3 用户名或密码错误
        if (email == null || password == null)
            return Toolkit.getFailResult(-1, "结构验证错误");
        User user1 = dao.fetch(User.class, Cnd.where("email", "=", email));
        if (user1 == null)
            return Toolkit.getFailResult(-2, "用户不存在");
        User user = dao.fetch(User.class, Cnd.where("email", "=", email).and("password", "=", password));
        if (user != null) {
            return Toolkit.getSuccessResult(user, "登陆成功");
        } else {
            return Toolkit.getFailResult(-3, "用户名或密码错误");
        }
    }

    //忘记密码
    @At("/forgetPW")
    @Ok("json")
    @Fail("http:403")
    @POST
    @Filters(@By(type = CrossOriginFilter.class))
    public Object forgetPW(@Param("email") String email,
                           @Param("code") String code,
                           @Param("Password") String password){
        if (email == null || password == null || code == null) {
            return Toolkit.getFailResult(-1, "结构验证错误");
        }
        VerificationCode codeVaild = dao.fetch(VerificationCode.class, Cnd.where("email", "=", email).and("code", "=", code).and("vaild", "=", 1));
        if (codeVaild == null)
            return Toolkit.getFailResult(-3, "验证码错误");
        dao.update(User.class, Chain.make("password",password), Cnd.where("email","=",email));
        dao.update(VerificationCode.class, Chain.make("vaild", 0), Cnd.where("email", "=", email));
        return Toolkit.getSuccessResult(null,"修改成功");
    }

    //发送验证码
    @At("/send")
    @Ok("json")
    @Fail("http:403")
    @GET
    @Filters(@By(type = CrossOriginFilter.class))
    public Object send(@Param("email") String email,
                       @Param("id") int id) {
        Product product = dao.fetch(Product.class, Cnd.where("id", "=", id));
        String url = product.getDownload();
        try {
            TestMail.sendMail(email, url, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Toolkit.getSuccessResult(null, "发送成功");
    }

    //获取验证码
    @At("/getVcode")
    @Ok("json")
    @Fail("http:403")
    @GET
    @Filters(@By(type = CrossOriginFilter.class))
    public Object getVcode(@Param("email") String email,
                           HttpServletRequest request) {
        User emailExist = dao.fetch(User.class, Cnd.where("email", "=", email));
        if (email.length() < 1 || email == null)
            return Toolkit.getFailResult(-1, "结构验证错误");
        if (emailExist != null)
            return Toolkit.getFailResult(-2, "此邮箱已注册");
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String code = MD5Utils.getPwd(timeStamp).substring(0, 5);
        dao.update(VerificationCode.class, Chain.make("vaild", 0), Cnd.where("email", "=", email));
        VerificationCode vcode = new VerificationCode();
        vcode.setEmail(email);
        vcode.setCode(code);
        vcode.setVaild(1);
        dao.insert(vcode);
        try {
            TestMail.sendMail(email, code, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Toolkit.getSuccessResult(null, "成功获取验证码");
    }

    //注册
    @At("/register")
    @Ok("json")
    @Fail("http:403")
    @POST
    @Filters(@By(type = CrossOriginFilter.class))
    public Object register(@Param("nickName") String nickName,
                           @Param("email") String email,
                           @Param("password") String password,
                           @Param("code") String code,
                           HttpServletRequest request) {
        if (nickName == null || email == null || password == null || code == null) {
            return Toolkit.getFailResult(-1, "结构验证错误");
        }
        User emailExist = dao.fetch(User.class, Cnd.where("email", "=", email));
        if (emailExist != null)
            return Toolkit.getFailResult(-2, "此邮箱已注册");
        VerificationCode codeVaild = dao.fetch(VerificationCode.class, Cnd.where("email", "=", email).and("code", "=", code).and("vaild", "=", 1));
        if (codeVaild == null)
            return Toolkit.getFailResult(-3, "验证码错误");
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setNickName(nickName);
        newUser.setPassword(password);
        newUser.setCredit(500.0);
        dao.insert(newUser);
        dao.update(VerificationCode.class, Chain.make("vaild", 0), Cnd.where("email", "=", email));
        return Toolkit.getSuccessResult(null, "注册成功");
    }

    //返回所有已审核商品商品
    @At("/getAll")
    @Ok("json:{locked:'Download'}")
    @Fail("http:403")
    @Filters(@By(type = CrossOriginFilter.class))
    public Object getAll(HttpServletRequest request) {
        List<Product> products = dao.query(Product.class, Cnd.where("checked","=",1));
        return Toolkit.getSuccessResult(products, "获取成功");
    }

    //返回所有未审核商品
    @At("/getVerify")
    @Ok("json")
    @Fail("http:403")
    @Filters(@By(type = CrossOriginFilter.class))
    public Object getVerify(HttpServletRequest request) {
        List<Product> products = dao.query(Product.class, Cnd.where("checked","=",0));
        return Toolkit.getSuccessResult(products, "获取成功");
    }

    //返回商品byType
    @At("/getByType")
    @Ok("json:{locked:'Download'}")
    @Fail("http:403")
    @Filters(@By(type = CrossOriginFilter.class))
    public Object getByType(@Param("type") int type,
                            HttpServletRequest request) {
        List<Product> products = dao.query(Product.class, Cnd.where("Type", "=", type).and("checked","=",1));
        if (products.size() < 1) return Toolkit.getFailResult(-1, "没有此类型");
        return Toolkit.getSuccessResult(products, "获取成功ByType");
    }

    //返回商品byWay
    @At("/getByWay")
    @Ok("json:{locked:'Download'}")
    @Fail("http:403")
    @Filters(@By(type = CrossOriginFilter.class))
    public Object getByWay(@Param("way") int way,
                           HttpServletRequest request) {
        List<Product> products = dao.query(Product.class, Cnd.where("Way", "=", way).and("checked","=",1));
        if (products.size() < 1) return Toolkit.getFailResult(-1, "没有此风格");
        return Toolkit.getSuccessResult(products, "获取成功ByWay");
    }

    //产品详情
    @At("/getProudctDetail")
    @Ok("json")
    @Fail("http:403")
    @Filters(@By(type = CrossOriginFilter.class))
    public Object getProudctDetail(@Param("id") int id,
                                   HttpServletRequest request) {
        List<Product> products = dao.query(Product.class, Cnd.where("id", "=", id));
        return Toolkit.getSuccessResult(products, "获取成功ById");
    }

    //查询
    @At("/Query")
    @Ok("json")
    @Fail("http:403")
    @Filters(@By(type = CrossOriginFilter.class))
    public Object Query(@Param("keyWord") String ls,
                        HttpServletRequest request) {
        try {
            ls = new String(ls.getBytes("iso-8859-1"), "utf-8");
            ls = URLDecoder.decode(ls, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(ls);
        List<Product> products1 = dao.query(Product.class, Cnd.where("Title", "LIKE", '%' + ls + '%').and("checked","=",1));
        List<Product> products2 = dao.query(Product.class, Cnd.where("Description", "LIKE", '%' + ls + '%').and("checked","=",1));
        List<Product> mylist = new ArrayList<Product>();
        mylist.addAll(products1);
        mylist.addAll(products2);
        HashSet h = new HashSet(mylist);
        mylist.clear();
        mylist.addAll(h);
        return Toolkit.getSuccessResult(products1, "查询成功");
    }
}


