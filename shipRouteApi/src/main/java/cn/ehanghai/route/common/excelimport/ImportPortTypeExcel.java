package cn.ehanghai.route.common.excelimport;

import net.ryian.commons.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ImportPortTypeExcel {


    public static void main(String[] args) throws Exception {
        String file="E:\\资料\\航线规划\\航线规划系统\\航线规划Beta版\\港口类型20190316.xlsx";
        List<ImportHarbourType> datas= ParseFile( file);
        System.out.println("datas.size() = " + datas.size());

    }


    public static   List<ImportHarbourType> ParseFile( String file) throws Exception {
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




    private static    boolean checkModel(Row row) {



        int firstCellIndex = row.getFirstCellNum();
        int lastCellIndex = row.getLastCellNum();
        int cellnum=lastCellIndex-firstCellIndex;
        boolean right=true;
        if(cellnum>=9) {    //yulj:李乐乐说暂时只需要前三种类型
            String content= row.getCell(firstCellIndex+0).toString();
            if(!content.equals("集装箱码头")) right=false;

            content= row.getCell(firstCellIndex+1).toString();
            if(!content.equals("编号")) right=false;

            content= row.getCell(firstCellIndex+2).toString();
            if(!content.equals("备注")) right=false;

            content= row.getCell(firstCellIndex+3).toString();
            if(!content.equals("散杂货码头")) right=false;

            content= row.getCell(firstCellIndex+4).toString();
            if(!content.equals("编号")) right=false;

            content= row.getCell(firstCellIndex+5).toString();
            if(!content.equals("备注")) right=false;

            content= row.getCell(firstCellIndex+6).toString();
            if(!content.equals("油船码头")) right=false;

            content= row.getCell(firstCellIndex+7).toString();
            if(!content.equals("编号")) right=false;

            content= row.getCell(firstCellIndex+8).toString();
            if(!content.equals("备注")) right=false;
        }
        else
        {
            right=false;
        }


        return right;

    }


    // 去读Excel的方法readExcel，该方法的入口参数为一个File对象
    private static List<ImportHarbourType> readExcel(Workbook workbook)
    {
        List<ImportHarbourType> datas=new ArrayList<>();
        //开始解析
        Sheet sheet = workbook.getSheetAt(0);     //读取sheet 0

        int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
        int lastRowIndex = sheet.getLastRowNum();



        for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行

            Row row = sheet.getRow(rIndex);
//            System.out.println("index="+rIndex);
//            if(rIndex==205)
//            {
//                System.out.println("ok");
//            }
            if (row != null) {

                int firstCellIndex = 0;//row.getFirstCellNum();
                int lastCellIndex = 8;//row.getLastCellNum();
                int cellnum=lastCellIndex-firstCellIndex;
                System.out.println("cellnum = " + cellnum);
                if(cellnum>=8)
                {
                    Cell cellName,cellCode;

                    cellName=row.getCell(firstCellIndex+0);
                    cellCode=row.getCell(firstCellIndex+1);
                    if(cellName!=null&&cellCode!=null)
                        datas.add(new ImportHarbourType("集装箱码头",cellCode.toString().trim()));

                    cellName=row.getCell(firstCellIndex+3);
                    cellCode=row.getCell(firstCellIndex+4);
                    if(cellName!=null&&cellCode!=null)
                        datas.add(new ImportHarbourType("散杂货码头",cellCode.toString().trim()));

                    cellName=row.getCell(firstCellIndex+6);
                    cellCode=row.getCell(firstCellIndex+7);
                    if(cellName!=null&&cellCode!=null)
                        datas.add(new ImportHarbourType("油船码头",cellCode.toString().trim()));
                    //String  name=row.getCell(firstCellIndex+0).toString().trim();
                    //String code=row.getCell(firstCellIndex+1).toString().trim();

/*
                    if(!StringUtils.isEmpty(name)&&!StringUtils.isEmpty(code))
                    datas.add(new ImportHarbourType("集装箱码头",code));

                      name=row.getCell(firstCellIndex+3).toString().trim();
                     code=row.getCell(firstCellIndex+4).toString().trim();

                    if(!StringUtils.isEmpty(name)&&!StringUtils.isEmpty(code))
                        datas.add(new ImportHarbourType("散杂货码头",code));

                      name=row.getCell(firstCellIndex+6).toString().trim();
                     code=row.getCell(firstCellIndex+7).toString().trim();

                    if(!StringUtils.isEmpty(name)&&!StringUtils.isEmpty(code))
                        datas.add(new ImportHarbourType("油船码头",code));*/


                }

            }
        }


        return datas;
    }



}
