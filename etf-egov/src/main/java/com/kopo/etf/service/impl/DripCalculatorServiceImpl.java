package com.kopo.etf.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.kopo.etf.service.DripCalculatorService;
import com.kopo.etf.vo.DripResultVO;

/**
 * 배당금 재투자(DRIP) 복리 시뮬레이션 구현체 (화면3)
 * 화면정의서 4-4 계산 로직 그대로 구현:
 *   월 세후 수익률 = (연 배당률 ÷ 12) × (1 − 0.154)
 *   목돈 일시납: 매월 div = assets × 월세후수익률 → assets += div
 *   월 적립식:   매월 assets += 월적립금 → div = assets × 월세후수익률 → assets += div
 */
@Service("dripCalculatorService")
public class DripCalculatorServiceImpl implements DripCalculatorService {

    // 해외주식 배당소득세율 15.4% (화면2와 동일 기준)
    private static final BigDecimal TAX_RATE = new BigDecimal("0.154");

    // 계산 정밀도 (소수점 자릿수 + 반올림 방식)
    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);

    @Override
    public DripResultVO simulate(BigDecimal initialAmountUsd,
                                  BigDecimal monthlyAmountUsd,
                                  BigDecimal annualDivYieldPercent,
                                  int months,
                                  boolean isMonthlyInvest) {

        // 1) 연 배당률(%)을 소수로 변환 (예: 13.44 → 0.1344)
        BigDecimal annualYieldDecimal = annualDivYieldPercent.divide(new BigDecimal("100"), MC);

        // 2) 월 세후 수익률 = (연 배당률 ÷ 12) × (1 − 0.154)
        BigDecimal monthlyYield = annualYieldDecimal
                .divide(new BigDecimal("12"), MC)
                .multiply(BigDecimal.ONE.subtract(TAX_RATE), MC);

        // 3) 초기 자산 세팅
        BigDecimal assets = initialAmountUsd;
        BigDecimal totalDividend = BigDecimal.ZERO;

        // 4) 매월 반복하며 복리 계산
        for (int i = 0; i < months; i++) {

            // 월 적립식이면, 배당 계산 전에 적립금을 먼저 자산에 더함
            if (isMonthlyInvest) {
                assets = assets.add(monthlyAmountUsd, MC);
            }

            // 이번 달 배당금 = 현재 자산 × 월 세후 수익률
            BigDecimal monthlyDividend = assets.multiply(monthlyYield, MC);

            // 배당금을 자산에 재투자 (DRIP 핵심 — 다음 달 계산의 기준이 커짐)
            assets = assets.add(monthlyDividend, MC);

            // 총 배당금 누적
            totalDividend = totalDividend.add(monthlyDividend, MC);
        }

        // 5) 결과 VO에 담아 반환 (소수점 2자리로 반올림하여 최종 정리)
        DripResultVO result = new DripResultVO();
        result.setTotalDividend(totalDividend.setScale(2, RoundingMode.HALF_UP));
        result.setFinalAssets(assets.setScale(2, RoundingMode.HALF_UP));

        return result;
    }

}