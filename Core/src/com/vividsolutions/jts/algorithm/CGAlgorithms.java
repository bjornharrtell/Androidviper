/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */
package com.vividsolutions.jts.algorithm;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Specifies and implements various fundamental Computational Geometric algorithms.
 * The algorithms supplied in this class are robust for double-precision floating point.
 *
 * @version 1.7
 */
public class CGAlgorithms
{
  /**
   * Returns the index of the direction of the point <code>q</code>
   * relative to a
   * vector specified by <code>p1-p2</code>.
   *
   * @param p1 the origin point of the vector
   * @param p2 the final point of the vector
   * @param q the point to compute the direction to
   *
   * @return 1 if q is counter-clockwise (left) from p1-p2
   * @return -1 if q is clockwise (right) from p1-p2
   * @return 0 if q is collinear with p1-p2
   */
  public static int orientationIndex(Coordinate p1, Coordinate p2, Coordinate q) {
    // travelling along p1->p2, turn counter clockwise to get to q return 1,
    // travelling along p1->p2, turn clockwise to get to q return -1,
    // p1, p2 and q are colinear return 0.
    double dx1 = p2.x - p1.x;
    double dy1 = p2.y - p1.y;
    double dx2 = q.x - p2.x;
    double dy2 = q.y - p2.y;
    return RobustDeterminant.signOfDet2x2(dx1, dy1, dx2, dy2);
  }

  public CGAlgorithms() {
  }
  
  /**
   * Computes the distance from a point p to a line segment AB
   *
   * Note: NON-ROBUST!
   *
   * @param p the point to compute the distance for
   * @param A one point of the line
   * @param B another point of the line (must be different to A)
   * @return the distance from p to line segment AB
   */
  public static double distancePointLine(Coordinate p, Coordinate A, Coordinate B)
  {
    // if start==end, then use pt distance
    if (  A.equals(B) ) return p.distance(A);

    // otherwise use comp.graphics.algorithms Frequently Asked Questions method
    /*(1)     	      AC dot AB
                   r = ---------
                         ||AB||^2
		r has the following meaning:
		r=0 P = A
		r=1 P = B
		r<0 P is on the backward extension of AB
		r>1 P is on the forward extension of AB
		0<r<1 P is interior to AB
	*/

    double r = ( (p.x - A.x) * (B.x - A.x) + (p.y - A.y) * (B.y - A.y) )
              /
            ( (B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y) );

    if (r <= 0.0) return p.distance(A);
    if (r >= 1.0) return p.distance(B);


    /*(2)
		     (Ay-Cy)(Bx-Ax)-(Ax-Cx)(By-Ay)
		s = -----------------------------
		             	L^2

		Then the distance from C to P = |s|*L.
	*/

    double s = ((A.y - p.y) *(B.x - A.x) - (A.x - p.x)*(B.y - A.y) )
              /
            ((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y) );

    return
      Math.abs(s) *
      Math.sqrt(((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y)));
  }
  
  /**
   * Computes the perpendicular distance from a point p
   * to the (infinite) line containing the points AB
   *
   * @param p the point to compute the distance for
   * @param A one point of the line
   * @param B another point of the line (must be different to A)
   * @return the distance from p to line AB
   */
  public static double distancePointLinePerpendicular(Coordinate p, Coordinate A, Coordinate B)
  {
    // use comp.graphics.algorithms Frequently Asked Questions method
    /*(2)
                     (Ay-Cy)(Bx-Ax)-(Ax-Cx)(By-Ay)
                s = -----------------------------
                                     L^2

                Then the distance from C to P = |s|*L.
        */

    double s = ((A.y - p.y) *(B.x - A.x) - (A.x - p.x)*(B.y - A.y) )
              /
            ((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y) );

    return
      Math.abs(s) *
      Math.sqrt(((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y)));
  }
	
  /**
   * Computes the distance from a line segment AB to a line segment CD
   *
   * Note: NON-ROBUST!
   *
   * @param A a point of one line
   * @param B the second point of  (must be different to A)
   * @param C one point of the line
   * @param D another point of the line (must be different to A)
   */
  public static double distanceLineLine(Coordinate A, Coordinate B, Coordinate C, Coordinate D)
  {
    // check for zero-length segments
    if (  A.equals(B) )	return distancePointLine(A,C,D);
    if (  C.equals(D) )	return distancePointLine(D,A,B);

    // AB and CD are line segments
    /* from comp.graphics.algo

	Solving the above for r and s yields
				(Ay-Cy)(Dx-Cx)-(Ax-Cx)(Dy-Cy)
	           r = ----------------------------- (eqn 1)
				(Bx-Ax)(Dy-Cy)-(By-Ay)(Dx-Cx)

		 	(Ay-Cy)(Bx-Ax)-(Ax-Cx)(By-Ay)
		s = ----------------------------- (eqn 2)
			(Bx-Ax)(Dy-Cy)-(By-Ay)(Dx-Cx)
	Let P be the position vector of the intersection point, then
		P=A+r(B-A) or
		Px=Ax+r(Bx-Ax)
		Py=Ay+r(By-Ay)
	By examining the values of r & s, you can also determine some other
limiting conditions:
		If 0<=r<=1 & 0<=s<=1, intersection exists
		r<0 or r>1 or s<0 or s>1 line segments do not intersect
		If the denominator in eqn 1 is zero, AB & CD are parallel
		If the numerator in eqn 1 is also zero, AB & CD are collinear.

	*/
    double r_top = (A.y-C.y)*(D.x-C.x) - (A.x-C.x)*(D.y-C.y) ;
    double r_bot = (B.x-A.x)*(D.y-C.y) - (B.y-A.y)*(D.x-C.x) ;

    double s_top = (A.y-C.y)*(B.x-A.x) - (A.x-C.x)*(B.y-A.y);
    double s_bot = (B.x-A.x)*(D.y-C.y) - (B.y-A.y)*(D.x-C.x);

    if  ( (r_bot==0) || (s_bot == 0) ) {
      return
        Math.min(distancePointLine(A,C,D),
	  Math.min(distancePointLine(B,C,D),
	    Math.min(distancePointLine(C,A,B),
	      distancePointLine(D,A,B)    ) ) );

    }
    double s = s_top/s_bot;
    double r=  r_top/r_bot;

    if ((r < 0) || ( r > 1) || (s < 0) || (s > 1) )	{
      //no intersection
      return
        Math.min(distancePointLine(A,C,D),
	  Math.min(distancePointLine(B,C,D),
	    Math.min(distancePointLine(C,A,B),
	      distancePointLine(D,A,B)    ) ) );
    }
    return 0.0; //intersection exists
  }
  
}
