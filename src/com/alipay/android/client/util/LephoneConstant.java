package com.alipay.android.client.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.net.Uri;

public class LephoneConstant {
	public static final int FLAG_ROCKET_MENU_NOTIFY = 0x00400000;
	
	private static final String LEPHONEBRAND_ONE = "3GC";
	private static final String LEPHONEBRAND_TWO = "3GW";
	public static final Uri CONTENT_URI = Uri.parse("content://contacts/phones");
	
	private static String getLephoneID(){
		return getSoftwareVersion("framework"); 
		
	}
	
	private static String getSoftwareVersion(String key)
	{	 
		try 
		{ 
			BufferedReader reader = new BufferedReader(new FileReader("/etc/version.conf"), 1024); 
			if(reader != null)
			{
				String a=""; 
				try 
				{ 
					while ((a=reader.readLine())!= null) 
					{ 
						if(a.indexOf(key) != -1) 
						{ 
							return a.substring(a.indexOf(',')+1, a.length()); 
						} 
					} 

				} 
				finally 
				{ 
					reader.close(); 
				} 

			}
		} 
        catch (IOException e) 
        {   
            //e.printStackTrace(); 
        } 
        
        return null; 
    } 
	
	public static Boolean s_isLephone = null;
	public static boolean isLephone(){
		
		if( s_isLephone != null )
			return s_isLephone;
		
		boolean isLephone = false;
		
		String lephoneID = getLephoneID();
		if( lephoneID != null && (lephoneID.indexOf(LEPHONEBRAND_ONE) != -1 ||lephoneID.indexOf(LEPHONEBRAND_TWO) != -1)){
			isLephone = true;
		}
		
		s_isLephone = isLephone;
		return isLephone;
	}
}
