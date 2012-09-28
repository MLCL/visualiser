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
 * A field is a diagonal symmetric matrix of size fieldSize x fieldSize.
 * fieldSize is generally the number of Terms in a TermCollection.
 * It specifies the idealDistance (distance specified by measured similarity)
 * and forceRate (inverse of spring constant) between Terms.
 */
package visualiser.distancemodel;

import java.text.DecimalFormat;

import static visualiser.distancemodel.GlobalParameters.*;

/**
 * @author Jeremy Reffin
 *         V2.0 20110823
 */
public class Field {

	/**
	 * **********************Fields*******************************
	 */
	private int fieldSize;                              //size of field
	private double[][] idealDistance;                   //ideal distance
	private double[][] forceRate;                       //inv spring constant
	private boolean[][] dataPresent;

	/***********Constructors and Static Factory Methods*************/
	/**
	 * Constructor. Builds a Field and populates objects fields
	 *
	 * @param initialField field size
	 * @param fM           default inverse spring constant
	 * @param wFM          weak inverse spring constant
	 * @param sFM          strong inverse spring constant
	 */
	public Field(int initialField) {
		fieldSize = initialField;
		idealDistance = initializeDoubField(0);
		dataPresent = initializeBoolField(false);
		forceRate = initializeDoubField(DEFAULT_FORCE_MULT);
	}

	/**
	 * *********************Accessors*****************************
	 */
	public int getFieldSize() {
		return fieldSize;
	}

	public double getIdealDist(int i, int j) {
		return idealDistance[i][j];
	}

	public double getForceRate(int i, int j) {
		return forceRate[i][j];
	}

	public boolean getDataPresent(int i, int j) {
		return dataPresent[i][j];
	}

	/**
	 * Given a similarity, it returns the distance. Several transformations
	 * from similarity to distance are set by enum SimilarityToDistance
	 *
	 * @param similarity
	 * @return distance
	 */
	public double getDistance(double similarity) {
		double result = (1 - similarity);   //default: linear distance measure
		switch (TRANSFORM) {
			case INVERSE:
				result = (1 / similarity);
				break;
			case INVERSE_OFFSET:
				result = (1 / similarity) - 1;
				break;
			case INVERSE_2_OFFSET:
				result = (1 / similarity / similarity) - 1;
				break;
			case INVERSE_3_OFFSET:
				result = (1 / similarity / similarity / similarity) - 1;
				break;
		}
		return result;
	}

	/**
	 * *********************Mutators******************************
	 */
	public void setForceRate(int i, int j, double value) {
		forceRate[i][j] = value;
		forceRate[j][i] = value;
	}

	public void setIdealDistance(int i, int j, double value) {
		idealDistance[i][j] = value;
		idealDistance[j][i] = value;
	}

	public void setDataPresent(int i, int j) {
		dataPresent[i][j] = true;
		dataPresent[j][i] = true;
	}

	public void setStrongForceRate(int arrayIndex, int numberOfTerms) {
		for (int i = 0; i < numberOfTerms; i++) {
			forceRate[i][arrayIndex] = STRONG_FORCE_MULT;
			forceRate[arrayIndex][i] = STRONG_FORCE_MULT;
		}
	}

	/**
	 * Set empty field entries sutiable for a minimum similarity. Field entry
	 * is empty if idealDiistance[i][j] == 0.
	 *
	 * @param min specified similarity measure (0<=min<=1)
	 */
	public void setRemainingFieldToMinSim(double min) {
		for (int i = 0; i < fieldSize; i++) {
			for (int j = 0; j < fieldSize; j++) {
				if (idealDistance[i][j] == 0 && i != j) {   //not yet set
					idealDistance[i][j] = getDistance(min); //set to parameter
					idealDistance[j][i] = idealDistance[i][j];
					forceRate[i][j] = WEAK_FORCE_MULT;      //set inv spring k
					forceRate[j][i] = forceRate[i][j];
					dataPresent[i][j] = true;
					dataPresent[j][i] = true;
				}
			}
		}
	}

	/*********************Utility Methods***************************/
	/**
	 * Initialize a field.
	 *
	 * @param value to which field is set (typically 0).
	 * @return double array of field
	 */
	private double[][] initializeDoubField(double value) {
		double[][] temp = new double[fieldSize][fieldSize];
		for (int i = 0; i < fieldSize; i++) {
			for (int j = i; j < fieldSize; j++) {
				temp[i][j] = value;
				temp[j][i] = value;
			}
		}
		return temp;
	}

	/**
	 * Initialize a field.
	 *
	 * @param value to which field is set (typically 0).
	 * @return double array of field
	 */
	private boolean[][] initializeBoolField(boolean value) {
		boolean[][] temp = new boolean[fieldSize][fieldSize];
		for (int i = 0; i < fieldSize; i++) {
			for (int j = i; j < fieldSize; j++) {
				temp[i][j] = value;
				temp[j][i] = value;
			}
		}
		return temp;
	}

	/**
	 * Expands instance variables field and identifiers when limit is reached
	 * Dimension of array is doubled each time (i.e. 4x for [][] field array)
	 */
	public void expandField(int numberOfTerms) {
		fieldSize *= 2;
		//expand idealDistance field
		double[][] tempField = initializeDoubField(0);
		for (int i = 0; i < numberOfTerms; i++) {
			System.arraycopy(idealDistance[i], 0, tempField[i], 0, numberOfTerms);
		}
		idealDistance = tempField;
		//expand forceRate field
		double[][] tempForceRate = initializeDoubField(DEFAULT_FORCE_MULT);
		for (int i = 0; i < numberOfTerms; i++) {
			System.arraycopy(forceRate[i], 0, tempForceRate[i], 0, numberOfTerms);
		}
		forceRate = tempForceRate;
		//expand dataPresent field
		boolean[][] tempPresent = initializeBoolField(false);
		for (int i = 0; i < numberOfTerms; i++) {
			System.arraycopy(dataPresent[i], 0, tempPresent[i], 0, numberOfTerms);
		}
		dataPresent = tempPresent;
	}

	/**
	 * ****************Standard Overrides*************************
	 */
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("0.00");
		String output = "Field Size: " + Integer.toString(fieldSize) + "\n";
		for (int i = 0; i < fieldSize; i++) {
			for (int j = 0; j < fieldSize; j++) {
				output += df.format(idealDistance[i][j]) + " ";
			}
			output += "\n";
		}
		return output;
	}
}
/*************************Fields********************************/
/***********Constructors and Static Factory Methods*************/
/************************Accessors******************************/
/************************Mutators*******************************/
/*******************Standard Overrides**************************/
/*********************Utility Methods***************************/
