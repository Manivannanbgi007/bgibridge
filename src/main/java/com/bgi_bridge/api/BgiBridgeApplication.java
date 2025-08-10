package com.bgi_bridge.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class BgiBridgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BgiBridgeApplication.class, args);
		log.info("BGI Bridge Application has started successfully.");
	}

}
