package util;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import static java.lang.System.out;

public class CompareDiffs {
	
	static final String inputFilePath = "/Users/martapancaldi/Documents/Gauss/ant/AntBugResults";
	static int len = inputFilePath.length();
	
	/* 
	 * RQ1: find SATD-related changes in introductory and fixing commit diffs
	 */
	public static void main(String[] args) {
		
		try {
			File folder = new File(inputFilePath);
			File[] SATDlist = folder.listFiles((dir, name) -> !name.equals(".DS_Store"));
			File output = new File(inputFilePath + "/1/RQ1data.csv");
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
				excel.append(introResult[0] + "," + introResult[1] + "," + introResult[2] +",");
					
				int[] fixResult = findLinesChangedFromReport(reportPath, fixDiff);
				out.println(fixResult[2]);
				excel.append(fixResult[0] + "," + fixResult[1] + "," + fixResult[2] + "\n");

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
	public static int callsFromMethod(String reportPath, File bugDiff) {

		int changeSum = 0;

		try {
			BufferedReader br = new BufferedReader(new FileReader(reportPath));
			String line = br.readLine();
			if (line.equals("")) return -1;
			while (line != null) { //Read report.java and go past the initial section, to start parsing from the method (avoid "methods" like "2.13" in commit messages)
				if (line.contains("End block index"))
					break;
				line = br.readLine();
			}
			/* EX: 
			 * 	...
			 * 	guiPackage.updateCurrentNode();
            	JMeterGUIComponent guicomp = guiPackage.getGui(component);
            	guicomp.configure(component);
				...
				Output = updateCurrentNode, getGui, configure
			 * */
			Pattern methodPattern = Pattern.compile("(\\.[\\s\\n\\r]*[\\w]+)[\\s\\n\\r]*(?=\\(.*\\))");
			Matcher methodMatcher;
			ArrayList<String> methodCallsList = new ArrayList<String>(); //List that contains all method names found
			//Set<String> methodCallsSet = new HashSet<String>();
			while (line != null) {
				line = line.replaceAll("^\\s+", ""); //Replace initial spaces
				methodMatcher = methodPattern.matcher(line);
				while (methodMatcher.find()) { 
					String match = methodMatcher.group(1).replaceAll("\\.", "");
					//if (!methodCallsList.contains(match)) //Add new method name if not already in list
					methodCallsList.add(match);
				}
				line = br.readLine();
			}
			Set<String> set = new HashSet<String>(methodCallsList);

			String methodName = getMethodName(reportPath).replaceAll("\\(", "");
			//if present, remove satd method from list, to avoid double count
			if (set.contains(methodName)) set.remove(methodName); 

			System.out.println("Method calls found: ");
			//			writer.println("Method calls found: ");
			for (String item : set) {
				System.out.println("* " + item);
				//				writer.println("* " + item);
			}

			Matcher headerMatcher;
			//For every method name X found, search for X method declaration within Diff file 
			for (String call : set) {
				String headerRegex = "(public|protected|private|static) ((synchronized|static|final) )*"
						+ "[\\w]+ (" + call + "\\()";
				Pattern headerPattern = Pattern.compile(headerRegex);

				br = new BufferedReader(new FileReader(bugDiff));
				line = br.readLine();
				while (line != null) {
					line = line.replaceAll("^\\s+", "");
					headerMatcher = headerPattern.matcher(line);
					if (headerMatcher.find()) {
						System.out.println("—————————\nMethod found in diff:\t" + line);
						//						writer.println("—————————\nMethod found in diff:\t" + line);
						changeSum += findLinesChangedFromMethod(line, bugDiff)[2]; //If method X is found, search for lines changed in it
						break;
					}
					line = br.readLine();
				}
			}
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return changeSum;
	}

	/*
	 * Find SATD method/class declaration, save name and search for it in diff file
	 * EX: public SampleResult sample(Entry entry) { => sample
	 */
	public static int[] methodInvocation(String reportPath, File diffFile) {

		int plusCount = 0, minusCount = 0;
		try {
			BufferedReader reportReader = new BufferedReader(new FileReader(reportPath));
			String line = reportReader.readLine();
			String methodHeader = "";
			//Find method declaration (or single statement) in the Report file
			String headerRegex = "(public|protected|private|static) ((synchronized|static|final) )*";
			Pattern headerPattern = Pattern.compile(headerRegex);
			Matcher headerMatcher;
			while (line != null) {
				line = line.replaceAll("^\\s+", ""); //Replace initial spaces
				headerMatcher = headerPattern.matcher(line);
				if (headerMatcher.find()) { //Save method declaration
					methodHeader = line;
					break;
				}
				line = reportReader.readLine();
			}
			reportReader.close();
			//Remove method modifiers and return type. 
			//EX: private static final JexlEngine jexl = new JexlEngine(); => jexl = new JexlEngine();
			methodHeader = methodHeader.replaceAll(headerRegex+"[\\w\\<\\>\\[\\]]+\\s+", "");
			Pattern nameEnd = Pattern.compile("[(| ]");
			Matcher openBraceMatcher = nameEnd.matcher(methodHeader);
			while(openBraceMatcher.find()) { 
				//Remove parameters and stuff after method/class name
				//EX: jexl = new JexlEngine(); => jexl
				methodHeader = methodHeader.substring(0, openBraceMatcher.start()+1);
				break;
			}
			System.out.println(methodHeader);
			//			writer.println(methodHeader);

			Pattern plus = Pattern.compile("^\\+"); //match + and - at the beginning of the line
			Pattern minus = Pattern.compile("^\\-");
			Matcher plusMatcher, minusMatcher;
			BufferedReader diffReader = new BufferedReader(new FileReader(diffFile));
			line = diffReader.readLine();
			while (line != null) {
				plusMatcher = plus.matcher(line);
				while (plusMatcher.find()) 
					if (line.contains(methodHeader)) {
						plusCount++;
						System.out.println(line);
						//						writer.println(line);
						break;
					}
				minusMatcher = minus.matcher(line);
				while (minusMatcher.find()) 
					if (line.contains(methodHeader)) {
						minusCount++;
						System.out.println(line);
						//						writer.println(line);
						break;
					}
				line = diffReader.readLine();
			}
			diffReader.close();
			//if (plusCount != 0 && minusCount != 0)
			System.out.println("\nLines added containing method: " + plusCount + ". Lines removed containing method: " + minusCount + ". "
					+ "Tot = " + (plusCount+minusCount));
			//			writer.println("\nLines added containing method: " + plusCount + ". Lines removed containing method: " + minusCount + ". "
			//					+ "Tot = " + (plusCount+minusCount));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return new int[] {plusCount, minusCount, (plusCount+minusCount)};
	}

	/* Counting curly braces in the whole file may not work anymore. Example:
	 * 	-  if (log.isDebugEnabled()) { => this { increments the count wrongly, as there is not matching }
		-  log.debug("HTMLAssertions.getResult(): Setup tidy ...");
		...
		+  if (log.isDebugEnabled()) {
		+  log.debug(
		...
           }
	 * So do not consider curly braces if there's a '-' at the beginning of the line
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
				int tempMinus = minusCount;
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
			br.close();
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