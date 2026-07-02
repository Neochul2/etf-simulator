package com.kopo.etf.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.kopo.etf.service.DripCalculatorService;
import com.kopo.etf.vo.DripResultVO;

/**
 * 배당금 재투자(DRIP) 복리 시뮬레이션 구현체 (화면3)
 * 월 세후 수익률 = (연 배당률 ÷ 12) × (1 − 0.154)
 * 목돈 일시납: 매월 div = assets × 월세후수익률 → assets += div
 * 월 적립식:   매월 assets += 월적립금 → div = assets × 월세후수익률 → assets += div
 */
@Service("dripCalculatorService")
public class DripCalculatorServiceImpl implements DripCalculatorService {

    // 해외주식 배당소득세율 15.4% (한미 조세조약 15% + 지방소득세 0.4%)
    private static final BigDecimal TAX_RATE = new BigDecimal("0.154");

    // 계산 정밀도: 유효숫자 20자리, 반올림 방식 HALF_UP
    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);

    @Override
    public DripResultVO simulate(BigDecimal initialAmountUsd,   // 초기 투자금 (USD)
                                  BigDecimal monthlyAmountUsd,  // 월 적립금 (USD)
                                  BigDecimal annualDivYieldPercent, // 연 배당률 (%)
                                  int months,                   // 투자 기간 (개월)
                                  boolean isMonthlyInvest) {    // true=월적립식, false=목돈일시납

        // ── 1) 월 세후 수익률 계산 ──────────────────────────────────────
        // 연 배당률(%) → 소수 변환 (예: 13.43 → 0.1343)
        BigDecimal annualYieldDecimal = annualDivYieldPercent
                .divide(new BigDecimal("100"), MC);

        // 월 세후 수익률 = (연 배당률 ÷ 12) × (1 - 0.154)
        // 예: (0.1343 ÷ 12) × 0.846 = 0.009473... → 약 0.9473%/월
        BigDecimal monthlyYield = annualYieldDecimal
                .divide(new BigDecimal("12"), MC)
                .multiply(BigDecimal.ONE.subtract(TAX_RATE), MC);

        // ── 2) 초기 자산 세팅 ────────────────────────────────────────────
        // 목돈 일시납: 초기 자산 = 투자금 전액
        // 월 적립식:   초기 자산 = 0 (첫 달 적립금이 루프 안에서 더해짐)
        BigDecimal assets = initialAmountUsd;
        BigDecimal totalDividend = BigDecimal.ZERO; // 누적 세후 배당금

        // ── 3) 매월 복리 계산 루프 ───────────────────────────────────────
        for (int i = 0; i < months; i++) {

            // [월 적립식만] 배당 계산 전에 이번 달 적립금을 먼저 자산에 추가
            // → 적립금도 이번 달부터 바로 배당 수익률에 반영됨
            if (isMonthlyInvest) {
                assets = assets.add(monthlyAmountUsd, MC);
            }

            // 이번 달 세후 배당금 = 현재 자산 × 월 세후 수익률
            // (배당 재투자 전 자산 기준으로 계산)
            BigDecimal monthlyDividend = assets.multiply(monthlyYield, MC);

            // 배당금을 자산에 즉시 재투자 (DRIP 핵심)
            // → 다음 달 배당 계산의 기준 자산이 커져서 복리 효과 발생
            assets = assets.add(monthlyDividend, MC);

            // 이번 달 배당금을 총 배당금에 누적
            totalDividend = totalDividend.add(monthlyDividend, MC);
        }

        // ── 4) 실 투자금 계산 ────────────────────────────────────────────
        // 목돈 일시납: 실투자금 = 초기 투자금 그대로
        // 월 적립식:   실투자금 = 월 적립금 × 투자 개월 수
        //              (예: $322/월 × 60개월 = $19,320)
        BigDecimal totalInvest = isMonthlyInvest
                ? monthlyAmountUsd.multiply(new BigDecimal(months), MC)
                : initialAmountUsd;

        // ── 5) 수익률 계산 ───────────────────────────────────────────────
        // 수익률(%) = 세후 총 배당금 ÷ 실 투자금 × 100
        // (예: $2,355 ÷ $19,320 × 100 = 12.2%)
        // 실투자금이 0이면 0으로 처리 (0 나눗셈 방지)
        BigDecimal returnRate = BigDecimal.ZERO;
        if (totalInvest.compareTo(BigDecimal.ZERO) > 0) {
            returnRate = totalDividend
                    .divide(totalInvest, MC)
                    .multiply(new BigDecimal("100"), MC)
                    .setScale(1, RoundingMode.HALF_UP); // 소수점 1자리 반올림
        }

        // ── 6) 결과 VO에 담아 반환 ──────────────────────────────────────
        // 최종 값은 소수점 2자리로 정리 (달러 센트 단위)
        DripResultVO result = new DripResultVO();
        result.setTotalDividend(totalDividend.setScale(2, RoundingMode.HALF_UP)); // 세후 총 배당금
        result.setFinalAssets(assets.setScale(2, RoundingMode.HALF_UP));          // 최종 총 자산
        result.setTotalInvest(totalInvest.setScale(2, RoundingMode.HALF_UP));     // 실 투자금
        result.setReturnRate(returnRate);                                           // 수익률 (%)
        return result;
    }
}