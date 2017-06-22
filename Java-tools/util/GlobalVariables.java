package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalVariables {

	/*Search for global variables in the SATD method's class that are changed somewhere in the class */
	public static void globalVariables(String reportPath, File bugDiff) {
		try {
			//String satdMethodHeader = getMethodDeclaration(reportPath);
			BufferedReader br = new BufferedReader(new FileReader(bugDiff));
			String line = br.readLine();
			//String methodHeader = "";
			//Pattern variablePattern = Pattern.compile("(^[\\w\\d]+(\\[[\\d\\w]+\\])?(\\s)+=)");
			Pattern globalVarPattern = Pattern.compile("^((public|private|protected|static) ((synchronized|static|final) )*"
					+ "[\\w\\<\\>\\[\\]]+\\s+(\\w)+)((\\s)*=(\\s)*[\\w\\s\\.\\(\\)\\<\\>\"\'\\$\\-,]+)?;");
			Matcher variableMatcher;
			
			while (line != null) {
				line = line.replaceAll("^\\s+", "");
				variableMatcher = globalVarPattern.matcher(line);
				while (variableMatcher.find())
					System.out.println(variableMatcher.group());
				//if (line.matches("\\{")) break;
				line = br.readLine();
			}
			
			
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
