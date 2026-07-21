package com.kopo.etf.exchange.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kopo.etf.exchange.mapper.ExchangeRateMapper;
import com.kopo.etf.exchange.vo.ExchangeRateVO;

@Service("exchangeRateService")
public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Resource(name = "exchangeRateMapper")
    private ExchangeRateMapper exchangeRateMapper;

    @Override
    public ExchangeRateVO getLatestRate() {
        return exchangeRateMapper.selectLatestRate();
    }

}