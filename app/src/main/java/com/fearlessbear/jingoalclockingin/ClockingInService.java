package com.fearlessbear.jingoalclockingin;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by BigFaceBear on 2018.03.29
 */

public class ClockingInService extends AccessibilityService {
    private static final String TAG = "ClockingInService";

    private static LinkedList<Operation> mClockingInOperations;

    static {
        mClockingInOperations = new LinkedList<>();
        //1.点击"应用"，跳转到应用Tab
        mClockingInOperations.add(new Operation("应用", AccessibilityNodeInfo.ACTION_CLICK));
        //2.点击考勤，跳转到考勤页面
        mClockingInOperations.add(new Operation("考勤", AccessibilityNodeInfo.ACTION_CLICK));
        //3.点击签到或签退
        mClockingInOperations.add(new Operation("签到", AccessibilityNodeInfo.ACTION_CLICK));
    }


    private OperationManager mOperationManager;
    private LinkedList<Operation> mOperations;

    private int mIndex = 0;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        mOperationManager = OperationManager.getInstance();

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        boolean clockingIn = mOperationManager.clockingIn();
        Log.d(TAG, "clockingIn is " + clockingIn);

        if (clockingIn) {
            switch (event.getPackageName().toString()) {
                case Constants.PACKAGE_NAME_JIN_GOAL:
                    jinGoalGo(event);
                    break;

            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    private void jinGoalGo(AccessibilityEvent event) {
        Log.d(TAG, "enter method jinGoalGo() ");
        Log.d(TAG, "event is " + event.toString());
        Log.d(TAG, "class is " + event.getClassName());

        if (event.getAction() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

            if (isTargetClass(event, "MainFrame") && mIndex == 0) {
                //1.打开今目标首页，点击"应用"TAB

                Log.d(TAG, "jinGoalGo: enter index 0");

                List<AccessibilityNodeInfo> nodes = getNodeInfosByText(event, "应用");
                if (nodes.size() != 0) {
                    AccessibilityNodeInfo node = nodes.get(0);
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    mIndex++;
                    Log.d(TAG, "jinGoalGo: execute index 0");
                }

            } else if (isTargetClass(event, "MainFrame") && mIndex == 1) {
                //2.跳转至"应用"TAB后，点击考勤

                Log.d(TAG, "jinGoalGo: enter index 1");

                List<AccessibilityNodeInfo> nodes = getNodeInfosByText(event, "考勤");
                if (nodes.size() != 0) {
                    AccessibilityNodeInfo node = nodes.get(0);
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    mIndex++;
                    Log.d(TAG, "jinGoalGo: execute index 1");
                }
            } else if (isTargetClass(event, "AttendanceMainPanelActivity") && mIndex == 2) {
                //3.点击签到

                Log.d(TAG, "jinGoalGo: enter index 2");

                List<AccessibilityNodeInfo> nodes = getNodeInfosByText(event, "签到");
                if (nodes.size() != 0) {
                    AccessibilityNodeInfo node = nodes.get(0);

                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    mOperationManager.setOperate(false);
                    mIndex = 0;
                    Log.d(TAG, "jinGoalGo: execute index 2");
                }

            }
        }
    }

    private boolean isTargetClass(AccessibilityEvent event, String target) {
        String clazzName = event.getClassName().toString();
        return clazzName.endsWith(target);
    }

    private List<AccessibilityNodeInfo> getNodeInfosByText(AccessibilityEvent event, String text) {
        AccessibilityNodeInfo source = event.getSource();
        return source.findAccessibilityNodeInfosByText(text);
    }

    private List<AccessibilityNodeInfo> getNodeInfosById(AccessibilityEvent event, String id) {
        AccessibilityNodeInfo source = event.getSource();
        return source.findAccessibilityNodeInfosByViewId(id);
    }


    private static class Operation {
        String text;
        int action;

        public Operation(String text, int action) {
            this.text = text;
            this.action = action;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }
    }
}
