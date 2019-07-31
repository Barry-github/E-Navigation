package cn.ehanghai.routecheck.poi.domain;

import cn.ehanghai.routecheck.common.domain.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;


@Table(name = "nav_t_errorline_info")
public class ErrorLine extends BaseDomain {


    private   String startCode;
    private   String endCode;


    private  String errorMsg;

    public ErrorLine() {
    }


    public ErrorLine( String startCode, String endCode, String errorMsg) {

        this.startCode = startCode;
        this.endCode = endCode;

        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
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




}

