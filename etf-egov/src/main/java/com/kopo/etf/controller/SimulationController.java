package com.kopo.etf.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import com.kopo.etf.service.DripCalculatorService;
import com.kopo.etf.service.EtfInfoService;
import com.kopo.etf.service.SimulationResultService;
import com.kopo.etf.vo.DripResultVO;
import com.kopo.etf.vo.EtfInfoVO;
import com.kopo.etf.vo.SimulationResultVO;

@Controller
public class SimulationController {

    @Resource(name = "etfInfoService")
    private EtfInfoService etfInfoService;

    @Resource(name = "dripCalculatorService")
    private DripCalculatorService dripCalculatorService;

    /** 시뮬레이션 결과 저장 Service 주입 */
    @Resource(name = "simulationResultService")
    private SimulationResultService simulationResultService;

    /** 화면3 진입 */
    @GetMapping("/etf/simulator.do")
    public String simulator() {
        return "simulation";
    }

    /**
     * 시뮬레이션 계산 실행
     * simulation.jsp에서 Ajax GET 호출 → DripResultVO JSON 응답
     */
    @GetMapping("/etf/{symbol}/simulate.do")
    @ResponseBody
    public DripResultVO simulate(
            @PathVariable("symbol") String symbol,
            @RequestParam("initialAmount") BigDecimal initialAmountUsd,
            @RequestParam(value = "monthlyAmount", defaultValue = "0") BigDecimal monthlyAmountUsd,
            @RequestParam("months") int months,
            @RequestParam("isMonthlyInvest") boolean isMonthlyInvest) {

        EtfInfoVO info = etfInfoService.getEtfInfo(symbol);
        return dripCalculatorService.simulate(
                initialAmountUsd, monthlyAmountUsd,
                info.getDivYield(), months, isMonthlyInvest);
    }

    /**
     * 시뮬레이션 결과 저장
     * simulation.jsp 결과 저장 버튼 클릭 → Ajax POST 호출
     * @ModelAttribute로 요청 파라미터를 VO에 자동 매핑
     * PathVariable(symbol)만 별도로 세팅
     */
    @PostMapping("/etf/{symbol}/simulation/save.do")
    @ResponseBody
    public Map<String, Object> saveSimulation(
            @PathVariable("symbol") String symbol,
            @ModelAttribute SimulationResultVO vo) {

        // PathVariable은 @ModelAttribute 자동 매핑 범위 밖이라 직접 세팅
        vo.setSymbol(symbol);

        simulationResultService.saveSimulationResult(vo);

        // 저장 성공 응답 (생성된 PK 포함)
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("savedId", vo.getId());
        return result;
    }
}