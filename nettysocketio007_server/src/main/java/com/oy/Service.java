package com.oy;

import com.alibaba.fastjson.JSONObject;

public class Service {
	private static String msg;

	// 向"channel_1" push数据
	public static void send(String[] args) throws Exception {
		int price = 0;
		if (args != null && args.length > 0) {
			try {
				price = Integer.parseInt(args[0]);
			} catch (Exception e) {
				UtilFunctions.log.info("args[0]不能转换为int");
				new Exception(e);
			}
		}

		for (int i = 0; i < 1000; i++) {
			JSONObject data = new JSONObject();
			data.put("current_price", price++);
			Service.msg = data.toJSONString(); // 把每次push的数据保存起来
			MessageEventHandler.sendAllEvent(data.toJSONString());
			Thread.sleep(1000 * 5);
		}
	}

	public static String getMsg() {
		return msg;
	}

	public static void setMsg(String msg) {
		Service.msg = msg;
	}
}
