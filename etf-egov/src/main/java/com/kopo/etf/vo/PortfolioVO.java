package com.kopo.etf.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class PortfolioVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String symbol;
    private String issuer;           // ETF 전체명 (etf_info 조인)
    private BigDecimal investAmt;    // 투자금액 (원화)
    private BigDecimal investUsd;    // 투자금액 (USD, 계산값)
    private BigDecimal divYield;     // 배당률 % (etf_info 조인)
    private BigDecimal monthlyDiv;   // 세후 월배당금 (원화, 계산값)
    private BigDecimal monthlyDivUsd;// 세후 월배당금 (USD, 계산값)
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public BigDecimal getInvestAmt() { return investAmt; }
    public void setInvestAmt(BigDecimal investAmt) { this.investAmt = investAmt; }

    public BigDecimal getInvestUsd() { return investUsd; }
    public void setInvestUsd(BigDecimal investUsd) { this.investUsd = investUsd; }

    public BigDecimal getDivYield() { return divYield; }
    public void setDivYield(BigDecimal divYield) { this.divYield = divYield; }

    public BigDecimal getMonthlyDiv() { return monthlyDiv; }
    public void setMonthlyDiv(BigDecimal monthlyDiv) { this.monthlyDiv = monthlyDiv; }

    public BigDecimal getMonthlyDivUsd() { return monthlyDivUsd; }
    public void setMonthlyDivUsd(BigDecimal monthlyDivUsd) { this.monthlyDivUsd = monthlyDivUsd; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}