package com.oy;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Service
public class RedisSub implements MessageListener {
	private static String msg;
	private String redisStr;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String msg = (String) redisTemplate.getValueSerializer().deserialize(message.getBody());
		String channel = (String) redisTemplate.getValueSerializer().deserialize(message.getChannel());

		if (null != channel && !"".equals(channel) && null != msg && !"".equals(msg)) {
			
			// 相同数据不push
			if(redisStr == null) {
				UtilFunctions.log.info("===== redisStr == null =====");
				redisStr = msg;
			} else if (redisStr.equals(msg)) {
				UtilFunctions.log.info("===== redisStr is same =====");
				return ;
			} else {
				UtilFunctions.log.info("===== redisStr:{} =====", redisStr);
				redisStr = msg;
			}
			
			JSONObject data = new JSONObject();
			JSONObject json = JSON.parseObject(msg);
			data.put(json.getString("contract"), json);
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("channel", channel);
			jsonObj.put("timestamp", new Date().getTime());
			jsonObj.put("data", data);
			
			MessageEventHandler.sendAllEvent2(jsonObj.toJSONString());
			RedisSub.msg = jsonObj.toJSONString();
			UtilFunctions.log.info("message from channel:{}, msg:{} ", channel, jsonObj.toJSONString());
		}

	}

	public static String getMsg() {
		return msg;
	}

	public static void setMsg(String msg) {
		RedisSub.msg = msg;
	}

}
