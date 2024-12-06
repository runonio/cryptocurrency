package io.runon.cryptocurrency.trading;

import com.google.common.collect.ImmutableSet;
import io.runon.commons.config.Config;

import java.util.Set;

/**
 * @author macle
 */
public class Markets {

    public static final String [] MARKET_ARRAY = Config.getConfig("cryptocurrency.markets", "USDT,BUSD,BNB,DAI,ALTS,AUD,BIDR,BRL,EUR,GBP,RUB,TRY,TUSD,USDC,UAH,VAI,IDRT,NGN,USDP").split(",");

    public final static Set<String> MARKET_SET = ImmutableSet.<String>builder()
           .add(MARKET_ARRAY)
           .build();

    //시장 이면서 종목인것
    public static final String [] MARKET_SYMBOL_ARRAY = Config.getConfig("cryptocurrency.market.symbol", "BTC,ETH").split(",");


    /**
     * 시장 지표를 계산할때 필요한 종목인지 여부
     * @param symbol 종목 symbol
     * @return 시장지표에 필요한 종목이면 true
     */
    public static boolean isMarketIndexSymbol(String symbol){
        //시장이면서 종목이면

        for(String marketSymbol : MARKET_SYMBOL_ARRAY){
            if(marketSymbol.equals(symbol)) {
                return true;
            }
        }

        return !MARKET_SET.contains(symbol);
    }

}
