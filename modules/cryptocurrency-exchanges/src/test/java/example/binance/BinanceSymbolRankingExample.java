package example.binance;

import io.runon.cryptocurrency.exchanges.binance.BinanceExchange;
import io.runon.trading.symbol.SymbolInteger;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author macle
 */
public class BinanceSymbolRankingExample {
    public static void main(String[] args) {
        Map<String, Integer> rankingMap = BinanceExchange.getSymbolRankingMap();
        Set<String> keys = rankingMap.keySet();

        SymbolInteger[] symbolIntegers = new SymbolInteger[rankingMap.size()];
        int index = 0;
        for(String key: keys){
            symbolIntegers[index++] = new SymbolInteger(key, rankingMap.get(key));
        }

        Arrays.sort(symbolIntegers, SymbolInteger.SORT);
        for(SymbolInteger symbolInteger : symbolIntegers){
            System.out.println("symbol: " + symbolInteger.getSymbol() +", ranking: " + symbolInteger.getNumber());
        }

    }
}
