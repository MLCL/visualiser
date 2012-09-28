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

/*Runs an experiment. Takes a file of similarities and develops a xD plot but
 * without any animation. Controlled for rotation and reflection.
 */
package visualiser.distancemodel.controlflow;

import visualiser.distancemodel.Model;
import visualiser.distancemodel.PlotDistances;

import static visualiser.distancemodel.GlobalParameters.*;

/**
 * @author Jeremy Reffin
 *         V2.0 20110822
 */
public class FastControlFlow extends ControlFlowAbs {

	/***********CONSTRUCTORS AND STATIC FACOTRY METHODS*************/
	/**
	 * Constructor.
	 *
	 * @param fileName Name of data file containing similarity data
	 * @param refTerm  Name of term of interest
	 */
	public FastControlFlow(String fileName, String refTerm) {
		super();
		run(fileName, refTerm);
	}

	/**
	 * ******LEVEL 1 DERIVATIVE EXECUTION CONTROL METHODS*********
	 */
	@Override
	protected void getSolution(Model model) {
		double sumError = 0;
		//calculate and plot search for best solution
		for (int i = 0; i < FINAL_ITERATIONS; i++) {
			sumError = model.imposeForces();   //impose force
			model.advanceTime();  //increment time
		}

		model.rotateCoords2D();
		model.reflectCoords2D();
		model.advanceTime();

		//Initialise display
		PlotDistances plotter =
		model.initialiseDisplay(DISPLAY_SIZE, DISPLAY_POSITION);

		printDebug("Final distortion for the graph plot is : " + sumError);
	}

	/*********************UTILITY METHODS***************************/
	/**
	 * test suite
	 */
	public static void main(String[] args) {
//        FastControlFlow obj = new FastControlFlow("martin_samuel",
//                "martin_samuel");
		FastControlFlow obj = new FastControlFlow("wind-100nn.txt",
		"wind");

	}
}
/*************************FIELDS********************************/
/***************CORE EXECUTION CONTROL METHODS******************/
/*********LEVEL 1 DERIVATIVE EXECUTION CONTROL METHODS**********/
/*********LEVEL 2 DERIVATIVE EXECUTION CONTROL METHODS**********/
/*********LEVEL 3 DERIVATIVE EXECUTION CONTROL METHODS**********/
/*********************UTILITY METHODS***************************/
/******************STATIC INNER CLASSES*************************/
