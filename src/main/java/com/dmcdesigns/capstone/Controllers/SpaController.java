package com.dmcdesigns.capstone.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to handle SPA routing fallback.
 * All non-API routes should serve the React app's index.html
 */
@Controller
public class SpaController {

    @RequestMapping(value = {
        "/dashboard",
        "/employees",
        "/employees/**",
        "/projects", 
        "/projects/**",
        "/departments",
        "/search",
        "/reports",
        "/performance",
        "/profile",
        "/settings",
        "/login",
        "/register",
        "/forgot-password",
        "/reset-password",
        "/unauthorized"
    })
    public String spa() {
        return "forward:/index.html";
    }
}
