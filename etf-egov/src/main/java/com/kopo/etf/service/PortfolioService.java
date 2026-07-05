package com.kopo.etf.service;

import java.math.BigDecimal;
import java.util.Map;

import com.kopo.etf.vo.PortfolioVO;

public interface PortfolioService {
    Map<String, Object> getPortfolioData(BigDecimal exchangeRate);
    int addPortfolio(PortfolioVO vo);
    int removePortfolio(Long id);
    int updatePortfolio(Long id, BigDecimal investAmt);
}