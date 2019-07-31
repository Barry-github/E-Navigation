 package cn.ehanghai.routecalc.common.utils;

 import cn.ehanghai.routecalc.common.constants.ResponseCode;

 public class ResponseBean
 {
   private int code;
   private String msg;
   private Object data;

   public ResponseBean(int code, String msg, Object data)
   {
     this.code = code;
     this.msg = msg;
     this.data = data;
   }

   public ResponseBean(ResponseCode responseCode, Object data) {
     this.code = responseCode.getCode();
     this.msg = responseCode.getMessage();
     this.data = data;
   }

   public int getCode() {
     return this.code;
   }

   public void setCode(int code) {
     this.code = code;
   }

   public String getMsg() {
     return this.msg;
   }

   public void setMsg(String msg) {
     this.msg = msg;
   }

   public Object getData() {
     return this.data;
   }

   public void setData(Object data) {
     this.data = data;
   }
 }

