package com.kopo.etf.simulation.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.kopo.etf.simulation.vo.DripResultVO;

@Service("dripCalculatorService")
public class DripCalculatorServiceImpl implements DripCalculatorService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.154");
    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);

    @Override
    public DripResultVO simulate(BigDecimal initialAmountUsd,
                                  BigDecimal monthlyAmountUsd,
                                  BigDecimal annualDivYieldPercent,
                                  int months,
                                  boolean isMonthlyInvest,
                                  BigDecimal exchangeRate) {

        // 1) 월 세후 수익률
        BigDecimal annualYieldDecimal = annualDivYieldPercent
                .divide(new BigDecimal("100"), MC);
        BigDecimal monthlyYield = annualYieldDecimal
                .divide(new BigDecimal("12"), MC)
                .multiply(BigDecimal.ONE.subtract(TAX_RATE), MC);

        // 2) 초기 자산
        BigDecimal assets = initialAmountUsd;
        BigDecimal totalDividend = BigDecimal.ZERO;

        // 3) 매월 복리 계산
        for (int i = 0; i < months; i++) {
            if (isMonthlyInvest) {
                assets = assets.add(monthlyAmountUsd, MC);
            }
            BigDecimal monthlyDividend = assets.multiply(monthlyYield, MC);
            assets = assets.add(monthlyDividend, MC);
            totalDividend = totalDividend.add(monthlyDividend, MC);
        }

        // 4) 실 투자금
        BigDecimal totalInvest = isMonthlyInvest
                ? monthlyAmountUsd.multiply(new BigDecimal(months), MC)
                : initialAmountUsd;

        // 5) 수익률
        BigDecimal returnRate = BigDecimal.ZERO;
        if (totalInvest.compareTo(BigDecimal.ZERO) > 0) {
            returnRate = totalDividend
                    .divide(totalInvest, MC)
                    .multiply(new BigDecimal("100"), MC)
                    .setScale(1, RoundingMode.HALF_UP);
        }

        // 6) 원화 환산 (Java에서 처리)
        BigDecimal totalInvestKrw   = totalInvest.multiply(exchangeRate, MC)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal totalDividendKrw = totalDividend.multiply(exchangeRate, MC)
                .setScale(0, RoundingMode.HALF_UP);
        BigDecimal finalAssetsKrw   = assets.multiply(exchangeRate, MC)
                .setScale(0, RoundingMode.HALF_UP);

        // 7) 결과 VO
        DripResultVO result = new DripResultVO();
        result.setTotalDividend(totalDividend.setScale(2, RoundingMode.HALF_UP));
        result.setFinalAssets(assets.setScale(2, RoundingMode.HALF_UP));
        result.setTotalInvest(totalInvest.setScale(2, RoundingMode.HALF_UP));
        result.setReturnRate(returnRate);
        result.setTotalInvestKrw(totalInvestKrw);
        result.setTotalDividendKrw(totalDividendKrw);
        result.setFinalAssetsKrw(finalAssetsKrw);
        return result;
    }
}