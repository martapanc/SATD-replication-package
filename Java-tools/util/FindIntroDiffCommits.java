package util;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import static java.lang.System.out;


public class FindIntroDiffCommits {

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("src/files/hibernate-comments-out")));
			String line = br.readLine();			
			ArrayList<String> satd = new ArrayList<>();
			ArrayList<ArrayList<String>> list = new ArrayList<>();
			while (line != null) {
				if (line.contains("*_*_*")) {
					list.add(satd);
					satd = new ArrayList<>();
				} else {
					satd.add(line);
				}
				line = br.readLine();
//				Pattern shaPattern = Pattern.compile("^\\+(?!\\+)(\\s)*");
//				
//				Matcher shaMatcher = shaPattern.matcher("++	// NONE + might be a better option moving forward in the case of callable");
//				out.println(shaMatcher.find());
			}
			
			
			br.close();
			Pattern shaPattern = Pattern.compile("^[0-9a-f]{9} ");
			Pattern plusPattern = Pattern.compile("^\\+(?!\\+)(\\s)*");
			Pattern minusPattern = Pattern.compile("^-(?!\\-)(\\s)*");
			Matcher shaMatcher, plusMatcher, minusMatcher;
			for (ArrayList<String> data : list) {
				if (data.get(1).equals("# shas =  1"))
					out.println(data.get(0));
				else {
					ArrayList<String> shas = new ArrayList<>();
					ArrayList<String> chLines = new ArrayList<>();
					ArrayList<String> paths = new ArrayList<>();
					ArrayList<String> completePaths = new ArrayList<>();
					for (String ln : data) {
						shaMatcher = shaPattern.matcher(ln);
						if (shaMatcher.find()) {
							shas.add(ln.substring(0,9));						
							paths.add(ln.substring(10, ln.length()).replace("hibernate-core/", "").replace("code/core/", ""));
							completePaths.add(ln.substring(10,ln.length()));
						}
							else {
								plusMatcher = plusPattern.matcher(ln);
								minusMatcher = minusPattern.matcher(ln);
								if (plusMatcher.find() || minusMatcher.find())
									chLines.add(ln);
							}
//								else paths.add(ln);
					}
					boolean found = false;
					for (int i = 0; i<paths.size()-1; i++) {
//						paths.get(i).replace("hibernate-core/", "").replace("code/core/", "");
						for (int j = i+1; j < paths.size(); j++) {
							plusMatcher = plusPattern.matcher(chLines.get(j));
							minusMatcher = minusPattern.matcher(chLines.get(i));
							if (paths.get(i).equals(paths.get(j)) && plusMatcher.find() && minusMatcher.find()) {
									out.println(data.get(0) + "," + completePaths.get(j) + "," + shas.get(j) + "," 
											+ completePaths.get(i) + "," + shas.get(i));
									found = true;
									break;
							}
						}	
						if (found) break;
					}
					
					if (!found) out.println(data.get(0));
				}		
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {	
			e.printStackTrace();
		}

	}

}
