package io.runon.cryptocurrency.trading.exception;

/**
 * 데이터 stream 관리
 * @author macle
 */
public class AlreadyStreamException extends RuntimeException{
    /**
     * 생성자
     */
    public AlreadyStreamException(){
        super();
    }

    /**
     * 생성자
     * @param e 예외
     */
    public AlreadyStreamException(Exception e){
        super(e);
    }

    /**
     * 생성자
     * @param message exception message
     */
    public AlreadyStreamException(String message){
        super(message);
    }
}
