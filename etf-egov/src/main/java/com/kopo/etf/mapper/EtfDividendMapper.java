package com.kopo.etf.mapper;

import java.util.List;

import com.kopo.etf.vo.EtfDividendVO;

/**
 * etf_dividend 테이블 접근용 Mapper 인터페이스
 * 실제 SQL은 egovframework/mapper/etf-dividend-mapper.xml 에 정의
 */
public interface EtfDividendMapper {

    /** 특정 ETF의 최근 6개월 배당 내역 조회 (화면1) */
    List<EtfDividendVO> selectRecentDividends(String symbol);

}