package io.github.weightrack.controller;

import io.github.weightrack.Service.CoalTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @Autowired
    CoalTypeService coalTypeService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("coalTypes", coalTypeService.getCoalType());
        return "index";
    }
}