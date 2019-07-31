package cn.ehanghai.routecheck.common.routelinecheck;

import java.util.Map;

public class Line {
	/// <summary>
	/// 斜率
	/// </summary>
	private	double _k;
	/// <summary>
	/// 斜率角度
	/// </summary>
	private	double _theta;
	/// <summary>
	/// 是否垂直
	/// </summary>
	private	boolean _verticalLine;
	/// <summary>
	/// 截距
	/// </summary>
	private	double _b;
	/// <summary>
	/// 垂直X的值--x=x0
	/// </summary>
	private	double _verticalx;


	public Line(Epoint mpa, Epoint mpb){

		GetLineInfo(mpa, mpb);
	}
	public Line(Epoint mpa, double k){

		_k = k;
		_theta =  Math.atan(k) / Math.PI * 180.0;
		if (_theta < 0) _theta = _theta + 360;
		_b = mpa.getY() - k * mpa.getX();
		_verticalLine = false;
	}

	public  Epoint Vertical(Epoint po)
	{
		if (_verticalLine)
		{
			return new   Epoint(_verticalx, po.getY());
		}

		double x = (po.getX() - _b * _k + _k * po.getY()) / (_k * _k + 1);
		double y = _k * x + _b;
		return new  Epoint(x, y);

	}


	public double PointToLineDis(Epoint po){
		if (_verticalLine)
		{
			return  Math.abs(po.getX() - _verticalx);
		}
		double d =  Math.abs(_k * po.getX() + _b - po.getY()) /  Math.sqrt(1 + _k * _k);
		return d;
	}
	private void GetLineInfo(Epoint mpa, Epoint mpb){
		double xm = mpb.getX() - mpa.getX();
		double ym = mpb.getY() - mpa.getY();

		//_theta = atan2(xm, ym) / Point::EPI * 180.0;
		_theta =  Math.atan2(ym, xm) /  Math.PI * 180.0;
		if (_theta < 0) _theta = _theta + 360;
		if ( Math.abs(xm) > 1e-5)
		{
			_k = ym / xm;
			_b = mpb.getY() - _k * mpb.getX();
			_verticalLine = false;
		}
		else
		{
			_verticalLine = true;
			_b = 0;
			_verticalx = mpa.getX();
		}


	}

	public boolean	LineMeet(Line line, Epoint meetMpoint){
		boolean ismeet = Meet(line);
		if (ismeet)
		{
			if (_verticalLine == false && line._verticalLine == false)
			{
				double x = (line._b - _b) / (_k - line._k);
				double y = _k * x + _b;
				//meetMpoint =new  Epoint(x, y);
				meetMpoint.setX(x);
				meetMpoint.setY(y);
			}
			else if (_verticalLine == false && line._verticalLine)
			{
				double x = line.GetVerticalx();
				double y = _k * x + _b;
				//meetMpoint =new  Epoint(x, y);
				meetMpoint.setX(x);
				meetMpoint.setY(y);
			}
			else if (_verticalLine && line._verticalLine == false)
			{
				double x = _verticalx;
				double y = line._k * x + line._b;
				//meetMpoint =new  Epoint(x, y);
				meetMpoint.setX(x);
				meetMpoint.setY(y);
			}
			return true;
		}

		return false;

	}


	public boolean Meet(Line line){
		if (_verticalLine == false && line._verticalLine == false)
		{
			if (Math.abs(_k - line.GetK()) < 1e-5)
			{
				return false;
			}
		}
		if (_verticalLine && line._verticalLine)
		{
			return false;
		}
		return true;
	}

	public boolean LineSegmentMeet(Epoint mpa, Epoint mpb,Epoint meetMpoint)
	{
		Line line=new Line(mpa, mpb);
		boolean ismeet = LineMeet(line, meetMpoint);
		return ismeet && Epoint.PointInSegment(mpa, mpb, meetMpoint);
	}
	public static boolean DoubleSegmentMeet(Epoint spa,Epoint spb, Epoint epa, Epoint epb, Epoint meetMpoint)
	{
		Line line=new Line(spa, spb);
		boolean ismeet = line.LineSegmentMeet(epa, epb, meetMpoint);
		return ismeet && Epoint.PointInSegment(spa, spb, meetMpoint);
	}


	private double GetK()
	{
		return _k;
	}
	private	double GetVerticalx(){ return _verticalx; }

}
