package pl.mkjb.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private static final String VIEW_NAME = "dashboard";

    @GetMapping
    public String show() {
        return VIEW_NAME;
    }
}

