package com.yuntongxun.ecdemo.ui.voip;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;

import com.yuntongxun.ecdemo.common.utils.ECNotificationManager;
import com.yuntongxun.ecdemo.common.utils.LogUtil;
import com.yuntongxun.ecdemo.ui.ECSuperActivity;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.platformtools.ECHandlerHelper;

/**
 * com.yuntongxun.ecdemo.ui.voip in ECDemo_Android
 * Created by Jorstin on 2015/7/3.
 */
public abstract class ECVoIPBaseActivity extends ECSuperActivity
        implements VoIPCallHelper.OnCallEventNotifyListener , ECCallControlUILayout.OnCallControlDelegate{

    private static final String TAG = "ECSDK_Demo.ECVoIPBaseActivity";

    /**昵称*/
    public static final String EXTRA_CALL_NAME = "con.yuntongxun.ecdemo.VoIP_CALL_NAME";
    /**通话号码*/
    public static final String EXTRA_CALL_NUMBER = "con.yuntongxun.ecdemo.VoIP_CALL_NUMBER";
    /**呼入方或者呼出方*/
    public static final String EXTRA_OUTGOING_CALL = "con.yuntongxun.ecdemo.VoIP_OUTGOING_CALL";
    /**VoIP呼叫*/
    public static final String ACTION_VOICE_CALL = "con.yuntongxun.ecdemo.intent.ACTION_VOICE_CALL";
    /**Video呼叫*/
    public static final String ACTION_VIDEO_CALL = "con.yuntongxun.ecdemo.intent.ACTION_VIDEO_CALL";

    /**通话昵称*/
    protected String mCallName;
    /**通话号码*/
    protected String mCallNumber;
    /**是否来电*/
    protected boolean mIncomingCall = false;
    /**呼叫唯一标识号*/
    protected String mCallId;
    /**VoIP呼叫类型（音视频）*/
    protected ECVoIPCallManager.CallType mCallType;

    protected ECCallHeadUILayout mCallHeaderView;
    protected ECCallControlUILayout mCallControlUIView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mIncomingCall = !(getIntent().getBooleanExtra(EXTRA_OUTGOING_CALL, false));
        mCallType = (ECVoIPCallManager.CallType) getIntent().getSerializableExtra(ECDevice.CALLTYPE);

        if(!VoIPCallHelper.mHandlerVideoCall && mCallType == ECVoIPCallManager.CallType.VIDEO) {
            VoIPCallHelper.mHandlerVideoCall = true;
            Intent mVideoIntent = new Intent(this , VideoActivity.class);
            mVideoIntent.putExtras(getIntent().getExtras());
            mVideoIntent.putExtra(VoIPCallActivity.EXTRA_OUTGOING_CALL , false);
            startActivity(mVideoIntent);
            super.finish();
            return;
        }

        if(mCallType == null) {
            mCallType = ECVoIPCallManager.CallType.VOICE;
        }

        getTopBarView().setVisibility(View.GONE);
        initProwerManager();
    }

    /**
     * 收到的VoIP通话事件通知是否与当前通话界面相符
     * @return 是否正在进行的VoIP通话
     */
    protected boolean isEqualsCall(String callId) {
        return (!TextUtils.isEmpty(callId) && callId.equals(mCallId));
    }

    /**
     * 是否需要做界面更新
     * @param callId
     * @return
     */
    protected boolean needNotify(String callId) {
        return !(isFinishing() || !isEqualsCall(callId));
    }

    @Override
    protected void onResume() {
        super.onResume();
        enterIncallMode();
        VoIPCallHelper.setOnCallEventNotifyListener(this);
        ECNotificationManager.cancelCCPNotification(ECNotificationManager.CCP_NOTIFICATOIN_ID_CALLING);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseWakeLock();
    }

    @Override
    protected void onStop() {
        super.onStop();
        VoIPCallHelper.setOnCallEventNotifyListener(null);
        if(VoIPCallHelper.isHoldingCall()) {
            ECNotificationManager.showCallingNotification(mCallType);
        }
    }

    @Override
    public void onViewAccept(ECCallControlUILayout controlPanelView, ImageButton view) {
        if(controlPanelView != null) {
            controlPanelView.setControlEnable(false);
        }
        VoIPCallHelper.acceptCall(mCallId);
    }

    @Override
    public void onViewRelease(ECCallControlUILayout controlPanelView, ImageButton view) {
        if(controlPanelView != null) {
            controlPanelView.setControlEnable(false);
        }
        VoIPCallHelper.releaseCall(mCallId);
    }

    @Override
    public void onViewReject(ECCallControlUILayout controlPanelView, ImageButton view) {
        if(controlPanelView != null) {
            controlPanelView.setControlEnable(false);
        }
        VoIPCallHelper.rejectCall(mCallId);
    }

    @Override
    public void finish() {
        //if(VoIPCallHelper.isHoldingCall()) {
            ECHandlerHelper.postDelayedRunnOnUI(new Runnable() {
                @Override
                public void run() {
                    ECVoIPBaseActivity.super.finish();
                }
            } , 3000);
        //}
        //super.finish();
    }
}
