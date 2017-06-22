package util;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class PlusMinus {

	/*
	 * Test if SATD methods include changed lines (+ or -) for each Bug commit. 
	 * Scans a diff file of a bug report commit and returns number of lines changed.
	 */
	static final String inputFilePath = "/Users/martapancaldi/Documents/Gauss/jmeter/104SATDmethods";
	static int len = inputFilePath.length();
	
	public static void main(String[] args) {
		
		File folder = new File(inputFilePath);
		File[] list = folder.listFiles((dir, name) -> !name.equals(".DS_Store"));
//		for (int i = 0; i < list.length; i++) {
			int i = 94;
			String reportPath = list[i].toString() + "/report.java";
			String betweenPath = list[i].toString() + "/Between";
			String afterPath = list[i].toString() + "/After";
			System.out.println(reportPath.substring(len, reportPath.length()));
			
			// Add to list only files ending with ".java" (exclude .DS_store,...)
			FilenameFilter filter = new FilenameFilter() { 
				public boolean accept(File dir, String name) {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.endsWith(".java")) {
						return true;
					}
					return false;
				}
			};
	
			System.out.println("Satd-method: " + getMethodDeclaration(reportPath)
			+ "\n********************************************\n********************************************");
			
			File[] betweenList = new File(betweenPath).listFiles(filter);
			File[] afterList = new File(afterPath).listFiles(filter);
			
			try {
				PrintWriter writer = new PrintWriter(new FileWriter(new File(list[i].toString() + "/betweenReport.java")));
//				PrintWriter writer = new PrintWriter(new FileWriter(new File(list[i].toString() + "/afterReport.java")));
				writer.println(reportPath.substring(len+1, reportPath.length()));
				writer.println("Satd-method: " + getMethodDeclaration(reportPath)
				+ "\n********************************************\n********************************************");
				if (betweenList != null)
					for (File file : betweenList)
//				if (afterList != null)
//					for (File file : afterList)
					{
						String filePath = file.toString().substring(len+1, file.toString().length());
						writer.println(filePath + "\n————————————————————————————————————————————————"
								+ "\n*** Lines Changed in Satd-Method: ***\n");
						findLinesChangedFromReport(reportPath, file, writer);
						writer.println("————————————————————————————————————————————————\n*** Changed calls OF Satd-Method in Diff: ***");
						methodInvocation(list[i] + "/report.java", file, writer);
						writer.println("————————————————————————————————————————————————\n*** Changed calls of methods FROM Satd-Method in Diff: ***");
						callsFromMethod(reportPath, file, writer);
						writer.println("********************************************\n********************************************");	
					}
				else 
					writer.println("No bugs found between Satd and Satd-fix.");
				
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			
//		} //End of "for"
		
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
	public static void callsFromMethod(String reportPath, File bugDiff, PrintWriter writer) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(reportPath));
			String line = br.readLine();
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
			
			while (line != null) {
				line = line.replaceAll("^\\s+", ""); //Remove initial spaces
				methodMatcher = methodPattern.matcher(line);
				while (methodMatcher.find()) { 
					String match = methodMatcher.group(1).replaceAll("\\.", "");
					methodCallsList.add(match);
				}
				line = br.readLine();
			}
			ArrayList<String> nonSATD = new ArrayList<String>(Arrays.asList("println","print","equals","size","exists","length",
					"isEmpty","add","get","set","toString","trim","equals","close","next","hasNext","elementAt", "put", "addText"));
			methodCallsList.removeAll(nonSATD);
			Set<String> set = new HashSet<String>(methodCallsList); //Creating a HashSet from a List to remove duplicate method calls

			String methodName = getMethodName(reportPath).replaceAll("\\(", "");
			if (set.contains(methodName)) //if present, remove satd method from list, to avoid double count
				set.remove(methodName); 
			System.out.println("Method calls found: ");
			writer.println("Method calls found: ");
			for (String item : set) {
				System.out.println("* " + item);
				writer.println("* " + item);
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
						writer.println("—————————\nMethod found in diff:\t" + line);
						findLinesChangedFromMethod(line, bugDiff, writer); //If method X is found, search for lines changed in it
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
	}
	
	/*
	 * Find SATD method/class declaration, save name and search for it in diff file
	 * EX: public SampleResult sample(Entry entry) {  =>  sample
	 */
	public static void methodInvocation(String reportPath, File diffFile, PrintWriter writer) {
		
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
				line = line.replaceAll("^\\+", "").replaceAll("^\\-", "").replaceAll("^\\s+", ""); //Replace initial spaces
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
			writer.println(methodHeader);
			
			Pattern plus = Pattern.compile("^\\+(?!\\+)"); //match + and - at the beginning of the line
			Pattern minus = Pattern.compile("^\\-(?!\\-)");
			Matcher plusMatcher, minusMatcher;
			BufferedReader diffReader = new BufferedReader(new FileReader(diffFile));
			line = diffReader.readLine();
			while (line != null) {
				plusMatcher = plus.matcher(line);
				while (plusMatcher.find()) 
					if (line.contains(methodHeader)) {
						plusCount++;
						System.out.println(line);
						writer.println(line);
						break;
					}
				minusMatcher = minus.matcher(line);
				while (minusMatcher.find()) 
					if (line.contains(methodHeader)) {
						minusCount++;
						System.out.println(line);
						writer.println(line);
						break;
					}
				line = diffReader.readLine();
			}
			diffReader.close();
			//if (plusCount != 0 && minusCount != 0)
			System.out.println("\nLines added containing method: " + plusCount + ". Lines removed containing method: " + minusCount + ". "
					+ "Tot = " + (plusCount+minusCount));
			writer.println("\nLines added containing method: " + plusCount + ". Lines removed containing method: " + minusCount + ". "
					+ "Tot = " + (plusCount+minusCount));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
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

	public static void findLinesChangedFromMethod(String methodHeader, File bugDiff, PrintWriter writer) {
		/*For every bug fix in the folder, start from the method header and reach the end of the method 
		 *(by counting curly braces) and search for added/removed lines inside the method.
		 */

		BufferedReader br;
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
			Matcher openMatcher, closeMatcher, plusMatcher, minusMatcher;
			int openCount = 0, closeCount = 0, endIndex = 0, plusCount = 0, minusCount = 0;
			for (int i = 0; i < content.size(); i++) {
				if (content.get(i).contains(methodHeader.replaceAll("^\\s+", ""))) { 
					// remove spaces on left of string
					headerIndex = i;
					break;
				}
			}
			for (int i = headerIndex; i < content.size(); i++) {
				plusMatcher = plus.matcher(content.get(i));
				while(plusMatcher.find()) {
					plusCount++;		
					System.out.println(content.get(i));
					writer.println(content.get(i));
				}
				minusMatcher = minus.matcher(content.get(i));
				int tempMinus = minusCount;
				while (minusMatcher.find()) {
					minusCount++;
					System.out.println(content.get(i));
					writer.println(content.get(i));
				}
				if (tempMinus == minusCount) { //Count { } only in lines not beginning with -
					openMatcher = open.matcher(content.get(i));
					while (openMatcher.find()) 
						openCount++;					
					closeMatcher = close.matcher(content.get(i));
					while (closeMatcher.find())
						closeCount++;
				}			
				if (openCount == closeCount) { 
					//exit from loop when braces are even (&& != 0)
					endIndex = i;
					break;
				}
			}

			//for (int i = headerIndex; i <= endIndex; i++)
				//System.out.println(content.get(i));
			System.out.println("\nLines added: " + plusCount + ". Lines removed: " + minusCount +
					". Tot = " + (plusCount+minusCount));
			writer.println("\nLines added: " + plusCount + ". Lines removed: " + minusCount +
					". Tot = " + (plusCount+minusCount));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void findLinesChangedFromReport(String reportPath, File bugDiff, PrintWriter writer) {
		String methodHeader = getMethodDeclaration(reportPath);
		findLinesChangedFromMethod(methodHeader, bugDiff, writer);
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