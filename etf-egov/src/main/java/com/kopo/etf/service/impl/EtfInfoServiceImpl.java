package com.kopo.etf.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kopo.etf.mapper.EtfDividendMapper;
import com.kopo.etf.mapper.EtfInfoMapper;
import com.kopo.etf.service.EtfInfoService;
import com.kopo.etf.vo.EtfDividendVO;
import com.kopo.etf.vo.EtfInfoVO;

/**
 * ETF 조회 관련 비즈니스 로직 구현체 (화면1)
 */
@Service("etfInfoService")
public class EtfInfoServiceImpl implements EtfInfoService {

    @Resource(name = "etfInfoMapper")
    private EtfInfoMapper etfInfoMapper;

    @Resource(name = "etfDividendMapper")
    private EtfDividendMapper etfDividendMapper;

    @Override
    public EtfInfoVO getEtfInfo(String symbol) {
        return etfInfoMapper.selectEtfInfo(symbol);
    }

    @Override
    public List<EtfDividendVO> getRecentDividends(String symbol) {
        return etfDividendMapper.selectRecentDividends(symbol);
    }
    
    @Override
    public List<EtfInfoVO> getAllEtfList() {
        return etfInfoMapper.selectAllEtfList();
    }

}