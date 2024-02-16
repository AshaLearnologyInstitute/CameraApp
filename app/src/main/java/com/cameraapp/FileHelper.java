package com.cameraapp;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.core.content.FileProvider;

class FileHelper {

   public static File getOutputMediaFile(Context context, String dirName, String extension) {

//      File mediaStorageDir = new File(getParentFolder(context.getString(R.string.app_name)) + "/" + dirName);
      File mediaStorageDir = new File(getDCIMpath()+"/MyCam");
      if (!mediaStorageDir.exists()) {
         if (!mediaStorageDir.mkdirs()) {
            Log.d("Camera App", "failed to create directory");
            return null;
         }
      }
      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      File mediaFile = new File(mediaStorageDir.getPath() + File.separator
              + "IMG_" + timeStamp + "." + extension);

      return mediaFile;
   }


   public static File getParentFolder(String mainFolder) {

      File parentFolder = new File(Environment.getExternalStorageDirectory() + "/" + mainFolder);
      if (!parentFolder.exists()) {
         parentFolder.mkdirs();
      }
      return parentFolder;
   }

   public static String getDCIMpath(){
      String path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
      return path;
   }

   public static Uri getFileURI(Context context, File path){
      Uri uri=FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",path);
      return uri;
   }

}
