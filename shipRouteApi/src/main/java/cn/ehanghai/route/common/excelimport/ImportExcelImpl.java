package cn.ehanghai.route.common.excelimport;

import cn.ehanghai.route.common.utils.LonLatUtil;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import tk.mybatis.mapper.entity.Example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class ImportExcelImpl {

    private  Workbook workbook;

    public static void main(String[] args) throws Exception {

       // String angle="123°06′22″";
       // ImportRouteNode.ParseAngle(angle);



       // test2();
        System.out.println("this is test.");
    }

    public static void test1()
    {
        ImportExcelImpl test = new ImportExcelImpl();
        String filePath="E:\\e航海\\航线规划\\航线编辑\\航线规划Beta版\\数据模板.xlsx";
        try {
            test.checkExcel(filePath);
            List<ImportRouteLine> lines=     test.readExcel();
            System.out.println("lines len:"+lines.size());
            for(int i=0;i<lines.size();++i)
            {

                lines.get(i). Print();
            }

        }
        catch ( Exception ex)
        {
            ex.printStackTrace();

        }

    }


    public static void test2() throws IOException {

        String template="F:\\SVN\\git\\shipRouteApi\\template\\template.xlsx";
        String filename="D:\\writeExcel.xlsx";

        File tempfile=new File(template);
        File excelfile=new File(filename);

        FileUtils.copyFile(tempfile, excelfile);


        ImportExcelImpl test = new ImportExcelImpl();
        String filePath="E:\\e航海\\航线规划\\航线编辑\\航线规划Beta版\\易航海制定的航线改2018.6.12全－最新.docx";
        List<Map> list=   test.testWord(filePath);
        test. writeExcel(list, 7, filename);
    }

    public  String checkExcel(String excelPath) throws Exception {

        File excel = new File(excelPath);
        if (excel.isFile() && excel.exists()) {   //判断文件是否存在

            String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！！！！！

            //根据文件后缀（xls/xlsx）进行判断
            FileInputStream fis = new FileInputStream(excel);   //文件流对象
            if ("xls".equals(split[1])) {
                workbook = new HSSFWorkbook(fis);
            } else if ("xlsx".equals(split[1])) {
                workbook = new XSSFWorkbook(fis);
            } else {

                return "文件类型错误!";
            }

            //开始解析
            Sheet sheet =  workbook.getSheetAt(0);     //读取sheet 0
            Row firstrow = sheet.getRow(0);
            return  checkModel(firstrow);
        }

        return "";
    }



    // 去读Excel的方法readExcel，该方法的入口参数为一个File对象
    public List<ImportRouteLine> readExcel()
    {
        List<ImportRouteLine> lines=new ArrayList<>();
        //开始解析
        Sheet sheet = workbook.getSheetAt(0);     //读取sheet 0

        int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
        int lastRowIndex = sheet.getLastRowNum();



        for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行

            Row row = sheet.getRow(rIndex);
            System.out.println("index="+rIndex);
            if(rIndex==205)
            {
                System.out.println("ok");
            }
            if (row != null) {

                int firstCellIndex = row.getFirstCellNum();
                int lastCellIndex = row.getLastCellNum();
                int cellnum=lastCellIndex-firstCellIndex;
                if(cellnum==7)
                {
                    String  name=row.getCell(firstCellIndex+0).toString().trim();
                    String nodeindex=row.getCell(firstCellIndex+1).toString().trim();
                    String angle=row.getCell(firstCellIndex+2).toString().trim();
                    String len=row.getCell(firstCellIndex+3).toString().trim();
                    String remark=row.getCell(firstCellIndex+4).toString().trim();
                    String lonstr=row.getCell(firstCellIndex+5).toString().trim();
                    String latstr=row.getCell(firstCellIndex+6).toString().trim();

                    if(name.equals("")&&nodeindex.equals("")&&angle.equals("")
                            &&len.equals("")&&remark.equals("")&&lonstr.equals("")&&latstr.equals(""))
                    {
                        continue;
                    }

                    ImportRouteNode node=new ImportRouteNode();
                    node.setName(name);

                    node.setNodeIndex((int)ParseDouble( nodeindex,"") );
                    node.setAngle(ImportRouteNode.ParseAngle(angle) );


                    node.setLen( ParseDouble( len,"NM"));

                    node.setRemark(remark);
                    double lon=ImportRouteNode.ParseAngle(lonstr);
                    double lat=ImportRouteNode.ParseAngle(latstr);
                    if(lon==-9999||lat==-9999)
                    {
                        lon=116.413;
                        lat=39.908;
                    }
                    else
                    {


                        if(Math.abs(lon)<90&&Math.abs(lat)>90)
                        {
                            double tmp=lon;
                            lon=lat;
                            lat=tmp;
                        }
                        else
                        {
                            if(Math.abs(lat)>90||Math.abs(lon)<90)
                            {
                                lon=116.413;
                                lat=39.908;
                            }

                        }

                    }
                    node.setLon(lon);
                    node.setLat(lat);
                    addNode( lines,  node);
                }

            }
        }


        return lines;
    }

 private    String checkModel(Row row) {



        int firstCellIndex = row.getFirstCellNum();
        int lastCellIndex = row.getLastCellNum();
        int cellnum=lastCellIndex-firstCellIndex;
        boolean right=true;
        if(cellnum==7) {
           String content= row.getCell(firstCellIndex+0).toString();
           if(!content.equals("起航点至到达点")) right=false;

            content= row.getCell(firstCellIndex+1).toString();
            if(!content.equals("航路点编号")) right=false;

            content= row.getCell(firstCellIndex+2).toString();
            if(!content.equals("航向")) right=false;

            content= row.getCell(firstCellIndex+3).toString();
            if(!content.equals("航程")) right=false;

            content= row.getCell(firstCellIndex+4).toString();
            if(!content.equals("备注")) right=false;

            content= row.getCell(firstCellIndex+5).toString();
            if(!content.equals("东经")) right=false;

            content= row.getCell(firstCellIndex+6).toString();
            if(!content.equals("北纬")) right=false;
        }
        else
        {
            right=false;
        }


        if(right==false)
        {

           return "模板错误，请检查模板";
        }

return "";

    }

    double ParseDouble(String content,String removeStr)
    {
        try{
            if(!removeStr.isEmpty())
            {
                content=content.replace(removeStr,"");
            }
            if(content.isEmpty())
            {
                return -9999;
            }
            else
            {
                return   Double.parseDouble(content);
            }
        }
        catch (Exception ex)
        {
            return -9999;
        }

    }


    private void addNode(   List<ImportRouteLine> lines, ImportRouteNode node)
    {

        ImportRouteLine line;
        if(lines.size()==0)
        {
            line=new ImportRouteLine();
            line.getNodes().add(node);
            lines.add(line);
        }
        else
        {
            line=lines.get(lines.size()-1);
            if(line.IsSameLine(node))
            {
                line.getNodes().add(node);
            }
            else
            {
                line=new ImportRouteLine();
                line.getNodes().add(node);
                lines.add(line);
            }
        }
    }

    private    List<Map> testWord(String filePath){
        List<Map> list=new ArrayList<Map>();

        Map<String, String> dataMap=new HashMap<String, String>();
//        dataMap.put("starttoend", "起航点至到达点");
//        dataMap.put("linecode", "航路点编号");
//        dataMap.put("angle", "航向");
//        dataMap.put("len", "航程");
//        dataMap.put("remark", "备注");
//        dataMap.put("lon", "东经");
//        dataMap.put("lat", "北纬");
//        list.add(dataMap);
        try{
            FileInputStream in = new FileInputStream(filePath);//载入文档
            //如果是office2007  docx格式
            if(filePath.toLowerCase().endsWith("docx")){
                //word 2007 图片不会被读取， 表格中的数据会被放在字符串的最后
                XWPFDocument xwpf = new XWPFDocument(in);//得到word文档的信息
//		    	 List<XWPFParagraph> listParagraphs = xwpf.getParagraphs();//得到段落信息
                Iterator<XWPFTable> it = xwpf.getTablesIterator();//得到word中的表格
                while(it.hasNext()){
                    XWPFTable table = it.next();
                    List<XWPFTableRow> rows=table.getRows();
                    //读取每一行数据
                    for (int i = 0; i < rows.size(); i++) {
                        XWPFTableRow row = rows.get(i);
                        //读取每一列数据
                        List<XWPFTableCell> cells = row.getTableCells();
                        dataMap=new HashMap<String, String>();
                        dataMap.put("starttoend", cells.get(1).getText());
                        dataMap.put("linecode", cells.get(2).getText());
                        dataMap.put("angle", cells.get(3).getText());
                        dataMap.put("len", cells.get(4).getText());
                        dataMap.put("remark",cells.get(5).getText());
                        dataMap.put("lon", cells.get(6).getText());
                        dataMap.put("lat", cells.get(7).getText());
                        list.add(dataMap);
//                        for (int j = 0; j < cells.size(); j++) {
//                            XWPFTableCell cell=cells.get(j);
//                            //输出当前的单元格的数据
//                            System.out.println(cell.getText());
//                        }
//                        break;
                    }

                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }



    public  void writeExcel(List<Map> dataList, int cloumnCount,String finalXlsxPath){
        OutputStream out = null;
        try {
            // 获取总列数
            // int columnNumCount = cloumnCount;
            // 读取Excel文档
            File finalXlsxFile = new File(finalXlsxPath);
            Workbook workBook = getWorkbok(finalXlsxFile);
            // sheet 对应一个工作页
            Sheet sheet = workBook.getSheetAt(0);
            /**
             * 删除原有数据，除了属性列
             */
            int rowNumber = sheet.getLastRowNum();    // 第一行从0开始算
            System.out.println("原始数据总行数，除属性列：" + rowNumber);
            for (int i = 1; i <=rowNumber; i++) {
                Row row = sheet.getRow(i);
                sheet.removeRow(row);
            }
            // 创建文件输出流，输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out =  new FileOutputStream(finalXlsxPath);
            workBook.write(out);
            /**
             * 往Excel中写新数据
             */
            for (int j = 0; j < dataList.size(); j++) {
                // 创建一行：从第二行开始，跳过属性列
                Row row = sheet.createRow(j);
                // 得到要插入的每一条记录
                Map dataMap = dataList.get(j);

                String starttoend = dataMap.get("starttoend").toString();
                String linecode = dataMap.get("linecode").toString();
                String angle = dataMap.get("angle").toString();
                String len = dataMap.get("len").toString();
                String remark = dataMap.get("remark").toString();
                String lon = dataMap.get("lon").toString();
                String lat = dataMap.get("lat").toString();

//                for (int k = 0; k <= columnNumCount; k++)
                {
                    // 在一行内循环
                    Cell cell = row.createCell(0);
                    cell.setCellValue(starttoend);

                    cell = row.createCell(1);
                    cell.setCellValue(linecode);

                    cell = row.createCell(2);
                    cell.setCellValue(angle);

                    cell = row.createCell(3);
                    cell.setCellValue(len);

                    cell = row.createCell(4);
                    cell.setCellValue(remark);

                    cell = row.createCell(5);
                    cell.setCellValue(lon);

                    cell = row.createCell(6);
                    cell.setCellValue(lat);

                }
            }
            // 创建文件输出流，准备输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
            out =  new FileOutputStream(finalXlsxPath);
            workBook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(out != null){
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("数据导出成功");
    }

    /**
     * 判断Excel的版本,获取Workbook
     * @return
     * @throws IOException
     */
    public  Workbook getWorkbok(File file) throws IOException {

        String EXCEL_XLS = "xls";
        String EXCEL_XLSX = "xlsx";


        Workbook wb = new XSSFWorkbook();
        FileInputStream in = new FileInputStream(file);
        // FileOutputStream fos=new FileOutputStream(file);
        if(file.getName().endsWith(EXCEL_XLS)){     //Excel&nbsp;2003
            wb = new HSSFWorkbook(in);
        }else if(file.getName().endsWith(EXCEL_XLSX)){    // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }


}
