package in.nanopay.redis_lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("in.nanopay")
@SpringBootApplication
public class RedisLockApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisLockApplication.class, args);
	}

}
