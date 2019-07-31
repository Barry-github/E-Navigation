package cn.ehanghai.routecheck.common.routelinecheck;


import cn.ehanghai.routecheck.poi.domain.ErrorLine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.ehanghai.routecheck.common.routelinecheck.PoiType.*;

public class RouteLineCheck {

	public static void main(String[] args) throws Exception {


		double lon=113.56834;
		double lat=22.20806;
Epoint point1=new Epoint(lon,lat);

		 lon=113.57194;
		 lat=22.18583;
		Epoint point2=new Epoint(lon,lat);

		point1=Epoint.LonLatToXY(point1);
		point2=Epoint.LonLatToXY(point2);



		lon=113.57694;
		lat=22.18889;
		Epoint point3=new Epoint(lon,lat);


		lon=113.50500;
		lat=22.16000;
		Epoint point4=new Epoint(lon,lat);


		point3=Epoint.LonLatToXY(point3);
		point4=Epoint.LonLatToXY(point4);

		Epoint meetpoint=new Epoint();
	boolean ismeet=	Line.DoubleSegmentMeet(point3,point4,point1,point2,meetpoint);


	}


	public static ErrorLine Check(RouteLineData  routeLineData, PoiData poiData)
	{

		Epoint startpoint=new Epoint(routeLineData.getStartpoint().getLon(),routeLineData.getStartpoint().getLat());
		Epoint endpoint=new Epoint(routeLineData.getEndpoint().getLon(),routeLineData.getEndpoint().getLat());
		startpoint=Epoint.LonLatToXY(startpoint);
		endpoint=Epoint.LonLatToXY(endpoint);

		List<Epoint> routelinepoints=new ArrayList<>();
		routelinepoints.add(startpoint);
		routelinepoints.add(endpoint);


		List<Epoint> poipoints=poiData.getLinepoints();
		PoiType poiType=poiData.getPoiType();

		if(poiType==bridge||poiType==aerialCable)
		{
			double shiphigh=routeLineData.getBaseLine().getHigh();
			if(poiData.getPoiAttr()==null) return  null;
			double poihigh=poiData.getPoiAttr().getHigh();
			String poiname=poiData.getPoiName();
			boolean isIntersect=IsIntersect(startpoint,endpoint, poipoints,false);
			if(isIntersect&&shiphigh>poihigh-0.1)
			{
				ErrorLine errorLine=new ErrorLine();
				errorLine.setStartCode(routeLineData.getStartpoint().getCode());
				errorLine.setEndCode(routeLineData.getEndpoint().getCode());
				errorLine.setErrorMsg("航线船高过于接近"+poiname+"高度，船高："+shiphigh+"米，"+poiname+"高度:"+poihigh+"米");

				return errorLine;
			}
		}
		else
		{

			ShapeType shapeType = GetShape(poiData.getShapeType());
			double shapethreshold = GetShapeThreshold(poiType);

			double mindis=Double.MAX_VALUE;
			double tmpmindis;
			boolean isnear=true;
			switch (shapeType) {
				case point:
					mindis= ToLinesDistance(poipoints.get(0), routelinepoints);
					break;

				case line:
					tmpmindis= ToLinesDistance(startpoint, poipoints);
					mindis=Math.min(mindis,tmpmindis);
					tmpmindis= ToLinesDistance(endpoint, poipoints);
					mindis=Math.min(mindis,tmpmindis);

					break;
				case area:

					boolean isin=IsInPolygonNormal(startpoint,poipoints);
					if(isin) {isnear=false;mindis=Double.MIN_VALUE;break;}
					isin=IsInPolygonNormal(endpoint,poipoints);
					if(isin) {isnear=false;mindis=Double.MIN_VALUE;break;}
					boolean isIntersect=IsIntersect(startpoint,endpoint, poipoints,true);
					if(isIntersect) {isnear=false;mindis=Double.MIN_VALUE;break;}

					tmpmindis= ToLinesDistance(startpoint, poipoints);
					mindis=Math.min(mindis,tmpmindis);
					tmpmindis= ToLinesDistance(endpoint, poipoints);
					mindis=Math.min(mindis,tmpmindis);
					break;

			}

			if (mindis < shapethreshold) {

				ErrorLine errorLine=new ErrorLine();
				String poiname=poiData.getPoiName();
				errorLine.setStartCode(routeLineData.getStartpoint().getCode());
				errorLine.setEndCode(routeLineData.getEndpoint().getCode());

				String text ="";
				if(isnear)
				{
					text ="航线过于接近"+poiname+"，最短距离为："+mindis+"米";

				}
				else
				{
					text ="航线穿插"+poiname+"";
				}
				errorLine.setErrorMsg(text);
				return errorLine;
			}
			else
			{
				return  null;
			}
		}

		return  null;
	}


	public static 	double GetShapeThreshold(PoiType poiType)
	{

		switch (poiType) {

			case noSailZone: // 禁航区

			case militaryRestrictedZone: // 军事禁区
			case missileGunFireZone:// 导弹火炮射击区
				return 500;
			case anchorage: // 锚地
			case suspectedMinefield:// 疑存雷区
				return 100;

			// 原则上外海：1.5海里。
			// 岛礁区、狭水道：视航道宽度而定：
			// 1. 1万吨船最小200米
			// 2. 5万吨船最小300米
			// 3. 10万吨船最小400米。

			case sunkenShip:// 沉船
			case rockawash://适淹礁
			case submergedReef:// 暗礁、

				return 200;

		}

		return 0;
	}

	public static 	ShapeType GetShape(int  poiType) {
		switch (poiType) {

			case 0: // 禁航区
				return ShapeType.point;
			case 1:
				return ShapeType.line;
			case 2:
				return  ShapeType.area;

		}

		return ShapeType.point;
	}



	private static double ToLinesDistance(Epoint shippos, List<Epoint> linepoints) {
		double mindis = Double.MAX_VALUE;
		for (int i = 0; i < linepoints.size() - 1; ++i) {
			Line line = new Line(linepoints.get(i), linepoints.get(i + 1));
			Epoint vpoint = line.Vertical(shippos);
			boolean isin = Epoint.PointInSegment(linepoints.get(i), linepoints.get(i + 1), vpoint);
			if (isin) {
				double distance = Epoint.Distance(shippos, vpoint);
				mindis = Math.min(mindis, distance);
			} else {
				double distance = Epoint.Distance(shippos, linepoints.get(i));
				mindis = Math.min(mindis, distance);
				distance = Epoint.Distance(shippos, linepoints.get(i + 1));
				mindis = Math.min(mindis, distance);
			}

		}

		return mindis;
	}


	private static	boolean  IsIntersect(Epoint startpoint,Epoint endpoint, List<Epoint> points,boolean isploygon)
	{
		if(points.size()<2) return false;
        Epoint meetpoint=new Epoint();
		for (int i = 0; i < points.size()-1; i++)
		{

			if(Line.DoubleSegmentMeet(startpoint,endpoint,points.get(i),points.get(i+1),meetpoint))
			{
				return true;
			}
		}

		if(isploygon&&Line.DoubleSegmentMeet(startpoint,endpoint,points.get(0),points.get(points.size()-1),meetpoint))
		{

			return true;
		}

		return false;


	}
	private static	boolean  IsInPolygonNormal(Epoint point, List<Epoint> points)
	{
		int nCross = 0;

		for (int i = 0; i < points.size(); i++)
		{
			Epoint p1 = points.get(i);
			Epoint p2 = points.get((i + 1) % points.size());

			if (Math.abs(p1.getY() - p2.getY()) < 1e-6) // p1p2 水平线
			{
				continue;
			}

			if (point.getY() < Math.min(p1.getY(), p2.getY())) // 交点在p1 p2延长线上
			{
				continue;
			}

			if (point.getY() >= Math.max(p1.getY(), p2.getY())) // 交点在p1p2延长线上
			{
				continue;
			}

			double x = (point.getY() - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY()) + p1.getX();

			if (x > point.getX())
			{
				nCross++;
			}
		}

		return nCross % 2 == 1;
	}
}
