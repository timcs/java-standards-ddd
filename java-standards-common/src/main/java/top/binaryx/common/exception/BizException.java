package top.binaryx.common.exception;

import lombok.Data;

@Data
public class BizException extends RuntimeException {
    private Integer code;

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
