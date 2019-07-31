package cn.ehanghai.routecheck.common.routelinecheck;

import ch.hsr.geohash.GeoHash;
import cn.ehanghai.routecheck.nav.domain.WaterDepth;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WaterDepthToDb {



    public  List<WaterDepth> readData(String filename, String resolution)
    {
        List<WaterDepth> result=new ArrayList<>();
        List<String> lines= readLines( filename);
        for (String line :
                lines) {
            String []items=   line.split("\t");
            List<Double> datas=new ArrayList<>();
            for (String item:items
                    ) {
                if(!item.equals(""))
                {
                    datas.add(Double.parseDouble(item));
                }
            }

            if(datas.size()==3)
            {
                double lon=datas.get(0);
                double lat=datas.get(1);

                GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lon, 12);
                String  text= geoHash.toBinaryString();
                result.add(new WaterDepth(lon,lat,datas.get(2),text,resolution));
            }
        }
        return result;
    }


    private static  List<String> readLines(String filename)
    {
        List<String> lines=new ArrayList<>();
        try
        {
            String tempstr=null;
            File file=new File(filename);
            if(!file.exists())
                throw new FileNotFoundException();

            FileInputStream fis=new FileInputStream(file);
            BufferedReader br=new BufferedReader(new InputStreamReader(fis));
            while((tempstr=br.readLine())!=null)
            {
                lines.add(tempstr);
            }
        }
        catch(IOException ex)
        {
            ex.getStackTrace();
        }
        return lines;
    }



    public  List<WaterDepth> readExcel(File file, String resolution)throws Exception
    {
        List<WaterDepth> result=new ArrayList<>();

            FileInputStream in = new FileInputStream(file);
            Workbook  workbook = new XSSFWorkbook(in);
            //开始解析
            Sheet sheet = workbook.getSheetAt(0);     //读取sheet 0

            int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
            int lastRowIndex = sheet.getLastRowNum();



            for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行

                Row row = sheet.getRow(rIndex);

                if (row != null) {
                    int firstCellIndex = row.getFirstCellNum();
                    String lonstr=row.getCell(firstCellIndex+1).toString().trim();
                    String latstr=row.getCell(firstCellIndex+2).toString().trim();
                    String depthstr=row.getCell(firstCellIndex+3).toString().trim();
                    double lon=Double.parseDouble(lonstr);
                    double lat=Double.parseDouble(latstr);
                    double depth=Double.parseDouble(depthstr);

                    GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lon, 12);
                    String  text= geoHash.toBinaryString();
                    result.add(new WaterDepth(lon,lat,depth,text,resolution));


                }
            }


            in.close();




        return result;

    }
}
