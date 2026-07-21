package com.kopo.etf.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import com.kopo.etf.info.service.EtfInfoService;
import com.kopo.etf.info.vo.EtfInfoVO;
import com.kopo.etf.service.DripCalculatorService;
import com.kopo.etf.service.ExchangeRateService;
import com.kopo.etf.service.SimulationResultService;
import com.kopo.etf.vo.DripResultVO;
import com.kopo.etf.vo.ExchangeRateVO;
import com.kopo.etf.vo.SimulationResultVO;

@Controller
public class SimulationController {

    @Resource(name = "etfInfoService")
    private EtfInfoService etfInfoService;

    @Resource(name = "dripCalculatorService")
    private DripCalculatorService dripCalculatorService;

    @Resource(name = "simulationResultService")
    private SimulationResultService simulationResultService;

    @Resource(name = "exchangeRateService")
    private ExchangeRateService exchangeRateService;

    @GetMapping("/etf/simulator.do")
    public String simulator() {
        return "simulation";
    }

    @GetMapping("/etf/{symbol}/simulate.do")
    @ResponseBody
    public DripResultVO simulate(
            @PathVariable("symbol") String symbol,
            @RequestParam("initialAmount") BigDecimal initialAmountKrw,
            @RequestParam(value = "monthlyAmount", defaultValue = "0") BigDecimal monthlyAmountKrw,
            @RequestParam("months") int months,
            @RequestParam("isMonthlyInvest") boolean isMonthlyInvest) {

        // 환율 조회
        ExchangeRateVO rateVO = exchangeRateService.getLatestRate();
        BigDecimal exchangeRate = rateVO != null ? rateVO.getRate() : new BigDecimal("1400");

        // KRW → USD 환산 (Java에서 처리)
        BigDecimal initialAmountUsd = initialAmountKrw
                .divide(exchangeRate, 2, RoundingMode.HALF_UP);
        BigDecimal monthlyAmountUsd = isMonthlyInvest
                ? monthlyAmountKrw.divide(exchangeRate, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        EtfInfoVO info = etfInfoService.getEtfInfo(symbol);
        DripResultVO result = dripCalculatorService.simulate(
                initialAmountUsd, monthlyAmountUsd,
                info.getDivYield(), months, isMonthlyInvest, exchangeRate);

        // 실 투자금 KRW 오차 수정 (환산 오차 방지)
        if (isMonthlyInvest) {
            result.setTotalInvestKrw(monthlyAmountKrw
                .multiply(new BigDecimal(months))
                .setScale(0, RoundingMode.HALF_UP));
        } else {
            result.setTotalInvestKrw(initialAmountKrw
                .setScale(0, RoundingMode.HALF_UP));
        }

        return result;
    }

    @PostMapping("/etf/{symbol}/simulation/save.do")
    @ResponseBody
    public Map<String, Object> saveSimulation(
            @PathVariable("symbol") String symbol,
            @ModelAttribute SimulationResultVO vo) {

        vo.setSymbol(symbol);
        simulationResultService.saveSimulationResult(vo);

        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("savedId", vo.getId());
        return result;
    }
}