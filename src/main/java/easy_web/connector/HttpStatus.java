package easy_web.connector;

/**
 * Package:easy_web.connector
 * Description:
 *
 * @author:鲍嘉鑫
 */
public enum HttpStatus {
    SC_OK(200,"OK"),
    SC_NOT_FOUNT(404,"File Not Find");

    private int statusCode;
    private String reason;

    HttpStatus(int statusCode, String reason) {
        this.statusCode = statusCode;
        this.reason = reason;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReason() {
        return reason;
    }
}
