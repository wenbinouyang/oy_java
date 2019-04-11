package com.oy;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

@Component
public class MessageEventHandler {
	public static SocketIOServer socketIoServer;

	@Autowired
	public MessageEventHandler(SocketIOServer server) {
		MessageEventHandler.socketIoServer = server;
	}

	@OnConnect
	public void onConnect(SocketIOClient client) {
		UUID socketSessionId = client.getSessionId();
		String ip = client.getRemoteAddress().toString();
		UtilFunctions.log.info("client connect, socketSessionId:{}, ip:{}", socketSessionId, ip);
	}

	@OnEvent("sub")
	public void sub(SocketIOClient client, AckRequest request, String channel) {
		UUID socketSessionId = client.getSessionId();
		String ip = client.getRemoteAddress().toString();
		client.joinRoom(channel);
		UtilFunctions.log.info("client sub, channel:{}, socketSessionId:{}, ip:{}", channel, socketSessionId, ip);
	
		Set<String> rooms = client.getAllRooms();
		for (String room : rooms) {
			UtilFunctions.log.info("after client connect, room:{}", room);
		}
		
		// 客户端一订阅，就马上push一次
		sendAllEvent(Service.getMsg());
	}

//	@OnEvent("unsub")
//	public void unsub(SocketIOClient client, AckRequest request, String channel) {
//		UUID socketSessionId = client.getSessionId();
//		String ip = client.getRemoteAddress().toString();
//		client.leaveRoom(channel);
//		UtilFunctions.log.info("client unsub, channel:{}, socketSessionId:{}, ip:{}", channel, socketSessionId, ip);
//	}

	@OnDisconnect
	public void onDisconnect(SocketIOClient client) {
		UUID socketSessionId = client.getSessionId();
		String ip = client.getRemoteAddress().toString();
		UtilFunctions.log.info("client disconnect, socketSessionId:{}, ip:{}", socketSessionId, ip);
		
		Set<String> rooms = client.getAllRooms();
		for (String room : rooms) {
			UtilFunctions.log.info("after client disconnect, room:{}", room);
		}
	}

	// broadcast to channel "channel_1"
	public static void sendAllEvent(String data) {
		socketIoServer.getRoomOperations("channel_1").sendEvent("channel_1", data);
	}
}