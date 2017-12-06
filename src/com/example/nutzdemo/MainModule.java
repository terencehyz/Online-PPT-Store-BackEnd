package com.example.nutzdemo;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@SetupBy(value = MainSetUp.class)
@IocBy(type = ComboIocProvider.class,args ={
        "*js",
        "ioc/",
        "*anno",
        "com.example.nutzdemo",
        "*tx",
        "*async"
})
@IocBean
@Modules(scanPackage = true)
public class MainModule {
}