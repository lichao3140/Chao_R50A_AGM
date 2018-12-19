package com.runvision.bean;

import com.arcsoft.face.FaceInfo;

/**
 * Created by Administrator on 2018/8/3.
 */

public class FaceInfoss {
    private byte[] des;

    private FaceInfo face;

    public FaceInfoss(byte[] des, FaceInfo face) {
        this.des = des;
        this.face = face;
    }

    public byte[] getDes() {
        return des;
    }

    public void setDes(byte[] des) {
        this.des = des;
    }

    public FaceInfo getFace() {
        return face;
    }

    public void setFace(FaceInfo face) {
        this.face = face;
    }
}
