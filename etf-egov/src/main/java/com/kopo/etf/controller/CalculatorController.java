package com.kopo.etf.controller;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kopo.etf.service.DividendCalculatorService;
import com.kopo.etf.service.EtfInfoService;
import com.kopo.etf.service.ExchangeRateService;
import com.kopo.etf.vo.CalculatorResultVO;
import com.kopo.etf.vo.EtfInfoVO;
import com.kopo.etf.vo.ExchangeRateVO;

/**
 * 화면2 — 월배당금 계산기 컨트롤러
 */
@Controller
public class CalculatorController {

    // 종목 배당률 조회용 (화면1 Service 재사용)
    @Resource(name = "etfInfoService")
    private EtfInfoService etfInfoService;

    // 최신 환율 조회용
    @Resource(name = "exchangeRateService")
    private ExchangeRateService exchangeRateService;

    // 세전/세후 배당금 계산용
    @Resource(name = "dividendCalculatorService")
    private DividendCalculatorService dividendCalculatorService;

    /**
     * 화면2 진입 — calculator.jsp 화면을 보여줌
     * URL 예: GET /etf/calculator.do
     */
    @GetMapping("/etf/calculator.do")
    public String calculator() {
        return "calculator";
    }

    /**
     * 월배당금 계산 실행 (Ajax 호출용)
     * URL 예: GET /etf/QQQI/calculate.do?investAmount=10000000
     *
     * @param symbol 선택된 ETF 티커
     * @param investAmountKrw 투자금액 (원화)
     * @return 계산 결과 VO (자동으로 JSON 변환)
     */
    @GetMapping("/etf/{symbol}/calculate.do")
    @ResponseBody
    public CalculatorResultVO calculate(
            @PathVariable("symbol") String symbol,
            @RequestParam("investAmount") BigDecimal investAmountKrw) {

        // 1) 선택된 ETF의 현재 배당률 조회 (etf_info.div_yield)
        EtfInfoVO info = etfInfoService.getEtfInfo(symbol);

        // 2) 최신 환율 조회 (exchange_rate.rate)
        ExchangeRateVO exchangeRate = exchangeRateService.getLatestRate();

        // 3) 계산 실행
        return dividendCalculatorService.calculate(
                investAmountKrw,
                exchangeRate.getRate(),
                info.getDivYield()
        );
    }

}