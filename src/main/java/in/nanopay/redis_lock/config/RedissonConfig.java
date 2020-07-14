package in.nanopay.redis_lock.config;

import cn.hutool.core.util.StrUtil;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author ：wanglei
 * @create ：2020-07-13 15:49
 * @description：
 */
@Component
@Configuration
@Slf4j
public class RedissonConfig {

	@Autowired
	private Environment env;

	@Bean
	public RedissonClient getRedisson() throws IOException {
		String host = env.getProperty("spring.redis.host");
		String nodes = env.getProperty("spring.redis.cluster.nodes");
		RedissonClient redisson = null;
		Config config = null;
		if (StrUtil.isNotBlank(nodes)) {
			config = cluster(nodes);
		} else if (StrUtil.isNotBlank(host)) {
			config = single(host);
		}
		redisson = Redisson.create(config);
		log.debug("redisson config" + redisson.getConfig().toJSON());
		return redisson;
	}

	/**
	 * 集群模式 (redisson版本是3.5，集群的ip前面要加上“redis://”，不然会报错，3.2版本可不加)
	 *
	 * @return
	 */
	private Config cluster(String configStr) {
		String[] nodes = configStr.split(",");
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = "redis://" + nodes[i];
		}
		Config config = new Config();
		config.useClusterServers() //这是用的集群server
				.setScanInterval(2000) //设置集群状态扫描时间
				.addNodeAddress(nodes)
				.setRetryAttempts(3) //重试次数
		;
		return config;
	}

	/**
	 * 单机模式
	 *
	 * @return
	 */
	private Config single(String configStr) {
		String port = env.getProperty("spring.redis.port");
		String database = env.getProperty("spring.redis.database");
		String addr = new StringBuffer("redis://")
				.append(configStr)
				.append(":")
				.append(port)
				.toString();

		Config config = new Config();
		config.useSingleServer()
				.setAddress(addr)
				.setDatabase(Integer.parseInt(database))
		;
		return config;
	}
}
