package com.kopo.etf.service;

import java.math.BigDecimal;

import com.kopo.etf.vo.DripResultVO;

/**
 * 배당금 재투자(DRIP) 복리 시뮬레이션 비즈니스 로직 인터페이스 (화면3)
 */
public interface DripCalculatorService {

    /**
     * DRIP 복리 시뮬레이션 실행
     *
     * @param initialAmountUsd 초기 투자금 (USD)
     * @param monthlyAmountUsd 월 적립금 (USD) — 목돈 일시납이면 0 전달
     * @param annualDivYieldPercent 연 배당률 (%, 예: 13.44)
     * @param months 투자 기간 (개월 수, 예: 5년이면 60)
     * @param isMonthlyInvest 월 적립식 여부 (true: 월 적립식, false: 목돈 일시납)
     * @return 세후 총 배당금 + 최종 총 자산을 담은 결과 VO
     */
    DripResultVO simulate(BigDecimal initialAmountUsd,
                           BigDecimal monthlyAmountUsd,
                           BigDecimal annualDivYieldPercent,
                           int months,
                           boolean isMonthlyInvest);

}