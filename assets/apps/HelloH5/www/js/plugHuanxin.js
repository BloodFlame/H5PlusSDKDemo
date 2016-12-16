"use strict";
document.addEventListener( "plusready",  function()
{
    var _BARCODE = 'plugHuanxin',
		B = window.plus.bridge;
	//定义回调
    var plugHuanxin = 
    {
    	init: function(callback) {
			var success = typeof callback !== 'function' ? null : function(args) 
			{
				callback(args);
			},
			fail = typeof callback !== 'function' ? null : function(args) 
			{
				callback(args);
			};
			var callbackID = B.callbackId(success);
			console.log(callbackID);
			return B.exec(_BARCODE, "init", [callbackID]);
    	},
    	login: function(name, pwd, callback) {
			var success = typeof callback !== 'function' ? null : function(args) 
			{
				callback(args);
			};
			var callbackID = B.callbackId(success);
			console.log(callbackID);
			return B.exec(_BARCODE, "login", [callbackID, name, pwd]);    		
    	},
    	privateSend: function(msg, toUser, callback){
 			var success = typeof callback !== 'function' ? null : function(args) 
			{
				callback(args);
			};
			var callbackID = B.callbackId(success);
			console.log(callbackID);
			return B.exec(_BARCODE, "privateSend", [callbackID, msg, toUser]);    		
    	},
    	onListen: function(callback){
 			var success = typeof callback !== 'function' ? null : function(args) 
			{
				callback(args);
			};
			var callbackID = B.callbackId(success);
			console.log(callbackID);
			return B.exec(_BARCODE, "onListen", [callbackID]);     		
    	},
    	removeListener: function(){
 			var success = typeof callback !== 'function' ? null : function(args) 
			{
				callback(args);
			};
			var callbackID = B.callbackId(success);
			console.log(callbackID);
			return B.exec(_BARCODE, "removeListener", [callbackID]);    		
    	},
    	getAllConversations: function(callback){//return an object [{userID,conversationID,msg},...]
  			var success = typeof callback !== 'function' ? null : function(args) 
			{
				var con = "["+args.toString()+"]";
				var conObj = JSON.parse(con);
				callback(conObj);
			};
			var callbackID = B.callbackId(success);
			console.log(callbackID);
			return B.exec(_BARCODE, "getAllConversations", [callbackID]);   		
    	},
    	loadMessage: function(conversation, callback){
  			var success = typeof callback !== 'function' ? null : function(args) 
			{
				var msgs = "["+args.toString()+"]";
				var msgsObj = JSON.parse(msgs);
				callback(msgsObj);
			};
			var callbackID = B.callbackId(success);
			console.log(callbackID);
			return B.exec(_BARCODE, "getMessage", [callbackID, conversation]);      		
    	}
 	};
    window.plus.plugHuanxin = plugHuanxin;
}, true );

