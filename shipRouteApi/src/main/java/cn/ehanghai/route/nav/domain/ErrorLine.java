package cn.ehanghai.route.nav.domain;

import cn.ehanghai.route.common.domain.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Table;

import java.text.SimpleDateFormat;
import java.util.Date;

@Table(name = "nav_t_errorline_info")
public class ErrorLine  extends BaseDomain {


    private Long id;
    private   String startCode;
    private   String endCode;

    private Date time;

    private  String errorMsg;

    public ErrorLine() {
    }


    public ErrorLine(Long errorId, String startCode, String endCode, Date time, String errorMsg) {
        this.id = errorId;
        this.startCode = startCode;
        this.endCode = endCode;
        this.time = time;
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }


    public ErrorInfo toErrorInfo()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        String datetime = sdf.format(time);
      ErrorInfo info=new ErrorInfo( id,  startCode,  endCode,  datetime,  errorMsg);
      return info;
    }
}
