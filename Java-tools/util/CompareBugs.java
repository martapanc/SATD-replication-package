package util;

import java.io.*;
import static java.lang.System.out;

public class CompareBugs {

	public static void main(String[] args) {
		
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("src/files/BugComparison.txt")));
			String line = br.readLine();
			while (line != null) {
				String[] data = line.split("\t");
				double b = Double.parseDouble(data[2]);
//				double aTot = Double.parseDouble(data[1]);
				double a = Double.parseDouble(data[5]);
//				double bTot = Double.parseDouble(data[3]);
				
				if (b > a) out.println("A");
					else if (b < a) out.println("B");
						else out.println("=");
//				if (aTot != 0) out.printf("%.3f,", a / aTot);
//					else out.print(0 + ",");
//				if (bTot != 0) out.printf("%.3f\n", b / bTot);
//					else out.println(0);
				
				line = br.readLine();
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
