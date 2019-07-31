package cn.ehanghai.route.common.excelimport;

import cn.ehanghai.route.nav.domain.Harbour;
import cn.ehanghai.route.nav.domain.LinePoint;
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



public class ImportPoiHabour
{

    public static List<Harbour> ParseFile(String file) throws Exception {
        Workbook workbook=null;
        File excel = new File(file);
        if (excel.isFile() && excel.exists()) {   //判断文件是否存在

            String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！

            //根据文件后缀（xls/xlsx）进行判断
            FileInputStream fis = new FileInputStream(excel);   //文件流对象
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
        if(cellnum>=2) return true;
        else return false;
    }


    // 去读Excel的方法readExcel，该方法的入口参数为一个File对象
    private static List<Harbour> readExcel(Workbook workbook)
    {
        List<Harbour> datas=new ArrayList<>();
        //开始解析
        Sheet sheet = workbook.getSheetAt(0);     //读取sheet 0

        int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
        int lastRowIndex = sheet.getLastRowNum();

        int code=100001;


        for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
            Row row = sheet.getRow(rIndex);
            if (row != null)
            {
                    Cell cellName,cellPos;
                    cellName=row.getCell(0);
                    cellPos=row.getCell(1);

                    if(cellName!=null&&cellPos!=null) {
                        Harbour har=new Harbour();
                        har.setName(cellName.toString().trim());
                        har.setLinePointCode("A-0"+code++);
                        har.setValid(1);
                        String[] hPos = cellPos.toString().split(",");
                        if(hPos.length>0)har.setNameLon(Double.parseDouble(hPos[0].trim()));
                        if(hPos.length>1)har.setNameLat(Double.parseDouble(hPos[1].trim()));
                        datas.add(har);
                    }
            }
        }

        return datas;
    }


}
