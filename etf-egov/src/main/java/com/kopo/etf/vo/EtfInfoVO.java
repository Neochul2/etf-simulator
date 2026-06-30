package com.kopo.etf.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ETF 기본정보 VO
 * etf_info 테이블 1행(row)을 자바 객체로 표현한 클래스
 * MyBatis가 SQL 조회 결과를 이 객체에 자동으로 채워줌
 */
public class EtfInfoVO implements Serializable {

    // 직렬화 버전 ID (VO/DTO는 관례상 항상 선언)
    private static final long serialVersionUID = 1L;

    private String symbol;        // 티커 심볼 (PK, 예: QQQI)
    private BigDecimal price;     // 현재가 (USD)
    private BigDecimal annualDiv; // 12개월 합산 배당금 (USD)
    private BigDecimal divYield;  // 연 배당률 (%)
    private Integer divCount;     // 12개월 배당 횟수
    private Date updatedAt;       // 데이터 기준일 (Polygon 기준)
    private Date createdAt;       // 최초 적재일시

    // ===== Getter / Setter =====
    // MyBatis가 SQL 결과 컬럼을 이 Setter들을 통해 채워 넣음

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