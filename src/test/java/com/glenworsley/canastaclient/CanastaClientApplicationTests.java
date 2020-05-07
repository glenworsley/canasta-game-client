package com.glenworsley.canastaclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("ContextTest")
@SpringBootTest
class CanastaClientApplicationTests {

	@Autowired
	private ClientMessageHandler clientMessageHandler;

	@Autowired
	private ServerMessageHandler serverMessageHandler;

	@Autowired
	private BufferedReader playerMessageReader;

	@Autowired
	private BufferedReader serverMessageReader;

	@Test
	void contextLoads() {

		assertThat(clientMessageHandler).isNotNull();
		assertThat(serverMessageHandler).isNotNull();
		assertThat(playerMessageReader).isNotNull();
		assertThat(serverMessageReader).isNotNull();
		assertThat(clientMessageHandler.getPlayerMessageReader()).isNotNull();

	}

}
