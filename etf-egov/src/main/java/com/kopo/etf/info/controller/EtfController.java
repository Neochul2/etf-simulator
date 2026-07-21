package com.kopo.etf.info.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.kopo.etf.info.service.EtfInfoService;
import com.kopo.etf.info.vo.EtfInfoVO;
import com.kopo.etf.service.ExchangeRateService;
import com.kopo.etf.vo.EtfDividendVO;
import com.kopo.etf.vo.ExchangeRateVO;

@Controller
public class EtfController {

    @Resource(name = "etfInfoService")
    private EtfInfoService etfInfoService;

    @Resource(name = "exchangeRateService")
    private ExchangeRateService exchangeRateService;

    @Value("${fastapi.base.url}")
    private String fastapiBaseUrl;

    @GetMapping("/etf/list.do")
    public String list() {
        return "etfList";
    }

    @GetMapping("/etf/{symbol}/detail.do")
    @ResponseBody
    public Map<String, Object> detail(@PathVariable("symbol") String symbol) {
        Map<String, Object> result = new HashMap<>();

        // 환율 조회
        ExchangeRateVO rateVO = exchangeRateService.getLatestRate();
        BigDecimal exchangeRate = rateVO != null ? rateVO.getRate() : new BigDecimal("1400");

        try {
            RestTemplate rt = new RestTemplate();

            // ETF 기본정보 (FastAPI)
            String infoUrl = fastapiBaseUrl + "/etf/" + symbol.toUpperCase();
            EtfInfoVO info = rt.getForObject(URI.create(infoUrl), EtfInfoVO.class);

            // 배당내역 (FastAPI)
            String divUrl = fastapiBaseUrl + "/etf/" + symbol.toUpperCase() + "/dividends";
            EtfDividendVO[] dividends = rt.getForObject(URI.create(divUrl), EtfDividendVO[].class);

            // afterTaxYield 계산 (Java에서 처리)
            if (info.getDivYield() != null) {
                BigDecimal afterTax = info.getDivYield()
                    .multiply(new BigDecimal("0.846"))
                    .setScale(2, RoundingMode.HALF_UP);
                info.setAfterTaxYield(afterTax);
            }

            // 한화 금액 계산 (Java에서 처리)
            calcKrwAmount(dividends, exchangeRate);

            result.put("info", info);
            result.put("dividends", dividends != null ? List.of(dividends) : List.of());
            result.put("source", "FastAPI");

        } catch (Exception e) {
            System.out.println("FastAPI 호출 실패: " + e.getMessage());
            e.printStackTrace();

            EtfInfoVO info = etfInfoService.getEtfInfo(symbol);
            List<EtfDividendVO> dividends = etfInfoService.getRecentDividends(symbol);

            // afterTaxYield 계산 (Java에서 처리)
            if (info.getDivYield() != null) {
                BigDecimal afterTax = info.getDivYield()
                    .multiply(new BigDecimal("0.846"))
                    .setScale(2, RoundingMode.HALF_UP);
                info.setAfterTaxYield(afterTax);
            }

            // 한화 금액 계산 (Java에서 처리)
            calcKrwAmount(dividends.toArray(new EtfDividendVO[0]), exchangeRate);

            result.put("info", info);
            result.put("dividends", dividends);
            result.put("source", "DB-fallback");
        }

        return result;
    }

    /** 전체 ETF 목록 조회 (드롭다운용) */
    @GetMapping("/etf/symbols.do")
    @ResponseBody
    public List<EtfInfoVO> symbols() {
        return etfInfoService.getAllEtfList();
    }

    /** 배당내역 한화 금액 계산 */
    private void calcKrwAmount(EtfDividendVO[] dividends, BigDecimal rate) {
        if (dividends == null || rate == null) return;
        for (EtfDividendVO d : dividends) {
            if (d.getCashAmount() != null) {
                d.setKrwAmount(d.getCashAmount()
                    .multiply(rate)
                    .setScale(0, RoundingMode.HALF_UP));
            }
        }
    }
}