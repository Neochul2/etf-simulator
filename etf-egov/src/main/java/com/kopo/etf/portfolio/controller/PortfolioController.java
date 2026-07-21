package com.kopo.etf.portfolio.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kopo.etf.portfolio.service.PortfolioService;
import com.kopo.etf.portfolio.vo.PortfolioVO;
import com.kopo.etf.service.ExchangeRateService;
import com.kopo.etf.vo.ExchangeRateVO;

@Controller
public class PortfolioController {

    @Resource(name = "portfolioService")
    private PortfolioService portfolioService;

    @Resource(name = "exchangeRateService")
    private ExchangeRateService exchangeRateService;

    // DB 조회 없이 JSP만 바로 반환 → 화면 즉시 표시
    @GetMapping("/etf/portfolio.do")
    public String portfolio() {
        return "portfolio";
    }

    // Ajax로 포트폴리오 데이터 조회
    @GetMapping("/etf/portfolio/data.do")
    @ResponseBody
    public Map<String, Object> portfolioData() {
        ExchangeRateVO rateVO = exchangeRateService.getLatestRate();
        BigDecimal exchangeRate = rateVO != null ? rateVO.getRate() : new BigDecimal("1400");
        return portfolioService.getPortfolioData(exchangeRate);
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