package top.cheivin.grpc.core;


import lombok.Data;
import lombok.ToString;

/**
 * 调用返回结果
 */
@Data
@ToString
public class GrpcResponse {

    /**
     * 响应状态
     */
    private int status;

    /**
     * 信息提示
     */
    private String message;

    /**
     * 返回结果
     */
    private Object result;

    public enum Status {

        SUCCESS(0), ERROR(-1);

        private int code;

        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static GrpcResponse error(String message) {
            GrpcResponse res = new GrpcResponse();
            res.message = message;
            res.status = ERROR.code;
            return res;
        }

        public static GrpcResponse success(Object result) {
            GrpcResponse res = new GrpcResponse();
            res.result = result;
            res.status = SUCCESS.code;
            return res;
        }
    }
}

