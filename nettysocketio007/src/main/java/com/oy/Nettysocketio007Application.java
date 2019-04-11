package com.oy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;

@SpringBootApplication
@Order(1)
public class Nettysocketio007Application implements CommandLineRunner {
	private SocketIOServer server;

	public static void main(String[] args) {
		SpringApplication.run(Nettysocketio007Application.class, args);
	}

	@Bean
	public SocketIOServer socketIOServer() {
		Configuration config = new Configuration();
		config.setHostname("localhost");
		config.setPort(4001);
		this.server = new SocketIOServer(config);
		return server;
	}

	@Bean
	public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
		return new SpringAnnotationScanner(socketServer);
	}

	@Override
	public void run(String... args) throws Exception {
		server.start();
		UtilFunctions.log.info("socket.io run success!");
		
		// 向"channel_1" push数据
		Service.send(args);
	}

}
