package sunny.zzhao.zzhao.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * 返回结果对象
 */
@Setter
@Getter
public class ResultResponse<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> ResultResponse<T> success(T data) {
        return new ResultResponse<>(0, "success", data);
    }

    public static <T> ResultResponse<T> fail(Integer code, String msg) {
        return new ResultResponse<>(code, msg, null);
    }

    public ResultResponse() {
    }

    public ResultResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

}
