package com.kopo.etf.exchange.service;

import com.kopo.etf.exchange.vo.ExchangeRateVO;

/**
 * 환율 조회 비즈니스 로직 인터페이스 (화면2·3 공통)
 */
public interface ExchangeRateService {

    /** 최신 USD/KRW 환율 조회 */
    ExchangeRateVO getLatestRate();

}