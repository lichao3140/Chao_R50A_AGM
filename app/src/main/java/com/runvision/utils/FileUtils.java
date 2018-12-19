package com.runvision.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

/**
 * Created by Administrator on 2018/7/21.
 */

public class FileUtils {
    public static void deleteTempter(String imageID, String path) {
        String sdCardDir = Environment.getExternalStorageDirectory() + "/FaceAndroid/" + path + "/" + imageID + ".jpg";
        File file=new File(sdCardDir);
        if(file.exists()){
           file.delete();
        }
    }

    public static void deleteTempter(String imageID) {
        String sdCardDir = imageID + ".jpg";
        File file=new File(sdCardDir);
        if(file.exists()){
            file.delete();
        }
    }

    public static void deleteFaceTempter(String imageID, String path) {
        String sdCardDir = Environment.getExternalStorageDirectory() + "/FaceAndroid/" + path + "/" + imageID+".data";
        File file=new File(sdCardDir);
        if(file.exists()){
            file.delete();
        }
    }

    public static File saveFile(Bitmap btImage, String fileName,String sddir) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String sdCardDir = Environment.getExternalStorageDirectory() + "/FaceAndroid/";
            if (!sddir.equals("")) {
                sdCardDir = sdCardDir +sddir+"/";
            }
            File dirFile = new File(sdCardDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File file = new File(sdCardDir, fileName + ".jpg");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                btImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                if(out!=null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }

    public static Bitmap loadTempBitmap(String fileName, String DirName){
        String sdCardDir = Environment.getExternalStorageDirectory() + "/FaceAndroid/" + DirName + "/" + fileName + ".jpg";
        File file=new File(sdCardDir);
        if(!file.exists()){
          return null;
        }
        return getSmallBitmap(sdCardDir);
    }

    private static Bitmap getSmallBitmap(String path) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path,options);

        int tempWidth = options.outWidth;
        int tempHeight = options.outHeight;

        while (tempWidth > 1024 || tempHeight > 1024) {
            tempWidth /= 2;
            tempHeight /= 2;
        }
        options.inSampleSize = calculateInSampleSize(options, tempWidth, tempHeight);

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path,options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static void cleanDir(String dirName) {
        String sdCardDir = Environment.getExternalStorageDirectory() + "/" + dirName;
        File file = new File(sdCardDir);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean socketSaveImage(byte[] data, String fileName) {


        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String sdCardDir = Environment.getExternalStorageDirectory() + "/SocketImage/";


          //  Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            //int w_bitmap = bitmap.getWidth();
           // int h_bitmap = bitmap.getHeight();
           // Log.i("Gavin_1203","w_bitmap:"+w_bitmap+"h_bitmap:"+h_bitmap);

            File dirFile = new File(sdCardDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }


            File file = new File(sdCardDir, fileName + ".jpeg");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(data, 0, data.length);
               // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return false;
        }
    }
    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName
     *            要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = FileUtils.deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = FileUtils.deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

    public static BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    public static String bitmaptoString(Bitmap bitmap) {
        //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    /**
     * 把身份证的图片变成算法需要的BGR24
     *
     * @param bmp
     * @return
     */
    public static byte[] bitmapToBGR24(Bitmap bmp) {
        if (bmp == null) {
            return null;
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        byte[] value = null;

        int[] pixels = new int[width * height];
        // 获取RGB32数据
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] BGR24 = new byte[width * height * 3];
        //byte[] gray = new byte[width * height];
        // 获取图片的RGB24数据和灰度图数据
        for (int i = 0; i < width * height; i++) {
            int r = (pixels[i] >> 16) & 0x000000FF;
            int g = (pixels[i] >> 8) & 0x000000FF;
            int b = pixels[i] & 0x000000FF;
            BGR24[i * 3] = (byte) (b & 0xFF);
            BGR24[i * 3 + 1] = (byte) (g & 0xFF);
            BGR24[i * 3 + 2] = (byte) (r & 0xFF);
        }

        return BGR24;

    }


    public static Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap decodeImg(String picStrInMsg) {
        Bitmap bitmap = null;

        byte[] imgByte = null;
        InputStream input = null;
        try{
            imgByte = Base64.decode(picStrInMsg, Base64.DEFAULT);
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize = 8;
            input = new ByteArrayInputStream(imgByte);
            SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(input, null, options));
            bitmap = (Bitmap)softRef.get();;
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(imgByte!=null){
                imgByte = null;
            }

            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
// TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }



    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }


 public static void copyFilesFromAssets(Context myContext, String ASSETS_NAME, String savePath, String saveName) {
        String filename = savePath + "/" + saveName;
        File dir = new File(savePath);
        // 如果目录不中存在，创建这个目录
        if (!dir.exists())
            dir.mkdir();
        try {
            if (!(new File(filename)).exists()) {
                InputStream is = myContext.getResources().getAssets().open(ASSETS_NAME);
                FileOutputStream fos = new FileOutputStream(filename);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                    }
                fos.close();
                is.close();
                }
            } catch (Exception e) {
            e.printStackTrace();
            }
        }
//public void testCopy(Context context) {
      //  String path=context.getFilesDir().getAbsolutePath();
      //  String name="test.txt";
      //  CopyFileFromAssets.copy(context, name, path, name);
      //  }
//}




}
