package com.kopo.etf.service;

import java.math.BigDecimal;
import com.kopo.etf.vo.DripResultVO;

public interface DripCalculatorService {
    DripResultVO simulate(BigDecimal initialAmountUsd,
                          BigDecimal monthlyAmountUsd,
                          BigDecimal annualDivYieldPercent,
                          int months,
                          boolean isMonthlyInvest,
                          BigDecimal exchangeRate);
}