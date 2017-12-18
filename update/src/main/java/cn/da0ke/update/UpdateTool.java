package cn.da0ke.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;

/**
 * Created by da0ke on 2017/11/29
 */

public class UpdateTool {
    private Activity activity;
    private String appName;

    public UpdateTool(Activity activity, String appName) {
        this.activity = activity;
        this.appName = appName;
    }

    public void doUpdate() {
        doBaiDuUpdate();
    }

    private void doBaiDuUpdate() {
        BDAutoUpdateSDK.cpUpdateCheck(activity, new MyCPCheckUpdateCallback(),false);
    }
    private class MyCPCheckUpdateCallback implements com.baidu.autoupdatesdk.CPCheckUpdateCallback {

        @Override
        public void onCheckUpdateCallback(com.baidu.autoupdatesdk.AppUpdateInfo info, com.baidu.autoupdatesdk.AppUpdateInfoForInstall infoForInstall) {
            if(infoForInstall != null && !android.text.TextUtils.isEmpty(infoForInstall.getInstallPath())) {
                final com.baidu.autoupdatesdk.AppUpdateInfoForInstall temp_infoForInstall = infoForInstall;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.bdp_update_dialog_style);
                builder.setTitle(appName);
                builder.setMessage("有新版本了");
                builder.setPositiveButton("立即更新",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                com.baidu.autoupdatesdk.BDAutoUpdateSDK.cpUpdateInstall(activity.getApplicationContext(), temp_infoForInstall.getInstallPath());
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton("以后再说",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();

            } else if(info != null) {
                final com.baidu.autoupdatesdk.AppUpdateInfo temp_info = info;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.bdp_update_dialog_style);
                builder.setTitle(appName);
                builder.setMessage("有新版本了");
                builder.setPositiveButton("立即更新",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                com.baidu.autoupdatesdk.BDAutoUpdateSDK.cpUpdateDownload(activity, temp_info, new UpdateDownloadCallback());
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton("以后再说",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();

            } else {
                Toast.makeText(activity,"已是最新版本",Toast.LENGTH_LONG).show();
            }
        }
    }
    private class UpdateDownloadCallback implements com.baidu.autoupdatesdk.CPUpdateDownloadCallback {
        ProgressDialog progressDialog;

        @Override
        public void onDownloadComplete(String apkPath) {
            progressDialog.dismiss();
            com.baidu.autoupdatesdk.BDAutoUpdateSDK.cpUpdateInstall(activity.getApplicationContext(), apkPath);
        }

        @Override
        public void onStart() {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setTitle("正在下载");
                progressDialog.setIndeterminate(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.incrementProgressBy(1);
                progressDialog.setProgress(0);
                progressDialog.setCancelable(false);
                progressDialog.setProgressNumberFormat("%1d KB/%2d KB");
            } else {
                progressDialog.setProgress(0);
            }
            progressDialog.show();
        }

        @Override
        public void onPercent(int percent, long rcvLen, long fileSize) {
            if(progressDialog.getMax() == 100) {
                progressDialog.setMax((int) (fileSize/1024));
            }
            int increment = (int) (fileSize/1024/100);
            progressDialog.incrementProgressBy(increment);
            progressDialog.setProgress(percent * increment);
        }

        @Override
        public void onFail(Throwable error, String content) {
                Toast.makeText(activity,"检查更新失败",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStop() {
        }

    }
}
