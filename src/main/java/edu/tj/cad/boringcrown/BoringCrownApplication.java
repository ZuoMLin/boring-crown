package edu.tj.cad.boringcrown;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("edu.tj.cad.boringcrown.dao")
public class BoringCrownApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoringCrownApplication.class, args);
	}
}
