package com.kopo.etf.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DRIP 복리 시뮬레이션 결과 VO (화면3)
 * DB 테이블과 매핑되지 않는 순수 계산 결과 객체
 */
public class DripResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal totalDividend; // 세후 총 배당금 (USD, 기간 전체 누적)
    private BigDecimal finalAssets;   // 총 자산 (USD, 기간 종료 시점)

    public BigDecimal getTotalDividend() {
        return totalDividend;
    }

    public void setTotalDividend(BigDecimal totalDividend) {
        this.totalDividend = totalDividend;
    }

    public BigDecimal getFinalAssets() {
        return finalAssets;
    }

    public void setFinalAssets(BigDecimal finalAssets) {
        this.finalAssets = finalAssets;
    }
}