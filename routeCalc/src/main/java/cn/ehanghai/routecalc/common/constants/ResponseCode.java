 package cn.ehanghai.routecalc.common.constants;

 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;

 public enum ResponseCode
 {
   SUCCESS(200, "成功!"),
   FAIL(999, "失败!"),
   EXCEPTION(401, "失败"),
   ERROR(2, "参数错误."),
   INVALID_TOKEN(90, "请重新登陆!");

   private static final ConcurrentMap<Integer, ResponseCode> CODE_MAP;
   private final int code;
   private final String message;

   private ResponseCode(int code, String message)
   {
     this.code = code;
     this.message = message;
   }

   public ResponseCode fromCode(Integer code) {
     return (ResponseCode)CODE_MAP.get(code);
   }

   public int getCode() {
     return this.code;
   }

   public String getMessage() {
     return this.message;
   }

   static
   {
     CODE_MAP = new ConcurrentHashMap(values().length);

     for (ResponseCode codeTable : values())
       CODE_MAP.put(Integer.valueOf(codeTable.code), codeTable);
   }
 }
