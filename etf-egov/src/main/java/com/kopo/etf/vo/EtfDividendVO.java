package com.kopo.etf.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ETF 배당 내역 VO
 * etf_dividend 테이블 1행(row)을 자바 객체로 표현한 클래스
 * 화면1의 "최근 6개월 배당 지급 내역" 표가 이 객체 리스트로 채워짐
 */
public class EtfDividendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;               // PK (AUTO_INCREMENT)
    private String symbol;         // ETF 티커 (etf_info.symbol을 논리적으로 참조, FK 미설정)
    private Date exDivDate;        // 배당락일 (Ex-Dividend Date)
    private Date payDate;          // 배당 지급일 (Pay Date)
    private BigDecimal cashAmount; // 주당 배당금 (USD)
    private Date createdAt;        // 적재일시

    // ===== Getter / Setter =====

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