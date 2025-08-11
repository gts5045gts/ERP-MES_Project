package com.bootstrap.study.e_approval.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/e_approval")
@Log4j2
public class EappController {

    @GetMapping("/doc_list")
    public String doc_list(Model model){
        return "/e_approval/e_app_doc_list";
    }


}
