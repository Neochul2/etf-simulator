package com.kopo.etf.simulation.vo;

import java.math.BigDecimal;

public class DripResultVO {

    private BigDecimal totalDividend;   // 세후 총 배당금 (USD)
    private BigDecimal finalAssets;     // 총 자산 (USD)
    private BigDecimal totalInvest;     // 실 투자금 (USD)
    private BigDecimal returnRate;      // 수익률 (%)
    private BigDecimal totalInvestKrw;  // 실 투자금 (원화)
    private BigDecimal totalDividendKrw;// 세후 총 배당금 (원화)
    private BigDecimal finalAssetsKrw;  // 총 자산 (원화)

    public BigDecimal getTotalDividend() { return totalDividend; }
    public void setTotalDividend(BigDecimal totalDividend) { this.totalDividend = totalDividend; }

    public BigDecimal getFinalAssets() { return finalAssets; }
    public void setFinalAssets(BigDecimal finalAssets) { this.finalAssets = finalAssets; }

    public BigDecimal getTotalInvest() { return totalInvest; }
    public void setTotalInvest(BigDecimal totalInvest) { this.totalInvest = totalInvest; }

    public BigDecimal getReturnRate() { return returnRate; }
    public void setReturnRate(BigDecimal returnRate) { this.returnRate = returnRate; }

    public BigDecimal getTotalInvestKrw() { return totalInvestKrw; }
    public void setTotalInvestKrw(BigDecimal totalInvestKrw) { this.totalInvestKrw = totalInvestKrw; }

    public BigDecimal getTotalDividendKrw() { return totalDividendKrw; }
    public void setTotalDividendKrw(BigDecimal totalDividendKrw) { this.totalDividendKrw = totalDividendKrw; }

    public BigDecimal getFinalAssetsKrw() { return finalAssetsKrw; }
    public void setFinalAssetsKrw(BigDecimal finalAssetsKrw) { this.finalAssetsKrw = finalAssetsKrw; }
}