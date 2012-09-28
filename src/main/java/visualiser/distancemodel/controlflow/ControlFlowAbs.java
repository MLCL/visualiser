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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package visualiser.distancemodel.controlflow;

import visualiser.distancemodel.Direc;
import visualiser.distancemodel.Model;

import java.io.File;

import static visualiser.distancemodel.GlobalParameters.*;

/**
 * @author Jeremy Reffin
 *         V2.0 20110822
 */
public abstract class ControlFlowAbs {

	/***********CONSTRUCTORS AND STATIC FACOTRY METHODS*************/
	/**
	 * Constructor. Empty.
	 */
	public ControlFlowAbs() {
	}

	/***************CORE EXECUTION CONTROL METHODS******************/
	/**
	 * Control execution of code
	 *
	 * @param fileName Name of data file containing similarity data
	 * @param refTerm  Name of term of interest
	 */
	protected void run(String fileName, String refTerm) {
		//Record starting time
		long startTime = System.nanoTime();

		//Load in the data for the experiment
		File file = new File(new Direc().get(), fileName);

		//Initialise model, set reference term and number of terms
		Model model = new Model(file, refTerm);
		int refTermIndex = model.getReference(refTerm);

		//Do exploratory runs and adopt best starting positions
		model.setPositions(findBestStartingPositions(model));

		// Plot on screen and drive the best through to completion
		getSolution(model);

		// Record time elapsed
		printDebug("Time elapsed to draw the graph in sec : " + ((System.
		nanoTime() - startTime)) / 1e9);
	}

	/*********LEVEL 1 DERIVATIVE EXECUTION CONTROL METHODS**********/
	/**
	 * Find a good starting point by doing NUMBER_OF_START exploratory runs
	 * (of INITIAL_ITERATIONS each).
	 *
	 * @param model        the set of terms being mapped
	 * @param refTermIndex the reference term of the set
	 * @return estimate of the best starting coordinates for a good solution
	 */
	protected double[][] findBestStartingPositions(Model model) {
		//Initial stage: find a good start point
		{
			//Initiate variables
			double sumError = 0;
			double stateError = (double) Double.MAX_VALUE;
			double[][] bestPositions = model.clonePositions();

			//Do a number of exploratory runs and pick what appears
			//the most promising start point
			for (int i = 0; i < NUMBER_OF_STARTS; i++) {
				//System.out.println("Start : " + i);
				model.resetTerms();            //reset terms
				double[][] startPositions = model.clonePositions(); //store
				for (int j = 0; j < INITIAL_ITERATIONS; j++) {
					sumError = model.imposeForces();
					model.advanceTime();
				}
				if (sumError < stateError) {
					stateError = sumError;              //best so far
					bestPositions = startPositions;     //store it
					//printDebug("Cloned result new error : " + stateError);
				}
			}
			return bestPositions;
		}
	}

	/**
	 * Get the solution given a term collection and reference term
	 *
	 * @param model        the set of terms being mapped
	 * @param refTermIndex the reference term of the set
	 */
	protected abstract void getSolution(Model model);

	/**
	 * ******************UTILITY METHODS**************************
	 */
	protected void printDebug(String output) {
		if (DEBUG) {
			System.out.println(output);
		}
	}
}
