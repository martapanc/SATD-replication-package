package util;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import static java.lang.System.out;

public class RQ3 {
	
	static final String inputFilePath = "/Users/martapancaldi/Documents/Gauss/ant/AntBugResults";
	static int len = inputFilePath.length();
	
	/* 
	 * RQ1: find SATD-related changes in introductory and fixing commit diffs
	 */
	public static void main(String[] args) {
		
		try {
			File folder = new File(inputFilePath);
			File[] SATDlist = folder.listFiles((dir, name) -> !name.equals(".DS_Store"));
			File output = new File(inputFilePath + "/1/RQ3data.csv");
			PrintWriter excel = new PrintWriter(new FileWriter(output));
			for (int i = 0; i < SATDlist.length; i++) {
//				int i = 11;
				String reportPath = SATDlist[i].toString() + "/report.java";

				File introDiff = new File(SATDlist[i].toString() + "/Satd-Introd-Diff.java");
				File fixDiff = new File(SATDlist[i].toString() + "/Satd-Fix-Diff.java");
				
				out.println(reportPath.substring(len, reportPath.length()));

				
				excel.append(getSatdId(reportPath) + ",");
				out.print(getSatdId(reportPath) + " ");

				int[] introResult = findLinesChangedFromReport(reportPath, introDiff);
				out.println(introDiff.toString() + " IntroRes: "+ introResult[2] + " ");
				excel.append(getSatdId(reportPath) + "," + introResult[2] +",");
					
				int[] fixResult = findLinesChangedFromReport(reportPath, fixDiff);
				out.println(fixResult[2]);
				excel.append(fixResult[2] + ",");
				
				/*
				 * If # changes in intro is (about) equal to # changes in fix, the method was added from scratch and then removed completely
				 */
				if (introResult[2] == fixResult[2]) {
					
				}
				
				if (introResult[2] > 30 && fixResult[2]-2 <= introResult[2] && introResult[2] <= fixResult[2]+2) {
					excel.append("M'=0\n");
				}

//				int[] callResult = methodInvocation(reportPath, satd);
//				excel.append(callResult[0] +","+ callResult[1] +","+ callResult[2]);

			} //End of "for"
			excel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		

		/*TODO: check cases when the method is completely removed in the SATD-fix commit: in this case, it does not make sense 
		 *to search for the method in the bug-fix diffs after the commit.
		 *Also consider cases when the bug-fix corresponds to the SATD-fix (e.g.id #100)
		 **/
	}
	
	public static boolean compareMethods(String methodHeader, String introDiff, String fixDiff) {
		BufferedReader br;
		out.println(methodHeader);
		try {
			br = new BufferedReader(new FileReader(introDiff));
			String line = br.readLine();
			int headerIndex = 0;
			List<String> introMethod = new ArrayList<>();
			while (line != null) {
				introMethod.add(line);
				line = br.readLine();
			}
			

			Pattern sc = Pattern.compile(";");
			Matcher scMatcher;
			for (int i = 0; i < introMethod.size(); i++) {
				if (introMethod.get(i).contains(methodHeader.replaceAll("^\\s+", ""))) { 
					headerIndex = i;
					scMatcher = sc.matcher(introMethod.get(i)); 
					//If ";" is found in the declaration, it is not a method but a statement
					if (scMatcher.find())
						return false;
					break;
				}
			}
			
			br.close();
		} catch (IOException ioe) {ioe.printStackTrace();}
		
		return true;
	}

	public static String getSatdId(String reportPath) {
		return reportPath.substring(len+1, reportPath.toString().length() - 12);
	}

	/*
	 * From methods called within SATD method, search if there are these methods' declarations in the diff file.
	 * EX: 
	 * public void satd-method(parameters) {
	 * 		X.calledMethod();
	 * }
	 * ...
	 * public void calledMethod() {
	 * + 	added line
	 * - 	removed line
	 * }
	 * */

	public static int[] findLinesChangedFromMethod(String methodHeader, File bugDiff) {
		/*For every bug fix in the folder, start from the method header and reach the end of the method 
		 *(by counting curly braces) and search for added/removed lines inside the method.
		 */
		int plusCount = 0, minusCount = 0;
		BufferedReader br;
		out.println(methodHeader);
		try {
			br = new BufferedReader(new FileReader(bugDiff));
			String line = br.readLine();
			int headerIndex = 0;
			List<String> content = new ArrayList<>();
			while (line != null) {
				content.add(line);
				line = br.readLine();
			}
			br.close();
			Pattern open = Pattern.compile("\\{");
			Pattern close = Pattern.compile("\\}");
			Pattern plus = Pattern.compile("^\\+"); //match + and - at the beginning of the line
			Pattern minus = Pattern.compile("^\\-");
			Pattern sc = Pattern.compile(";");
			Matcher openMatcher, closeMatcher, plusMatcher, minusMatcher, scMatcher;
			int openCount = 0, closeCount = 0;
			for (int i = 0; i < content.size(); i++) {
				if (content.get(i).contains(methodHeader.replaceAll("^\\s+", ""))) { 
					headerIndex = i;
					scMatcher = sc.matcher(content.get(i)); 
					//If ";" is found in the declaration, it is not a method but a statement
					if (scMatcher.find())
						return new int[]{0,0,0};
					break;
				}
			}
			for (int i = headerIndex; i < content.size(); i++) {
				plusMatcher = plus.matcher(content.get(i));
				while(plusMatcher.find()) {
					plusCount++;		
					System.out.println(content.get(i));
					
				}
				minusMatcher = minus.matcher(content.get(i));
//				int tempMinus = minusCount;
				while (minusMatcher.find()) {
					minusCount++;
					System.out.println(content.get(i));
				}
//				if (tempMinus == minusCount) { //Count { } only in lines not beginning with -
					openMatcher = open.matcher(content.get(i));
					while (openMatcher.find()) 
						openCount++;					
					closeMatcher = close.matcher(content.get(i));
					while (closeMatcher.find())
						closeCount++;
//				}			
				if (openCount == closeCount && openCount != 0) { 
					//exit from loop when braces are even
					//endIndex = i;
					break;
				}
			}

			//for (int i = headerIndex; i <= endIndex; i++)
			//System.out.println(content.get(i));
			System.out.println("\nLines added: " + plusCount + ". Lines removed: " + minusCount +
					". Tot = " + (plusCount+minusCount));
			//			writer.println("\nLines added: " + plusCount + ". Lines removed: " + minusCount +
			//					". Tot = " + (plusCount+minusCount));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new int[] {plusCount, minusCount, (plusCount+minusCount)};
	}

	public static int[] findLinesChangedFromReport(String reportPath, File bugDiff) {
		String methodHeader = getMethodDeclaration(reportPath);
		return findLinesChangedFromMethod(methodHeader, bugDiff);
	}

	public static String getMethodDeclaration(String reportPath) {
		String methodDecl = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(reportPath));
			String line = br.readLine();
			String headerRegex = "(public|protected|private|static) ((synchronized|static|final|class) )*";
			Pattern headerPattern = Pattern.compile(headerRegex);
			Matcher headerMatcher;
			while (line != null) { //Read report.java and go past the initial section, to start parsing from the method (avoid "methods" like "2.13" in commit messages)
				if (line.contains("End block index"))
					break;
				line = br.readLine();
			}
			while (line != null) {
				line = line.replaceAll("^\\s+", "");
				headerMatcher = headerPattern.matcher(line);
				if (headerMatcher.find()) {
					methodDecl = line;
					break;
				}
				line = br.readLine();
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return methodDecl;
	}

	public static String getMethodName(String reportPath) {
		String methodName = getMethodDeclaration(reportPath);
		//Remove method modifiers and return type. 
		//EX: public String execute(SampleResult previousResult) => execute(SampleResult previousResult)
		methodName = methodName.replaceAll("(public|protected|private|static) ((synchronized|static|final) )*[\\w\\<\\>\\[\\]]+\\s+", "");
		Pattern nameEnd = Pattern.compile("[(| ]");
		Matcher openBraceMatcher = nameEnd.matcher(methodName);
		while(openBraceMatcher.find()) { 
			//Remove parameters and stuff after method/class name
			//EX: execute(SampleResult previousResult) => execute(
			methodName = methodName.substring(0, openBraceMatcher.start()+1);
			break;
		}
		return methodName;
	}

}