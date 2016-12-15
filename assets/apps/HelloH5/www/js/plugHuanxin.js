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
    	}
 	};
    window.plus.plugHuanxin = plugHuanxin;
}, true );

