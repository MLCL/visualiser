/*
 * Copyright (c) 2012, Jeremy Reffin, University of Sussex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the University of Sussex nor the names of its
 *    contributors may be used to endorse or promote products  derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL JEREMY REFFIN OR THE UNIVERSITY OF SUSSEX
 *  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Coordinate interface
 */
package visualiser.distancemodel.coordinates;

/**
 * @author Jeremy Reffin V1.0 20110419
 */
public interface CoordI {

	/************************Accessors******************************/
	/**
	 * return coords
	 */
	public double[] getCoords();

	/**
	 * return coord[i]
	 */
	public double getCoord(int i);

	/**
	 * return dimensions
	 */
	public int getDimensions();

	/************************Mutators*******************************/
	/**
	 * set new coordinates. Input must match dimensions
	 */
	public void set(double[] c);

	/**
	 * scalar add. Input added to each dimension
	 */
	public void add(double f);

	/**
	 * element-wise add. Input must match dimensions of coordinate
	 */
	public void add(double[] c);

	/**
	 * Coord add. Dimensions must agree
	 */
	public void add(CoordI other);

	/**
	 * element-wise subtract. Input must match dimensions of coordinate
	 */
	public void subtract(double[] c);

	/**
	 * Coord subtract. Dimensions must agree
	 */
	public void subtract(CoordI other);

	/**
	 * Scalar multiply
	 */
	public void mult(double f);

	/**
	 * element-wise multiply. Input must match dimensions of coordinate
	 */
	public void mult(double[] c);

	/**
	 * Coord multiply. Dimensions must agree
	 */
	public void mult(CoordI other);

	/**
	 * Scalar multiply and return result as new coordinate. Don't change orig
	 */
	public CoordI getMult(double f);

	/**
	 * returns cosine of angle between point and x axis
	 */
	public double getCosTheta2D();

	/**
	 * returns sine of angle between point and x axis
	 */
	public double getSinTheta2D();

	/**
	 * counter-clockwise rotation by angle theta
	 */
	public void rotate2D(double sinTheta, double cosTheta);

}
