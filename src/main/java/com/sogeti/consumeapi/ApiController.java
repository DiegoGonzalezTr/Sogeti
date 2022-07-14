package com.sogeti.consumeapi;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ch.qos.logback.core.pattern.parser.Parser;
import kong.unirest.Unirest;


@RestController
public class ApiController {

	@Autowired
	private RestTemplate restTemplate;

	// get the list of spaceships form the api
	private static String url = "https://swapi.dev/api/people";

	JsonArray people = new JsonArray();
	
	
	//This method is to show all the persons
	@GetMapping("/people")
	public String getAllPersons() {
		String body = Unirest.get(url).asString().getBody();
		JsonParser jsonParser = new JsonParser();

		JsonObject objeto = jsonParser.parse(body).getAsJsonObject();
		while(!objeto.get("next").isJsonNull()) {
			people.addAll(objeto.get("results").getAsJsonArray());
			//Store the next url
			String url = objeto.get("next").getAsString();
			body = Unirest.get(url).asString().getBody();
			objeto = jsonParser.parse(body).getAsJsonObject();
				
			
			
		}
		
		return people.toString();
	}
	
	//This method is for search by name
	@GetMapping("/people/{name}")
	public ResponseEntity<String> getCountries(@PathVariable String name) {

		

		String body = Unirest.get(url).asString().getBody();

		JsonParser jsonParser = new JsonParser();

		JsonObject objeto = jsonParser.parse(body).getAsJsonObject();

		int count=0;
		while (true) {

			JsonArray people = objeto.get("results").getAsJsonArray();
			
			
			for (JsonElement obj : people) {
				if (obj.getAsJsonObject().get("name").getAsString().equals(name)) {
					return new ResponseEntity<String>(obj.getAsJsonObject().toString(),HttpStatus.OK);
				}
			}

			if (!objeto.get("next").isJsonNull()) {
				String url1 = objeto.get("next").getAsString();
				body = Unirest.get(url1).asString().getBody();
				objeto = jsonParser.parse(body).getAsJsonObject();
				
			} else break;
			
			
		}
		
		
	
		
		return new ResponseEntity<String>("Not found", HttpStatus.NOT_FOUND);
		


	}
}
