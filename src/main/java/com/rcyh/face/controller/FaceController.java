package com.rcyh.face.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rcyh.face.bean.ImageCompare;
import com.rcyh.util.Json;
import com.rcyh.util.Result;

import sun.misc.BASE64Decoder;

@Controller
@RequestMapping("/Face")
public class FaceController {

	@RequestMapping("/Index")
	public void faceTest(HttpServletRequest request, HttpServletResponse response, String pp, String username)
			throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String inputImageFilename = "D:\\JavaEE\\Qimages\\person\\test.png";
		long currentTimeM = System.currentTimeMillis();
		String outputImageFilename = "D:\\JavaEE\\Qimages\\person\\" + currentTimeM + ".png";

		// 获取人脸数据图并保存
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] b = decoder.decodeBuffer(pp);
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		BufferedImage bi1 = ImageIO.read(bais);
		File file = new File(inputImageFilename);
		ImageIO.write(bi1, "png", file);

		// 截取人脸数据图并保存
		System.load("D:\\openCV\\opencv\\build\\java\\x64\\opencv_java310.dll");
		CascadeClassifier faceDetector = new CascadeClassifier(
				"D:\\openCV\\opencv\\build\\etc\\haarcascades\\haarcascade_frontalface_alt2.xml");
		Mat image = Imgcodecs.imread(inputImageFilename);
		// 检测人脸.
		// MatOfRect 是矩形容器.
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image, faceDetections);
		// 找出最大的1张脸
		Rect maxRect = new Rect(0, 0, 0, 0);
		Result result = null;
		if (faceDetections.toArray().length != 0) {
			for (Rect rect : faceDetections.toArray()) {
				if (calcArea(maxRect) < calcArea(rect)) {
					maxRect = rect;
				}
			}
			if (calcArea(maxRect) > 0) {
				// 创建人脸拷贝区域
				Mat roi_img = new Mat(image, maxRect);
				// 创建临时的人脸拷贝图形
				Mat tmp_img = new Mat();
				// 人脸拷贝
				roi_img.copyTo(tmp_img);
				// 保存最大的1张脸
				Imgcodecs.imwrite(outputImageFilename, tmp_img);
				File file1 = new File(outputImageFilename);
				zipWidthHeightImageFile(file1, file1, 60, 60, 0.8f);
			}
			saveCookies(username, currentTimeM + ".png", response);
			result = new Result(200, "SUCCESS", "SUCCESS");
		} else {
			result = new Result(201, "未识别到人脸数据，请重新拍照！", "FAIL");
		}
		file.delete();
		Json.toJson(result, response);
	}

	@RequestMapping("/Confirm")
	public void faceUploadTest1(HttpServletRequest request, HttpServletResponse response, String pp, String username)
			throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		String inputImageFilename = "D:\\JavaEE\\Qimages\\person\\check.png";
		long currentTimeM = System.currentTimeMillis();
		String outputImageFilename = "D:\\JavaEE\\Qimages\\person\\" + currentTimeM + ".png";

		// 获取人脸数据图并保存
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] b = decoder.decodeBuffer(pp);
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		BufferedImage bi1 = ImageIO.read(bais);
		File file = new File(inputImageFilename);
		ImageIO.write(bi1, "png", file);

		// 截取人脸数据图并保存
		System.load("D:\\openCV\\opencv\\build\\java\\x64\\opencv_java310.dll");
		CascadeClassifier faceDetector = new CascadeClassifier(
				"D:\\openCV\\opencv\\build\\etc\\haarcascades\\haarcascade_frontalface_alt2.xml");
		Mat image = Imgcodecs.imread(inputImageFilename);
		// 检测人脸.
		// MatOfRect 是矩形容器.
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image, faceDetections);
		// 找出最大的1张脸
		Rect maxRect = new Rect(0, 0, 0, 0);
		Result result = null;
		if (faceDetections.toArray().length != 0) {
			for (Rect rect : faceDetections.toArray()) {
				if (calcArea(maxRect) < calcArea(rect)) {
					maxRect = rect;
				}
			}
			if (calcArea(maxRect) > 0) {
				// 创建人脸拷贝区域
				Mat roi_img = new Mat(image, maxRect);
				// 创建临时的人脸拷贝图形
				Mat tmp_img = new Mat();
				// 人脸拷贝
				roi_img.copyTo(tmp_img);
				// 保存最大的1张脸
				Imgcodecs.imwrite(outputImageFilename, tmp_img);
				File file1 = new File(outputImageFilename);
				zipWidthHeightImageFile(file1, file1, 60, 60, 0.8f);
			}

			String fileName = null;

			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (username.equals(cookie.getName())) {
						cookie.setHttpOnly(true);
						fileName = cookie.getValue();
					}
				}
			}

			if (fileName != null) {

				System.load("D:\\openCV\\opencv\\build\\java\\x64\\opencv_java310.dll");

				// Set image path
				String filename1 = outputImageFilename;
				String filename2 = "D:\\JavaEE\\Qimages\\person\\" + fileName;

				double ret;
				ret = ImageCompare.CompareAndMarkDiff(filename1, filename2);

				if (ret > 0.70) {
					result = new Result(200, "SUCCESS", "SUCCESS");
				} else {
					result = new Result(201, "FAIL", "FAIL");
				}
			} else {
				result = new Result(202, "NO DATA", "NO DATA");
			}
		} else {
			result = new Result(201, "未识别到人脸数据，请重新拍照！", "FAIL");
		}
		file.delete();
		File file1 = new File(outputImageFilename);
		file1.delete();
		Json.toJson(result, response);
	}

	public static double calcArea(Rect rect) {
		return rect.width * rect.height;
	}

	// 保存人脸图片信息到cookie里
	public static void saveCookies(String username, String fileName, HttpServletResponse response) throws Exception {
		Cookie cookie = new Cookie(username, fileName);
		cookie.setMaxAge(60 * 60);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * 按设置的宽度高度压缩图片文件<br>
	 * 先保存原文件，再压缩、上传
	 * 
	 * @param oldFile
	 *            要进行压缩的文件全路径
	 * @param newFile
	 *            新文件
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param quality
	 *            质量
	 * @return 返回压缩后的文件的全路径
	 */
	public static String zipWidthHeightImageFile(File oldFile, File newFile, int width, int height, float quality) {
		if (oldFile == null) {
			return null;
		}
		String newImage = null;
		try {
			/** 对服务器上的临时文件进行处理 */
			Image srcFile = ImageIO.read(oldFile);

			String srcImgPath = newFile.getAbsoluteFile().toString();
			String subfix = "png";
			subfix = srcImgPath.substring(srcImgPath.lastIndexOf(".") + 1, srcImgPath.length());

			BufferedImage buffImg = null;
			if (subfix.equals("png")) {
				buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			} else {
				buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			}

			Graphics2D graphics = buffImg.createGraphics();
			graphics.setBackground(new Color(255, 255, 255));
			graphics.setColor(new Color(255, 255, 255));
			graphics.fillRect(0, 0, width, height);
			graphics.drawImage(srcFile.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

			ImageIO.write(buffImg, subfix, new File(srcImgPath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newImage;
	}

}
