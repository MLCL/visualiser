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
 * Creates a field for terms, reads in terms and similarity relations
 * then calculates and displays the relations in 2 DIMENSIONS using
 * the "Hooke's law" algorithm
 *
 * Divided into following sections:
 * ACCESSORS: SIMULATION RESULTS.   Method(s) to create simulation display
 * MUTATORS: RUNNING SIMULATION.    Methods for running the simulation
 * MUTATORS: TERM POSITIONS.        Methods for modifying term positions
 * MUTATORS: REFERENCE TERM.        Methods for identifying reference term
 * OBJECT CREATION METHODS.         Methods used to create an instance
 * STANDARD OVERRIDES.              toString method
 */
package visualiser.distancemodel;

import visualiser.distancemodel.exceptions.ClockInconsistentException;
import visualiser.distancemodel.terms.Term;

import javax.swing.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import static visualiser.distancemodel.GlobalParameters.*;

/**
 * @author Jeremy Reffin
 *         V2.0 20110822
 */
public class Model {

	/**
	 * *********************CONSTANTS*****************************
	 */
	private static final int INITIAL_FIELD = 16;     //initial field size
	private static final boolean RE_SCALE = true;   //not implemented yet
	/**
	 * **********************FIELDS*******************************
	 */
	private int internalClock;                          //internal clock
	private Field field;        //the field (ideal distances, spring constants)
	private HashMap<String, Term> terms;                //index-to-term
	private String[] identifiers;                       //Array of labels
	private int numberOfTerms;                          //Number of terms
	private double sumError;                            //Overall distortion
	private String referenceTerm;
	private int refTermIndex;
	private int[] orientors;

	/***********Constructors and Static Factory Methods*************/
	/**
	 * @param f filename of data file holding similarity relations
	 */
	public Model(File f, String refTerm) {
		//Set these fields
		sumError = 0;
		referenceTerm = refTerm;
		//Initialise these fields
		terms = new HashMap<String, Term>();
		numberOfTerms = 0;
		field = new Field(INITIAL_FIELD);
		identifiers = initializeIdentifiers(INITIAL_FIELD);      //set to ""
		//Read data and set terms, numberOfTerms, field, & identifiers
		readData(f);
		refTermIndex = setReferenceTerm(referenceTerm);
		System.out.println("Reference term: " + referenceTerm);
		System.out.println("ref Term Index: " + refTermIndex);
		orientors = setOrientors();
		for (int test : orientors) {
			System.out.println("Orientor: " + identifiers[test]);
		}
	}

	/***************ACCESSORS: SIMULATION RESULTS********************/
	/**
	 * Provide array reference for a term given its identifier string
	 *
	 * @param identifier name of term
	 * @return reference for array given string or -1 if not present
	 */
	public int getReference(String identifier) {
		Term term = terms.get(identifier);
		if (term == null) {
			return -1;
		} else
			return term.getArray();
	}

	/**
	 * Displays results in a (DISPLAY_SIZE x DISPLAY_SIZE) window
	 * at (DISPLAY_POSITION,DISPLAY_POSITION)
	 *
	 * @param displaySize     window size
	 * @param displayPosition position on screen of display
	 * @return the object responsible for plotting results
	 */
	public PlotDistances initialiseDisplay(int displaySize, int displayPosition) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		PlotDistances plotter = new PlotDistances(terms, numberOfTerms,
		identifiers, refTermIndex);
		f.add(plotter);
		f.setSize(displaySize, displaySize);
		f.setLocation(displayPosition, displayPosition);
		f.setVisible(true);
		return plotter;
	}

	/***************MUTATORS: RUNNING SIMULATION********************/
	/**
	 * impose forces on terms
	 */
	public double imposeForces() {
		double[] forces = new double[DIMENSIONS];
		sumError = 0.0;
		for (int j = 0; j < numberOfTerms; j++) {
			Term term = terms.get(identifiers[j]);
			Arrays.fill(forces, 0);                 //initialize result
			double[] coord = term.getPosition();    //get coords for distance
			int arrayIndex = term.getArray();       //get array index
			double theta[] = new double[DIMENSIONS];
			double cosTheta;
			double sinTheta;
			double sumRate = 0;
			for (int i = 0; i < numberOfTerms; i++) {   //loop through terms
				if (i != arrayIndex && field.getDataPresent(arrayIndex, i)) {
					Term otherTerm = terms.get(identifiers[i]);
					double[] otherCoord = otherTerm.getPosition();
					double[] delta = new double[DIMENSIONS];
					double distance = 0;
					for (int k = 0; k < DIMENSIONS; k++) {
						delta[k] = otherCoord[k] - coord[k];
						distance += Math.pow(delta[k], 2);
					}
					distance = Math.sqrt(distance);
					for (int k = 0; k < DIMENSIONS; k++) {
						if (Math.abs(distance) < 0.0005) {
							theta[k] = 1 / Math.sqrt(2); //default if coincident
						} else {
							theta[k] = delta[k] / distance;
						}
					}
					double difference = (Math.abs(distance) - field.getIdealDist(
					arrayIndex, i)) / 2;
					sumError += Math.abs(difference);   //accumulator of error
					double forceRate = field.getForceRate(arrayIndex, i);
					sumRate += Math.log10(forceRate);
					double[] dif = new double[DIMENSIONS];
					for (int k = 0; k < DIMENSIONS; k++) {
						dif[k] = difference * theta[k];
						forces[k] += dif[k] / forceRate;    //spring constant
					}
				}
			}
			sumRate = sumRate / (numberOfTerms - 1);
			sumRate = Math.pow(10, sumRate);
			double friction = Math.sqrt(numberOfTerms / sumRate);
			term.imposeForce(forces, friction);
		}
		//System.out.println(sumError);
		return sumError;
	}

	/**
	 * impose reference frame
	 */
	// We have to do this in several loops: shift coords, then rotate, then change sign
	public void advanceTime() {
		Term term = terms.get(identifiers[refTermIndex]);
		double[] refCoords = term.cloneAfterPosition();
		for (int i = 0; i < numberOfTerms; i++) {
			term = terms.get(identifiers[i]);
			term.shiftCoords(refCoords);
			term.advanceTime();
		}
		internalClock++;
	}

	/**
	 * Rotate so that first orientor remains on x-axis
	 */
	public void rotateCoords2D() {
		Term term = terms.get(identifiers[orientors[0]]);
		double sinTheta = term.getSinTheta2D();
		double cosTheta = term.getCosTheta2D();
		for (int i = 0; i < numberOfTerms; i++) {
			term = terms.get(identifiers[i]);
			term.rotateCoords2D(sinTheta, cosTheta);
		}
	}

	/**
	 * Rotate so that first orientor remains on x-axis
	 */
	public void reflectCoords2D() {
		Term term = terms.get(identifiers[orientors[1]]);
		double[] coords = term.getAfterPosition();
		if (coords[1] < 0) {
			for (int i = 0; i < numberOfTerms; i++) {
				term = terms.get(identifiers[i]);
				term.reflectXAxis();
			}
		}
	}

	/*****************MUTATORS: TERM POSITIONS**********************/
	/**
	 * Reset all terms to a random starting coordinate and zero velocity
	 * Also reset all clocks
	 */
	public void resetTerms() {
		for (int i = 0; i < numberOfTerms; i++) {
			Term term = terms.get(identifiers[i]);
			term.reset();
			term.setPosition(term.randomCoords());
			term.setInternalClock(0);
			internalClock = 0;
		}
	}

	/**
	 * Get clone of all terms
	 */
	public double[][] clonePositions() {
		double[][] result = new double[numberOfTerms][DIMENSIONS];
		for (int i = 0; i < numberOfTerms; i++) {
			Term term = terms.get(identifiers[i]);
			result[i] = term.cloneAfterPosition();
		}
		return result;
	}

	/**
	 * Apply clone of all terms
	 */
	public void setPositions(double[][] bestPositions) {
		for (int i = 0; i < numberOfTerms; i++) {
			Term term = terms.get(identifiers[i]);
			term.setPosition(bestPositions[i]);
		}
	}

	/*****************MUTATORS: REFERENCE TERM**********************/
	/**
	 * set up the key reference term
	 */
	public int setReferenceTerm(String reference) {
		int referenceTermIndex = getReference(reference);
		Term term = terms.get(reference);
		term.setMass(REFERENCE_MASS);
		field.setStrongForceRate(term.getArray(), numberOfTerms);
		if (!INCLUDE_REFERENCE)
			excludeTerm(term.getArray());   //need this ?
		return referenceTermIndex;
	}

	/**
	 * exclude term from the collection
	 */
	private void excludeTerm(int array) {
		terms.remove(identifiers[array]);
		numberOfTerms--;
		for (int i = array; i < numberOfTerms; i++) {
			identifiers[i] = identifiers[i + 1];
		}
	}

	/*****************OBJECT CREATION METHODS*********************/
	/**
	 * Initialize identifiers
	 */
	private String[] initializeIdentifiers(int fieldSize) {
		String[] temp = new String[fieldSize];
		for (int i = 0; i < fieldSize; i++) {
			temp[i] = "";
		}
		return temp;
	}

	/**
	 * Expand identifiers
	 */
	private void expandIdentifiers() {
		String[] tempIdentifiers = initializeIdentifiers(field.getFieldSize());
		System.arraycopy(identifiers, 0, tempIdentifiers, 0, numberOfTerms);
		identifiers = tempIdentifiers;
	}

	/**
	 * Load objects and similarities into arena
	 */
	private void readData(File file) {
		double[] similarityBounds = new double[2];      //[0]=min, [1]=max
		similarityBounds[0] = (double) Double.MAX_VALUE;
		similarityBounds[1] = (double) Double.MIN_VALUE;
		ArrayList<SimRel> tempContents = new ArrayList<SimRel>();
		Scanner scanner = null;
		try {
			boolean moreData = true;
			scanner = new Scanner(file);
			//Read identifier1, identifier 2, similarity blocks in turn
			while (moreData && scanner.hasNextLine()) {
				// Read term1 - term2 - similarity;
				if (scanner.hasNext()) {
					//read identifiers and similarity relationship
					String identifier1 = scanner.next();
					String identifier2 = scanner.next();
					double similarity = getSimilarity(scanner.nextDouble());
					//populate field
					if (similarity > 0.0 && similarity < 1.0) { //ignore 0.0 and 1.0
						similarityBounds =
						checkBounds(similarity, similarityBounds);
						createIdentifierTerm(identifier1);
						createIdentifierTerm(identifier2);
						checkExpandField();             //check if expand field
						int id1 = terms.get(identifier1).getArray();
						int id2 = terms.get(identifier2).getArray();
						tempContents.add(new SimRel(id1, id2, similarity));
						field.setIdealDistance(id1, id2, field.getDistance(
						similarity));
						if (USE_DATA) {
							field.setDataPresent(id1, id2);
						}
					}
				} else {
					moreData = false;
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open" + file.toString());
		} catch (IOException e) {
			System.err.println("A problem was encountered reading "
			+ file.toString());
		} finally {
			//System.out.println("Hitting finally...");
			if (scanner != null) {
				scanner.close();
			}

			System.out.println("Min: " + similarityBounds[0]);
			System.out.println("Max: " + similarityBounds[1]);
			//
			//Possible to re-scale stuff. Not implemented yet
//            double ratio = 1;
//            if (similarityBounds[0] != similarityBounds[1]) {
//                double targetRange = (0.99 - 0.01);
//                //double targetRange = (similarityBounds[1] - similarityBounds[0]);
//                double sourceRange = (similarityBounds[1] - similarityBounds[0]);
//                ratio = targetRange / sourceRange;
//            }
//            for (SimRel content : tempContents) {
//                int id1 = content.id1();
//                int id2 = content.id2();
//                double similarity = content.similarity();
//                //System.out.print(similarity + "\t");
//
//                similarity = similarityBounds[0]
//                        + ((similarity - similarityBounds[0]) * ratio);
//                System.out.println("Revised similarity : " + similarity);
//
//                field.setIdealDistance(id1, id2, field.getDistance(
//                        similarity));
			//field.setIdealDistance(id1, id2, 0.0);
//            }

			//Now set empty values in idealDistance field to minimum similarity
			if (SET_MISSING_TO_MIN) {
				field.setRemainingFieldToMinSim(similarityBounds[0]);
			}
		}
	}

	private double[] checkBounds(double similarity, double[] similarityBounds) {
		if (similarity < similarityBounds[0]) {     //check for minimum
			similarityBounds[0] = similarity;
		}
		if (similarity > similarityBounds[1]) {
			similarityBounds[1] = similarity;
		}
		return similarityBounds;
	}

	private double getSimilarity(double data) {
		double sim = (Math.round(data * 1000));
		sim = sim / 1000;
		double similarity = Math.min(sim, 1.0);
		return similarity;
	}

	private void checkExpandField() {
		if (numberOfTerms >= (field.getFieldSize() - 1)) {
			//System.out.println("Expanding");
			field.expandField(numberOfTerms);
			expandIdentifiers();
		}
	}

	private void createIdentifierTerm(String identifier) {
		//if first time seen add identifier, create new term
		if (!terms.containsKey(identifier)) {
			identifiers[numberOfTerms] = identifier;
			//System.out.println("Ident: " + numberOfTerms);
			terms.put(identifier, new Term(DIMENSIONS,
			numberOfTerms++, identifier));
			//System.out.println(terms.get(identifier));
		}
	}

	/**
	 * Find first 2 terms that are not the reference term for use in orienting
	 * the display (rotation and reflection)
	 *
	 * @return result a an array of two integers indexing the orientor terms
	 */
	private int[] setOrientors() {
		int[] result = new int[2];                  //Need 2 orientors
		int found = 0;                              //number found
		int index = 0;                              //index
		while (found < 2) {                         //Need 2 orientors
			if (refTermIndex != index) {
				result[found++] = index;            //valid term as not reference
			}
			index++;                                //increment index
		}
		return result;
	}

	/**
	 * ***************STATIC INNER CLASSES************************
	 */
	private static class SimRel {

		private int identifier1;
		private int identifier2;
		private double similarity;

		public SimRel(int a, int b, double s) {
			identifier1 = a;
			identifier2 = b;
			similarity = s;
		}

		public int id1() {
			return identifier1;
		}

		public int id2() {
			return identifier2;
		}

		public double similarity() {
			return similarity;
		}
	}

	/**
	 * ****************STANDARD OVERRIDES*************************
	 */
	@Override
	public String toString() {
		String output = "";
		double[] coords = new double[DIMENSIONS];
		DecimalFormat Coord = new DecimalFormat("#0.00");
		for (int i = 0; i < numberOfTerms; i++) {
			Term term = terms.get(identifiers[i]);
			coords = term.getPosition();
			output += "Name: " + identifiers[i];
			for (int k = 0; k < DIMENSIONS; k++) {
				output += " Dim " + k + ": " + Coord.format(coords[k]);
			}
			output += "\n";
		}
		return output;
	}

	/****************CURRENTLY UNUSED METHODS***********************/
	/**
	 * Check clock consistency and return clock
	 *
	 * @return internal clock
	 */
	public int checkClock() throws ClockInconsistentException {
		for (int i = 0; i < numberOfTerms; i++) {
			Term term = terms.get(identifiers[i]);
			int check = term.getInternalClock();
			if (check != internalClock)
				throw new ClockInconsistentException("Clock for "
				+ identifiers[i] + " out of line");
		}
		return internalClock;
	}

	public void saveCollection(String fileName) {
		//open the file
		File file = new File(new Direc().get(), fileName);
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(file));
			writeCollection(bufferedWriter);
		} catch (FileNotFoundException ex) {
			System.err.println("Caught FileNotFoundException: "
			+ ex.getMessage());
		} catch (IOException ex) {
			System.err.println("Caught this IOException: " + ex.getMessage());
		} finally {
			//Close the BufferedWriter
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				System.err.println("Caught A FINAL IOException: " + ex.
				getMessage());
			}
		}
	}

	private void writeCollection(BufferedWriter bf) throws IOException {
		//iterate through the HashMap
		for (Entry<String, Term> entry : terms.entrySet()) {
			writeEntry(bf, entry.getKey(), entry.getValue().getPosition());
		}
	}

	/**
	 * Write an entry to a text file
	 *
	 * @param bf    BufferedWriter
	 * @param name1
	 * @param name2
	 * @param sim   similarity relation
	 * @throws IOException
	 */
	private void writeEntry(BufferedWriter bf, String name, double[] coords)
	throws IOException {
		bf.write(name);
		bf.write("\t");
		String coord = Double.toString(coords[0]);
		bf.write(coord);
		bf.write("\t");
		coord = Double.toString(coords[1]);
		bf.write(coord);
		bf.newLine();
	}
}
