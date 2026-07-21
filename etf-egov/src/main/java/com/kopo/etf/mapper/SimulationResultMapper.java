package com.kopo.etf.mapper;

import com.kopo.etf.simulation.vo.SimulationResultVO;

/**
 * simulation_result 테이블 Mapper 인터페이스
 * 실제 SQL은 simulation-result-mapper.xml에 정의
 * context-mapper.xml의 MapperScannerConfigurer가 자동으로 Bean 등록
 */
public interface SimulationResultMapper {

    /**
     * 시뮬레이션 결과 1건 INSERT
     * saved_at은 DB DEFAULT(CURRENT_TIMESTAMP)로 자동 처리
     * @param vo 저장할 시뮬레이션 결과 데이터
     */
    void insertSimulationResult(SimulationResultVO vo);
}