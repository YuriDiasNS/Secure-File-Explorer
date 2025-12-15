package com.yuridiasns.secure_file_explorer_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SecureFileExplorerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureFileExplorerBackendApplication.class, args);
	}

}
