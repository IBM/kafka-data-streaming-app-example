/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
public class NotificationServiceSpringApp {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceSpringApp.class, args);
	}

	@Bean
	public SecurityWebFilterChain filterChain(ServerHttpSecurity httpSecurity) {
		// ignore security in this example
		httpSecurity.csrf(csrf -> csrf.disable())
			.authorizeExchange(exchanges -> exchanges.anyExchange().permitAll());

		return httpSecurity.build();
	}
}
