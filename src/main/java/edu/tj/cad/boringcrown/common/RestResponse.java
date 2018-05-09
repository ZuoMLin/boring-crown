package edu.tj.cad.boringcrown.common;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created by zuomlin
 */
@Data
public class RestResponse<T> implements Serializable {

    /**
     * 请求是否成功
     */
    private boolean success;

    /**
     * 请求相关文案
     */
    private String msg;

    /**
     * 具体内容
     */
    private T content;

    public RestResponse(T content) {
        this.content = content;
        this.success = true;
        this.msg = StringUtils.EMPTY;
    }

    public RestResponse(boolean success, String msg, T content) {
        this.success = success;
        this.msg = msg;
        this.content = content;
    }


}
