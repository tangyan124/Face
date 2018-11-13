package com.rcyh.face.bean;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class ImageCompare {
	@SuppressWarnings("unused")
	private static boolean compareResult = false;
	private static String mark = "_compareResult";

	/**
	 * 比较两张图片，如不同则将不同处标记并输出到新的图片中
	 * 
	 * @param imagePath1
	 *            图片1的路径
	 * @param imagePath2
	 *            图片2的路径
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public static double CompareAndMarkDiff(String imagePath1, String imagePath2) throws Exception {
		Mat mat1 = readMat(imagePath1);
		Mat mat2 = readMat(imagePath2);
		mat1 = Imgcodecs.imdecode(mat1, Imgcodecs.IMREAD_UNCHANGED);
		mat2 = Imgcodecs.imdecode(mat2, Imgcodecs.IMREAD_UNCHANGED);
		/*
		 * Mat mat1 = Imgcodecs.imread(imagePath1, Imgcodecs.IMREAD_UNCHANGED);
		 * Mat mat2 = Imgcodecs.imread(imagePath2, Imgcodecs.IMREAD_UNCHANGED);
		 */
		if (mat1.cols() == 0 || mat2.cols() == 0 || mat1.rows() == 0 || mat2.rows() == 0) {
			System.out.println("图片文件路径异常，获取的图片大小为0，无法读取");
			return 0;
		}
		if (mat1.cols() != mat2.cols() || mat1.rows() != mat2.rows()) {
			System.out.println("两张图片大小不同，无法比较");
			return 0;
		}
		mat1.convertTo(mat1, CvType.CV_8UC1);
		mat2.convertTo(mat2, CvType.CV_8UC1);
		Mat mat1_gray = new Mat();
		Imgproc.cvtColor(mat1, mat1_gray, Imgproc.COLOR_BGR2GRAY);
		Mat mat2_gray = new Mat();
		Imgproc.cvtColor(mat2, mat2_gray, Imgproc.COLOR_BGR2GRAY);
		mat1_gray.convertTo(mat1_gray, CvType.CV_32F);
		mat2_gray.convertTo(mat2_gray, CvType.CV_32F);
		double result = Imgproc.compareHist(mat1_gray, mat2_gray, Imgproc.CV_COMP_CORREL);
		// if (result == 1) {
		// compareResult = true;// 此处结果为1则为完全相同
		// return;
		// }
		System.out.println("相似度数值为:" + result);
		Mat mat_result = new Mat();
		// 计算两个灰度图的绝对差值，并输出到一个Mat对象中
		Core.absdiff(mat1_gray, mat2_gray, mat_result);
		// 将灰度图按照阈值进行绝对值化
		mat_result.convertTo(mat_result, CvType.CV_8UC1);
		List<MatOfPoint> mat2_list = new ArrayList<MatOfPoint>();
		Mat mat2_hi = new Mat();
		// 寻找轮廓图
		Imgproc.findContours(mat_result, mat2_list, mat2_hi, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Mat mat_result1 = mat1;
		Mat mat_result2 = mat2;
		// 使用红色标记不同点
		// System.out.println(mat2_list.size());
		for (MatOfPoint matOfPoint : mat2_list) {
			Rect rect = Imgproc.boundingRect(matOfPoint);
			Imgproc.rectangle(mat_result1, rect.tl(), rect.br(), new Scalar(0, 0, 255), 2);
			Imgproc.rectangle(mat_result2, rect.tl(), rect.br(), new Scalar(0, 0, 255), 2);
		}
		String fileName1 = getFileName(imagePath1);
		String targetPath1 = getParentDir(imagePath2) + File.separator + fileName1.replace(".", mark + ".");
		String fileName2 = getFileName(imagePath2);
		String targetPath2 = getParentDir(imagePath2) + File.separator + fileName2.replace(".", mark + ".");
		// System.out.println(targetPath1);
		// System.out.println(targetPath2);
		// 图片一的带标记的输出文件；
		// Imgcodecs.imwrite(targetPath1, mat_result1);
		// 图片二的带标记的输出文件；
		// Imgcodecs.imwrite(targetPath2, mat_result2);
		// writeImage(mat_result1, targetPath1);
		// writeImage(mat_result2, targetPath2);
		return result;
	}

	@SuppressWarnings("unused")
	private static void writeImage(Mat mat, String outPutFile) throws Exception {
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".png", mat, matOfByte);
		byte[] byteArray = matOfByte.toArray();
		BufferedImage bufImage = null;
		InputStream in = new ByteArrayInputStream(byteArray);
		bufImage = ImageIO.read(in);
		ImageIO.write(bufImage, "png", new File(outPutFile));
	}

	private static String getFileName(String filePath) {
		File f = new File(filePath);
		return f.getName();
	}

	private static String getParentDir(String filePath) {
		File f = new File(filePath);
		return f.getParent();
	}

	private static Mat readMat(String filePath) {
		try {
			File file = new File(filePath);
			@SuppressWarnings("resource")
			FileInputStream inputStream = new FileInputStream(filePath);
			byte[] byt = new byte[(int) file.length()];
			@SuppressWarnings("unused")
			int read = inputStream.read(byt);
			List<Byte> bs = convert(byt);
			Mat mat1 = Converters.vector_char_to_Mat(bs);
			return mat1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Mat();
	}

	private static List<Byte> convert(byte[] byt) {
		List<Byte> bs = new ArrayList<Byte>();
		for (int i = 0; i < byt.length; i++) {
			bs.add(i, byt[i]);
		}
		return bs;
	}
}
