package com.yuridiasns.archiveexplorer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SimpleDockerArchiveExplorerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleDockerArchiveExplorerBackendApplication.class, args);
	}

}
