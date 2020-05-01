package com.glenworsley.canastaclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


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
	public BufferedWriter getOutputWriter() {
		return new BufferedWriter(new OutputStreamWriter(System.out));
	}

}
