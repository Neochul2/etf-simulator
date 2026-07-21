package com.kopo.etf.info.mapper;

import java.util.List;

import com.kopo.etf.info.vo.EtfDividendVO;

/**
 * etf_dividend 테이블 접근용 Mapper 인터페이스
 * 실제 SQL은 egovframework/mapper/etf-dividend-mapper.xml 에 정의
 */
public interface EtfDividendMapper {

    /**
     * 특정 ETF의 최근 6개월 배당 내역 조회 (화면1)
     * 테이블정의서 기준: ex_div_date DESC LIMIT 6
     * @param symbol 조회할 ETF 티커
     * @return 최근 배당 내역 리스트 (최신순)
     */
    List<EtfDividendVO> selectRecentDividends(String symbol);

}