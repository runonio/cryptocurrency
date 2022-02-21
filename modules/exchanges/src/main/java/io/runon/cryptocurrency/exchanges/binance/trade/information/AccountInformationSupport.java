package io.runon.cryptocurrency.exchanges.binance.trade.information;

import com.binance.client.model.trade.AccountInformation;
import com.binance.client.model.trade.Leverage;
import com.binance.client.model.trade.Position;
import io.runon.cryptocurrency.exchanges.binance.trade.api.BinanceApiManager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountInformationSupport {
    AccountInformation accountInformation;
    private Map<String, BigDecimal> symbolLeverageMap;
    private static class SingleTonHolder{ private static final AccountInformationSupport INSTANCE = new AccountInformationSupport();}
    private AccountInformationSupport(){
        updateAccountInformation();
    }
    public static AccountInformationSupport getInstance(){return SingleTonHolder.INSTANCE;}

    private void updateSymbolLeverageMap() {
        symbolLeverageMap = new HashMap<>();
        List<Position> positions = accountInformation.getPositions();
        for (Position position : positions) {
            symbolLeverageMap.put(position.getSymbol(), position.getLeverage());
        }
    }

    /**
     * 코인의 레버리지를 변경 한다.
     * @param symbol 심볼
     * @param leverage 레버리지
     */
    public void updateSymbolLeverage(String symbol, BigDecimal leverage){
        Leverage changeLeverage = BinanceApiManager.getInstance().getApi().changeInitialLeverage(symbol,
                leverage.intValue());
        symbolLeverageMap.put(symbol, leverage);
    }

    /**
     * 계정 정보를 업데이트 한다.
     */
    public void updateAccountInformation(){
        this.accountInformation =
                BinanceApiManager.getInstance().getApi().getAccountInformation();
        updateSymbolLeverageMap();
    }

    /**
     * 코인의 레버리지를 얻어온다. (메모리)
     * @param symbol
     * @return
     */
    public BigDecimal getLeverage(String symbol){
        return symbolLeverageMap.get(symbol);
    }

    /**
     * 코인의 레버리지를 얻어온다. (실시간)
     * @param symbol
     * @return
     */
    public BigDecimal getLiveLeverage(String symbol){
        updateAccountInformation();
        return symbolLeverageMap.get(symbol);
    }

    public AccountInformation getAccountInformation() {
        return accountInformation;
    }

    public AccountInformation getLiveAccountInformation(){
        updateAccountInformation();
        return accountInformation;
    }
}
