package com.kopo.etf.service;

import java.util.List;

import com.kopo.etf.vo.EtfDividendVO;
import com.kopo.etf.vo.EtfInfoVO;

/**
 * ETF 조회 관련 비즈니스 로직 인터페이스 (화면1)
 */
public interface EtfInfoService {

    /** 티커 심볼로 ETF 기본정보 단건 조회 */
    EtfInfoVO getEtfInfo(String symbol);

    /** 특정 ETF의 최근 6개월 배당 내역 조회 */
    List<EtfDividendVO> getRecentDividends(String symbol);

}