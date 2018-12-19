package com.runvision.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.runvision.g69a_sn.R;


/**
 * FileName    : PayDialog.java
 * Description :
 * 密码自定义输入框
 *
 * @author : Murphy
 * @version : 1.0
 * @Date : 2017/9/7 16:57
 **/
public class PayDialog extends Dialog implements View.OnClickListener {

   // private TextView mDialogMoney;

    private EditText mEtPwdReal;
    private ImageView mEt1, mEt2, mEt3, mEt4, mEt5, mEt6;

    public interface PasswordCallback {
        void callback(String password);
    }

    public PasswordCallback mPasswordCallback;

    public PayDialog(Context context) {
        super(context, R.style.PasswordDialog);
        initDilaog();
    }

    void initDilaog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_pay);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        findViewById(R.id.dialog_close).setOnClickListener(this);

       // mDialogMoney = (TextView) findViewById(R.id.dialog_money);

        mEtPwdReal = (EditText) findViewById(R.id.et_pwd_real);
        mEtPwdReal.addTextChangedListener(new PasswordEditChangedListener(mEtPwdReal));

        mEt1 = (ImageView) findViewById(R.id.et_pwd_1);
        mEt2 = (ImageView) findViewById(R.id.et_pwd_2);
        mEt3 = (ImageView) findViewById(R.id.et_pwd_3);
        mEt4 = (ImageView) findViewById(R.id.et_pwd_4);
        mEt5 = (ImageView) findViewById(R.id.et_pwd_5);
        mEt6 = (ImageView) findViewById(R.id.et_pwd_6);
    }

    private void requestFocus() {
        mEtPwdReal.requestFocus();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * 重制
     */
    public void clearPasswordText() {
        mEtPwdReal.setText("");
        requestFocus();
    }



    /**
     * 设置回调
     *
     * @param passwordCallback
     */
    public void setPasswordCallback(PasswordCallback passwordCallback) {
        this.mPasswordCallback = passwordCallback;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_close:
                dismiss();
                break;
        }
    }

    private class PasswordEditChangedListener implements TextWatcher {

        private EditText mEditText;

        public PasswordEditChangedListener(EditText editText) {
            this.mEditText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String ret = s.toString().trim();
            if (mEditText.getId() == R.id.et_pwd_real) {
                char[] array = ret.toCharArray();
                for (int i = 0; i < array.length; i++) {
                    //设置图片为黑色圆点
                    if (i == 0) {
                        mEt1.setImageResource(R.mipmap.icon_pwd);
                    } else if (i == 1) {
                        mEt2.setImageResource(R.mipmap.icon_pwd);
                    } else if (i == 2) {
                        mEt3.setImageResource(R.mipmap.icon_pwd);
                    } else if (i == 3) {
                        mEt4.setImageResource(R.mipmap.icon_pwd);
                    } else if (i == 4) {
                        mEt5.setImageResource(R.mipmap.icon_pwd);
                    } else if (i == 5) {
                        mEt6.setImageResource(R.mipmap.icon_pwd);
                    }
                }
                clearTextView(array.length, mEt1, mEt2, mEt3, mEt4, mEt5, mEt6);
                //自动提交
                if (array.length == 6) {
                    if (mPasswordCallback != null)
                        mPasswordCallback.callback(String.valueOf(array));
                }
            }
        }

        /**
         * 按删除键后，删除的值对应的图标还原
         *
         * @param length
         * @param editTexts
         */
        void clearTextView(int length, ImageView... editTexts) {
            for (int i = 0; i < 6; i++) {
                //比如当前密码长度为4位，当大于4的图标，就要还原成未输入情况
                if (i > length - 1) {
                    editTexts[i].setImageResource(0);
                }
            }
        }
    }
}
