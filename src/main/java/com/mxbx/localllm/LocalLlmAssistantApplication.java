package com.mxbx.localllm;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalLlmAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalLlmAssistantApplication.class, args);
        System.out.println("Application Started Successfully!!!");
	}

}
