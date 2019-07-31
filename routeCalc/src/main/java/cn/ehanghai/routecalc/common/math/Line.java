 package cn.ehanghai.routecalc.common.math;

 public class Line
 {
   private double _k;
   private double _theta;
   private boolean _verticalLine;
   private double _b;
   private double _verticalx;

   public Line(Epoint mpa, Epoint mpb)
   {
     GetLineInfo(mpa, mpb);
   }

   public Line(Epoint mpa, double k) {
     this._k = k;
     this._theta = (Math.atan(k) / 3.141592653589793D * 180.0D);
     if (this._theta < 0.0D) this._theta += 360.0D;
     this._b = (mpa.getY() - k * mpa.getX());
     this._verticalLine = false;
   }

   public Epoint Vertical(Epoint po)
   {
     if (this._verticalLine)
     {
       return new Epoint(this._verticalx, po.getY());
     }

     double x = (po.getX() - this._b * this._k + this._k * po.getY()) / (this._k * this._k + 1.0D);
     double y = this._k * x + this._b;
     return new Epoint(x, y);
   }

   public double PointToLineDis(Epoint po)
   {
     if (this._verticalLine)
     {
       return Math.abs(po.getX() - this._verticalx);
     }
     double d = Math.abs(this._k * po.getX() + this._b - po.getY()) / Math.sqrt(1.0D + this._k * this._k);
     return d;
   }
   private void GetLineInfo(Epoint mpa, Epoint mpb) {
     double xm = mpb.getX() - mpa.getX();
     double ym = mpb.getY() - mpa.getY();

     this._theta = (Math.atan2(ym, xm) / 3.141592653589793D * 180.0D);
     if (this._theta < 0.0D) this._theta += 360.0D;
     if (Math.abs(xm) > 1.E-005D)
     {
       this._k = (ym / xm);
       this._b = (mpb.getY() - this._k * mpb.getX());
       this._verticalLine = false;
     }
     else
     {
       this._verticalLine = true;
       this._b = 0.0D;
       this._verticalx = mpa.getX();
     }
   }

   public boolean LineMeet(Line line, Epoint meetMpoint)
   {
     boolean ismeet = Meet(line);
     if (ismeet)
     {
       if ((!this._verticalLine) && (!line._verticalLine))
       {
         double x = (line._b - this._b) / (this._k - line._k);
         double y = this._k * x + this._b;

         meetMpoint.setX(x);
         meetMpoint.setY(y);
       }
       else if ((!this._verticalLine) && (line._verticalLine))
       {
         double x = line.GetVerticalx();
         double y = this._k * x + this._b;

         meetMpoint.setX(x);
         meetMpoint.setY(y);
       }
       else if ((this._verticalLine) && (!line._verticalLine))
       {
         double x = this._verticalx;
         double y = line._k * x + line._b;

         meetMpoint.setX(x);
         meetMpoint.setY(y);
       }
       return true;
     }

     return false;
   }

   public boolean Meet(Line line)
   {
     if ((!this._verticalLine) && (!line._verticalLine))
     {
       if (Math.abs(this._k - line.GetK()) < 1.E-005D)
       {
         return false;
       }

     }

     return (!this._verticalLine) || (!line._verticalLine);
   }

   public boolean LineSegmentMeet(Epoint mpa, Epoint mpb, Epoint meetMpoint)
   {
     Line line = new Line(mpa, mpb);
     boolean ismeet = LineMeet(line, meetMpoint);
     return (ismeet) && (Epoint.PointInSegment(mpa, mpb, meetMpoint));
   }

   public static boolean DoubleSegmentMeet(Epoint spa, Epoint spb, Epoint epa, Epoint epb, Epoint meetMpoint) {
     Line line = new Line(spa, spb);
     boolean ismeet = line.LineSegmentMeet(epa, epb, meetMpoint);
     return (ismeet) && (Epoint.PointInSegment(spa, spb, meetMpoint));
   }

   private double GetK()
   {
     return this._k;
   }
   private double GetVerticalx() { return this._verticalx;
   }
 }

