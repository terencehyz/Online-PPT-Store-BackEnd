package com.example.nutzdemo.Module;

import com.example.nutzdemo.Bean.Product;
import com.example.nutzdemo.Bean.Upload;
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
public class SellerModule {
    @Inject
    Dao dao;

    @At("/addProduct")
    @Ok("json")
    @Fail("http:403")
    @POST
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
}
