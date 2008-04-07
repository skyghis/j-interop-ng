package org.jinterop.dcom.test;

public class SysInfoEvents {

	
		public SysInfoEvents()
		{
			
		}
		public void PowerStatusChanged()
		{
			System.out.println("Called by COM -> PowerStatusChanged");
		}
		
		public void TimeChanged()
		{
			System.out.println("Called by COM -> TimeChanged");
		}
		
	}
