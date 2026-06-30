package com.kopo.etf.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ETF 배당 내역 VO
 * etf_dividend 테이블 1행을 표현하는 객체
 */
public class EtfDividendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;               // PK
    private String symbol;         // ETF 티커 (etf_info.symbol 참조)
    private Date exDivDate;        // 배당락일
    private Date payDate;          // 배당 지급일
    private BigDecimal cashAmount; // 주당 배당금 (USD)
    private Date createdAt;        // 적재일시

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Date getExDivDate() {
        return exDivDate;
    }

    public void setExDivDate(Date exDivDate) {
        this.exDivDate = exDivDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}