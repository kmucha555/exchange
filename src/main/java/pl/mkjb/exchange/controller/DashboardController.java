package pl.mkjb.exchange.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.service.DashboardService;

@Slf4j
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private static final String VIEW_NAME = "dashboard";
    private final DashboardService dashboardService;

    @GetMapping
    public String show() {
        return VIEW_NAME;
    }

    @ResponseBody
    @GetMapping("/currencies")
    public CurrencyRatesModel getAll() {
        return dashboardService.findNewestRates();
    }
}

