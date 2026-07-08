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

    private static final long serialVersionUID = 1L;

    private String symbol;          // 티커 심볼 (PK, 예: QQQI)
    private BigDecimal price;       // 현재가 / 전일 종가 (USD)
    private BigDecimal openPrice;   // 시작가 (USD) ★ 신규
    private BigDecimal highPrice;   // 고가 (USD) ★ 신규
    private BigDecimal lowPrice;    // 저가 (USD) ★ 신규
    private Long volume;            // 거래량 ★ 신규
    private String issuer;          // ETF 전체 이름 (Polygon name)
    private String description;     // ETF 설명 (AI 생성) ★ 신규
    private BigDecimal annualDiv;   // 12개월 합산 배당금 (USD)
    private BigDecimal divYield;    // 연 배당률 (%)
    private BigDecimal afterTaxYield; // 세후 배당률 (%)
    private Integer divCount;       // 12개월 배당 횟수
    private Date updatedAt;         // 데이터 기준일 (Polygon 기준)
    private Date createdAt;         // 최초 적재일시

    // ===== Getter / Setter =====

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }

    public BigDecimal getHighPrice() { return highPrice; }
    public void setHighPrice(BigDecimal highPrice) { this.highPrice = highPrice; }

    public BigDecimal getLowPrice() { return lowPrice; }
    public void setLowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAnnualDiv() { return annualDiv; }
    public void setAnnualDiv(BigDecimal annualDiv) { this.annualDiv = annualDiv; }

    public BigDecimal getDivYield() { return divYield; }
    public void setDivYield(BigDecimal divYield) { this.divYield = divYield; }
    
    public BigDecimal getAfterTaxYield() { return afterTaxYield; }
    public void setAfterTaxYield(BigDecimal afterTaxYield) { this.afterTaxYield = afterTaxYield; }

    public Integer getDivCount() { return divCount; }
    public void setDivCount(Integer divCount) { this.divCount = divCount; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}