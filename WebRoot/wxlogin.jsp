<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>JAVA微信登录</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta name="viewport" content="maximum-scale=1.0,minimum-scale=1.0,user-scalable=0,width=device-width,initial-scale=1.0"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.2.0.js"></script>
	<script type="text/javascript" src="jquery-1.9.1.min.js"></script>
  </head>
  
  <body style="text-align:center;font-size:18px;">
    <a href="/java-weixin/wxLogin">java微信登录</a>
    <input type="button" value="分享微博" onclick="sharetimeline()">
    <input type="button" value="选择" onclick="chooseImage()">
    <input type="button" value="录音" onclick="startRecord()">
    <input type="button" value="扫码" onclick="scancode()">
  </body>
  <script type="text/javascript">
  	$.ajax({
		url:"http://192.168.1.107:8080/java-weixin/config/jsapi",
		dataType:'json',
		async:true,
		type:"POST",
		success:function(data){
			alert("data:="+JSON.stringify(data));
			wx.config({
			    debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
			    appId: 'wx5782d440eb6a1f0d', // 必填，公众号的唯一标识
			    timestamp:data.timestamp , // 必填，生成签名的时间戳
			    nonceStr:data.noncestr, // 必填，生成签名的随机串
			    signature:data.signature,// 必填，签名，见附录1
			    jsApiList: [
				'checkJsApi',
				'onMenuShareTimeline',
				'onMenuShareAppMessage',
				'onMenuShareQQ',
				'onMenuShareWeibo',
				'hideMenuItems',
				'showMenuItems',
				'hideAllNonBaseMenuItem',
				'showAllNonBaseMenuItem',
				'translateVoice',
				'scanQRCode',
				'chooseImage',
				'chooseWXPay'
				] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
			});
		 	wx.ready(function(){
		 	    // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
		 	});
		 	wx.error(function(res){
		 	    // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
		 	});
		 	
		 	wx.checkJsApi({
		 	    jsApiList: ['chooseImage'], // 需要检测的JS接口列表，所有JS接口列表见附录2,
		 	    success: function(res) {
		 	    	alert(JSON.stringify(ret));
		 	        // 以键值对的形式返回，可用的api值true，不可用为false
		 	        // 如：{"checkResult":{"chooseImage":true},"errMsg":"checkJsApi:ok"}
		 	    }
		 	});
		},
		error:function(result){
			alert(JSON.stringify(result));			
		}
  	});
 	
 	function sharetimeline(){
 		wx.onMenuShareTimeline({
 	 	    title: '测试版本', // 分享标题
 	 	    link: 'http://f5616918.ngrok.io/java-weixin/wxlogin.jsp', // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
 	 	    imgUrl: 'share_icon.jpg', // 分享图标
 	 	    success: function () { 
 	 	        // 用户确认分享后执行的回调函数
 	 	        alert("你已经分享成功");
 	 	    },
 	 	    cancel: function () { 
 	 	        // 用户取消分享后执行的回调函数
 	 	        alert("你取消了分享");
 	 	    }
 	 	}); 
 	}
 	
 	function chooseImage(){
 		wx.chooseImage({
 		    count: 1, // 默认9
 		    sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
 		    sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
 		    success: function (res) {
 		        var localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
 		    }
 		});
 	}
 	
 	function startRecord(){
 		wx.startRecord();
 	}
 	
 	function scancode(){
 		wx.scanQRCode({
 		    needResult: 0, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
 		    scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
 		    success: function (res) {
 		    	var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
 			}
 		});
 	}
 	
 	function pay(){
 		wx.chooseWXPay({

 		    timestamp: 0, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符

 		    nonceStr: '', // 支付签名随机串，不长于 32 位

 		    package: '', // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=***）

 		    signType: '', // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'

 		    paySign: '', // 支付签名

 		    success: function (res) {

 		        // 支付成功后的回调函数

 		    }

 		});
 	}
  </script>
</html>
