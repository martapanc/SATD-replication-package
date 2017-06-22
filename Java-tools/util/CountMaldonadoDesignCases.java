package util;

import java.io.*;
import static java.lang.System.out;

public class CountMaldonadoDesignCases {
	
	public static void main (String[] args) {
		
		int zeroCount = 0, twoCount = 0, twoMoreCount = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("src/files/Design_comments_Maldonado_Out.txt")));
			String line = br.readLine();
			while (line != null) {
				String arr[] = line.split(",");
				if (arr[0].equals("0")) {
					zeroCount++;
				} else {
					int count = arr.length -1;
					if (count == 2) twoCount++;
					if (count > 2) twoMoreCount++;
				}
				line = br.readLine();
			}
			out.println("0 count = " + zeroCount + "\nTwo Count = " + twoCount + "\nTwoMore Count = " + twoMoreCount);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
