package com.kopo.etf.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.kopo.etf.info.service.EtfInfoService;
import com.kopo.etf.info.vo.EtfInfoVO;
import com.kopo.etf.service.DividendCalculatorService;
import com.kopo.etf.service.ExchangeRateService;
import com.kopo.etf.vo.CalculatorResultVO;
import com.kopo.etf.vo.ExchangeRateVO;

/**
 * 화면2 — 월배당금 계산기 컨트롤러
 */
@Controller
public class CalculatorController {

    @Resource(name = "etfInfoService")
    private EtfInfoService etfInfoService;

    @Resource(name = "exchangeRateService")
    private ExchangeRateService exchangeRateService;

    @Resource(name = "dividendCalculatorService")
    private DividendCalculatorService dividendCalculatorService;

    @Value("${fastapi.base.url}")
    private String fastapiBaseUrl;

    @GetMapping("/etf/calculator.do")
    public String calculator() {
        return "calculator";
    }

    @GetMapping("/etf/{symbol}/calculate.do")
    @ResponseBody
    public CalculatorResultVO calculate(
            @PathVariable("symbol") String symbol,
            @RequestParam("investAmount") BigDecimal investAmountKrw) {

        BigDecimal divYield;
        BigDecimal rate;

        try {
            RestTemplate rt = new RestTemplate();

            // FastAPI에서 ETF 배당률 조회
            String infoUrl = fastapiBaseUrl + "/etf/" + symbol.toUpperCase();
            EtfInfoVO info = rt.getForObject(URI.create(infoUrl), EtfInfoVO.class);
            divYield = info.getDivYield();

            // FastAPI에서 환율 조회
            String rateUrl = fastapiBaseUrl + "/exchange/usdkrw";
            Map rateData = rt.getForObject(URI.create(rateUrl), Map.class);
            rate = new BigDecimal(rateData.get("rate").toString());

            System.out.println("✅ FastAPI 연결 성공 - 계산기 | " + symbol
                    + " | 배당률: " + divYield + "% | 환율: " + rate);

        } catch (Exception e) {
            // fallback → DB 직접 조회
            System.out.println("⚠️ FastAPI 호출 실패 → DB fallback: " + e.getMessage());
            EtfInfoVO info = etfInfoService.getEtfInfo(symbol);
            ExchangeRateVO exchangeRate = exchangeRateService.getLatestRate();
            divYield = info.getDivYield();
            rate = exchangeRate.getRate();
        }

        return dividendCalculatorService.calculate(investAmountKrw, rate, divYield);
    }
}