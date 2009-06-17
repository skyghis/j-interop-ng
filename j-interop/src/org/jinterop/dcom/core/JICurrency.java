/**j-Interop (Pure Java implementation of DCOM protocol)
 * Copyright (C) 2006  Vikram Roopchand
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * Though a sincere effort has been made to deliver a professional,
 * quality product,the library itself is distributed WITHOUT ANY WARRANTY;
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110, USA
 */

package org.jinterop.dcom.core;


/**Definition from MSDN: <i> encapsulates the CURRENCY data type used in Automation. CURRENCY is implemented
 * as an 8-byte, two's-complement integer value scaled by 10,000. This gives a fixed-point number
 * with 15 digits to the left of the decimal point and 4 digits to the right. The CURRENCY data type
 * is extremely useful for calculations involving money, or for any fixed-point calculation where accuracy
 * is important. It is one of the possible types for the VARIANT data type of Automation.<p>
 *
 * for example :- <br>
 * If the absolute value of the fractional part is greater than 10,000, the appropriate adjustment
 * is made to the units, as shown in the third of the following examples. <p>
 *
 * Note that the units and fractional part are specified by signed long values. The fourth of the following
 * examples shows what happens when the parameters have different signs. <p>
 *
 * COleCurrency curA;           // value: 0.0000 <br>
 * curA.SetCurrency(4, 500);    // value: 4.0500 <br>
 * curA.SetCurrency(2, 11000);  // value: 3.1000 <br>
 * curA.SetCurrency(2, -50);    // value: 1.9950 <br>
 *
 * </i>
 * @since 1.0
 */
public final class JICurrency {

	private int units = 0;
	private int fractionalUnits = 0;

//	private double value = 0;
//	public JICurrency(double value)
//	{
//		this.value = value;
//	}

	public JICurrency(int units, int fractionalUnits)
	{
		this.units = units;
		this.fractionalUnits = fractionalUnits;
	}

	/**Returns the units value. <br>
	 *
	 * @return
	 */
	public int getUnits()
	{
		return units;
	}

	/**Returns the fractionalUnits value. <br>
	 *
	 * @return
	 */
	public int getFractionalUnits()
	{
		return fractionalUnits;
	}

//	/**Returns the encapsulated value.
//	 *
//	 * @return
//	 */
//	public double getValue()
//	{
//		return value;
//	}

}
