package com.bootstrap.study.e_approval.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/e_app")
public class EappController {

    @GetMapping("/doc_list")
    public String doc_list(Model model){


        return "/e_app/doc_list";
    }


}
