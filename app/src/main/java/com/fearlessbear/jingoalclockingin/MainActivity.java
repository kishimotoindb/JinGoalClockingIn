package com.fearlessbear.jingoalclockingin;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;


/*
 * 架构：
 * 1.Service监测时间和地点
 * 2.BroadcastReceiver接收指令开启今目标
 * 3.AccessibilityService完成剩余的操作
 * 4.Activity配置打卡时间和地点
 *
 * 使用Service监测时间和地点，在时间符合并且地点符合的情况下，发送开启今目标的广播。广播接收者收到
 * 广播之后开启今目标，然后AccessibilityService完成打卡流程。
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ClockingInService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openJinGoal();
            }
        });


        //开启今目标
        openJinGoal();
    }

    private void openJinGoal() {

        if (isJinGoalInstalled()) {
            Log.d(TAG, "openJinGoal: ");
            PackageManager pm = getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage("com.jingoal.mobile.android.jingoal");
            startActivity(launchIntent);

            //开始执行签到
            OperationManager.getInstance().setOperate(true);
        } else {
            Toast.makeText(this, "今目标未安装，请安装应用后再使用胖熊爱心助手。胖熊退下了！", Toast.LENGTH_LONG).show();
            System.exit(0);
        }
    }

    private boolean isJinGoalInstalled() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (PackageInfo p : installedPackages) {
            if (p.packageName.equals("com.jingoal.mobile.android.jingoal")) {
                return true;
            }
        }
        return false;
    }
}
