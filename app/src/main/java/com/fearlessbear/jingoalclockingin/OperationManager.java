package com.fearlessbear.jingoalclockingin;

import java.util.Calendar;

/**
 * Created by BigFaceBear on 2018.03.30
 */

public class OperationManager {

    private boolean mOperate = false;
    private int mIndex;

    private Calendar mCalendar;

    private OperationManager() {
        mCalendar = Calendar.getInstance();
    }

    private static class ManagerHolder {
        private static final OperationManager sInstance = new OperationManager();
    }

    public static OperationManager getInstance() {
        return ManagerHolder.sInstance;
    }

    public boolean clockingIn() {
        return mOperate && isInTime();
    }

    public void setOperate(boolean b) {
        mOperate = b;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int i) {
        mIndex = i;
    }

    public void clear() {
        mIndex = 0;
    }

    private boolean isInTime() {
        // TODO: 30/03/2018 reset
        return true;

//        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
//        int min = mCalendar.get(Calendar.MINUTE);
//        return (hour == 8 && min < 30)
//                || (hour >= 17 && min > 30);

    }
}
