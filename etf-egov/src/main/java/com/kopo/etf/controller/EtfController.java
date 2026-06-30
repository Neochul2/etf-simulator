package com.kopo.etf.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kopo.etf.service.EtfInfoService;
import com.kopo.etf.vo.EtfDividendVO;
import com.kopo.etf.vo.EtfInfoVO;

@Controller
public class EtfController {

    @Resource(name = "etfInfoService")
    private EtfInfoService etfInfoService;

    @GetMapping("/etf/list.do")
    public String list() {
        return "etfList";
    }

    @GetMapping("/etf/{symbol}/detail.do")
    @ResponseBody
    public Map<String, Object> detail(@PathVariable("symbol") String symbol) {

        EtfInfoVO info = etfInfoService.getEtfInfo(symbol);
        List<EtfDividendVO> dividends = etfInfoService.getRecentDividends(symbol);

        Map<String, Object> result = new HashMap<>();
        result.put("info", info);
        result.put("dividends", dividends);

        return result;
    }

}