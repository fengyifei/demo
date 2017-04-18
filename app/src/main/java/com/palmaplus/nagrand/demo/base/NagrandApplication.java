package com.palmaplus.nagrand.demo.base;

import android.app.Application;

import com.palmaplus.nagrand.core.Engine;
import com.palmaplus.nagrand.demo.constants.Constant;
import com.palmaplus.nagrand.demo.utils.FileUtils;


/**
 * Created by lchad on 2016/11/1.
 * Github: https://github.com/lchad
 */
public class NagrandApplication extends Application {

    public static NagrandApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (FileUtils.copyLuaFinished()) {
            /**
             * 初始化引擎
             */
            Engine engine = Engine.getInstance();
            /**
             * 设置验证license，可以通过开发者平台去查找自己的license
             */
            engine.startWithLicense(Constant.APP_KEY, this);
        }
    }
}
