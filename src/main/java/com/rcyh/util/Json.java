package com.rcyh.util;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 抛出JSON数据格式
 * 
 * @author TY
 *
 */
public class Json {

	public static void toJson(Result result, HttpServletResponse response) throws Exception {
		response.setContentType("text/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");

		PrintWriter writer = response.getWriter();

		String json = JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullBooleanAsFalse,
				SerializerFeature.DisableCircularReferenceDetect);
		writer.write(json);
		writer.close();
	}

}
