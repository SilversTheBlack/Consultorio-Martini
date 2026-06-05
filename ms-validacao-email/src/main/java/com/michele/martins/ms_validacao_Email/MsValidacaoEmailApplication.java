package com.michele.martins.ms_validacao_Email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MsValidacaoEmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsValidacaoEmailApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
