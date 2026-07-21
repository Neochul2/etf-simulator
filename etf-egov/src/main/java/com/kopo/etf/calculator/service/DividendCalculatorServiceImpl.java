package com.kopo.etf.calculator.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.kopo.etf.calculator.vo.CalculatorResultVO;

/**
 * 월배당금 계산기 구현체 (화면2)
 * 화면정의서 표 9 계산식 그대로 구현:
 *   USD 환산 = 투자금액(원) ÷ 환율
 *   세전 연배당금(USD) = 투자금(USD) × 배당률
 *   세후 연배당금(USD) = 세전 연배당금 × (1 − 0.154)
 *   세전/세후 월배당금 = 연배당금 ÷ 12
 *   원화 환산 = 각 USD × 환율
 */
@Service("dividendCalculatorService")
public class DividendCalculatorServiceImpl implements DividendCalculatorService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.154");
    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);

    @Override
    public CalculatorResultVO calculate(BigDecimal investAmountKrw,
                                         BigDecimal exchangeRate,
                                         BigDecimal annualDivYieldPercent) {

        // 1) USD 환산 = 투자금액(원) ÷ 환율
        BigDecimal investAmountUsd = investAmountKrw.divide(exchangeRate, MC);

        // 2) 연 배당률(%)을 소수로 변환 (예: 13.44 → 0.1344)
        BigDecimal annualYieldDecimal = annualDivYieldPercent.divide(new BigDecimal("100"), MC);

        // 3) 세전 연배당금(USD) = 투자금(USD) × 배당률
        BigDecimal beforeTaxYearlyUsd = investAmountUsd.multiply(annualYieldDecimal, MC);

        // 4) 세후 연배당금(USD) = 세전 연배당금 × (1 − 0.154)
        BigDecimal afterTaxYearlyUsd = beforeTaxYearlyUsd.multiply(BigDecimal.ONE.subtract(TAX_RATE), MC);

        // 5) 세전/세후 월배당금 = 연배당금 ÷ 12
        BigDecimal beforeTaxMonthlyUsd = beforeTaxYearlyUsd.divide(new BigDecimal("12"), MC);
        BigDecimal afterTaxMonthlyUsd = afterTaxYearlyUsd.divide(new BigDecimal("12"), MC);

        // 6) 원화 환산 = 각 USD × 환율
        BigDecimal beforeTaxYearlyKrw = beforeTaxYearlyUsd.multiply(exchangeRate, MC);
        BigDecimal afterTaxYearlyKrw = afterTaxYearlyUsd.multiply(exchangeRate, MC);
        BigDecimal beforeTaxMonthlyKrw = beforeTaxMonthlyUsd.multiply(exchangeRate, MC);
        BigDecimal afterTaxMonthlyKrw = afterTaxMonthlyUsd.multiply(exchangeRate, MC);

        // 7) 결과 VO 구성 (USD는 소수점 2자리, 원화는 정수로 반올림)
        CalculatorResultVO result = new CalculatorResultVO();
        result.setInvestAmountUsd(investAmountUsd.setScale(2, RoundingMode.HALF_UP));
        result.setBeforeTaxMonthlyUsd(beforeTaxMonthlyUsd.setScale(2, RoundingMode.HALF_UP));
        result.setAfterTaxMonthlyUsd(afterTaxMonthlyUsd.setScale(2, RoundingMode.HALF_UP));
        result.setBeforeTaxYearlyUsd(beforeTaxYearlyUsd.setScale(2, RoundingMode.HALF_UP));
        result.setAfterTaxYearlyUsd(afterTaxYearlyUsd.setScale(2, RoundingMode.HALF_UP));
        result.setBeforeTaxMonthlyKrw(beforeTaxMonthlyKrw.setScale(0, RoundingMode.HALF_UP));
        result.setAfterTaxMonthlyKrw(afterTaxMonthlyKrw.setScale(0, RoundingMode.HALF_UP));
        result.setBeforeTaxYearlyKrw(beforeTaxYearlyKrw.setScale(0, RoundingMode.HALF_UP));
        result.setAfterTaxYearlyKrw(afterTaxYearlyKrw.setScale(0, RoundingMode.HALF_UP));

        return result;
    }

}