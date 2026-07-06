package com.kopo.etf.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
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
    public Map<String, Object> addPortfolio(
            @RequestParam String symbol,
            @RequestParam BigDecimal investAmt) {
        PortfolioVO vo = new PortfolioVO();
        vo.setSymbol(symbol);
        vo.setInvestAmt(investAmt);
        int added = portfolioService.addPortfolio(vo);

        Map<String, Object> result = new HashMap<>();
        if (added > 0) {
            ExchangeRateVO rateVO = exchangeRateService.getLatestRate();
            BigDecimal exchangeRate = rateVO != null ? rateVO.getRate() : new BigDecimal("1400");
            Map<String, Object> data = portfolioService.getPortfolioData(exchangeRate);
            result.put("status", "success");
            result.putAll(data);
        } else {
            result.put("status", "fail");
        }
        return result;
    }

    @PostMapping("/etf/portfolio/delete.do")
    @ResponseBody
    public Map<String, Object> deletePortfolio(@RequestParam Long id) {
        int deleted = portfolioService.removePortfolio(id);

        Map<String, Object> result = new HashMap<>();
        if (deleted > 0) {
            ExchangeRateVO rateVO = exchangeRateService.getLatestRate();
            BigDecimal exchangeRate = rateVO != null ? rateVO.getRate() : new BigDecimal("1400");
            Map<String, Object> data = portfolioService.getPortfolioData(exchangeRate);
            result.put("status", "success");
            result.putAll(data);
        } else {
            result.put("status", "fail");
        }
        return result;
    }

    @PostMapping("/etf/portfolio/update.do")
    @ResponseBody
    public Map<String, Object> updatePortfolio(
            @RequestParam Long id,
            @RequestParam BigDecimal investAmt) {
        int updated = portfolioService.updatePortfolio(id, investAmt);

        Map<String, Object> result = new HashMap<>();
        if (updated > 0) {
            ExchangeRateVO rateVO = exchangeRateService.getLatestRate();
            BigDecimal exchangeRate = rateVO != null ? rateVO.getRate() : new BigDecimal("1400");
            Map<String, Object> data = portfolioService.getPortfolioData(exchangeRate);
            result.put("status", "success");
            result.putAll(data);
        } else {
            result.put("status", "fail");
        }
        return result;
    }
}