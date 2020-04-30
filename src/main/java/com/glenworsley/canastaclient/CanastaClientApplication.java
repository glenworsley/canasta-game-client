package com.glenworsley.canastaclient;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@SpringBootApplication
@Log4j2
public class CanastaClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CanastaClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("starting client");
		Console console = System.console();
		String gamecode = console.readLine("Please enter the gamecode for the game you wish to join:");
		String playerName = console.readLine("Please enter your name: ");
		//connect to the game server
		log.info("connecting to server");
		Socket socket = new Socket("127.0.0.1", 4001);
		log.info("connected to server");
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("event", "join_game");
		jsonObject.put("playerName", playerName);
		jsonObject.put("gamecode", gamecode);
		log.info(playerName + " wants to join game " + gamecode);
		out.println(jsonObject.toString());
		String response = in.readLine();
		log.info("response from server: {}", response);

		console.printf("Hi %s!\nWaiting for the other players to join...\n", playerName);
		String message = in.readLine();
		log.info(message);
		while (true) {
			Thread.sleep(10000);
			console.printf("Still waiting...\n");
		}
	}


}
