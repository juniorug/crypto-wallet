package com.postfinance.cryptowallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoWalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoWalletApplication.class, args);
	}

}
