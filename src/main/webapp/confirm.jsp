<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>人脸数据比对</title>
</head>
<body>
	<div align="center">
		<table border=1>
			<tr>
				<td><video id="video" width="320px" height="240px"
						autoplay="autoplay"></video></td>
			</tr>
			<tr>
				<td align="center"><input type="text" id="username"
					placeholder="请输入用户名"></td>
			</tr>
			<tr>
				<td align="center"><input type="button" title="开启摄像头"
					value="开启摄像头" onclick="getMedia()" />
					<button id="snap" onclick="takePhoto()">数据比对</button></td>
			</tr>
			<tr>
				<td><canvas id="canvas" width="320px" height="240px"></canvas></td>
			</tr>
		</table>
	</div>
	<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
	<script>
		function getMedia() {
			let
			constraints = {
				video : {
					width : 320,
					height : 240
				},
				audio : false
			};
			//获得video摄像头区域
			let
			video = document.getElementById("video");
			//这里介绍新的方法，返回一个 Promise对象
			// 这个Promise对象返回成功后的回调函数带一个 MediaStream 对象作为其参数
			// then()是Promise对象里的方法
			// then()方法是异步执行，当then()前的方法执行完后再执行then()内部的程序
			// 避免数据没有获取到
			let
			promise = navigator.mediaDevices.getUserMedia(constraints);
			promise.then(function(MediaStream) {
				video.srcObject = MediaStream;
				video.play();
			});
		}

		function takePhoto() {
			//获得Canvas对象
			let
			video = document.getElementById("video");
			let
			canvas = document.getElementById("canvas");
			let
			ctx = canvas.getContext('2d');
			ctx.drawImage(video, 0, 0, 320, 240);

			var saveImage = canvas.toDataURL('image/png');
			var b64 = saveImage.substring(22);
			$.ajax({
				type : 'post',
				url : 'Face/Confirm',
				dataType : 'json',
				data : {
					username : $("#username").val(),
					pp : b64
				},
				success : function(data) {
					alert(data.msg);
				},
				error : function(data) {
					console.log(data.msg);
				}
			});
		}
	</script>
</body>
</html>