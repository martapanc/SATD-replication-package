package util;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import static java.lang.System.out;

public class CompareSATDmethodsRQ2 {
	
//	static final String inputFilePath = "/Users/martapancaldi/Documents/Gauss/jruby/JRubyBugResults";
	static final String inputFilePath = "/Users/martapancaldi/Documents/Gauss/jmeter/104SATDmethods";
	static int len = inputFilePath.length();
	
	/* 
	 * RQ1: find SATD-related changes in introductory and fixing commit diffs
	 */
	public static void main(String[] args) {
		
		try {
			File folder = new File(inputFilePath);
			File[] SATDlist = folder.listFiles((dir, name) -> !name.equals(".DS_Store"));

			File sizeOutput = new File(inputFilePath + "/5/sizes2.csv");
			
			PrintWriter sizes = new PrintWriter(new FileWriter(sizeOutput));
			for (int i = 0; i < SATDlist.length; i++) {
//				int i = 92;				
				File output = new File(SATDlist[i].toString() + "/last_version_report.java");
				
				
				String reportPath = SATDlist[i].toString() + "/report.java";
				String finalVersion = SATDlist[i].toString()+ "/last_version.java";
				int[] sz = finalMethod(reportPath, finalVersion, output);
				out.println(sz[0] + " " + sz[1]);
				sizes.println(getSatdId(reportPath) + ";" + sz[0] + ";" + sz[1]);
				
//				excel.println("SATD id: " + getSatdId(reportPath) + "\t\tSize: " + methodBlock.size());
//				sizes.println(getSatdId(reportPath) + ";" + methodBlock.size());
				out.println(reportPath);
//				for (String ln : methodBlock)
//				{
//					excel.println(ln);
//					out.println(ln);
//				}
//				excel.println("*****************************************\n*****************************************");
				out.println("*****************************************\n*****************************************");
				

			} //End of "for"
//			excel.close();
			sizes.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getSatdId(String reportPath) {
		return reportPath.substring(len+1, reportPath.toString().length() - 12);
	}
	
	public static int[] finalMethod(String reportPath, String finalVersion, File output) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(reportPath));	
		String line = br.readLine();
		while (line != null) {
			if (line.contains("End block index")) {
				break;
			}
			line = br.readLine();
		}
		
		String headerRegex = "(public|protected|private|static) ((synchronized|static|final) )*";
		String SATDheader = "";
		ArrayList<String> methodBlock = new ArrayList<>();
		Pattern headerPattern = Pattern.compile(headerRegex);
		Matcher headerMatcher;
		line = br.readLine();
		
		while (line != null) {
			methodBlock.add(line);
			headerMatcher = headerPattern.matcher(line);
			if (headerMatcher.find()) 
				SATDheader = line;
			if (line.contains("**********")) break;
			line = br.readLine();		
		}
		br.close();
//		if (methodBlock.size() != 0 ) methodBlock.remove(methodBlock.size()-1);
		
		ArrayList<String> finalMethodBlock = new ArrayList<>();
		int startId = 0, endId = 0;
		
		if (!SATDheader.equals("")) {
			BufferedReader finalReader = new BufferedReader(new FileReader(new File(finalVersion)));
			String fline = finalReader.readLine();
			if (fline == null ) {
				finalReader.close();
				return new int[]{methodBlock.size(), -5};
			}
			while (fline != null) {
				finalMethodBlock.add(fline);
				fline = finalReader.readLine();
			}
			finalReader.close();
			Pattern open = Pattern.compile("\\{");
			Pattern close = Pattern.compile("\\}");

			Matcher openMatcher, closeMatcher;
			int openCount = 0, closeCount = 0;
			
			for (int i = 0; i < finalMethodBlock.size(); i++) {
				if (finalMethodBlock.get(i).contains(SATDheader.replaceAll("^\\s+", ""))) {
					startId = i;
					break;
				}
			}
			
			for (int j = startId; j < finalMethodBlock.size(); j++) {
				openMatcher = open.matcher(finalMethodBlock.get(j));
				while (openMatcher.find()) 
					openCount++;					
				closeMatcher = close.matcher(finalMethodBlock.get(j));
				while (closeMatcher.find())
					closeCount++;				
				if (openCount == closeCount && openCount != 0) { 		
					endId = j;
					break;
				}			
			}
			PrintWriter excel = new PrintWriter(new FileWriter(output));
			if (startId != 0 && endId != 0) {
				
				for (int j = startId; j <= endId; j++) {
					excel.println(finalMethodBlock.get(j));
				}
				excel.close();
				return new int[]{methodBlock.size(), endId-startId+1};	
			} 
			excel.close();
			return new int[]{methodBlock.size(), -2};	
			
		} 
		
		return new int[]{methodBlock.size(), -1};
	}
	

}