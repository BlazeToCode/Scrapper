package com.scrapper.scrapper;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScrapperApplication {

	@Autowired
	private Scrapper scrapper;

	private static Scrapper scrapperStatic;

	public static void main(String[] args) {
		SpringApplication.run(ScrapperApplication.class, args);
		scrapperStatic.retrieveLatest(Integer.parseInt(args[0]));
	}

	@PostConstruct
	private void initStatic() {
		scrapperStatic = this.scrapper;
	}

}
