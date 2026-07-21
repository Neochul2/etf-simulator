package com.kopo.etf.info.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kopo.etf.info.mapper.EtfDividendMapper;
import com.kopo.etf.info.mapper.EtfInfoMapper;
import com.kopo.etf.info.vo.EtfInfoVO;
import com.kopo.etf.vo.EtfDividendVO;

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