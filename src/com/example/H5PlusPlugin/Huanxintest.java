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
         * �����Ҫ��Ӧ������ʱ���г�ʼ�������Լ̳��������������properties.xml�ļ���service�ڵ������չ�����ע�ἴ�ɴ���onStart����
         * */
    }
    
    public void init(IWebview pWebview, JSONArray array)
    {
    	// ԭ�������л�ȡJS�㴫�ݵĲ�����
    	// �����Ļ�ȡ˳����JS�㴫�ݵ�˳��һ��
    	appContext = pWebview.getContext();
        String CallBackID = array.optString(0);
        String ReturnString = "";
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // ���APP������Զ�̵�service����application:onCreate�ᱻ����2��
        // Ϊ�˷�ֹ����SDK����ʼ��2�Σ��Ӵ��жϻᱣ֤SDK����ʼ��1��
        // Ĭ�ϵ�APP�����԰���ΪĬ�ϵ�process name�����У�����鵽��process name����APP��process name����������
         
        if (processAppName == null ||!processAppName.equalsIgnoreCase(appContext.getPackageName())) {
             
            // ���application::onCreate �Ǳ�service ���õģ�ֱ�ӷ���
            ReturnString = "Already Init.";
        }else{
        	EMOptions options = new EMOptions();
        	// Ĭ����Ӻ���ʱ���ǲ���Ҫ��֤�ģ��ĳ���Ҫ��֤
        	options.setAcceptInvitationAlways(false);
            // set if need read ack
            options.setRequireAck(true);
            // set if need delivery ack
            options.setRequireDeliveryAck(false);
        	//��ʼ��
        	EMClient.getInstance().init(this.appContext, options);
        	//�����������ʱ���ر�debugģʽ���������Ĳ���Ҫ����Դ
        	EMClient.getInstance().setDebugMode(true);
        	ReturnString = "Init Success.";
        }

        // ���÷�����ԭ�������ִ�н�����ظ�js�㲢������Ӧ��JS��ص�����
        JSUtil.execCallback(pWebview, CallBackID, ReturnString, JSUtil.OK, false);

    }
    public void login(final IWebview pWebview, final JSONArray array){
    	String userName = array.optString(1);
    	String password = array.optString(2);
    	EMClient.getInstance().login(userName,password,new EMCallBack() {//�ص�
    	    @Override
    	    public void onSuccess() {
    	        EMClient.getInstance().groupManager().loadAllGroups();
    	        EMClient.getInstance().chatManager().loadAllConversations();
    	            Log.d("main", "��¼����������ɹ���");
    	            String ReturnString = "��¼����������ɹ���";
    	            String CallbackID = array.optString(0);
    	            JSUtil.execCallback(pWebview, CallbackID, ReturnString, JSUtil.OK, false);
    	    }
    	 
    	    @Override
    	    public void onProgress(int progress, String status) {
    	 
    	    }
    	 
    	    @Override
    	    public void onError(int code, String message) {
    	        Log.d("main", "��¼���������ʧ�ܣ�");
    	        String ReturnString = "��¼���������ʧ�ܣ�";
	            String CallbackID = array.optString(0);
	            JSUtil.execCallback(pWebview, CallbackID, ReturnString, JSUtil.OK, false);
    	    }
    	});
    }
    public void privateSend(final IWebview pWebview, final JSONArray array){
    	String content = array.optString(1);
    	String toChatUsername = array.optString(2);
    	String CallbackID = array.optString(0);
    	String ReturnString = "���ͳɹ�";
    	//����һ���ı���Ϣ��contentΪ��Ϣ�������ݣ�toChatUsernameΪ�Է��û�����Ⱥ�ĵ�id�����Ľ������
    	EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
    	//������Ϣ
    	EMClient.getInstance().chatManager().sendMessage(message);
    	JSUtil.execCallback(pWebview, CallbackID, ReturnString, JSUtil.OK, false);
    }
    public void onListen (final IWebview pWebview, final JSONArray array){
    	
    	EMMessageListener msgListener = new EMMessageListener() {
    	     
    	    @SuppressWarnings("deprecation")
			@Override
    	    public void onMessageReceived(List<EMMessage> messages) {
    	        //�յ���Ϣ
    	    	String CallbackID = array.optString(0);
    	    	String ReturnString = messages.toString();
    	    	Log.d("HUANXIN ONLISTEN", ReturnString);
    	    	JSUtil.execCallback(pWebview, CallbackID, ReturnString, JSUtil.OK, false, true); 
    	    }
    	     
    	    @Override
    	    public void onCmdMessageReceived(List<EMMessage> messages) {
    	        //�յ�͸����Ϣ
    	    }
    	     
    	    @Override
    	    public void onMessageReadAckReceived(List<EMMessage> messages) {
    	        //�յ��Ѷ���ִ
    	    }
    	     
    	    @Override
    	    public void onMessageDeliveryAckReceived(List<EMMessage> message) {
    	        //�յ����ʹ��ִ
    	    }
    	     
    	    @Override
    	    public void onMessageChanged(EMMessage message, Object change) {
    	        //��Ϣ״̬�䶯
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
