package fnozoszzt.pdd;

import fnozoszzt.pdd.common.MyExceptionHandler;
import fnozoszzt.pdd.common.MyRestExceptionHandler;
import fnozoszzt.pdd.session.GlobalSessionFilter;
import fnozoszzt.pdd.session.RedisSessionStore;
import fnozoszzt.pdd.session.SessionStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisShardInfo;

import java.nio.charset.StandardCharsets;

@EnableDiscoveryClient
@SpringBootApplication(exclude={
		RedisAutoConfiguration.class,
		RedisRepositoriesAutoConfiguration.class
})
@ServletComponentScan
public class MyApplication {

	@Value("${redis.server.host}")
	private String redisHost;
	@Value("${redis.server.port}")
	private Integer redisPort;
	@Value("${global.session.timeout}")
	private Integer sessionTimeout;

	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);
	}

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		//Response status code 4XX or 5XX to the cliet.
		restTemplate.setErrorHandler(new MyRestExceptionHandler());
		//设置RestTemplate的编码方式，防止中文乱码
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		// 保证RestTemplate能够访问Https请求
		//restTemplate.setRequestFactory(new MyClientHttpRequestFactory());
		return restTemplate;
	}

	@Bean
	GlobalSessionFilter globalSessionFilter() {
		GlobalSessionFilter filter = new GlobalSessionFilter();

		SessionStore store = new RedisSessionStore(
				new JedisConnectionFactory(
						new JedisShardInfo(redisHost, redisPort)));
		filter.setSessionStore(store);
		filter.setSessionTimeout(sessionTimeout);

		return filter;
	}
}
