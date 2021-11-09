package io.runon.cryptocurrency.service;

import java.math.BigDecimal;

/**
 * 서비스 환율 관리
 * 싱글턴
 * @author macle
 */
public class ExchangeRateManager {
    private static class Singleton {
        private static final ExchangeRateManager instance = new ExchangeRateManager();
    }

    public static ExchangeRateManager getInstance(){
        return Singleton.instance;
    }

    private ExchangeRateManager(){
        
    }

    BigDecimal usdDivideKrw = new BigDecimal(1100);

    /**
     * 원 달러 환율
     * @return 원달러 환율 얻기
     */
    public BigDecimal getUsdDivideKrw(){
        return usdDivideKrw;
    }
    
    

}
