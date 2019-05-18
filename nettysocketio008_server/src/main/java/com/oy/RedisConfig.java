package com.oy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {
	public static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		RedisSerializer<String> redisSerializer = new StringRedisSerializer();

		// RedisTemplate
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		template.setKeySerializer(redisSerializer);
		template.setValueSerializer(redisSerializer);
		template.setHashKeySerializer(redisSerializer);
		template.setHashValueSerializer(redisSerializer);

		template.afterPropertiesSet();
		return template;
	}

	@Bean(destroyMethod = "destroy")
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory,
			MessageListener redisMessageListener) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(factory);

		ThreadPoolTaskScheduler taskExecutor = new ThreadPoolTaskScheduler();
		taskExecutor.setPoolSize(10);
		taskExecutor.initialize();
		container.setTaskExecutor(taskExecutor);

		Map<MessageListener, Collection<? extends Topic>> listeners = new HashMap<>();
		List<Topic> list = new ArrayList<>();
		list.add(new ChannelTopic("cfd_md"));
		listeners.put(redisMessageListener, list);
		container.setMessageListeners(listeners);

		return container;
	}

}
