package util;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import static java.lang.System.out;

public class AllBugsHandler {

	public static void main(String[] args) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("src/files/AllBugs.txt")));
			String line = br.readLine();
			ArrayList<String[]> relatedBugList = new ArrayList<String[]>();
			
			while (line != null) {
				String[] bug = line.split(";");
				if (bug.length == 9 && !bug[0].equals("0")) {
					String[] spaces = bug[0].replaceAll("  ", " ").split(" ");
					String[] data = new String[3];
					data[0] = spaces[0].replaceAll("/", "").replaceAll("BetweenBug", "").replaceAll("Betweenbug", "").replaceAll("AfterBug", "");
					data[1] = spaces[1];
					data[2] = spaces[2].substring(0, 8);
//					out.println(spaces[2]);
					relatedBugList.add(data);
				}
				line = br.readLine();
			}
			br.close();
			
			for (String[] s : relatedBugList)
				out.println(s[0] + "," + s[1] + "," + s[2]);
			out.println(relatedBugList.size());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
