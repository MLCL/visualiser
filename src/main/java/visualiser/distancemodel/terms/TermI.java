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

/* Interface ofr a term
 */

package visualiser.distancemodel.terms;

/**
 * @author Jeremy Reffin
 *         V2.0 20110822
 */
public interface TermI {

	/************************Accessors******************************/

	/**
	 * Methods return position before and after update, velocity, acceleration,
	 * mass, clock, and array. Also signal if term is included
	 */
	public double[] getPosition();

	public double[] getAfterPosition();

	public double[] cloneAfterPosition();

	public double[] getVelocity();

	public double[] getAcceleration();

	public double getMass();

	public int getInternalClock();

	public int getArray();

	public boolean isIncluded();

	/************************Mutators*******************************/
	/**
	 * Methods set position (applied to after), velocity, mass.
	 */
	public void setPosition(double[] newCoords);

	public void setVelocity(double[] newVelocity);

	public void setMass(double newMass);

	/**
	 * Set the term's internal clock
	 *
	 * @param new clock time
	 */
	public void setInternalClock(int c);

	/**
	 * Impose force and record upadated position
	 */
	public void imposeForce(double[] force, double damping);

	/**
	 * Advance one time tick and change settings accordingly
	 */
	public void advanceTime();

	/**
	 * Shift coordinates (usually to impose a reference frame)
	 */
	public void shiftCoords(double[] shift);

}
