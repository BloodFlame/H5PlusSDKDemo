package com.example.H5PlusPlugin;
import java.util.Iterator;
import java.util.List;

import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;


public class Huanxintest extends StandardFeature{
	public void onStart(Context pContext, Bundle pSavedInstanceState, String[] pRuntimeArgs) {
        
        /**
         * 如果需要在应用启动时进行初始化，可以继承这个方法，并在properties.xml文件的service节点添加扩展插件的注册即可触发onStart方法
         * */
    }
    
    public void init(IWebview pWebview, JSONArray array)
    {
    	// 原生代码中获取JS层传递的参数，
    	// 参数的获取顺序与JS层传递的顺序一致
    	appContext = pWebview.getContext();
        String CallBackID = array.optString(0);
        String ReturnString = "";
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
         
        if (processAppName == null ||!processAppName.equalsIgnoreCase(appContext.getPackageName())) {
             
            // 则此application::onCreate 是被service 调用的，直接返回
            ReturnString = "Already Init.";
        }else{
        	EMOptions options = new EMOptions();
        	// 默认添加好友时，是不需要验证的，改成需要验证
        	options.setAcceptInvitationAlways(false);
            // set if need read ack
            options.setRequireAck(true);
            // set if need delivery ack
            options.setRequireDeliveryAck(false);
        	//初始化
        	EMClient.getInstance().init(this.appContext, options);
        	//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        	EMClient.getInstance().setDebugMode(true);
        	ReturnString = "Init Success.";
        }

        // 调用方法将原生代码的执行结果返回给js层并触发相应的JS层回调函数
        JSUtil.execCallback(pWebview, CallBackID, ReturnString, JSUtil.OK, false);

    }
    public void login(final IWebview pWebview, final JSONArray array){
    	String userName = array.optString(1);
    	String password = array.optString(2);
    	EMClient.getInstance().login(userName,password,new EMCallBack() {//回调
    	    @Override
    	    public void onSuccess() {
    	        EMClient.getInstance().groupManager().loadAllGroups();
    	        EMClient.getInstance().chatManager().loadAllConversations();
    	            Log.d("main", "登录聊天服务器成功！");
    	            String ReturnString = "登录聊天服务器成功！";
    	            String CallbackID = array.optString(0);
    	            JSUtil.execCallback(pWebview, CallbackID, ReturnString, JSUtil.OK, false);
    	    }
    	 
    	    @Override
    	    public void onProgress(int progress, String status) {
    	 
    	    }
    	 
    	    @Override
    	    public void onError(int code, String message) {
    	        Log.d("main", "登录聊天服务器失败！");
    	        String ReturnString = "登录聊天服务器失败！";
	            String CallbackID = array.optString(0);
	            JSUtil.execCallback(pWebview, CallbackID, ReturnString, JSUtil.OK, false);
    	    }
    	});
    }
    public void privateSend(final IWebview pWebview, final JSONArray array){
    	String content = array.optString(1);
    	String toChatUsername = array.optString(2);
    	String CallbackID = array.optString(0);
    	String ReturnString = "发送成功";
    	//创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
    	EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
    	//发送消息
    	EMClient.getInstance().chatManager().sendMessage(message);
    	JSUtil.execCallback(pWebview, CallbackID, ReturnString, JSUtil.OK, false);
    }
    public void onListen (final IWebview pWebview, final JSONArray array){
    	
    	EMMessageListener msgListener = new EMMessageListener() {
    	     
    	    @SuppressWarnings("deprecation")
			@Override
    	    public void onMessageReceived(List<EMMessage> messages) {
    	        //收到消息
    	    	String CallbackID = array.optString(0);
    	    	String ReturnString = messages.toString();
    	    	Log.d("HUANXIN ONLISTEN", ReturnString);
    	    	JSUtil.execCallback(pWebview, CallbackID, ReturnString, JSUtil.OK, false, true); 
    	    }
    	     
    	    @Override
    	    public void onCmdMessageReceived(List<EMMessage> messages) {
    	        //收到透传消息
    	    }
    	     
    	    @Override
    	    public void onMessageReadAckReceived(List<EMMessage> messages) {
    	        //收到已读回执
    	    }
    	     
    	    @Override
    	    public void onMessageDeliveryAckReceived(List<EMMessage> message) {
    	        //收到已送达回执
    	    }
    	     
    	    @Override
    	    public void onMessageChanged(EMMessage message, Object change) {
    	        //消息状态变动
    	    }
    	};
    	EMClient.getInstance().chatManager().addMessageListener(msgListener);   	
    }
    
    private Context appContext;
    private String getAppName(int pID) {
    	if(null == appContext){
    		Log.e("NULL ERROR", "appContext is null.");
    	}
        String processName = null;
        ActivityManager am = (ActivityManager) this.appContext.getSystemService(Activity.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
}
