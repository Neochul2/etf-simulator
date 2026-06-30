package com.kopo.etf.controller;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import com.kopo.etf.service.DripCalculatorService;
import com.kopo.etf.service.EtfInfoService;
import com.kopo.etf.vo.DripResultVO;
import com.kopo.etf.vo.EtfInfoVO;

@Controller
public class SimulationController {

	@Resource(name = "etfInfoService")
	private EtfInfoService etfInfoService;

	@Resource(name = "dripCalculatorService")
	private DripCalculatorService dripCalculatorService;

	@GetMapping("/etf/simulator.do")
	public String simulator() {
		return "simulation";
	}

	@GetMapping("/etf/{symbol}/simulate.do")
	@ResponseBody
	public DripResultVO simulate(@PathVariable("symbol") String symbol,
			@RequestParam("initialAmount") BigDecimal initialAmountUsd,
			@RequestParam(value = "monthlyAmount", defaultValue = "0") BigDecimal monthlyAmountUsd,
			@RequestParam("months") int months, @RequestParam("isMonthlyInvest") boolean isMonthlyInvest) {

		EtfInfoVO info = etfInfoService.getEtfInfo(symbol);

		return dripCalculatorService.simulate(initialAmountUsd, monthlyAmountUsd, info.getDivYield(), months,
				isMonthlyInvest);
	}

}