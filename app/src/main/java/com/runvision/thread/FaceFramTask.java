package com.runvision.thread;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.arcsoft.face.LivenessInfo;
import com.runvision.bean.AppData;
import com.runvision.bean.FaceInfoss;
import com.runvision.bean.ImageStack;
import com.runvision.core.Const;
import com.runvision.g69a_sn.MyApplication;
import com.runvision.myview.MyCameraSuf;
import com.runvision.utils.CameraHelp;
import com.runvision.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2018/5/29.
 */

public class FaceFramTask extends AsyncTask<Void, Rect, Void> {

    private String TAG = "FaceFramTask";
    private Handler handler;
    public boolean isRuning = true;
    private ImageStack imageStack;
    private MyCameraSuf mCameraView;
    private List<com.arcsoft.face.FaceInfo> result = new ArrayList<>();
    private List<LivenessInfo> livenessInfoList = new ArrayList<>();

   // List<AFT_FSDKFace> result = new ArrayList<>();
    byte[] des;
    private boolean flag=false;
    public static boolean faceflag=false;

    public void setRuning(boolean runing) {
        isRuning = runing;
    }

    public FaceFramTask(Handler handler, MyCameraSuf mCameraView) {
        this.handler = handler;
        this.mCameraView = mCameraView;
        imageStack = mCameraView.getImgStack();
    }

    public FaceFramTask(MyCameraSuf mCameraView) {
        flag=true;
        this.mCameraView = mCameraView;
        imageStack = mCameraView.getImgStack();
    }
    @Override
    protected Void doInBackground(Void... params) {
        while (isRuning) {
            des= CameraHelp.rotateCamera(imageStack.pullImageInfo().getData(), 640, 480, 270);
            MyApplication.mFaceLibCore.FaceDetection(des, 480, 640, result);
            if (result.size() != 0) {
                Log.i(TAG, result.get(0).getRect().left + "," + result.get(0).getRect().top);
                publishProgress(result.get(0).getRect());

                Boolean live=true;
                if(SPUtil.getBoolean(Const.KEY_ISOPENLIVE, Const.OPEN_LIVE)==true) {
                    live = MyApplication.mFaceLibCore.Livingthing(des, 480, 640, result,livenessInfoList);
                }
                faceflag=true;
                if((!flag)&&(live)&&(SPUtil.getBoolean(Const.KEY_ISOPEN1_N, Const.OPEN_1_N)==true)) {
                   // AppData.getAppData().SetNFaceBmp(CameraHelp.getFaceImgByInfraredJpg(result.get(0).getRect().left, result.get(0).getRect().top, result.get(0).getRect().right, result.get(0).getRect().bottom, CameraHelp.getBitMap(des)));
                    FaceInfoss info = new FaceInfoss(des,result.get(0));
                    Message msg = new Message();
                    msg.obj = info;
                    msg.what = Const.MSG_FACE;
                    handler.sendMessage(msg);

                }


            } else {
                Log.i(TAG, "无人脸");
                if (imageStack!=null)
                {
                    imageStack.clearAll();
                   // imageStack=null;
                }
                faceflag=false;
                publishProgress(new Rect(0, 0, 0, 0));
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Rect... values) {
        super.onProgressUpdate(values);
        mCameraView.setFacePamaer(values[0]);
        if (mCameraView.getCamerType() == 1 && result != null && Const.is_regFace && flag) {
                Bitmap map = CameraHelp.getBitMap(des);
                AppData.getAppData().setFaceBmp(CameraHelp.getFaceImgByInfraredJpg(values[0].left, values[0].top, values[0].right, values[0].bottom, map));
                AppData.getAppData().setFlag(Const.REG_FACE);
                Const.is_regFace=false;
           // }
        }

    }
}
