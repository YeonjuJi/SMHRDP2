package com.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@RequestMapping("/")
	public String Home() {
		return "landing-page";
	}
	
	@PostMapping("/graphShow")
	public String graphShow(@RequestParam("gph") String gph) {
		if(gph.equals("daily")) {
			return "dayGph";
		}else if(gph.equals("weekly")) {
			return "weekGph";
		}else if(gph.equals("monthly")) {
			return "monthGph";
		}else if(gph.equals("country")) {
			return "countryGph"; 
		}else if(gph.equals("cpu")){
			return "cpuGph";
		}else {
			return "Traffic";
		}
	}
	
	@PostMapping("/elasticTest")
	public String testPage() {
		return "elasticTest";
	}
	
	@PostMapping("/map")
	public String goMap() {
		return "map";
	}
	
	@GetMapping("/goJoin")
	public String join() {
		return "Join";
	}
	
	@GetMapping("/login")
	public String goLogin() {
		return "Login";
	}
	
	@RequestMapping("/main")
	public String goMain(HttpSession session) {
		if(session.getAttribute("loginInfo") != null) {
			return "Home";
		}
		else {
			return "Login";
		}
	}
	
}
