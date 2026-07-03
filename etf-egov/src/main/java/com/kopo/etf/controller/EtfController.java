package com.kopo.etf.controller;

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

import com.kopo.etf.service.EtfInfoService;
import com.kopo.etf.vo.EtfDividendVO;
import com.kopo.etf.vo.EtfInfoVO;

@Controller
public class EtfController {

    @Resource(name = "etfInfoService")
    private EtfInfoService etfInfoService;

    // globals.properties에서 FastAPI URL 주입
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

        try {
            // FastAPI 호출
            RestTemplate rt = new RestTemplate();

         // ETF 기본정보 (FastAPI)
            String infoUrl = fastapiBaseUrl + "/etf/" + symbol.toUpperCase();
            EtfInfoVO info = rt.getForObject(URI.create(infoUrl), EtfInfoVO.class);

         // 배당내역 (FastAPI)
            String divUrl = fastapiBaseUrl + "/etf/" + symbol.toUpperCase() + "/dividends";
            EtfDividendVO[] dividends = rt.getForObject(URI.create(divUrl), EtfDividendVO[].class);
            
            result.put("info", info);
            result.put("dividends", dividends != null ? List.of(dividends) : List.of());
            result.put("source", "FastAPI");  // 연계 확인용
            
     
        } catch (Exception e) {
        	// 에러 내용 출력 (원인 파악용)
            System.out.println("FastAPI 호출 실패: " + e.getMessage());
            e.printStackTrace();
            // FastAPI 호출 실패 시 DB에서 직접 조회 (fallback)
            EtfInfoVO info = etfInfoService.getEtfInfo(symbol);
            List<EtfDividendVO> dividends = etfInfoService.getRecentDividends(symbol);
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
}