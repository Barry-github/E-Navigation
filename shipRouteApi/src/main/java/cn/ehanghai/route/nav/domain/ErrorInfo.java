package cn.ehanghai.route.nav.domain;


public class ErrorInfo {
    private Long errorId;
    private   String startCode;
    private   String endCode;

    private String time;

    private  String errorMsg;

    public ErrorInfo() {
    }

    public ErrorInfo(Long errorId, String startCode, String endCode, String time, String errorMsg) {
        this.errorId = errorId;
        this.startCode = startCode;
        this.endCode = endCode;
        this.time = time;
        this.errorMsg = errorMsg;
    }

    public Long getErrorId() {
        return errorId;
    }

    public void setErrorId(Long errorId) {
        this.errorId = errorId;
    }

    public String getStartCode() {
        return startCode;
    }

    public void setStartCode(String startCode) {
        this.startCode = startCode;
    }

    public String getEndCode() {
        return endCode;
    }

    public void setEndCode(String endCode) {
        this.endCode = endCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
