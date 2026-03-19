package com.example.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("projectName", "ecommerce-demo-backend");
        model.addAttribute("sellerUsername", "seller_demo");
        model.addAttribute("buyerUsername", "buyer_demo");
        model.addAttribute("demoPassword", "password123");
        return "index";
    }
}
