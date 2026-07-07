package com.kopo.etf.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kopo.etf.mapper.ExchangeRateMapper;
import com.kopo.etf.service.ExchangeRateService;
import com.kopo.etf.vo.ExchangeRateVO;

@Service("exchangeRateService")
public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Resource(name = "exchangeRateMapper")
    private ExchangeRateMapper exchangeRateMapper;

    @Override
    public ExchangeRateVO getLatestRate() {
        return exchangeRateMapper.selectLatestRate();
    }

}