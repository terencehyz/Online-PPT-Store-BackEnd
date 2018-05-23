package com.example.nutzdemo.Module;

import com.example.nutzdemo.Bean.Product;
import com.example.nutzdemo.Bean.Upload;
import com.example.nutzdemo.Bean.User;
import com.example.nutzdemo.Util.Toolkit;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;

@IocBean
@Fail("http:500")
@Filters(@By(type = CrossOriginFilter.class))
public class AdminModule {
    @Inject
    Dao dao;

    @At("/addProduct")
    @Ok("json")
    @Fail("http:403")
    @POST
    @Filters(@By(type = CrossOriginFilter.class))
    public Object addProduct(@Param("Title") String Title,
                             @Param("Description") String Description,
                             @Param("Thumbnail") String Thumbnail,
                             @Param("Download") String Download,
                             @Param("Type") int Type,
                             @Param("Way") int Way,
                             @Param("value") double value,
                             @Param("Uid") int Uid) {
        Product product = new Product();
        product.setTitle(Title);
        product.setDescription(Description);
        product.setThumbnail(Thumbnail);
        product.setDownload(Download);
        product.setFavoriteCount(0);
        product.setType(Type);
        product.setWay(Way);
        product.setValue(value);
        if (Uid==1||Uid==2){
            product.setChecked(1);
        }
        else {
            product.setChecked(0);
        }
        dao.insert(product);
        Upload upload = new Upload();
        upload.setUid(Uid);
        /*int Pid;
        Pid = dao.query(Product.class, Cnd.where("Download","=",Download));*/
        upload.setPid(product.getId());
        dao.insert(upload);
        return Toolkit.getSuccessResult(null, "添加成功！");
    }

    @At("/verifySuccess")
    @Ok("json")
    @Fail("http:403")
    @POST
    @Filters(@By(type = CrossOriginFilter.class))
    public Object verifySuccess(@Param("Pid") int Pid) {
        Product product = dao.fetch(Product.class, Cnd.where("id","=",Pid));
        product.setChecked(1);
        dao.update(product);

        Upload upload = dao.fetch(Upload.class, Cnd.where("Pid","=",Pid));

        User user = dao.fetch(User.class,Cnd.where("id","=",upload.getUid()));
        user.setCredit(user.getCredit()+100);
        dao.update(user);

        return Toolkit.getSuccessResult(null, "审核完成：通过");
    }

    @At("/verifyFailed")
    @Ok("json")
    @Fail("http:403")
    @POST
    @Filters(@By(type = CrossOriginFilter.class))
    public Object verifyFailed(@Param("Pid") int Pid) {
        Product product = dao.fetch(Product.class, Cnd.where("id","=",Pid));
        product.setChecked(2);
        dao.update(product);
        return Toolkit.getSuccessResult(null, "审核完成:不通过");
    }
}
