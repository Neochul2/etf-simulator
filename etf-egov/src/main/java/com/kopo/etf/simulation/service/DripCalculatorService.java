package com.kopo.etf.simulation.service;

import java.math.BigDecimal;

import com.kopo.etf.simulation.vo.DripResultVO;

public interface DripCalculatorService {
    DripResultVO simulate(BigDecimal initialAmountUsd,
                          BigDecimal monthlyAmountUsd,
                          BigDecimal annualDivYieldPercent,
                          int months,
                          boolean isMonthlyInvest,
                          BigDecimal exchangeRate);
}