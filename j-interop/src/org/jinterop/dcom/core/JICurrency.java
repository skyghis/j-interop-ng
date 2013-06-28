/**
* j-Interop (Pure Java implementation of DCOM protocol)
*     
* Copyright (c) 2013 Vikram Roopchand
* 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Vikram Roopchand  - Moving to EPL from LGPL v3.
*  
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
	
	public JICurrency(String value)
	{
		if (value.startsWith("."))
		{
			value = "0" + value;
		}
		
		if (value.endsWith("."))
		{
			value = value + "0";
		}
		
		String[] str = value.split("\\.");
		
		units = Integer.parseInt(str[0]);
		if (str.length > 1)
		{
			fractionalUnits = Integer.parseInt(str[1]);
		}	
		
	}

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
