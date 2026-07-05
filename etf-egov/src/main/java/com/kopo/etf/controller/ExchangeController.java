package com.kopo.etf.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.kopo.etf.service.ExchangeRateService;
import com.kopo.etf.vo.ExchangeRateVO;

/**
 * 환율 조회 컨트롤러 (화면2·3 공통)
 */
@Controller
public class ExchangeController {

    @Resource(name = "exchangeRateService")
    private ExchangeRateService exchangeRateService;

    @Value("${fastapi.base.url}")
    private String fastapiBaseUrl;

    /**
     * 최신 USD/KRW 환율 조회 (Ajax 호출용)
     * URL 예: GET /exchange/latest.do
     */
    @GetMapping("/exchange/latest.do")
    @ResponseBody
    public ExchangeRateVO latest() {
        return exchangeRateService.getLatestRate();
    }

    /**
     * 환율 수동 업데이트 (네비바 🔄 버튼)
     * FastAPI /exchange/update 호출 → 수출입은행 API → DB 업서트
     * URL 예: POST /exchange/update.do
     */
    @PostMapping("/exchange/update.do")
    @ResponseBody
    public Map<String, Object> updateExchange() {
        Map<String, Object> result = new HashMap<>();
        try {
            RestTemplate rt = new RestTemplate();
            String url = fastapiBaseUrl + "/exchange/update";
            Map data = rt.postForObject(URI.create(url), null, Map.class);
            result.put("status", "ok");
            result.put("rate", data.get("rate"));
        } catch (Exception e) {
            result.put("status", "fail");
            result.put("message", e.getMessage());
        }
        return result;
    }
}