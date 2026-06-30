package com.kopo.etf.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ETF 기본정보 VO
 * etf_info 테이블 1행을 표현하는 객체
 */
public class EtfInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String symbol;        // 티커 심볼 (PK)
    private BigDecimal price;     // 현재가 (USD)
    private BigDecimal annualDiv; // 연 배당금 (USD)
    private BigDecimal divYield;  // 연 배당률 (%)
    private Integer divCount;     // 배당 횟수
    private Date updatedAt;       // 데이터 기준일
    private Date createdAt;       // 최초 적재일시

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAnnualDiv() {
        return annualDiv;
    }

    public void setAnnualDiv(BigDecimal annualDiv) {
        this.annualDiv = annualDiv;
    }

    public BigDecimal getDivYield() {
        return divYield;
    }

    public void setDivYield(BigDecimal divYield) {
        this.divYield = divYield;
    }

    public Integer getDivCount() {
        return divCount;
    }

    public void setDivCount(Integer divCount) {
        this.divCount = divCount;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}