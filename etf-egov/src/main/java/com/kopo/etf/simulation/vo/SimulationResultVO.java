package com.kopo.etf.simulation.vo;

import java.io.Serializable;

/**
 * 화면3 시뮬레이션 결과 저장 VO
 * simulation_result 테이블과 1:1 매핑
 * DB INSERT 시 사용 / @ResponseBody JSON 응답 시 사용
 */
public class SimulationResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** PK (AUTO_INCREMENT) — 저장 후 DB가 자동 부여 */
    private Long id;

    /** ETF 티커 심볼 (예: JEPI) */
    private String symbol;

    /** 투자 방식 — "lumpsum"(목돈일시납) 또는 "monthly"(월적립식) */
    private String investType;

    /** 초기 투자금 (USD) — 월적립식이면 0 */
    private double initialAmt;

    /** 월 적립금 (USD) — 목돈일시납이면 0 */
    private double monthlyAmt;

    /** 투자 기간 (개월) — 12 / 36 / 60 / 120 */
    private int months;

    /** 시뮬레이션 적용 배당률 (%) — etf_info.div_yield 값 */
    private double divYield;

    /** 세후 총 배당금 (USD) — DripCalculatorService 계산 결과 */
    private double totalDiv;

    /** 시뮬레이션 종료 시점 총 자산 (USD) — initialAmt + totalDiv */
    private double finalAssets;

    /** 저장 일시 — DB DEFAULT CURRENT_TIMESTAMP, 조회 시 문자열로 받음 */
    private String savedAt;

    // ── Getter / Setter ──────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getInvestType() { return investType; }
    public void setInvestType(String investType) { this.investType = investType; }

    public double getInitialAmt() { return initialAmt; }
    public void setInitialAmt(double initialAmt) { this.initialAmt = initialAmt; }

    public double getMonthlyAmt() { return monthlyAmt; }
    public void setMonthlyAmt(double monthlyAmt) { this.monthlyAmt = monthlyAmt; }

    public int getMonths() { return months; }
    public void setMonths(int months) { this.months = months; }

    public double getDivYield() { return divYield; }
    public void setDivYield(double divYield) { this.divYield = divYield; }

    public double getTotalDiv() { return totalDiv; }
    public void setTotalDiv(double totalDiv) { this.totalDiv = totalDiv; }

    public double getFinalAssets() { return finalAssets; }
    public void setFinalAssets(double finalAssets) { this.finalAssets = finalAssets; }

    public String getSavedAt() { return savedAt; }
    public void setSavedAt(String savedAt) { this.savedAt = savedAt; }
}