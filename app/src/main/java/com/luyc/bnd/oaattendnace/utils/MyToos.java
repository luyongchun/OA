package com.luyc.bnd.oaattendnace.utils;

import android.os.Build;

/**
 * Created by admin on 2017/9/11.
 */

public class MyToos {

    /**
     * 判断Android系统版本是否 >= LOLLIPOP(API21)
     *
     * @return boolean
     */
    public static boolean isLOLLIPOP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        } else {
            return false;
        }
    }

}
