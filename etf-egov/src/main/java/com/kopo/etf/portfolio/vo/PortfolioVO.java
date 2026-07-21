package com.kopo.etf.portfolio.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class PortfolioVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String symbol;
    private String issuer;
    private BigDecimal investAmt;
    private BigDecimal investUsd;
    private BigDecimal divYield;
    private BigDecimal monthlyDiv;
    private BigDecimal monthlyDivUsd;
    private BigDecimal yearlyDiv;     // 세후 연배당금 (원화, 계산값)
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

    public BigDecimal getYearlyDiv() { return yearlyDiv; }
    public void setYearlyDiv(BigDecimal yearlyDiv) { this.yearlyDiv = yearlyDiv; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}