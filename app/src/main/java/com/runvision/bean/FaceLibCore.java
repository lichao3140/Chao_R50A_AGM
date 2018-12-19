package com.runvision.bean;


import android.content.Context;
import android.util.Log;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.LivenessInfo;
import com.runvision.core.Const;

import java.util.List;

/**
 * Created by Administrator on 2018/8/3.
 */
public class FaceLibCore {
    private String TAG = this.getClass().getSimpleName();
    FaceEngine faceEngine = new FaceEngine();
    private byte[] a = new byte[1];
    private byte[] b = new byte[1];


    //活体增加 | FaceEngine.ASF_LIVENESS
    public int initLib(Context context) {
        int activeCode = faceEngine.active(context, Const.APP_ID, Const.SDK_KEY);
        if((activeCode == ErrorInfo.MOK) || (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED)) {
            int afCode = faceEngine.init(context, FaceEngine.ASF_DETECT_MODE_IMAGE, FaceEngine.ASF_OP_0_HIGHER_EXT,
                    16, 20, FaceEngine.ASF_FACE_RECOGNITION|FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_GENDER);
            if (afCode != ErrorInfo.MOK) {
                Log.e(TAG, "引擎初始化失败,错误码:" + afCode);
                faceEngine = null;
                return afCode;
            }
        }else
        {
            Log.e(TAG, "激活引擎失败,错误码:" + activeCode);
            faceEngine = null;
            return activeCode;
        }


        return 0;
    }

    /**
     * 人脸定位跟踪接口
     *
     * @param des    nv21的相机流  需要流的方向为0度
     * @param  w     流的宽度
     * @param  h    流的高度
     * @param result 人脸列表，传入后赋值
     * @return 0表示成功  其他表示失败
     */
        public int FaceDetection(byte[] des, int w, int h, List<FaceInfo> result) {
        int ret = -1;
        if (faceEngine == null) {
            return ret;
        }
        synchronized (a) {
             ret = faceEngine.detectFaces(des, w, h, FaceEngine.CP_PAF_NV21, result);
        }
        return ret;
    }


    /**
     * 活体检测
     *
     * @param data    nv21的相机流  需要流的方向为0度
     * @param mWidth      流的宽度
     * @param mHeight      流的高度
     * @param faceInfos   人脸列表，传入后赋值
     * @return true表示活体  其他表示非活体
     */
    public boolean Livingthing(byte[] data, int mWidth, int mHeight,List<FaceInfo> faceInfos, List<LivenessInfo> livenessInfoList) {

        //活体检测(目前只支持单人脸，且无论有无人脸都需调用)
         Boolean Living_thing=false;
       //  List<LivenessInfo> livenessInfoList = new ArrayList<>();

       int code = faceEngine.process(data, mWidth, mHeight, FaceEngine.CP_PAF_NV21, faceInfos,  FaceEngine.ASF_LIVENESS);
        if (code != ErrorInfo.MOK) {
            Log.e(TAG, "process返回失败,错误码:" + code);
        }

        if (code == ErrorInfo.MOK) {
            if (faceInfos.size() == 0) {
                Log.i("Gavin", "无人脸");
                Const.Livenum=0;
            }
            code = faceEngine.getLiveness(livenessInfoList);
            Log.i(TAG, "getLivenessScore: liveness " + code);
            if (code == 0) {
                for(int i=0;i<faceInfos.size();i++)
                {
                    if(livenessInfoList.get(i).getLiveness()==1)
                    {
                        if(Const.Livenum>5)
                        {
                            Living_thing = false;
                            Const.islive++;
                            Log.i("Gavin", "cscscscscscs"+Const.Livenum);
                        }else
                            {
                            Log.i("Gavin", "活体");
                            Living_thing = true;
                            Const.Livenum = 0;
                        }

                    }else if(livenessInfoList.get(i).getLiveness()==0)
                    {
                        Living_thing = false;
                        Log.i("Gavin", "未知或者非活体");
                        Const.Livenum++;
                        Const.islive=0;
                    }
                }
               // Log.i("Gavin", "活体");
            }  else {
               // Log.i("Gavin", "未知或者非活体");
                Living_thing = false;
            }

        }
        return Living_thing;
    }


    /**
     * 特征提取
     *
     * @param des 图像数据
     * @param w   图像的宽度
     * @param h   图像的高度
     * @param faceInfos   图像的颜色空间格式，支持NV21(CP_PAF_NV21)、BGR24(CP_PAF_BGR24)
     * @return
     */
    public int FaceFeatureExtract(byte[] des, int w, int h, FaceInfo faceInfos,FaceFeature faceFeature) {
        int ret = -1;
        if (faceEngine == null) {
            return ret;
        }
        synchronized (b) {
            ret = faceEngine.extractFaceFeature(des, w, h, FaceEngine.CP_PAF_NV21, faceInfos, faceFeature);
        }
        return ret;
    }

    /**
     * 分数比对
     *
     * @param face1
     * @param face2
     * @param score
     * @return
     */
    public int FacePairMatching(FaceFeature face1, FaceFeature face2, FaceSimilar score) {
        int ret = -1;
        if (faceEngine == null) {
            return ret;
        }

        synchronized (b) {
            ret = faceEngine.compareFaceFeature(face1, face2, score);
        }
        return ret;
    }


    /**
     * 销毁人脸引擎
     */
}





//}
