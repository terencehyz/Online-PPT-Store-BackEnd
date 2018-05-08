package com.example.nutzdemo.Module;

import com.example.nutzdemo.Bean.*;
import com.example.nutzdemo.Util.Toolkit;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@IocBean
@Fail("http:500")
@Filters(@By(type = CrossOriginFilter.class))
public class CustomerModule {
    @Inject
    Dao dao;

    @At("/addToCart")
    @Ok("json")
    @Fail("http:403")
    @POST
    @Filters(@By(type = CrossOriginFilter.class))
    public Object addToCart(@Param("productId") int Pid,
                            @Param("userId") int Uid,
                            HttpServletRequest request) {
        Cart cart = new Cart();
        cart.setPid(Pid);
        cart.setUid(Uid);
        Cart cartTest = dao.fetch(Cart.class, Cnd.where("Pid", "=", Pid).and("Uid", "=", Uid));
        if (cartTest != null)
            return Toolkit.getFailResult(-1, "购物车中已经存在");
        dao.insert(cart);
        List<Cart> carts = dao.query(Cart.class, Cnd.where("Uid", "=", Uid));
        return Toolkit.getSuccessResult(carts, "加入成功！");
    }

    @At("/Signalpurchase")
    @Ok("json")
    @Fail("http:403")
    @POST
    @Filters(@By(type = CrossOriginFilter.class))
    public Object Signalpurchase(@Param("Pid") int Pid,
                                 @Param("Password") String password,
                                 @Param("Uid") int Uid) {
        User user = dao.fetch(User.class, Cnd.where("id", "=", Uid).and("password", "=", password));
        if (user == null)
            return Toolkit.getFailResult(-1, "密码错误，请重新输入");
        Product product = dao.fetch(Product.class, Cnd.where("id", "=", Pid));
        if (product.getValue() > user.getCredit())
            return Toolkit.getFailResult(-2, "余额不足，请充值");
        Purchase purchase = new Purchase();
        purchase.setUid(Uid);
        purchase.setPid(Pid);
        dao.insert(purchase);
        Cart tempCart = dao.fetch(Cart.class, Cnd.where("Pid", "=", Pid).and("Uid", "=", Uid));
        dao.delete(tempCart);
        // TODO
//        User user = dao.fetch(User.class, Cnd.where("id", "=", Uid));
//        user.setNickName(nickName);
//        dao.update(user);


        return Toolkit.getSuccessResult(null, "购买成功");
    }

    @At("/visiable")
    @Ok("json")
    @Fail("http:403")
    @Filters(@By(type = CrossOriginFilter.class))
    @POST
    public Object visiable(@Param("Uid") int Uid,
                           @Param("Pid") int Pid) {
        Purchase purchase = dao.fetch(Purchase.class, Cnd.where("Uid", "=", Uid).and("Pid", "=", Pid));
        if (purchase == null) {
            Upload upload = dao.fetch(Upload.class, Cnd.where("Uid","=",Uid).and("Pid","=",Pid));
            if (upload==null)
                return Toolkit.getFailResult(-1, "未购买");
            else
                return Toolkit.getSuccessResult(null, "已购买");
        } else {
            return Toolkit.getSuccessResult(null, "已购买");
        }
    }

    @At("/getCart")
    @Ok("json")
    @Fail("http:403")
    @Filters(@By(type = CrossOriginFilter.class))
    public Object getCart(@Param("Uid") int Uid,
                          HttpServletRequest request) {
        List<Cart> carts = dao.query(Cart.class, Cnd.where("Uid", "=", Uid));
        List<Product> products = new ArrayList<Product>();
        for (int i = 0; i < carts.size(); i++) {
            Product product = dao.fetch(Product.class, Cnd.where("id", "=", carts.get(i).getPid()));
            if (product != null)
                products.add(product);
        }
        return Toolkit.getSuccessResult(products, "获取成功");
    }

    @At("/getPurchase")
    @Ok("json")
    @Fail("http:403")
    @GET
    @Filters(@By(type = CrossOriginFilter.class))
    public Object getPurchase(@Param("Uid") int Uid,
                              HttpServletRequest request) {
        List<Purchase> purchases = dao.query(Purchase.class, Cnd.where("Uid", "=", Uid));
        List<Product> products = new ArrayList<Product>();
        for (int i = 0; i < purchases.size(); i++) {
            Product product = dao.fetch(Product.class, Cnd.where("id", "=", purchases.get(i).getPid()));
            if (product != null)
                products.add(product);
        }
        return Toolkit.getSuccessResult(products, "获取成功");
    }

    @At("/checkOut")
    @Ok("json")
    @Fail("http:403")
    @POST
    @Filters(@By(type = CrossOriginFilter.class))
    public Object checkOut(@Param("Uid") int Uid,
                           @Param("Pids") int[] arrayList,
                           @Param("password") String pwd) {
        System.out.println("*****" + pwd);
        User uTest = dao.fetch(User.class, Cnd.where("id", "=", Uid).and("password", "=", pwd));
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        if (uTest == null)
            return Toolkit.getFailResult(-2, "密码错误");
        double cost = 0;
        ArrayList<Purchase> list = new ArrayList<Purchase>();
        for (int i = 0; i < arrayList.length; i++) {
            Product p = dao.fetch(Product.class, Cnd.where("id", "=", arrayList[i]));
            cost += p.getValue();
            Purchase toPur = new Purchase();
            toPur.setPid(arrayList[i]);
            toPur.setUid(Uid);
            list.add(toPur);
        }
        User user = dao.fetch(User.class, Cnd.where("id", "=", Uid));
        double userCredit = user.getCredit();
        if (cost <= userCredit) {
            for (int i = 0; i < list.size(); i++) {
                Cart cart = dao.fetch(Cart.class, Cnd.where("Uid", "=", Uid).and("Pid", "=", arrayList[i]));
                dao.delete(cart);
                dao.insert(list.get(i));
            }
            dao.update(User.class, Chain.make("credit", userCredit - cost), Cnd.where("id", "=", Uid));
            return Toolkit.getSuccessResult(null, "购买成功！");
        } else {
            return Toolkit.getFailResult(-1, "余额不足，请充值！");
        }
    }

    @At("/addCredit")
    @Ok("json:{locked:'password'}")
    @Fail("http:403")
    @POST
    @Filters(@By(type = CrossOriginFilter.class))
    public Object addCredit(@Param("Uid") int Uid,
                            @Param("Credit") double credit) {
        User user = dao.fetch(User.class, Cnd.where("id", "=", Uid));
        double userCredit = user.getCredit();
        double tempCredit = userCredit + credit;
        user.setCredit(tempCredit);
        dao.update(user, "^credit$");
        return Toolkit.getSuccessResult(dao.fetch(User.class, Cnd.where("id", "=", Uid)), "充值成功！");
    }

    @At("/getUserInfo")
    @Ok("json:{locked:'password'}")
    @Fail("http:403")
    @GET
    @Filters(@By(type = CrossOriginFilter.class))
    public Object getUserInfo(@Param("Uid") int Uid) {
        User user = dao.fetch(User.class, Cnd.where("id", "=", Uid));
        return Toolkit.getSuccessResult(user, "获取成功");
    }

    @At("/modifyUser")
    @Ok("json:{locked:'password'}")
    @Fail("http:403")
    @POST
    @Filters(@By(type = CrossOriginFilter.class))
    public Object modifyUser(@Param("Uid") int Uid,
                             @Param("NickName")String nickName) {
        User user = dao.fetch(User.class, Cnd.where("id", "=", Uid));
        user.setNickName(nickName);
        dao.update(user);
        return Toolkit.getSuccessResult(user, "修改成功");
    }

    @At("/getUploaded")
    @Ok("json")
    @Fail("http:403")
    @GET
    @Filters(@By(type = CrossOriginFilter.class))
    public Object getUploaded(@Param("Uid") int Uid,
                              HttpServletRequest request) {
        List<Upload> uploads = dao.query(Upload.class, Cnd.where("Uid","=",Uid));
        List<Product> products = new ArrayList<Product>();
        for (int i = 0; i < uploads.size(); i++) {
            Product product = dao.fetch(Product.class, Cnd.where("id", "=", uploads.get(i).getPid()));
            if (product != null)
                products.add(product);
        }
        return Toolkit.getSuccessResult(products, "获取成功");
    }

}
