package com.glenworsley.canastaclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.*;
import java.net.Socket;


@SpringBootApplication
public class CanastaClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(CanastaClientApplication.class, args);
	}

	@Bean(name = "uiInputReader")
	public BufferedReader getInputReader() {
		return new BufferedReader(new InputStreamReader(System.in));
	}

	@Bean(name = "uiOutputWriter")
	public PrintWriter getOutputWriter() {
		return new PrintWriter(new OutputStreamWriter(System.out), true);
	}

	@Bean(name = "playerMessagePort")
	public int getPlayerMessagePort() {
		return 4001;
	}

	@Bean
	public int getServerMessagePort() {
		return 4002;
	}

	@Bean
	public Socket getPlayerMessageSocket() throws IOException {
		return new Socket("127.0.0.1", getPlayerMessagePort());
	}

	@Bean
	public Socket getServerMessageSocket() throws IOException {
		return new Socket("127.0.0.1", getServerMessagePort());
	}

	@Bean(name = "playerMessageReader")
	public BufferedReader getPlayerMessageReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getPlayerMessageSocket().getInputStream()));
	}

	@Bean(name = "playerMessageWriter")
	public PrintWriter getPlayerMessageWriter() throws IOException {
		return new PrintWriter(new OutputStreamWriter(getPlayerMessageSocket().getOutputStream()), true);
	}

	@Bean(name = "serverMessageReader")
	public BufferedReader getServerMessageReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getServerMessageSocket().getInputStream()));
	}

}
