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
 * Each instance is a term for display on a similarity diagram with position,
 * velocity, acceleraion, and mass.
 */
package visualiser.distancemodel.terms;

import visualiser.distancemodel.coordinates.Coordinate;

import java.text.DecimalFormat;
import java.util.Random;

import static visualiser.distancemodel.GlobalParameters.DEFAULT_MASS;

/**
 * @author Jeremy Reffin
 *         V2.0 20110822
 */
public class Term implements TermI {

	/**
	 * ********************Constants*******************************
	 */
	public static final int DEFAULT_INDEX = 0;           //constructor modified
	public static final int DEFAULT_CLOCK = 0;           //fixed at present
	public static final String DEFAULT_IDENTIFIER = "";  //constructor modified

	/**
	 * **********************Fields*******************************
	 */
	private String identifier;                      //label for term
	private int arrayIndex;                 //term's index in an array of terms
	private Coordinate beforePosition;              //position (x,y,..) b4 move
	private Coordinate afterPosition;               //position (x,y,..) aftr move
	private Coordinate velocity;                    //velcoity (vx,vy,..)
	private Coordinate acceleration;                //acceleration (ax,ay,..)
	private double mass;                            //"mass" of term
	private int internalClock;                      //internal clock
	private int dimensions;                         //dimensions of diagram
	private boolean include;                        //flag for inclusion

	/**
	 * ********Constructors and Static Factory Methods************
	 */
	/* Input parameters are:
		 * - number of dimensions for the similarity diagram
		 * - term's index (when part of an array of terms)
		 * - term's identification label
		 * - coordinates on the similarity diagram.
		 * These are set to defaults when not specified
		 * (coordinates are random within range -1 to +1).
		 */
	public Term(int dim) {
		dimensions = dim;
		setUp(DEFAULT_INDEX, DEFAULT_IDENTIFIER, randomCoords());
	}

	public Term(int dim, int arrayIndex) {
		dimensions = dim;
		setUp(arrayIndex, "", randomCoords());
	}

	public Term(int dim, int arrayIndex, String identifier) {
		dimensions = dim;
		setUp(arrayIndex, identifier, randomCoords());
	}

	public Term(int dim, int arrayIndex, String identifier, double[] coord) {
		dimensions = dim;
		setUp(arrayIndex, identifier, coord);
	}

	/**
	 * *********************Accessors*****************************
	 */
	/*Methods return coordinates, velocity, acceleration, mass, clock, array */
	public double[] getPosition() {
		return beforePosition.getCoords();
	}

	public double[] getAfterPosition() {
		return afterPosition.getCoords();
	}

	public double[] cloneAfterPosition() {
		return afterPosition.cloneCoords();
	}

	public double[] getVelocity() {
		return velocity.getCoords();
	}

	public double[] getAcceleration() {
		return acceleration.getCoords();
	}

	public double getMass() {
		return mass;
	}

	public int getInternalClock() {
		return internalClock;
	}

	public int getArray() {
		return arrayIndex;
	}

	public boolean isIncluded() {
		return include;
	}

	public String getIdentifier() {
		return identifier;
	}

	public double getCosTheta2D() {
		return afterPosition.getCosTheta2D();
	}

	public double getSinTheta2D() {
		return afterPosition.getSinTheta2D();
	}

	/**
	 * *********************Mutators******************************
	 */
	/*Methods set coordinates, velocity, mass, internal clock  */
	public void setPosition(double[] newCoords) {
		afterPosition.set(newCoords);
	}

	public void setVelocity(double[] newVelocity) {
		velocity.set(newVelocity);
	}

	public void setMass(double newMass) {
		mass = newMass;
	}

	public void setInternalClock(int c) {
		internalClock = c;
	}

	/**
	 * Advance one time tick and change settings accordingly
	 */
	public void advanceTime() {
		internalClock++;
		beforePosition.set(afterPosition.cloneCoords()); //Correct for 1d array
	}

	/**
	 * Shift coordinates (usually to impose a reference frame)
	 */
	public void shiftCoords(double[] shift) {
		afterPosition.subtract(shift);
	}

	/**
	 * Rotate coordinates (imposing orientation frame)
	 */
	public void rotateCoords2D(double sinTheta, double cosTheta) {
		afterPosition.rotate2D(sinTheta, cosTheta);
	}

	/**
	 * Reflect in x-axis
	 */
	public void reflectXAxis() {
		afterPosition.reflectXAxis();
	}

	/**
	 * set the term to be included
	 */
	public void include() {
		include = true;
	}

	/**
	 * set the term to be excluded
	 */
	public void exclude() {
		include = false;
	}

	/**
	 * reset the term
	 */
	public void reset() {
		beforePosition.reset();
		afterPosition.reset();
		velocity.reset();
		acceleration.reset();
		internalClock = 0;
	}

	/**
	 * Impose a force on the object.
	 *
	 * @param force.   Force imposed on object
	 * @param damping. Damping coefficient
	 */
	public void imposeForce(double[] force, double damping) {

		//calculate external force & express as Coordinate vector
		Coordinate externalForce = getExternalForce(force);
		externalForce.subtract(getFrictionForce(damping));  //subtract friction

		//calculate acceleration
		externalForce.mult((1 / mass));        //div by mass to get acceleration
		acceleration.set(externalForce.cloneCoords());      //set acceleration

		//calculate impact on velocity
		Coordinate oldVelocity = new Coordinate(dimensions);
		oldVelocity.set(velocity.cloneCoords());        //store old velocity
		Coordinate accelIntegral = (Coordinate) acceleration.getMult(1);
		velocity.add(accelIntegral);                    //calculate new velocity

		//calculate average velocity
		Coordinate averageVelocity = new Coordinate(dimensions);
		averageVelocity.set(oldVelocity.cloneCoords());
		averageVelocity.add(velocity);
		averageVelocity.mult(0.5);

		//calculate impact on position
		Coordinate velIntegral = (Coordinate) averageVelocity.getMult(1);
		afterPosition.add(velIntegral);             //calculate after position
	}

	/*********************Utility Methods***************************/
	/**
	 * Constructor utility method. Set up fields
	 */
	private void setUp(int a, String i, double[] xy) {
		identifier = cleanIdentifier(i);
		arrayIndex = a;
		beforePosition = new Coordinate(dimensions, xy);
		afterPosition = new Coordinate(dimensions, xy);
		velocity = new Coordinate(dimensions);      //default 0
		acceleration = new Coordinate(dimensions);  //default 0
		mass = DEFAULT_MASS;
		internalClock = DEFAULT_CLOCK;
		include = true;
	}

	private String cleanIdentifier(String i) {
		if (!i.equals("")) {
			return toTitleCase(i.replaceAll("_", " "));
		} else {
			return "";
		}
	}

	public String toTitleCase(String input) {
		char[] letters = input.toCharArray();
		String output = "";
		String strThisLetter = "";
		String strPreviousLetter = " ";
		for (char letter : letters) {
			strThisLetter = Character.toString(letter);
			if (strPreviousLetter.equals(" ")) {
				output += strThisLetter.toUpperCase();
			} else {
				output += strThisLetter;
			}
			strPreviousLetter = strThisLetter;
		}
		return output;
	}

	/**
	 * Generate random coordinates
	 *
	 * @return random coordinate in range [-1,+1]
	 */
	public final double[] randomCoords() {
		Random generator = new Random();
		double[] c = new double[dimensions];
		for (int index = 0; index < dimensions; index++) {
			c[index] = generator.nextDouble() * 2 - 1;
		}
		return c;
	}

	/* imposeForce utility method. calculate external force */
	private Coordinate getExternalForce(double[] force) {
		Coordinate externalForce = new Coordinate(dimensions);
		externalForce.set(force);                //force imposed from outside
		return externalForce;
	}

	/* imposeForce utility method. calculate friction force */
	private Coordinate getFrictionForce(double damping) {
		Coordinate frictionForce = new Coordinate(dimensions); //calculte friction vector
		frictionForce.set(velocity.cloneCoords());   //friction force: damping * vel
		frictionForce.mult(damping * 0.5);                 //damping coefficient
		return frictionForce;                               //0.5 as divide in 2
	}

	/**
	 * ****************Standard Overrides*************************
	 */
	@Override
	public String toString() {
		DecimalFormat Coord = new DecimalFormat("#0.00");

		String output = "";
		output += identifier + "\n" + "Pos bef : ";
		for (int i = 0; i < dimensions; i++) {
			output += Coord.format(beforePosition.getCoord(i)) + "  ";
		}
		output += "\n" + "Pos aft : ";
		for (int i = 0; i < dimensions; i++) {
			output += Coord.format(afterPosition.getCoord(i)) + "  ";
		}
		output += "\n" + "Velocity: ";
		for (int i = 0; i < dimensions; i++) {
			output += Coord.format(velocity.getCoord(i)) + "  ";
		}
		output += "\n" + "Accel   : ";
		for (int i = 0; i < dimensions; i++) {
			output += Coord.format(acceleration.getCoord(i)) + "  ";
		}
		output += "\n" + "Mass    :" + mass;
		output += "\n" + "Time    :" + internalClock + "\n";
		return output;
	}

	/*******************Main Method**************************/
	/**
	 * main method as a test utility
	 */
	public static void main(String[] args) {

		//Test constructors
		Term termOne = new Term(3);                     //3 dimension term
		Term termTwo = new Term(3, 0, "Jeremy");
		double[] c = new double[] {0.85, 0.25, 0.11};
		Term termThree = new Term(3, 0, "Sammy", c);
		System.out.println("Testing constructors:");
		System.out.println(termOne);
		System.out.println(termTwo);
		System.out.println(termThree);

		//Test mutators
		System.out.println("Testing mutators:");
		double[] d = new double[] {0.99, 1.25, 3.99};
		termOne.setPosition(d);                         //set position
		double[] e = new double[] {0.3, 0.3, 0.3};
		termOne.setVelocity(e);                         //set velocity
		termOne.setMass(10);                            //set mass
		System.out.println(termOne);
		double[] f = new double[] {0.1, 0.2, 0.3};
		termOne.shiftCoords(f);                         //shift coordinates
		System.out.println(termOne);

		//Test specific actions for arena
		System.out.println("Testing specific actions:");
		System.out.println(termTwo);
		double[] g = new double[] {2.0, 2.0, 2.0};
		System.out.println("First impose a force :");
		double damping = Math.sqrt(2 / 100);
		termTwo.imposeForce(g, damping);              //First time_tick
		System.out.println(termTwo);
		System.out.println("Now shift the coordinate :");
		termTwo.shiftCoords(f);
		System.out.println(termTwo);
		System.out.println("Now advance the time :");
		termTwo.advanceTime();
		System.out.println(termTwo);
		System.out.println("Impose the same force again :");
		termTwo.imposeForce(g, damping);              //Second time _tick
		System.out.println(termTwo);
		System.out.println("And advance the time again :");
		termTwo.advanceTime();
		System.out.println(termTwo);
	}
}
/*************************Fields********************************/
/***********Constructors and Static Factory Methods*************/
/************************Accessors******************************/
/************************Mutators*******************************/
/*******************Standard Overrides**************************/
/*********************Utility Methods***************************/
