package com.kopo.etf.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 월배당금 계산기 결과 VO (화면2)
 * 화면정의서 표 9 계산식 결과를 담는 객체
 */
public class CalculatorResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal investAmountUsd;     // 투자금액 USD 환산
    private BigDecimal beforeTaxMonthlyUsd; // 세전 월 배당금 (USD)
    private BigDecimal afterTaxMonthlyUsd;  // 세후 월 배당금 (USD)
    private BigDecimal beforeTaxYearlyUsd;  // 세전 연 배당금 (USD)
    private BigDecimal afterTaxYearlyUsd;   // 세후 연 배당금 (USD)
    private BigDecimal beforeTaxMonthlyKrw; // 세전 월 배당금 (원화)
    private BigDecimal afterTaxMonthlyKrw;  // 세후 월 배당금 (원화)
    private BigDecimal beforeTaxYearlyKrw;  // 세전 연 배당금 (원화)
    private BigDecimal afterTaxYearlyKrw;   // 세후 연 배당금 (원화)

    public BigDecimal getInvestAmountUsd() {
        return investAmountUsd;
    }

    public void setInvestAmountUsd(BigDecimal investAmountUsd) {
        this.investAmountUsd = investAmountUsd;
    }

    public BigDecimal getBeforeTaxMonthlyUsd() {
        return beforeTaxMonthlyUsd;
    }

    public void setBeforeTaxMonthlyUsd(BigDecimal beforeTaxMonthlyUsd) {
        this.beforeTaxMonthlyUsd = beforeTaxMonthlyUsd;
    }

    public BigDecimal getAfterTaxMonthlyUsd() {
        return afterTaxMonthlyUsd;
    }

    public void setAfterTaxMonthlyUsd(BigDecimal afterTaxMonthlyUsd) {
        this.afterTaxMonthlyUsd = afterTaxMonthlyUsd;
    }

    public BigDecimal getBeforeTaxYearlyUsd() {
        return beforeTaxYearlyUsd;
    }

    public void setBeforeTaxYearlyUsd(BigDecimal beforeTaxYearlyUsd) {
        this.beforeTaxYearlyUsd = beforeTaxYearlyUsd;
    }

    public BigDecimal getAfterTaxYearlyUsd() {
        return afterTaxYearlyUsd;
    }

    public void setAfterTaxYearlyUsd(BigDecimal afterTaxYearlyUsd) {
        this.afterTaxYearlyUsd = afterTaxYearlyUsd;
    }

    public BigDecimal getBeforeTaxMonthlyKrw() {
        return beforeTaxMonthlyKrw;
    }

    public void setBeforeTaxMonthlyKrw(BigDecimal beforeTaxMonthlyKrw) {
        this.beforeTaxMonthlyKrw = beforeTaxMonthlyKrw;
    }

    public BigDecimal getAfterTaxMonthlyKrw() {
        return afterTaxMonthlyKrw;
    }

    public void setAfterTaxMonthlyKrw(BigDecimal afterTaxMonthlyKrw) {
        this.afterTaxMonthlyKrw = afterTaxMonthlyKrw;
    }

    public BigDecimal getBeforeTaxYearlyKrw() {
        return beforeTaxYearlyKrw;
    }

    public void setBeforeTaxYearlyKrw(BigDecimal beforeTaxYearlyKrw) {
        this.beforeTaxYearlyKrw = beforeTaxYearlyKrw;
    }

    public BigDecimal getAfterTaxYearlyKrw() {
        return afterTaxYearlyKrw;
    }

    public void setAfterTaxYearlyKrw(BigDecimal afterTaxYearlyKrw) {
        this.afterTaxYearlyKrw = afterTaxYearlyKrw;
    }
}