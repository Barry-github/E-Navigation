package cn.ehanghai.routecalc.nav.action;

//成山角至杭州湾 各分道通航 案例
//1、首先整理 分道通航 的航线 起始点。
//2、判断经过了那条 线路，， 找到位置后 依次外延

import cn.ehanghai.routecalc.common.utils.Tuple;
import cn.ehanghai.routecalc.nav.domain.LinePoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChengShanAndHangZhouCase {

    private List<Tuple<String,String>> upLines;
    private List<Tuple<String,String>> downLines;
    private Tuple<Double,Double> latRnage;
    public  ChengShanAndHangZhouCase()
    {
        upLines=new ArrayList<>();
        upLines.add(new Tuple<>("E-0007734","E-0006579"));
        upLines.add(new Tuple<>("E-0007940","E-0006677"));
        upLines.add(new Tuple<>("E-0007940","E-0001869"));

        downLines=new ArrayList<>();
        downLines.add(new Tuple<>("E-0006580","E-0007729"));
        downLines.add(new Tuple<>("E-0006678","E-0007931"));
        downLines.add(new Tuple<>("E-0002500","E-0007931"));

        latRnage=new Tuple<>(30.815833333333334,37.118611111111115);

    }


    public  List<Tuple<String,String>> findCodes(List<String> path, LinePoint start, LinePoint end){

       List<Tuple<String,String>> result;

        if(start.getLat()<end.getLat())
        {
            result=  findCodes( path,upLines);
        }
        else
        {
            result=  findCodes( path,downLines);
        }
        return  result;

    }

    private  List<Tuple<String,String>> findCodes(List<String> path, List<Tuple<String,String>> lines)
    {
        List<Tuple<String,String>> result=new ArrayList<>();
        int index=0;
        for(int i=0;i<lines.size();++i)
        {
            String code=lines.get(i).first;
            Optional<String> findop= path.stream().filter(a->a.equals(code)).findFirst();

            if(findop.isPresent())
            {
                index=i;
                break;
            }
        }

        for(int k=index;k<lines.size();++k)
        {
            result.add(lines.get(k));
        }
        return  result;
    }


    public  boolean check(LinePoint start, LinePoint end){

        double ra=(latRnage.first-start.getLat())*(latRnage.first-end.getLat());
        double rb=(latRnage.second-start.getLat())*(latRnage.second-end.getLat());

        if(ra<0&&rb<0)
        {
            return true;
        }
        return false;
    }


    public  boolean checkInner(LinePoint start, LinePoint end)
    {
        double ra=(start.getLat()-latRnage.first)*(start.getLat()-latRnage.second);
        double rb=(end.getLat()-latRnage.first)*(end.getLat()-latRnage.second);
        if(ra<0||rb<0)
        {
            return  true;
        }
        return  false;
    }


}
