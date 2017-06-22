package util;

import java.io.*;
import java.text.*;
import java.util.*;

public class SATDtime {

	
	public static void main (String[] args) {
		
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/util/SATDcommitDates.txt"));
			String line = br.readLine();
			ArrayList<String[]> commentList = new ArrayList<>();
			
			while (line != null) {
				String[] arr = line.split(",");
				commentList.add(arr);
				
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
				try {
					Date introd = df.parse(arr[2]);
					Date fix = df.parse(arr[4]);
					
					long diff = (fix.getTime() - introd.getTime()) / (24 * 60 * 60 * 1000);
					
					System.out.println(diff);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				line = br.readLine();
				
			}
			
			for (int i = 0; i < commentList.size(); i++) {
				System.out.println(commentList.get(i)[2]);
			}
			
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
