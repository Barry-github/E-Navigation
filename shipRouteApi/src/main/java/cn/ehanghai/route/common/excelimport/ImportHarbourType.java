package cn.ehanghai.route.common.excelimport;

public class ImportHarbourType {

    private  String name;

    private  String code;

    public ImportHarbourType(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public ImportHarbourType() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
