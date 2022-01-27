package io.runon.cryptocurrency.trading.exception;
/**
 * 아이디가 알려진 패턴이 아닐때 예외
 * @author macle
 */
public class IdNotPatternException extends RuntimeException{
    /**
     * 생성자
     */
    public IdNotPatternException(){
        super();
    }

    /**
     * 생성자
     * @param e 예외
     */
    public IdNotPatternException(Exception e){
        super(e);
    }

    /**
     * 생성자
     * @param message exception message
     */
    public IdNotPatternException(String message){
        super(message);
    }
}