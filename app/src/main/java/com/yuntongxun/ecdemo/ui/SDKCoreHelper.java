package com.yuntongxun.ecdemo.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.yuntongxun.ecdemo.ECApplication;
import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.common.CCPAppManager;
import com.yuntongxun.ecdemo.common.utils.ECNotificationManager;
import com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings;
import com.yuntongxun.ecdemo.common.utils.ECPreferences;
import com.yuntongxun.ecdemo.common.utils.LogUtil;
import com.yuntongxun.ecdemo.common.utils.ToastUtil;
import com.yuntongxun.ecdemo.core.ClientUser;
import com.yuntongxun.ecdemo.storage.ContactSqlManager;
import com.yuntongxun.ecdemo.storage.ConversationSqlManager;
import com.yuntongxun.ecdemo.storage.GroupMemberSqlManager;
import com.yuntongxun.ecdemo.storage.GroupNoticeSqlManager;
import com.yuntongxun.ecdemo.storage.GroupSqlManager;
import com.yuntongxun.ecdemo.storage.IMessageSqlManager;
import com.yuntongxun.ecdemo.storage.ImgInfoSqlManager;
import com.yuntongxun.ecdemo.ui.chatting.IMChattingHelper;
import com.yuntongxun.ecdemo.ui.voip.VoIPCallActivity;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDeskManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECGroupManager;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECNotifyOptions;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;
import com.yuntongxun.ecsdk.SdkErrorCode;

import java.io.InvalidClassException;

/**
 * Created by Jorstin on 2015/3/17.
 */
public class SDKCoreHelper implements ECDevice.InitListener , ECDevice.OnECDeviceConnectListener,ECDevice.OnLogoutListener {

    private static final String TAG = "SDKCoreHelper";
    public static final String ACTION_LOGOUT = "com.yuntongxun.ECDemo_logout";
    public static final String ACTION_SDK_CONNECT = "com.yuntongxun.Intent_Action_SDK_CONNECT";
    public static final String ACTION_KICK_OFF = "com.yuntongxun.Intent_ACTION_KICK_OFF";
    private static SDKCoreHelper sInstance;
    private Context mContext;
    private ECDevice.ECConnectState mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
    private ECInitParams mInitParams;
    private ECInitParams.LoginMode mMode = ECInitParams.LoginMode.FORCE_LOGIN;
    private boolean mKickOff = false;
    private ECNotifyOptions mOptions;
    public static SoftUpdate mSoftUpdate;
    private SDKCoreHelper() {
        initOptions();
    }

    private void initOptions() {
        mOptions = new ECNotifyOptions();
        mOptions.enable = true;
        mOptions.mNewMsgShake = ECPreferences.getSharedPreferences().getBoolean(ECPreferenceSettings.SETTINGS_NEW_MSG_SHAKE.getId(), true);
        mOptions.mNewMsgSound = ECPreferences.getSharedPreferences().getBoolean(ECPreferenceSettings.SETTINGS_NEW_MSG_SOUND.getId(), true);
        mOptions.clazz = LauncherActivity.class;
    }

    private static SDKCoreHelper getInstance() {
        if (sInstance == null) {
            sInstance = new SDKCoreHelper();
        }
        return sInstance;
    }

    public static boolean isKickOff() {
        return getInstance().mKickOff;
    }

    public static void init(Context ctx) {
        init(ctx, ECInitParams.LoginMode.AUTO);
    }

    public static void init(Context ctx , ECInitParams.LoginMode mode) {
        getInstance().mKickOff = false;
        LogUtil.d(TAG , "[init] start regist..");
        ctx = ECApplication.getInstance().getApplicationContext();
        getInstance().mMode = mode;
        getInstance().mContext = ctx;
        // 判断SDK是否已经初始化，没有初始化则先初始化SDK
        if(!ECDevice.isInitialized()) {
            getInstance().mConnect = ECDevice.ECConnectState.CONNECTING;
            // ECSDK.setNotifyOptions(getInstance().mOptions);
            ECDevice.initial(ctx, getInstance());

            postConnectNotify();
            return ;
        }
        LogUtil.d(TAG, " SDK has inited , then regist..");
        // 已经初始化成功，直接进行注册
        getInstance().onInitialized();
    }

    public static void setSoftUpdate(String version , int mode) {
        getInstance().mSoftUpdate = new SoftUpdate(version , mode);
    }

    @Override
    public void onInitialized() {
        LogUtil.d(TAG, "ECSDK is ready");
        ClientUser clientUser = CCPAppManager.getClientUser();
        if (mInitParams == null || mInitParams.getInitParams() == null || mInitParams.getInitParams().isEmpty()){
            mInitParams = new ECInitParams();
        }
        mInitParams.reset();
        // 如：VoIP账号/手机号码/..
        mInitParams.setUserid(clientUser.getUserId());
        // appkey
        mInitParams.setAppKey(clientUser.getAppKey());
        // mInitParams.setAppKey(/*clientUser.getAppKey()*/"ff8080813d823ee6013d856001000029");
        // appToken
        mInitParams.setToken(clientUser.getAppToken());
        // mInitParams.setToken(/*clientUser.getAppToken()*/"d459711cd14b443487c03b8cc072966e");
        // ECInitParams.LoginMode.FORCE_LOGIN
        mInitParams.setMode(getInstance().mMode);

        // 如果有密码（VoIP密码，对应的登陆验证模式是）
        // ECInitParams.LoginAuthType.PASSWORD_AUTH
        if(!TextUtils.isEmpty(clientUser.getPassword())) {
            mInitParams.setPwd(clientUser.getPassword());
        }

        // 设置登陆验证模式（是否验证密码/如VoIP方式登陆）
        if(clientUser.getLoginAuthType() != null) {
            mInitParams.setAuthType(clientUser.getLoginAuthType());
        }

        if(!mInitParams.validate()) {
            ToastUtil.showMessage(R.string.regist_params_error);
            Intent intent = new Intent(ACTION_SDK_CONNECT);
            intent.putExtra("error", -1);
            mContext.sendBroadcast(intent);
            return ;
        }

        // 设置接收VoIP来电事件通知Intent
        // 呼入界面activity、开发者需修改该类
        Intent intent = new Intent(getInstance().mContext, VoIPCallActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity( getInstance().mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mInitParams.setPendingIntent(pendingIntent);

        // 设置SDK注册结果回调通知，当第一次初始化注册成功或者失败会通过该引用回调
        // 通知应用SDK注册状态
        // 当网络断开导致SDK断开连接或者重连成功也会通过该设置回调
        mInitParams.setOnChatReceiveListener(IMChattingHelper.getInstance());
        mInitParams.setOnDeviceConnectListener(this);


        if(ECDevice.getECMeetingManager() != null) {
            ECDevice.getECMeetingManager().setOnMeetingListener(MeetingMsgReceiver.getInstance());
        }
        ECDevice.login(mInitParams);
    }

    @Override
    public void onConnect() {
        // Deprecated
    }

    @Override
    public void onDisconnect(ECError error) {
        // SDK与云通讯平台断开连接
        // Deprecated
    }

    @Override
    public void onConnectState(ECDevice.ECConnectState state, ECError error) {
        if(state == ECDevice.ECConnectState.CONNECT_FAILED && error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
            try {
                ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_REGIST_AUTO, "", true);
            } catch (InvalidClassException e) {
                e.printStackTrace();
            }
            mKickOff = true;
            // 失败，账号异地登陆
            Intent intent = new Intent(ACTION_KICK_OFF);
            intent.putExtra("kickoffText" , error.errorMsg);
            mContext.sendBroadcast(intent);
            LauncherActivity.mLauncherUI.handlerKickOff(error.errorMsg);
            ECNotificationManager.getInstance().showKickoffNotification(mContext ,error.errorMsg);
        }
        getInstance().mConnect = state;
        Intent intent = new Intent(ACTION_SDK_CONNECT);
        intent.putExtra("error", error.errorCode);
        mContext.sendBroadcast(intent);
        postConnectNotify();
    }

    /**
     * 当前SDK注册状态
     * @return
     */
    public static ECDevice.ECConnectState getConnectState() {
        return getInstance().mConnect;
    }

    @Override
    public void onLogout() {
        getInstance().mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
        if(mInitParams != null && mInitParams.getInitParams() != null) {
            mInitParams.getInitParams().clear();
        }
        mInitParams = null;
        mContext.sendBroadcast(new Intent(ACTION_LOGOUT));
    }

    @Override
    public void onError(Exception exception) {
        LogUtil.e(TAG, "ECSDK couldn't start: " + exception.getLocalizedMessage());
        ECDevice.unInitial();
    }

    /**
     * 状态通知
     */
    private static void postConnectNotify() {
        if(getInstance().mContext instanceof LauncherActivity) {
            ((LauncherActivity) getInstance().mContext).onNetWorkNotify(getConnectState());
        }
    }

    public static void logout() {
        ECDevice.logout(getInstance());
        release();
    }

    public static void release() {
        getInstance().mKickOff = false;
        IMChattingHelper.getInstance().destory();
        ContactSqlManager.reset();
        ConversationSqlManager.reset();
        GroupMemberSqlManager.reset();
        GroupNoticeSqlManager.reset();
        GroupSqlManager.reset();
        IMessageSqlManager.reset();
        ImgInfoSqlManager.reset();
    }

    /**
     * IM聊天功能接口
     * @return
     */
    public static ECChatManager getECChatManager() {
        ECChatManager ecChatManager = ECDevice.getECChatManager();
        LogUtil.d(TAG, "ecChatManager :" + ecChatManager);
        return ecChatManager;
    }

    /**
     * 群组聊天接口
     * @return
     */
    public static ECGroupManager getECGroupManager() {
        return ECDevice.getECGroupManager();
    }

    public static ECDeskManager getECDeskManager() {
        return ECDevice.getECDeskManager();
    }

    /**
     * VoIP呼叫接口
     * @return
     */
    public static ECVoIPCallManager getVoIPCallManager() {
        return ECDevice.getECVoIPCallManager();
    }

    public static ECVoIPSetupManager getVoIPSetManager() {
        return ECDevice.getECVoIPSetupManager();
    }


    public static class SoftUpdate  {
        public String version;
        public int mode;

        public SoftUpdate(String version , int mode) {
            this.version = version;
            this.mode = mode;
        }
    }
}
