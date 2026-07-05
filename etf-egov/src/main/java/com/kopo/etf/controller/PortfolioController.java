package com.kopo.etf.controller;

import java.math.BigDecimal;

import java.util.Map;

import javax.annotation.Resource;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.kopo.etf.service.ExchangeRateService;
import com.kopo.etf.service.PortfolioService;
import com.kopo.etf.vo.ExchangeRateVO;
import com.kopo.etf.vo.PortfolioVO;

@Controller
public class PortfolioController {

    @Resource(name = "portfolioService")
    private PortfolioService portfolioService;

    @Resource(name = "exchangeRateService")
    private ExchangeRateService exchangeRateService;

   
    @GetMapping("/etf/portfolio.do")
    public String portfolio(Model model) {
        // FastAPI 대신 DB에서 환율 조회
        BigDecimal exchangeRate;
        ExchangeRateVO rateVO = exchangeRateService.getLatestRate();
        exchangeRate = rateVO != null ? rateVO.getRate() : new BigDecimal("1400");

        Map<String, Object> data = portfolioService.getPortfolioData(exchangeRate);
        model.addAllAttributes(data);
        return "portfolio";
    }

    @PostMapping("/etf/portfolio/add.do")
    @ResponseBody
    public String addPortfolio(
            @RequestParam String symbol,
            @RequestParam BigDecimal investAmt) {
        PortfolioVO vo = new PortfolioVO();
        vo.setSymbol(symbol);
        vo.setInvestAmt(investAmt);
        int result = portfolioService.addPortfolio(vo);
        return result > 0 ? "success" : "fail";
    }

    @PostMapping("/etf/portfolio/delete.do")
    @ResponseBody
    public String deletePortfolio(@RequestParam Long id) {
        int result = portfolioService.removePortfolio(id);
        return result > 0 ? "success" : "fail";
    }

    @PostMapping("/etf/portfolio/update.do")
    @ResponseBody
    public String updatePortfolio(
            @RequestParam Long id,
            @RequestParam BigDecimal investAmt) {
        int result = portfolioService.updatePortfolio(id, investAmt);
        return result > 0 ? "success" : "fail";
    }
}