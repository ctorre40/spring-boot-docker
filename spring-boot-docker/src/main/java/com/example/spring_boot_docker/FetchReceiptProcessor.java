package com.example.spring_boot_docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class FetchReceiptProcessor {

	public static void main(String[] args) {

		SpringApplication.run(FetchReceiptProcessor.class, args);
	}
}
