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
 * A tuple object that defins a coordinate. Contains methods for simple
 * manipulations
 */
package visualiser.distancemodel.coordinates;

import visualiser.distancemodel.exceptions.WrongDimensionsException;

import java.util.Arrays;

/**
 * @author Jeremy Reffin
 *         V2.0 20110822
 */
public class Coordinate implements CoordI {
	/**
	 * **********************FIELDS*******************************
	 */
	private double[] coord;
	private int dimensions;

	/***********Constructors and Static Factory Methods*************/
	/**
	 * Constructor dimensions only
	 */
	public Coordinate(int d) {
		dimensions = d;
		coord = new double[dimensions];
		Arrays.fill(coord, 0);
	}

	/**
	 * Constructor dimensions and values specified
	 */
	public Coordinate(int d, double[] c) {
		dimensions = d;
		coord = new double[dimensions];
		System.arraycopy(c, 0, coord, 0, dimensions);
	}

	/************************Accessors******************************/
	/**
	 * return coords
	 */
	public double[] getCoords() {
		return coord;
	}

	/**
	 * return coord[i]
	 */
	public double getCoord(int i) {
		return coord[i];
	}

	/**
	 * return dimensions
	 */
	public int getDimensions() {
		return dimensions;
	}

	/**
	 * return a copy of the coords
	 */
	public double[] cloneCoords() {
		double copy[] = (double[]) coord.clone();       //Works for 1d array
		return copy;
	}

	/************************Mutators*******************************/

	/**
	 * set new coordinates. Input must match dimensions
	 */
	public void set(double[] c) throws WrongDimensionsException {
		checkDimensions(c.length);                      //may throw exception
		coord = c;
	}

	/**
	 * reset coordinates to zero
	 */
	public void reset() {
		Arrays.fill(coord, 0);
	}

	/**
	 * scalar add. Input added to each dimension
	 */
	public void add(double f) {
		for (int i = 0; i < dimensions; i++) {
			coord[i] += f;
		}
	}

	/**
	 * element-wise add. Input must match dimensions of coordinate
	 */
	public void add(double[] c) throws WrongDimensionsException {
		checkDimensions(c.length);                      //may throw exception
		for (int i = 0; i < dimensions; i++) {
			coord[i] += c[i];
		}
	}

	/**
	 * Coord add. Dimensions must agree
	 */
	public void add(CoordI other) throws WrongDimensionsException {
		checkDimensions(other.getDimensions());         //may throw exception
		for (int i = 0; i < dimensions; i++) {
			coord[i] += other.getCoord(i);
		}
	}

	/**
	 * element-wise subtract. Input must match dimensions of coordinate
	 */
	public void subtract(double[] c) throws WrongDimensionsException {
		checkDimensions(c.length);                      //may throw exception
		for (int i = 0; i < dimensions; i++) {
			coord[i] -= c[i];
		}
	}

	/**
	 * Coord subtract. Dimensions must agree
	 */
	public void subtract(CoordI other) throws WrongDimensionsException {
		checkDimensions(other.getDimensions());         //may throw exception
		for (int i = 0; i < dimensions; i++) {
			coord[i] -= other.getCoord(i);
		}
	}

	/**
	 * Scalar multiply
	 */
	public void mult(double f) {
		for (int i = 0; i < dimensions; i++) {
			coord[i] *= f;
		}
	}

	/**
	 * element-wise multiply. Input must match dimensions of coordinate
	 */
	public void mult(double[] c) throws WrongDimensionsException {
		checkDimensions(c.length);                      //may throw exception
		for (int i = 0; i < dimensions; i++) {
			coord[i] *= c[i];
		}
	}

	/**
	 * Coord multiply. Dimensions must agree
	 */
	public void mult(CoordI other) throws WrongDimensionsException {
		checkDimensions(other.getDimensions());         //may throw exception
		for (int i = 0; i < dimensions; i++) {
			coord[i] *= other.getCoord(i);
		}
	}

	/**
	 * Scalar multiply and return result as new coordinate. Don't change orig
	 */
	public CoordI getMult(double f) {
		double[] values = new double[dimensions];
		for (int i = 0; i < dimensions; i++) {
			values[i] = coord[i] * f;
		}
		return new Coordinate(dimensions, values);
	}

	/**
	 * returns cosine of angle between point and x axis
	 */
	public double getCosTheta2D() {
		double hyp = Math.sqrt((coord[0] * coord[0]) + (coord[1] * coord[1]));
		return (coord[0] / hyp);
	}

	/**
	 * returns sine of angle between point and x axis
	 */
	public double getSinTheta2D() {
		double hyp = Math.sqrt((coord[0] * coord[0]) + (coord[1] * coord[1]));
		return (coord[1] / hyp);
	}

	/**
	 * counter-clockwise rotation by angle theta
	 */
	public void rotate2D(double sinTheta, double cosTheta) {
		double oldx = coord[0];
		coord[0] = (cosTheta * coord[0]) + (sinTheta * coord[1]);
		coord[1] = -(sinTheta * oldx) + (cosTheta * coord[1]);
	}

	/**
	 * reflect in y-axis
	 */
	public void reflectXAxis() {
		coord[1] *= -1;
	}

	/**
	 * ****************Standard Overrides*************************
	 */
	@Override
	public String toString() {
		String output = "[";
		for (int i = 0; i < (dimensions - 1); i++) {
			output += coord[i] + ", ";
		}
		output += coord[dimensions - 1] + "]";
		return output;
	}

	/*********************Utility Methods***************************/

	/**
	 * Check for incorrect number of dimensions on input
	 *
	 * @param dim number of dimensions of input
	 * @throws WrongDimensionsException
	 */
	private void checkDimensions(int dim) throws WrongDimensionsException {
		if (dim != dimensions) {
			throw new WrongDimensionsException("Wrong number of simensions");
		}
	}

	/**
	 * main method as test utility
	 */
	public static void main(String args[]) {

		//Test constructors
		Coordinate newOne = new Coordinate(3);
		Coordinate newTwo = new Coordinate(3);
		double[] c = new double[] {0.85, 0.25, 0.1};
		Coordinate newThree = new Coordinate(3, c);
		System.out.println(newOne);
		System.out.println(newTwo);
		System.out.println(newThree);

		//Test accessors
		double[] test = newThree.getCoords();
		System.out.println("3rd coord: " + test[2]);
		System.out.println("3rd coord: " + newThree.getCoord(2));

		//Test mutators
		newOne.add(2);
		newOne.mult(5);
		System.out.println("Revised newOne: " + newOne);
		double[] a = new double[] {6, 11, 5};
		newOne.add(a);
		System.out.println("Revised newOne: " + newOne);
		newOne.mult(a);
		System.out.println("Revised newOne: " + newOne);
	}

}


/*************************Fields********************************/
/***********Constructors and Static Factory Methods*************/
/************************Accessors******************************/
/************************Mutators*******************************/
/*******************Standard Overrides**************************/
/*********************Utility Methods***************************/
