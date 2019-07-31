package cn.ehanghai.routecheck.nav.action;

import ch.hsr.geohash.GeoHash;
import cn.ehanghai.routecheck.common.routelinecheck.Epoint;
import cn.ehanghai.routecheck.common.routelinecheck.IdwInterpolation;
import cn.ehanghai.routecheck.nav.domain.GeoPoint;
import cn.ehanghai.routecheck.nav.domain.WaterDepth;
import cn.ehanghai.routecheck.nav.service.WaterDepthService;
import org.springframework.beans.factory.support.ManagedMap;

import java.util.*;
import java.util.stream.Collectors;

public class WaterDepthCalc {

    private  WaterDepthService waterDepthService;
    private  List<WaterDepth> allDatas;
    private  double NoData=-9999;


    public  double BatchCalc(double startlon, double startlat, double endlon, double endlat)
    {
        if(allDatas==null)
        {
            allDatas=waterDepthService.getAllData("50W");
        }

       double depth= CalcWaterDepth( startlon,  startlat,  endlon,  endlat, true);
        return depth;
    }

    public  double NoBatchCalc(double startlon, double startlat, double endlon, double endlat)
    {
        double depth= CalcWaterDepth( startlon,  startlat,  endlon,  endlat, false);
        return depth;
    }


    private double CalcWaterDepth(double startlon, double startlat, double endlon, double endlat,boolean batch)
    {

        double step=1000;//1公里
        Epoint startp=new Epoint(startlon,startlat);
        startp=  Epoint.LonLatToXY(startp);
        Epoint endp=new Epoint(endlon,endlat);
        endp=Epoint.LonLatToXY(endp);

        double distance=Epoint.Distance(startp,endp);

        Epoint vec=new Epoint(endp.getX()-startp.getX(),endp.getY()-startp.getY());

        Epoint unitvec=new Epoint(vec.getX()/distance,vec.getY()/distance);

        List<Epoint> epoints=new ArrayList<>();

        int num=(int)(distance/step);
        double left=distance-num*step;
        if(left>0) num=num+1;
        for(int i=0;i<num;++i)
        {
            double len=step*i;
            double x=unitvec.getX()*len+startp.getX();
            double y=unitvec.getY()*len+startp.getY();
            Epoint tmp=new Epoint(x,y);
            //tmp= Epoint.XYToLonLat(tmp);
            epoints.add(tmp);
        }
        if(left>0)
        {
            epoints.add(endp);
        }

        if(epoints.size()<=0)
        {
            System.out.println("distance"+distance+"-points:0");
        }

        List<GeoPoint> geoPoints=new ArrayList<>();
        HashMap<String,List<Epoint>> geomaps=new HashMap<>();
        for (Epoint epoint : epoints) {
            Epoint lonlatp = Epoint.XYToLonLat(epoint);
            GeoHash geoHash = GeoHash.withCharacterPrecision(lonlatp.getY(), lonlatp.getX(), 12);

            String text = geoHash.toBinaryString();
//            String geohashcontent = text.substring(0, 23);
            String geohashcontent = text.substring(0, 15);
            if(geomaps.containsKey(geohashcontent))
            {
                geomaps.get(geohashcontent).add(epoint);
            }
            else
            {
                List<Epoint> list=new ArrayList<>();
                list.add(epoint);
                geomaps.put(geohashcontent,list);
            }
        }

        for(String geohashcontent:geomaps.keySet()) {
            List<Epoint> listpoints = geomaps.get(geohashcontent);
            List<WaterDepth> waterDepths = getNearPoints(geohashcontent, batch);

            for (Epoint epoint : listpoints) {
                Epoint lonlatp = Epoint.XYToLonLat(epoint);
//                GeoHash geoHash = GeoHash.withCharacterPrecision(lonlatp.getY(), lonlatp.getX(), 12);
//
//                String text = geoHash.toBinaryString();
//                String geohashcontent = text.substring(0, 23);

                List<GeoPoint> depthpoints = new ArrayList<>();
                for (WaterDepth depth : waterDepths) {
                    Epoint real = new Epoint(depth.getLon(), depth.getLat());
                    Epoint mpoint = Epoint.LonLatToXY(real);
                    double dis = Epoint.Distance(mpoint, epoint);
                    depthpoints.add(new GeoPoint(mpoint.getX(), mpoint.getY(), depth.getDepth(), dis));
                }

                if (depthpoints.size() == 0) continue;

                if (depthpoints.size() > 5) {
                    Collections.sort(depthpoints, (o1, o2) ->
                    {
                        double value = o1.getDistance() - o2.getDistance();
                        if (value > 0) return 1;
                        if (value < 0) return -1;
                        return 0;
                    });

                    depthpoints = depthpoints.subList(0, 5);
                }

                GeoPoint mcheckpoint = new GeoPoint(epoint.getX(), epoint.getY(), 0, 0);
                double depth = IdwInterpolation.ShepardInterpolation(depthpoints, mcheckpoint);

                geoPoints.add(new GeoPoint(lonlatp.getX(), lonlatp.getY(), depth, 0));
            }
        }


        double depth=NoData;

        if(geoPoints.size()>0)
        {
            DoubleSummaryStatistics stats=geoPoints.stream().filter(a->!Double.isNaN( a.getZ())).mapToDouble((x)->x.getZ()).summaryStatistics();
            double mindepth= stats.getMin();
            if(!Double.isNaN(mindepth))
            {
                depth= mindepth;
            }
        }

        return depth;

    }


    private   List<WaterDepth> getNearPoints(String geohashcontent,boolean batch)
    {
        List<WaterDepth> datas;
        if(batch)
        {
        datas=   allDatas.stream().filter(a->a.getGeohash().startsWith(geohashcontent)).collect(Collectors.toList());
        }
        else
        {

            datas=  waterDepthService.getNearPoints(geohashcontent,"50W");
        }


        return datas;
    }


    public WaterDepthService getWaterDepthService() {
        return waterDepthService;
    }

    public void setWaterDepthService(WaterDepthService waterDepthService) {
        this.waterDepthService = waterDepthService;
    }
}
