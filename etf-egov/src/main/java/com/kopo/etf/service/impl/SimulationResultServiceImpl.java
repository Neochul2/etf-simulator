package com.kopo.etf.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kopo.etf.mapper.SimulationResultMapper;
import com.kopo.etf.service.SimulationResultService;
import com.kopo.etf.vo.SimulationResultVO;

/**
 * 시뮬레이션 결과 저장 Service 구현체
 * @Service("simulationResultService") — Spring Bean 이름 명시
 * Controller에서 @Resource(name="simulationResultService")로 주입받음
 */
@Service("simulationResultService")
public class SimulationResultServiceImpl implements SimulationResultService {

    /**
     * MyBatis Mapper 주입
     * MapperScannerConfigurer가 SimulationResultMapper를 Bean으로 등록
     */
    @Resource(name = "simulationResultMapper")
    private SimulationResultMapper simulationResultMapper;

    /**
     * 시뮬레이션 결과 INSERT
     * Mapper XML의 insertSimulationResult 호출
     * useGeneratedKeys로 저장 후 vo.id에 생성된 PK 자동 주입됨
     */
    @Override
    public void saveSimulationResult(SimulationResultVO vo) {
        simulationResultMapper.insertSimulationResult(vo);
    }
}