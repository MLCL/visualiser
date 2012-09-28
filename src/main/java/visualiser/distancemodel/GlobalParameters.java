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

/* Specifies global GlobalParameters for the simulation
 */
package visualiser.distancemodel;

/**
 * @author Jeremy Reffin
 *         V2.0 20110822
 */
public final class GlobalParameters {

	/********************GLOBAL PARAMETERS*************************/
	/* Must be set manually */

	/**
	 * Debug GlobalParameters
	 */
	public static final boolean DEBUG = true;

	/**
	 * ***Control flow GlobalParameters*****
	 */
	public static final int NUMBER_OF_STARTS = 30;        //random starts
	public static final int INITIAL_ITERATIONS = 1500;    //iters per random
	public static final int FINAL_ITERATIONS = 8000;     //iters for solution

	/**
	 * ****Graphic GlobalParameters *******
	 */
	public static final int DISPLAY_SIZE = 700;         //size of display
	public static final int DISPLAY_POSITION = 300;     //position of display

	/**
	 * term GlobalParameters
	 */
	public static final int DEFAULT_MASS = 1;            //fixed at present
	public static final int REFERENCE_MASS = 1;         //reference mass
	public static final boolean INCLUDE_REFERENCE = true;//include in diagram

	/**
	 * forceRate defaults. These are inverse of spring constant
	 */
	public static final double DEFAULT_FORCE_MULT = 500;//baseline
	public static final double WEAK_FORCE_MULT = 500;    //min sim
	public static final double STRONG_FORCE_MULT = 500; //>min sim

	/**
	 * Number of dimensions to which relationships are being reduced *
	 */
	public static final int DIMENSIONS = 2;

	/**
	 * Animation GlobalParameters
	 */
	public static final double INITIAL_SLEEP = 6;       //pause time
	public static final double REDUCTION_FACTOR = 0.999;//pause reduction rate

	/**
	 * similarity data handling parameters
	 */
	//true=missing data are set at minimum similarity observed (e.g.for thresholded thesauri)
	//false=missing data are not included
	public static final boolean SET_MISSING_TO_MIN = true;

	//should normally be true. If set to false then data values are ignored!...
	//...when combined with SET_MISSING_TO_MIN = true, models on only the missing
	//minimum threshold ("dissimilarity thesaurus modelling")
	public static final boolean USE_DATA = true;

	// transform function from similarity to distance
	// INVERSE_OFFSET : proximity = (1 / similarity) - 1
	// ONE_MINUS : proximity = (1 - similarity)
	// INVERSE : proximity = (1 / similarity)
	public static final SimilarityToDistance TRANSFORM =
	SimilarityToDistance.INVERSE_3_OFFSET;


	/**
	 * ********Constructors and Static Factory Methods************
	 */
	private GlobalParameters() {
		throw new AssertionError();                     //prevents construction
	}

	/**
	 * **********************ENUMS********************************
	 */
	public enum SimilarityToDistance {

		INVERSE, INVERSE_OFFSET, ONE_MINUS, INVERSE_2_OFFSET, INVERSE_3_OFFSET
	}


}
