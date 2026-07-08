package com.kopo.etf.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class EtfDividendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String symbol;
    private Date exDivDate;
    private Date payDate;
    private BigDecimal cashAmount;
    private BigDecimal krwAmount;  // 한화 금액 (계산값)
    private Date createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public Date getExDivDate() { return exDivDate; }
    public void setExDivDate(Date exDivDate) { this.exDivDate = exDivDate; }

    public Date getPayDate() { return payDate; }
    public void setPayDate(Date payDate) { this.payDate = payDate; }

    public BigDecimal getCashAmount() { return cashAmount; }
    public void setCashAmount(BigDecimal cashAmount) { this.cashAmount = cashAmount; }

    public BigDecimal getKrwAmount() { return krwAmount; }
    public void setKrwAmount(BigDecimal krwAmount) { this.krwAmount = krwAmount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}