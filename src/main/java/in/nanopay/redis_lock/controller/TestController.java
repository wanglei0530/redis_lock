package in.nanopay.redis_lock.controller;

import cn.hutool.core.date.DateUtil;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author ：wanglei
 * @create ：2020-07-13 13:17
 * @description：
 */
@RestController
@Slf4j
public class TestController {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private RedissonClient redisson;

//	@Autowired
//	private RedissLockUtil redissLockUtil;

//	@GetMapping("/many")
//	public String many(@Param("productId") String productId, @Param("qty") Integer qty) {
//		try {
//			String uuid = UUID.randomUUID().toString();
//			boolean lock = redisTemplate.opsForValue().setIfAbsent(productId, uuid, Duration.ofSeconds(10));
//			log.info("加锁: " + uuid);
//			if (!lock) {
//				return "操作频发！";
//			}
//			int stock = getStock();
//			if (stock <= 0) {
//				return "没有库存了！";
//			}
//			int realstock = stock - qty;
//			redisTemplate.opsForValue().set("stock", realstock + "");
//			log.info("剩余库存: " + realstock);
//			return "" + getStock();
//		} finally {
//			log.info("释放锁: " + redisTemplate.opsForValue().get(productId));
//			redisTemplate.delete(productId);
//		}
//	}

	@GetMapping("/many")
	public String many(@Param("productId") String productId, @Param("qty") Integer qty) throws InterruptedException {
		RLock redissonLock = redisson.getLock(productId);
		try {
			redissonLock.lock(10,  TimeUnit.SECONDS);
			int stock = getStock();
			if (stock <= 0) {
				return "没有库存了！";
			}
			int realstock = stock - qty;
			redisTemplate.opsForValue().set("stock", realstock + "");
			log.info(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss") + " --- 剩余库存: " + realstock);
			return "" + getStock();
		} finally {
			redissonLock.unlock();
		}
	}

//	@GetMapping("/many")
//	public String many(@Param("productId") String productId, @Param("qty") Integer qty) {
//		RLock redissonLock = redissLockUtil.lock(productId, 10);
//		try {
//			int stock = getStock();
//			if (stock <= 0) {
//				return "没有库存了！";
//			}
//			int realstock = stock - qty;
//			redisTemplate.opsForValue().set("stock", realstock + "");
//			log.info(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " --- 剩余库存: " + realstock);
//			return "" + getStock();
//		} finally {
//			redissonLock.unlock();
//		}
//	}

	private int getStock() {
		return Integer.parseInt(redisTemplate.opsForValue().get("stock"));
	}

}
