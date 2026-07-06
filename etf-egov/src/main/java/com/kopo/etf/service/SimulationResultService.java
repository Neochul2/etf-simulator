package com.kopo.etf.service;

import com.kopo.etf.vo.SimulationResultVO;

/**
 * 시뮬레이션 결과 저장 Service 인터페이스
 * Controller는 구현체가 아닌 이 인터페이스에만 의존 (DI 원칙)
 */
public interface SimulationResultService {

    
    void saveSimulationResult(SimulationResultVO vo);
}