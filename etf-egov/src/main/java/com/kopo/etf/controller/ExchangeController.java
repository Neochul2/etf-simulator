package com.kopo.etf.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kopo.etf.service.ExchangeRateService;
import com.kopo.etf.vo.ExchangeRateVO;

/**
 * 환율 조회 컨트롤러 (화면2·3 공통)
 */
@Controller
public class ExchangeController {

    @Resource(name = "exchangeRateService")
    private ExchangeRateService exchangeRateService;

    /**
     * 최신 USD/KRW 환율 조회 (Ajax 호출용)
     * URL 예: GET /exchange/latest.do
     */
    @GetMapping("/exchange/latest.do")
    @ResponseBody
    public ExchangeRateVO latest() {
        return exchangeRateService.getLatestRate();
    }

}