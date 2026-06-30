package com.kopo.etf.mapper;

import com.kopo.etf.vo.EtfInfoVO;

/**
 * etf_info 테이블 접근용 Mapper 인터페이스
 * 실제 SQL은 egovframework/mapper/etf-info-mapper.xml 에 정의
 */
public interface EtfInfoMapper {

    /** 티커 심볼로 ETF 단건 조회 (화면1) */
    EtfInfoVO selectEtfInfo(String symbol);

}