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

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        mOperationManager = OperationManager.getInstance();

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        boolean clockingIn = mOperationManager.clockingIn();
        Log.d(TAG, "是否打卡？doAction is " + clockingIn);

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
        Log.d(TAG, "-----------------------------开始处理今目标产生的辅助事件---------------------------------------------- ");
        Log.d(TAG, "辅助事件信息： " + event.toString());
        Log.d(TAG, "产生当前辅助事件的类文件信息： " + event.getClassName());

        if (mOperationManager.getIndex() == 0) {
            //1.打开今目标首页，点击"应用"TAB

            Log.d(TAG, "进入第一步，查找应用按钮");

            if (doAction("应用", true, AccessibilityNodeInfo.ACTION_CLICK, true)) {
                mOperationManager.setIndex(mOperationManager.getIndex() + 1);
            }

        } else if (mOperationManager.getIndex() == 1) {
            //2.跳转至"应用"TAB后，点击考勤

            Log.d(TAG, "进入第二步，查找考勤按钮");

            if (doAction("考勤", true, AccessibilityNodeInfo.ACTION_CLICK, true)) {
                mOperationManager.setIndex(mOperationManager.getIndex() + 1);
            }
        } else if (mOperationManager.getIndex() == 2) {
            //3.点击签到

            Log.d(TAG, "进入第三步，查找签到按钮");

            if (doAction("com.jingoal.mobile.android.jingoal:id/rl_atte_main_sign_vp", false, AccessibilityNodeInfo.ACTION_CLICK, false)) {
                mOperationManager.setOperate(false);
                mOperationManager.clear();
            }
        }

        Log.d(TAG, "-----------------------------处理今目标产生的辅助事件结束---------------------------------------------- ");
    }

    private boolean doAction(String text, boolean byText, int action, boolean onParent) {
        AccessibilityNodeInfo root = getRootInActiveWindow();

        if (root != null) {

            List<AccessibilityNodeInfo> nodes;
            if (byText) {
                nodes = root.findAccessibilityNodeInfosByText(text);
            } else {
                nodes = root.findAccessibilityNodeInfosByViewId(text);
            }
            Log.d(TAG, "找到了 " + text + " 节点，节点数量是 " + nodes.size() + "个");

            if (nodes.size() != 0) {
                AccessibilityNodeInfo node = nodes.get(0);
                Log.d(TAG, "第一个节点包含的信息： " + node.toString());

                if (onParent) {
                    AccessibilityNodeInfo parent = node.getParent();
                    if (parent == null) {
                        return false;
                    } else {
                        parent.performAction(action);
                    }
                } else {
                    node.performAction(action);
                }
                Log.d(TAG, "节点成功完成相应Action的操作");

                for (AccessibilityNodeInfo nodeInfo : nodes) {
                    nodeInfo.recycle();
                }
                return true;
            }
        }

        return false;
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
