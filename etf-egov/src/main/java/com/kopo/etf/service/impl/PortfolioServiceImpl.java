package com.kopo.etf.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.kopo.etf.mapper.EtfInfoMapper;
import com.kopo.etf.mapper.PortfolioMapper;
import com.kopo.etf.service.PortfolioService;
import com.kopo.etf.vo.EtfInfoVO;
import com.kopo.etf.vo.PortfolioVO;

@Service("portfolioService")
public class PortfolioServiceImpl implements PortfolioService {

    @Resource(name = "portfolioMapper")
    private PortfolioMapper portfolioMapper;

    @Resource(name = "etfInfoMapper")
    private EtfInfoMapper etfInfoMapper;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.154");
    private static final BigDecimal TWELVE   = new BigDecimal("12");
    private static final BigDecimal HUNDRED  = new BigDecimal("100");

    @Override
    public Map<String, Object> getPortfolioData(BigDecimal exchangeRate) {
        List<PortfolioVO> list = portfolioMapper.selectPortfolioList();

        BigDecimal totalAmt          = BigDecimal.ZERO;
        BigDecimal totalMonthlyDiv   = BigDecimal.ZERO;
        BigDecimal totalMonthlyDivUsd = BigDecimal.ZERO;

        for (PortfolioVO vo : list) {
            totalAmt = totalAmt.add(vo.getInvestAmt());

            EtfInfoVO etfInfo = etfInfoMapper.selectEtfInfo(vo.getSymbol());
            if (etfInfo != null) {
                vo.setIssuer(etfInfo.getIssuer());
                vo.setDivYield(etfInfo.getDivYield());

                // USD 환산
                BigDecimal investUsd = vo.getInvestAmt()
                        .divide(exchangeRate, 2, RoundingMode.HALF_UP);
                vo.setInvestUsd(investUsd);

                // 세후 월배당금 = 투자금(USD) × 배당률/100 × (1-0.154) / 12
                BigDecimal monthlyDivUsd = investUsd
                        .multiply(etfInfo.getDivYield()
                                .divide(HUNDRED, 10, RoundingMode.HALF_UP))
                        .multiply(BigDecimal.ONE.subtract(TAX_RATE))
                        .divide(TWELVE, 4, RoundingMode.HALF_UP);
                vo.setMonthlyDivUsd(monthlyDivUsd);
                vo.setMonthlyDiv(monthlyDivUsd
                        .multiply(exchangeRate)
                        .setScale(0, RoundingMode.HALF_UP));

                totalMonthlyDivUsd = totalMonthlyDivUsd.add(monthlyDivUsd);
                totalMonthlyDiv    = totalMonthlyDiv.add(vo.getMonthlyDiv());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("portfolioList",       list);
        result.put("totalAmt",            totalAmt);
        result.put("totalAmtUsd",         totalAmt.divide(exchangeRate, 2, RoundingMode.HALF_UP));
        result.put("totalMonthlyDiv",     totalMonthlyDiv);
        result.put("totalMonthlyDivUsd",  totalMonthlyDivUsd);
        result.put("exchangeRate",        exchangeRate);
        return result;
    }

    @Override
    public int addPortfolio(PortfolioVO vo) {
        return portfolioMapper.insertPortfolio(vo);
    }

    @Override
    public int removePortfolio(Long id) {
        return portfolioMapper.deletePortfolio(id);
    }

    @Override
    public int updatePortfolio(Long id, BigDecimal investAmt) {
        return portfolioMapper.updatePortfolio(id, investAmt);
    }
}