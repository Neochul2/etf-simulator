package com.kopo.etf.mapper;

import com.kopo.etf.vo.EtfInfoVO;

/**
 * etf_info 테이블 접근용 Mapper 인터페이스
 * - 이 인터페이스는 "어떤 조회가 가능한지"만 선언하고, 실제 구현 코드는 없음
 * - 실제 SQL은 egovframework/mapper/etf-info-mapper.xml 에 정의되어 있고
 *   MyBatis가 메서드명과 XML의 id를 자동으로 매칭해서 동작시킴
 * - context-mapper.xml의 MapperConfigurer가 com.kopo.etf.mapper 패키지를 스캔해서
 *   이 인터페이스를 "etfInfoMapper" 라는 이름의 Spring Bean으로 자동 등록함
 */
public interface EtfInfoMapper {

    /**
     * 티커 심볼로 ETF 기본정보 단건 조회 (화면1)
     * @param symbol 조회할 ETF 티커 (예: QQQI)
     * @return 조회된 ETF 기본정보 VO (없으면 null)
     */
    EtfInfoVO selectEtfInfo(String symbol);

}