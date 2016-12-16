package com.example.H5PlusPlugin;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
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
    	appContext = pWebview.getActivity().getBaseContext();
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
    	
    	msgListener = new EMMessageListener() {
    	     
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
    public void removeListener(final IWebview pWebview, final JSONArray array){
    	String CallbackID = array.optString(0);
    	if(null != msgListener){
        	EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        	Log.d("RETURN STRING", msgListener==null?"NULL":"NOT NULL");
    	}
    	Log.d("RETURN STRING", "msgListener is already null");
    	JSUtil.execCallback(pWebview, CallbackID, "操作成功", JSUtil.OK, false);    	
    }
    public void getAllConversations (final IWebview pWebview, final JSONArray array){
    	String CallbackID = array.optString(0);
    	Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
    	JSONArray newArray = new JSONArray();
    	for(String key : conversations.keySet()){
    		String returnObj = "{ \"userID\": \""+key+"\", \"conversationID\": \""+conversations.get(key).conversationId()+"\", " +
    				"\"msg\":"+conversations.get(key).getLastMessage().getBody().toString().replace("txt:", "")+"}";
    		newArray.put(returnObj);
    	}
    	Log.d("RETURN STRING", newArray.toString());
    	JSUtil.execCallback(pWebview, CallbackID, newArray, JSUtil.OK, false);
    }
    public void getMessage (final IWebview pWebview, final JSONArray array){
    	String username = array.optString(1);
    	String CallbackID = array.optString(0);
    	JSONArray newArray = new JSONArray();
    	EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
    	//获取此会话的所有消息
    	List<EMMessage> messages = conversation.getAllMessages();
    	Log.d("RETURN MESSAGE", messages.toString());
    	for(int i = 0; i < messages.size(); i++){
    		EMMessage message = messages.get(i);
    		String returnObj = "{\"id\":\""+message.getMsgId()+"\",\"from\":\""+message.getFrom()+"\",\"msg\":\""+message.getBody().toString().replace("txt:", "")+"\"}";
    		newArray.put(returnObj);
    	}
    	JSUtil.execCallback(pWebview, CallbackID, newArray, JSUtil.OK, false);
    	//SDK初始化加载的聊天记录为20条，到顶时需要去DB里获取更多
    	//获取startMsgId之前的pagesize条消息，此方法获取的messages SDK会自动存入到此会话中，APP中无需再次把获取到的messages添加到会话中
    	//List<EMMessage> messages = conversation.loadMoreMsgFromDB(startMsgId, pagesize);
    }
    private EMMessageListener msgListener;
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
