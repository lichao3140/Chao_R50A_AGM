package com.runvision.g69a_sn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UpdateActivity extends Activity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.main_iv_icon_1)
    ImageView iv_icon_1;
    @BindView(R.id.main_iv_icon_2)
    ImageView iv_icon_2;
    @BindView(R.id.main_tv_name)
    TextView tv_name;
    @BindView(R.id.main_tv_version)
    TextView tv_version;

    private Drawable drawable1;
    private Drawable drawable2;
    private String appName;
    private String version;

    private Disposable disposable;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_update);
      //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideBottomUIMenu();
        ButterKnife.bind(this);
        Observable.timer(2, TimeUnit.SECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> { initView(); });
        /* 定时器 定时查找要安装的软件 */
        disposable = Observable.interval(10, TimeUnit.SECONDS, Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> initView());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭定时器
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @SuppressLint("NewApi")
    protected void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            // for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * 获APK包的信息:版本号,名称,图标 等..
     *
     * @param usbPath APK包的绝对路径
     */
    private void apkInfo(String usbPath) {
        Log.i(TAG, "apkInfo: ----");
        PackageManager pm = getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(usbPath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
            appInfo.sourceDir = usbPath;
            appInfo.publicSourceDir = usbPath;
            // 得到应用名
            String appName = pm.getApplicationLabel(appInfo).toString();
            this.appName = appName;
            // 得到包名
            String packageName = appInfo.packageName;
            // 得到版本信息
            String version = pkgInfo.versionName;
            this.version = version;
            /* icon1和icon2其实是一样的 */
            Drawable icon1 = pm.getApplicationIcon(appInfo);// 得到图标信息
            Drawable icon2 = appInfo.loadIcon(pm);
            drawable1 = icon1;
            drawable2 = icon2;
            String pkgInfoStr = String.format("PackageName:%s, Vesion: %s, AppName: %s", packageName, version, appName);
            Log.i(TAG, String.format("PkgInfo: %s", pkgInfoStr));
        } else {
            Log.e(TAG, "apkInfo: null");
        }

    }

    private void initView() throws Exception {
        String apkPath = "/mnt/usb_storage/USB_DISK1/udisk0";//本地APK的根目录,不同的屏路径不同
        Log.d(TAG, "initView: -当前版本号-" + getVersionName());
        if (!(new File(apkPath).exists())) {
            Log.e(TAG, "initView: ----没找到该文件夹");
            Intent intent = new Intent((Activity) this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String name = getApkName(apkPath);//获取U盘里的APK名称
        if (name != null) {
            apkPath = apkPath + "/" + name;//APK的绝对路径
            Log.d(TAG, "initView:APK路径: " + apkPath);
            apkInfo(apkPath);
//            iv_icon_1.setImageDrawable(drawable1);
 //           iv_icon_2.setImageDrawable(drawable2);
//            tv_name.setText(appName);
//            tv_version.setText(version);
            updataApk(apkPath);
        } else {
            Log.e(TAG, "initView: --没找到apk文件");
            Intent intent = new Intent((Activity) this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 判断是否需要更新APK
     *
     * @param apkPath apk绝对路径
     * @throws Exception
     */
    private void updataApk(String apkPath) throws Exception {
        if (appName.equals(getResources().getString(R.string.app_name))) {
            //比较版本号的大小~
            if (Integer.valueOf(getNumber(version)) > Integer.valueOf(getNumber(getVersionName()))) {
                //关闭定时器
                if (disposable != null && !disposable.isDisposed()) {
                    disposable.dispose();
                }
                File apkfile = new File(apkPath);
                //会根据用户的数据类型打开android系统相应的Activity。
                          /*  Intent intent = new Intent(Intent.ACTION_VIEW);
                            //设置intent的数据类型是应用程序application
                            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
                           // intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
                            //为这个新apk开启一个新的activity栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //开始安装
                            startActivity(intent);
                            //关闭旧版本的应用程序的进程
                            android.os.Process.killProcess(android.os.Process.myPid());
                            uninstallSlient();*/

                // installApk(apkfile);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
                this.startActivity(intent);
              //  installApk(apkPath);
            } else {
                Intent intent = new Intent((Activity) this, MainActivity.class);
                startActivity(intent);
                finish();
                Log.e(TAG, "updataApk: ---APK文件的版本是过低");
                Toast.makeText(this, "这个已经是最新版本了", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent intent = new Intent((Activity) this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.e(TAG, "updataApk: ---这个APK文件不能用于本地更新");
            Toast.makeText(this, "没有可更新的软件", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 安装APK
     *
     * @param apkPath U盘APK文件绝对路径
     */
    private void installApk(String apkPath) throws Exception {
        //关闭定时器
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        StringBuffer sb = new StringBuffer();
        sb.append("当前版本：");
        sb.append(getVersionName());
        sb.append("\n要更新的版本:");
        sb.append(version);
        sb.append("\n是否更新？");
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("软件更新")
                .setMessage(sb.toString())
                // 设置内容
                .setPositiveButton("更新", // 设置确定按钮
                        (dialog1, which) -> {
                            //apk文件的本地路径
                            File apkfile = new File(apkPath);
                            //会根据用户的数据类型打开android系统相应的Activity。
                          /*  Intent intent = new Intent(Intent.ACTION_VIEW);
                            //设置intent的数据类型是应用程序application
                            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
                           // intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
                            //为这个新apk开启一个新的activity栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //开始安装
                            startActivity(intent);
                            //关闭旧版本的应用程序的进程
                            android.os.Process.killProcess(android.os.Process.myPid());
                            uninstallSlient();*/

                            // installApk(apkfile);

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
                            this.startActivity(intent);
                        })
                .setNegativeButton("暂不更新",
                        (dialog12, whichButton) -> {
                            // 点击"取消"按钮之后退出程序
//                                finish();
                            dialog12.dismiss();
                        }).create();// 创建

        dialog.show();
    }

    /**
     * 提取String中的数字
     *
     * @param s 字符串
     * @return
     */
    private String getNumber(String s) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);
        return m.replaceAll("").trim();
    }

    /**
     * 检查扩展名，判断是否是.apk文件
     *
     * @param fName 文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    private boolean checkIsApkFile(String fName) {
        boolean isApkFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("apk")) {
            isApkFile = true;
        } else {
            isApkFile = false;
        }
        return isApkFile;
    }

    /**
     * 获取APK文件的名字
     *
     * @param usbPath APK在U盘里的根目录
     */
    private String getApkName(String usbPath) {
        ArrayList<String> datas = new ArrayList<>();
        File file = new File(usbPath);
        if (file.exists()) {//判断是否有指定文件夹
            // 得到该路径文件夹下所有的文件
            File[] files = file.listFiles();
            // 将所有的文件存入ArrayList中,并过滤所有apk格式的文件
            if (files != null) {
                for (File file1 : files) {
                    //判断是否是要更新的apk文件  1.是否是apk文件 2.更新apk前半部分名称是否相同 (UpdateApk)
                    if (checkIsApkFile(file1.getPath()) &&
                            file1.getName().substring(0, 8).equals("G69A_AGM")) {
                        datas.add(file1.getName());//添加到本地数组中
                        Log.i(TAG, "ssss::: " + file1.getName());
                    }
                }
            }
            if (datas.size() < 1) {
                Log.e(TAG, "getApkName: 文件夹里为null");
                return null;
            } else {
                Log.d(TAG, "getApkName: ---有这个安装包:" + datas.get(datas.size() - 1));
                return datas.get(datas.size() - 1);//返回当前数组中最后一个APK文件名
            }
        } else {
            Toast.makeText(this, "没有知道当前文件夹~", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    /**
     * 获取版本号
     *
     * @return
     * @throws Exception
     */
    public String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionName;
    }


    // 闈欓粯鍗歌浇

    private void uninstallSlient() {
//		Log.i("Gavin", "uninstallSlient:" + mActivity.getPackageName());
        String cmd = "pm uninstall " + this.getPackageName();

        Process process = null;

        DataOutputStream os = null;

        BufferedReader successResult = null;

        BufferedReader errorResult = null;

        StringBuilder successMsg = null;

        StringBuilder errorMsg = null;

        try {

            // 鍗歌浇涔熼渶瑕乺oot鏉冮檺

            process = Runtime.getRuntime().exec("su");

            os = new DataOutputStream(process.getOutputStream());

            os.write(cmd.getBytes());

            os.writeBytes("\n");

            os.writeBytes("exit\n");

            os.flush();

            // 鎵ц鍛戒护

            process.waitFor();

            // 鑾峰彇杩斿洖缁撴灉

            successMsg = new StringBuilder();

            errorMsg = new StringBuilder();

            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));

            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String s;

            while ((s = successResult.readLine()) != null) {

                successMsg.append(s);

            }

            while ((s = errorResult.readLine()) != null) {

                errorMsg.append(s);

            }
        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {

                if (os != null) {

                    os.close();

                }

                if (process != null) {

                    process.destroy();

                }

                if (successResult != null) {

                    successResult.close();

                }

                if (errorResult != null) {

                    errorResult.close();

                }

            } catch (Exception e) {

                e.printStackTrace();

            }

        }
    }


    public void installApk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean b = this.getPackageManager().canRequestPackageInstalls();
            if (b) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(this.getApplicationContext(),
                        BuildConfig.APPLICATION_ID+".fileProvider", apkFile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                this.startActivity(intent);
            } /*else {
                //请求安装未知应用来源的权限
                ConfirmDialog confirmDialog =new ConfirmDialog(context);
                confirmDialog.setStyle("安装权限","Android8.0安装应用需要打开未\n知来源权限，请去设置中开启权限",
                        "去设置","取消");
                confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
                    @Override
                    public void doConfirm() {
                        String[] mPermissionList = new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES};
                        ActivityCompat.requestPermissions(context, mPermissionList, 2);
                    }
                });
                confirmDialog.show();
            }*/
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(this.getApplicationContext(),
                        BuildConfig.APPLICATION_ID+".fileProvider", apkFile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                this.startActivity(intent);
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
            }
        }

    }
}
