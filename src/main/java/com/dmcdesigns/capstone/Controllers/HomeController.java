package com.dmcdesigns.capstone.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

@Controller
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("Employee Management System API is running! Try /api/auth/login to get started.");
    }

}
