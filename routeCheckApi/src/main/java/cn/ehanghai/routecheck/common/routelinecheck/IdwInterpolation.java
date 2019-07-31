package cn.ehanghai.routecheck.common.routelinecheck;



import cn.ehanghai.routecheck.nav.domain.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class IdwInterpolation {


    public static double ShepardInterpolation(List<GeoPoint> points, GeoPoint point)
    {

        List<double[]> inputdatas=new ArrayList<>();
        for (GeoPoint epoint :
                points  ) {
            double[] array=new double[3];
            array[0]=epoint.getX();
            array[1]=epoint.getY();
            array[2]=epoint.getZ();
            inputdatas.add(array);
        }

        double [] dpoint=new double[2];
        dpoint[0]=point.getX();
        dpoint[1]=point.getY();

        return   ShepardInterpolation(inputdatas,  dpoint, 1.4);
    }



    private static double ShepardInterpolation(List<double[]> inputDatas, double[] point, double p)
    {
        double sumdisv = 0;
        double sumdis = 0;
        for (int i = 0; i < inputDatas.size(); ++i)
        {
            double disx = inputDatas.get(i)[0] - point[0];
            double disy = inputDatas.get(i)[1] - point[1];
            double dis = Math.sqrt(disx * disx + disy * disy);
            dis = Math.pow(dis, p);
            sumdisv += inputDatas.get(i)[2] / dis;
            sumdis += 1.0 / dis;
        }
        return sumdisv / sumdis;
    }
    private static double ShepardInterpolationWd(List<double[]> inputDatas, double[] point, double raduis)
    {
        double sumdisv = 0;
        double sumdis = 0;
        for (int i = 0; i < inputDatas.size(); ++i)
        {
            double disx = inputDatas.get(i)[0] - point[0];
            double disy = inputDatas.get(i)[1] - point[1];

            double dis = Math.sqrt(disx * disx + disy * disy);
            double wd = Wd(dis, raduis);

            sumdisv += wd * inputDatas.get(i)[2];
            sumdis += wd;
        }
        return sumdisv / sumdis;
    }
    private static double Wd(double dis, double raduis)
    {
        if (dis > 0 && dis <= raduis / 3.0)
        {
            return 1.0 / dis;
        }
        if (dis > raduis / 3.0 && dis <= raduis)
        {
            return (27.0 / (4 * raduis)) * Math.pow((dis / raduis - 1.0), 2.0);
        }
        return 0;
    }
}
