package com.kopo.etf.service;

import java.math.BigDecimal;

import com.kopo.etf.vo.CalculatorResultVO;

/**
 * 월배당금 계산기 비즈니스 로직 인터페이스 (화면2)
 */
public interface DividendCalculatorService {

    /**
     * 투자금액(원화) 기준 세전/세후 월·연 배당금 계산
     *
     * @param investAmountKrw 투자금액 (원화)
     * @param exchangeRate 환율 (KRW/USD)
     * @param annualDivYieldPercent 연 배당률 (%, 예: 13.44)
     * @return 계산 결과 VO
     */
    CalculatorResultVO calculate(BigDecimal investAmountKrw,
                                  BigDecimal exchangeRate,
                                  BigDecimal annualDivYieldPercent);

}