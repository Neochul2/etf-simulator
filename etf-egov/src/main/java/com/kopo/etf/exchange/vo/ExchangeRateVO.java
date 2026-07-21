package com.kopo.etf.exchange.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * USD/KRW 환율 정보 VO
 * exchange_rate 테이블 1행을 표현하는 객체
 */
public class ExchangeRateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;          // PK
    private String baseCur;      // 기준 통화 (USD)
    private String targetCur;    // 대상 통화 (KRW)
    private BigDecimal rate;     // 환율
    private Date rateDate;       // 환율 기준일자
    private Date createdAt;      // 적재일시

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBaseCur() {
        return baseCur;
    }

    public void setBaseCur(String baseCur) {
        this.baseCur = baseCur;
    }

    public String getTargetCur() {
        return targetCur;
    }

    public void setTargetCur(String targetCur) {
        this.targetCur = targetCur;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Date getRateDate() {
        return rateDate;
    }

    public void setRateDate(Date rateDate) {
        this.rateDate = rateDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}