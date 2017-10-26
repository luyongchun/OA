package com.luyc.bnd.oaattendnace.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by admin on 2017/10/26.
 */

public class FileUtil {
    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }
}
