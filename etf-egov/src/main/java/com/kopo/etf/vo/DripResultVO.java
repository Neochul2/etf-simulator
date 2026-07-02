package com.kopo.etf.vo;

import java.math.BigDecimal;

public class DripResultVO {

    private BigDecimal totalDividend;   // 세후 총 배당금 (USD)
    private BigDecimal finalAssets;     // 총 자산 (USD)
    private BigDecimal totalInvest;     // 실 투자금 (USD)
    private BigDecimal returnRate;      // 수익률 (%)

    public BigDecimal getTotalDividend() { return totalDividend; }
    public void setTotalDividend(BigDecimal totalDividend) { this.totalDividend = totalDividend; }

    public BigDecimal getFinalAssets() { return finalAssets; }
    public void setFinalAssets(BigDecimal finalAssets) { this.finalAssets = finalAssets; }

    public BigDecimal getTotalInvest() { return totalInvest; }
    public void setTotalInvest(BigDecimal totalInvest) { this.totalInvest = totalInvest; }

    public BigDecimal getReturnRate() { return returnRate; }
    public void setReturnRate(BigDecimal returnRate) { this.returnRate = returnRate; }
}