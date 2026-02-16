package com.yuridiasns.secure_file_explorer_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAsync
public class SecureFileExplorerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureFileExplorerBackendApplication.class, args);
	}

}
