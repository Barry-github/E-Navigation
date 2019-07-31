package cn.ehanghai.route.common.excelimport;

import ch.hsr.geohash.GeoHash;
import cn.ehanghai.route.nav.domain.Harbour;
import cn.ehanghai.route.nav.domain.TDepth;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ImportDepth {


    public static List<TDepth> ParseFile(File file) throws Exception {
        Workbook workbook=null;
        if (file.isFile() && file.exists()) {   //判断文件是否存在

            String[] split = file.getName().split("\\.");  //.是特殊字符，需要转义！！！！！

            //根据文件后缀（xls/xlsx）进行判断
            FileInputStream fis = new FileInputStream(file);   //文件流对象
            if ("xls".equals(split[1])) {
                workbook = new HSSFWorkbook(fis);
            } else if ("xlsx".equals(split[1])) {
                workbook = new XSSFWorkbook(fis);
            } else {

//                return "文件类型错误!";
            }

            //开始解析
            Sheet sheet =  workbook.getSheetAt(0);     //读取sheet 0
            Row firstrow = sheet.getRow(0);
            boolean check=  checkModel(firstrow);

            if(check)
            {
                return readExcel( workbook);
            }

        }

        return  new ArrayList<>();
    }

    private static   boolean checkModel(Row row) {
        int firstCellIndex = row.getFirstCellNum();
        int lastCellIndex = row.getLastCellNum();
        int cellnum=lastCellIndex-firstCellIndex;
        boolean right=true;
        if(cellnum>=4) return true;
        else return false;
    }

    // 去读Excel的方法readExcel，该方法的入口参数为一个File对象
    private static List<TDepth> readExcel(Workbook workbook)
    {
        List<TDepth> datas=new ArrayList<>();
        //开始解析
        Sheet sheet = workbook.getSheetAt(0);     //读取sheet 0

        int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
        int lastRowIndex = sheet.getLastRowNum();

        for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
            Row row = sheet.getRow(rIndex);
            if (row != null)
            {
                Cell celllon,celllat,celldep;
                celllon=row.getCell(1);
                celllat=row.getCell(2);
                celldep=row.getCell(3);

                if(celllon!=null&&celllat!=null&&celldep!=null) {
                    TDepth dep=new TDepth();
                    double lon=Double.parseDouble(celllon.toString().trim());
                    double lat=Double.parseDouble(celllat.toString().trim());
                    dep.setLon(lon);
                    dep.setLat(lat);
                    dep.setDepth(Double.parseDouble(celldep.toString().trim()));

                    GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lon, 12);
                    dep.setGeohash(geoHash.toBinaryString());
                    datas.add(dep);
                }
            }
        }

        return datas;
    }


}
