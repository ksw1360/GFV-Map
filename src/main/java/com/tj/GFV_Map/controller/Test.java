package com.tj.GFV_Map.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Test {
    @GetMapping("/login")
    public String test(){
        return  "Test springboot";
    }
}
