package com.tiger.jump.high.sharecamera.utils;

import android.os.Environment;

import com.tiger.jump.high.sharecamera.Const;

import java.io.File;

/**
 * Created by yb on 16-4-10.
 */
public class FileUtils {

    private static File getExterRootFile() {
        File sdRoot = null;
        try {
            sdRoot = Environment.getExternalStorageDirectory().getCanonicalFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdRoot;
    }

    private static File getAppRootFile() {
        File sdRoot = getExterRootFile();
        if (sdRoot != null) {
            File appRoot = new File(sdRoot, Const.ROOT_DIR);
            if (!appRoot.exists()) {
                appRoot.mkdirs();
            }
            return appRoot;
        } else {
            return null;
        }
    }

    private static File getHandleBaseFile(String  str) {
        File appRoot = getAppRootFile() ;
        if(appRoot!=null){
            File pRoot = new File(appRoot , str) ;
            if(!pRoot.exists()){
                pRoot.mkdirs();
            }
            return pRoot;
        }else {
            return null ;
        }
    }

    public static File getVideoFile() {
        return getHandleBaseFile(Const.ROOT_VIDEO);
    }

}
