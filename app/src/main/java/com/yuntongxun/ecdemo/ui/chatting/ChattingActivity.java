/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.yuntongxun.ecdemo.ui.chatting;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.utils.CrashHandler;
import com.yuntongxun.ecdemo.common.utils.LogUtil;
import com.yuntongxun.ecdemo.ui.ECFragmentActivity;

/**
 * @author 容联•云通讯
 * @date 2014-12-9
 * @version 4.0
 */
public class ChattingActivity extends ECFragmentActivity implements ChattingFragment.OnChattingAttachListener{

    private static final String TAG = "ECSDK_Demo.ChattingActivity";
    public ChattingFragment mChattingFragment;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtil.d(TAG , "chatting ui dispatch key event :" + event);
        if(mChattingFragment != null && mChattingFragment.onKeyDown(event.getKeyCode() , event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CrashHandler.getInstance().setContext(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onCreate");
        super.onCreate(null);
        getWindow().setFormat(PixelFormat.TRANSPARENT);
        String recipients = getIntent().getStringExtra(ChattingFragment.RECIPIENTS);
        if (recipients == null) {
            finish();
            LogUtil.e(TAG, "recipients is null !!");
            return;
        }
        setContentView(R.layout.chattingui_activity_container);
        mChattingFragment = new ChattingFragment();
        Bundle bundle = getIntent().getExtras();
        bundle.putBoolean(ChattingFragment.FROM_CHATTING_ACTIVITY, true);
        mChattingFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.ccp_root_view , mChattingFragment).commit();
        onActivityCreate();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d(TAG, "chatting ui on key down, " + keyCode + ", " + event);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        LogUtil.d(TAG , "chatting ui on key up");
        return super.onKeyUp(keyCode, event);
    }

    public boolean isPeerChat() {
        if(mChattingFragment != null) {
            return mChattingFragment.isPeerChat();
        }
        return false;
    }

    @Override
    public void onChattingAttach() {
        LogUtil.d(TAG , "onChattingAttach");
    }
}