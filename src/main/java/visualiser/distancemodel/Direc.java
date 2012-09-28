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

package visualiser.distancemodel;

import java.io.File;

/**
 * Creates a string with the correct file diectory root. Must be modified for
 * the system being used.
 *
 * @author Jeremy Reffin
 *         V2.0 20110822
 */
public class Direc {

	/**
	 * **********************Fields*******************************
	 */
	// Must be changed manually
//	private static final String COMPUTER = "Unimac";
	//private static final String COMPUTER = "Unipc";
	//private static final String COMPUTER = "Homepc";
	//private static final String COMPUTER = "MacAir";
	private File direc;
	private String parent;

	/***********Constructors and Static Factory Methods*************/
	/**
	 * Constructor with a directory passed as parameter
	 */
	public Direc(String p) {
		parent = p;
		direc = new File(parent);
	}

	/**
	 * Constructor no directory parameter - default used based on COMPUTER
	 */
	public Direc() {
		direc = getDirectory();             //parent is set in getDirectory()
	}

	/*** Utility methods for Constructors ***/
	/**
	 * Returns the current directory based on the setting of COMPUTER.
	 *
	 * @return String full The directory
	 */
	private File getDirectory() {
		return new File("sampledata");
	}

	/************************Accessors******************************/
	/**
	 * Returns the directory as a File object
	 *
	 * @return directory
	 */
	public File get() {
		return direc;
	}

	/**
	 * Returns the directory as a String object
	 *
	 * @return
	 */
	public String getString() {
		return parent;
	}

	public static String getRCV1Directory() {
		return "/Volumes/research/calps/data1/public/corpora/rcv1";
	}

	/**
	 * ****************Standard Overrides*************************
	 */
	@Override
	public String toString() {
		return getString();
	}

	/*********************Utility Methods***************************/
	/**
	 * test suite
	 */
	public static void main(String args[]) {
		String name = "2313newsML.xml";
		Direc direc = new Direc();
		File file = new File(direc.get(), name);
		System.out.println(file);
	}
}
/*************************FIELDS********************************/
/***********CONSTRUCTORS AND STATIC FACOTRY METHODS*************/
/********************PUBLIC ACCESSORS***************************/
/********************PUBLIC MUTATORS****************************/
/*******************STANDARD OVERRIDES**************************/
/*********************UTILITY METHODS***************************/
/************************TEST SUITE*****************************/
