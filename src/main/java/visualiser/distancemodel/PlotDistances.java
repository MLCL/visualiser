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
 * Graphical display of similarity relationships in 2D
 */
package visualiser.distancemodel;

import visualiser.distancemodel.exceptions.WrongDimensionsException;
import visualiser.distancemodel.terms.Term;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.HashMap;

import static visualiser.distancemodel.GlobalParameters.DIMENSIONS;

/**
 * @author Jeremy Reffin
 *         V2.0 20110823
 */
public class PlotDistances extends JPanel {

	/**
	 * *********************CONSTANTS*****************************
	 */
	private static final int PAD = 80;                  //border to window edge

	/**
	 * GlobalParameters for points showing position
	 */
	private static final boolean DRAW_POINT = true;             //draw point ?
	private static final int POINT_SIZE = 4;                    //Size of point
	private static final Color POINT_COLOUR = Color.BLACK;      //point colour

	/**
	 * GlobalParameters for legends describing terms
	 */
	private static final Color TERM_COLOUR = Color.BLUE;        //term colour
	private static final String TERM_FONT = "Lucida Grande";    //term font
	private static final int TERM_FONT_SIZE = 16;               //term font size
	private static final Color REFTERM_COLOUR = Color.RED;      //ref term colour
	private static final String REFTERM_FONT = "Lucida Grande"; //ref term font
	private static final int REFTERM_FONT_SIZE = 16;            //ref term size

	/**
	 * **********************FIELDS*******************************
	 */
	HashMap<String, Term> terms;                //Index into terms
	String[] identifiers;                       //Array of labels
	int numberOfTerms;                          //Number of terms
	double[] min;                               //minimum in each dimension;
	double[] max;                               //maximum in each dimension
	double[] screen;                            //screen DIMENSIONS
	double[] scale;                             //scaling for display
	double[] coords;                            //coordinates for terms
	int refTermIndex;                           //reference term index

	/**
	 * ********Constructors and Static Factory Methods************
	 */
	public PlotDistances(HashMap<String, Term> t, int n, String[] i, int rti) {
		terms = t;
		numberOfTerms = n;
		identifiers = i;
		refTermIndex = rti;
		screen = new double[DIMENSIONS];
		scale = new double[DIMENSIONS];
		coords = new double[DIMENSIONS];
		max = new double[DIMENSIONS];
		min = new double[DIMENSIONS];
	}

	/*******************Standard Overrides**************************/
	/**
	 * Plot in 2D or 3D. 3D version currently doesn't work
	 */
	@Override
	protected void paintComponent(Graphics g) throws WrongDimensionsException {

		checkPlotDimensions();                      //only 2 or 3 DIMENSIONS

		super.paintComponent(g);                    //call suprclass paintComp
		Graphics2D graphic = (Graphics2D) g;        //cast to Graphics2D

		setBackground(Color.WHITE);
		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);

		setScreen();                                //Set up screen coordinates
		setMinMaxScale();                           //work out max, min & scale
		//plotAxes(graphic);                        //plot axes (x,y,(z))

		setCoords();                                //set coordinates of terms
		drawTerms(graphic);                         //draw Terms
	}

	/*********************Utility Methods***************************/
	/**
	 * Check for incorrect number of DIMENSIONS on input
	 *
	 * @throws WrongDimensionsException
	 */
	private void checkPlotDimensions() throws WrongDimensionsException {
		if (DIMENSIONS < 2 || DIMENSIONS > 3) {
			throw new WrongDimensionsException("Can't plot in " + DIMENSIONS
			+ " dimensions");
		}
	}

	/**
	 * Plots axes on the screen
	 *
	 * @param g2 Graphics2D object
	 */
	private void plotAxes(Graphics2D g2) {
		g2.setPaint(Color.DARK_GRAY);
		g2.draw(new Line2D.Double(PAD + (max[0] - min[0]) * scale[0],
		screen[1] - (PAD - min[1] * scale[1]), PAD,
		screen[1] - (PAD - min[1] * scale[1])));
		g2.draw(new Line2D.Double(PAD - min[0] * scale[0], screen[1] - PAD,
		PAD - min[0] * scale[0], screen[1] - (PAD + (max[1] - min[1])
		* scale[1])));
		if (DIMENSIONS == 3) {                      //Draw z-axis
			g2.draw(new Line2D.Double(PAD - min[0] * scale[0],
			screen[1] - (PAD - min[1] * scale[1]),
			PAD - min[0] * scale[0],
			screen[1] - (PAD - min[1] * scale[1])));
		}
	}

	/**
	 * Set up screen coordinates
	 */
	private void setScreen() {
		screen[0] = getWidth();                     //x-axis
		screen[1] = getHeight();                    //y-axis
		if (DIMENSIONS == 3) {                      //z-axis
			screen[2] = Math.min(screen[0], screen[1]) * Math.sqrt(2);
		}
	}

	/**
	 * Set coordinates of terms
	 */
	private void setCoords() {
		for (int i = 0; i < numberOfTerms; i++) {   //only 2 or 3 DIMENSIONS
			coords = terms.get(identifiers[i]).getPosition();
		}
	}

	/**
	 * Work out max, min and scale
	 */
	private void setMinMaxScale() {
		for (int k = 0; k < DIMENSIONS; k++) {
			max[k] = (double) Double.MIN_VALUE;
			min[k] = (double) Double.MAX_VALUE;
		}
		for (int i = 0; i < numberOfTerms; i++) {   //only 2 or 3 DIMENSIONS
			Term term = terms.get(identifiers[i]);
			coords = term.getPosition();
			for (int k = 0; k < 2; k++) {
				switch (DIMENSIONS) {
					case 2:                         //2 DIMENSIONS
						if (coords[k] > max[k]) {
							max[k] = coords[k];
						}
						if (coords[k] < min[k]) {
							min[k] = coords[k];
						}
						break;
					case 3:                         //3 DIMENSIONS
						if ((coords[k] + coords[2] * Math.sqrt(2)) > max[k]) {
							max[k] = coords[k];
						}
						if ((coords[k] + coords[2] * Math.sqrt(2)) < min[k]) {
							min[k] = coords[k];
						}
						break;
				}
			}
		}
		setScale();
	}

	/**
	 * Work out scale (scaling for the screen)
	 *
	 * @return scale
	 */
	private double[] setScale() {
		double dif = 0;
		for (int k = 0; k < DIMENSIONS; k++) {        //equal scale on each dim
			if ((max[k] - min[k]) > dif) {
				dif = max[k] - min[k];
			}
		}
		for (int k = 0; k < DIMENSIONS; k++) {
			scale[k] = (double) (screen[k] - 2 * PAD) / dif; //Scale it
		}
		return scale;
	}

	/**
	 * draw each of the terms
	 *
	 * @param g2 Graphics 2D object
	 */
	private void drawTerms(Graphics2D g2) {
		Term term = new Term(DIMENSIONS);
		for (int i = 0; i < numberOfTerms; i++) {
			term = terms.get(identifiers[i]);
			String legend = term.getIdentifier();
			coords = term.getPosition();
			double xFrame = PAD + (coords[0] - min[0]) * scale[0];
			double yFrame = screen[1] - (PAD + (coords[1] - min[1]) * scale[1]);
			if (DIMENSIONS == 3) {
				xFrame += (coords[2] - min[2]) * scale[2] / Math.sqrt(2);
				yFrame -= (coords[2] - min[2]) * scale[2] / Math.sqrt(2);
			}
			drawPoint(xFrame, yFrame, g2);
			boolean reference = (i == refTermIndex) ? true : false;
			writeLegend(xFrame, yFrame, legend, g2, reference);
		}
	}

	private void drawPoint(double x, double y, Graphics2D g2) {
		if (DRAW_POINT) {
			g2.setPaint(POINT_COLOUR);
			g2.fill(new Ellipse2D.Double(x - (POINT_SIZE / 2),
			y - (POINT_SIZE / 2), POINT_SIZE, POINT_SIZE));
		}
	}


	/**
	 * Write legend on screen offset from position (x,y)
	 *
	 * @param x,      y plot position (0,0) is top left
	 * @param legend  to be displayed
	 * @param graphic Graphics 2D object
	 */
	private void writeLegend(double x, double y, String legend, Graphics2D g2,
	                         boolean reference) {
		Font font = new Font(TERM_FONT, Font.PLAIN, TERM_FONT_SIZE);
		g2.setPaint(TERM_COLOUR);
		if (reference) {
			g2.setPaint(REFTERM_COLOUR);
			font = new Font(REFTERM_FONT, Font.BOLD, REFTERM_FONT_SIZE);
		}
		g2.setFont(font);
		FontMetrics fm = g2.getFontMetrics();
		x -= fm.stringWidth(legend) / 2;
		y += fm.getAscent() / 2;
		g2.drawString(legend, (float) x, (float) y);
	}
}
