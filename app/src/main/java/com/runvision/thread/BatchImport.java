package com.runvision.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.runvision.bean.AppData;
import com.runvision.core.Const;
import com.runvision.db.User;
import com.runvision.g69a_sn.MyApplication;
import com.runvision.utils.CameraHelp;
import com.runvision.utils.DateTimeUtils;
import com.runvision.utils.FileUtils;
import com.runvision.utils.IDUtils;
import com.runvision.utils.SendData;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import com.face.sv.FaceInfo;

/**
 * Created by Administrator on 2018/7/23.
 */

public class BatchImport implements Runnable {

    private List<File> mList;

    private Handler handler;

    private int messageID;

    private int num;

    private SocketThread socketThread;

    public BatchImport(SocketThread socketThread,List<File> mList, Handler handler, int messageID) {
        this.mList = mList;
        this.handler = handler;
        this.messageID = messageID;
        this.num = mList.size();
        this.socketThread=socketThread;
    }

    @Override
    public void run() {
        for (int i = 1; i <= num; i++) {

            File file = mList.get(i - 1);
            Bitmap old_bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), FileUtils.getBitmapOption(1));
            Bitmap bitmap =CameraHelp.alignBitmapForNv21(old_bitmap);//裁剪
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Log.i("Gavin_1203","w:"+old_bitmap.getWidth()+"h:"+old_bitmap.getHeight());


            byte[] nv21 = CameraHelp.bitmapToNv21(bitmap,w, h);//转nv21

            String time = DateTimeUtils.parseDataTimeToFormatString(new Date());


            String userName = file.getName().substring(0, file.getName().indexOf("."));
            String[] strs = userName.split("&");
            if (strs.length != 6) {
                continue;
            }
            //保存图片
            //生成随机图片ID
            String imageID = IDUtils.genImageName();
            FileUtils.saveFile(bitmap, imageID, "FaceTemplate");
            User user = new User(strs[0], strs[1], strs[2], Integer.parseInt(strs[3]), strs[4], strs[5], imageID,DateTimeUtils.getTime());
            int id = MyApplication.faceProvider.addUserOutId(user);

            if (nv21 == null) {
                MyApplication.faceProvider.deleteUserById(id);
                //FileUtils.saveFile(bitmap, userName, "errorImage");
                FileUtils.deleteTempter(imageID, "FaceTemplate");
                sendMsg(i);
//                publishProgress();
                System.out.println("RBG==NULL");
                continue;
            }


            List<FaceInfo> result = new ArrayList<FaceInfo>();
            MyApplication.mFaceLibCore.FaceDetection(nv21, w, h, result);
            if (result.size() != 0) {
                FaceFeature faceFeature = new FaceFeature();
                int ret = MyApplication.mFaceLibCore.FaceFeatureExtract(nv21, w, h,result.get(0), faceFeature);
                if (ret == 0) {
                    CameraHelp.saveFile(Const.Templatepath, imageID + ".data", faceFeature.getFeatureData());
                    CameraHelp.saveImgToDisk(Const.ImagePath, imageID + ".jpg", old_bitmap);
                    FileUtils.saveFile(old_bitmap, imageID, "FaceTemplate");
                    MyApplication.mList.put(imageID, faceFeature.getFeatureData());
                    System.out.println("存入模板库");
                    Log.i("Gavin", "存入模板库:");

                    file.delete();
                    sendMsg(i);
                } else {
                    //提取特征失败
                    AppData.getAppData().setErroemsg("提取特征失败");
                    AppData.getAppData().setErrormsgidnum(strs[5]);
                    AppData.getAppData().setErrormsgname(strs[0]);
                    AppData.getAppData().setErrormsgpicname(userName);

                    if (socketThread != null) {
                        try {
                            SendData.VMSErrorMsg(socketThread);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        AppData.getAppData().clean();
                    }

                    System.out.println("注册模版error");
                    Log.i("Gavin", "注册模版error:" );
                    MyApplication.faceProvider.deleteUserById(id);
                    // error++;
                    FileUtils.deleteTempter(imageID, "FaceTemplate");
                    sendMsg(i);
                    continue;
                }
            } else {
                //无人脸
                AppData.getAppData().setErroemsg("图片无人脸");
                AppData.getAppData().setErrormsgidnum(strs[5]);
                AppData.getAppData().setErrormsgname(strs[0]);
                AppData.getAppData().setErrormsgpicname(userName);
                if (socketThread != null) {
                    try {
                        SendData.VMSErrorMsg(socketThread);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    AppData.getAppData().clean();
                }
                MyApplication.faceProvider.deleteUserById(id);
                FileUtils.deleteTempter(imageID, "FaceTemplate");
                System.out.println("人脸定位失败");
                Log.i("Gavin", "人脸定位失败:" );
                sendMsg(i);
                continue;
            }

        }

    }

    private void sendMsg(int i) {

        Message msg = new Message();
        msg.what = messageID;
        msg.obj = i;
        handler.sendMessage(msg);
    }
}
