package com.glenworsley.canastaclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.*;


@SpringBootApplication
public class CanastaClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(CanastaClientApplication.class, args);
	}

	@Bean
	public BufferedReader getInputReader() {
		return new BufferedReader(new InputStreamReader(System.in));
	}

	@Bean
	public PrintWriter getOutputWriter() {
		return new PrintWriter(new OutputStreamWriter(System.out), true);
	}

}
