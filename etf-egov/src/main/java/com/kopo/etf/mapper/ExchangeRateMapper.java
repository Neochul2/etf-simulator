package com.kopo.etf.mapper;

import com.kopo.etf.vo.ExchangeRateVO;

/**
 * exchange_rate 테이블 접근용 Mapper 인터페이스
 * 실제 SQL은 egovframework/mapper/exchange-rate-mapper.xml 에 정의
 */
public interface ExchangeRateMapper {

    /** 최신 환율 1건 조회 (화면2·3) */
    ExchangeRateVO selectLatestRate();

}