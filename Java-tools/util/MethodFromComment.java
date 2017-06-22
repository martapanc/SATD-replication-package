package util;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class MethodFromComment {
	//static String FILE_PATH = "src/text/HTMLAssertion.txt";
	//static String COMMENT = "check if filename defined";

	//Run from command line: java -jar ... satd-introd/file/path "TODO comment" 
	public static void main (String[] args) { 

		//PlusMinus.findLinesChangedFixed("", new File[2]);
		try {
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String line = br.readLine();
			List<String> content = new ArrayList<>();
			String comm = args[1];
			int commIndex = 0; //index of line containing comment
			while (line != null) {
				content.add(line + "");
				line = br.readLine();
			}
			br.close();
			for (int i = 0; i < content.size(); i ++) 
				if (content.get(i).contains(comm)) {
					commIndex = i;
					break; 
					//The same comment may be present twice or more - if so, only the first one is analysed
					//For our purpose, comments we search for are very specific, so we can assume there is only one instance per class
				}
			if (commIndex != 0) {
				Pattern declaration = Pattern.compile("(private|public|protected|static) ");   
				//Pattern declaration = Pattern.compile("(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;])");
				Pattern open = Pattern.compile("\\{");   
				Pattern close = Pattern.compile("\\}");
				Matcher openMatcher, closeMatcher, methodMatcher;
				int openCount = 0, closeCount = 0, startIndex = 0, endIndex = 0;
				
				/*Normal case when the TODO comment is inside a method*/
				for (int i = commIndex; i > 0; i--) { //Parse the code backwards until a method declaration is found
					methodMatcher = declaration.matcher(content.get(i));
					if (methodMatcher.find()) { 
						startIndex = i;
						break;
					}   
				}
	
				for (int i = startIndex; i < content.size(); i ++) { 
					//Once a declaration is found, go forward to parse the entire method block (count open and closed {} braces)
					openMatcher = open.matcher(content.get(i));
					while (openMatcher.find())
						openCount++;
					closeMatcher = close.matcher(content.get(i));
					while (closeMatcher.find())
						closeCount++;
					if (openCount != 0 && openCount == closeCount) {
						endIndex = i;
						//System.out.println("End block index: " + endIndex);
						break;
					}
				}
				
				/*Case when the TODO is not inside a block but refers to a method that starts immediately below the comment.
				 * In this case, the previous code parsed the entire method block that preceeds the comment,
				 * therefore the comment itself is not included in the parsed block.
				 * Viceversa, if the comment is inside the method, the previous code parsed it successfully.*/
				
				//TODO does not work correctly if SATD refers to a method declaration w/o {}
				int a = startIndex;
				boolean containsComment = false;
				while (a <= endIndex) {
					if (content.get(a).contains(args[1]))
						containsComment = true;
					a++;
				}
				
				if (!containsComment) {
					//System.out.println("Comment was not inside method. Retry...");
					for (int k = commIndex+1; k < content.size(); k ++) {
						methodMatcher = declaration.matcher(content.get(k));
						if (methodMatcher.find()) { 
							startIndex = k ;
							
							break;
						}  
					}
					
					for (int i = startIndex; i < content.size(); i ++) { 
						//Once a declaration is found, go forward to parse the entire method block (count open and closed {} braces)
						openMatcher = open.matcher(content.get(i));
						while (openMatcher.find())
							openCount++;
						closeMatcher = close.matcher(content.get(i));
						while (closeMatcher.find())
							closeCount++;
						if (openCount > 0 && openCount == closeCount) {
							endIndex = i;
							//System.out.println("End block index: " + endIndex);
							break;
						}
					}
					startIndex = commIndex;
				}
				System.out.println("Start block index: " + startIndex);
				System.out.println("End block index: " + endIndex);
				int j = startIndex;
				while (j <= endIndex) {
					System.out.println(content.get(j));
					j++;
				}
			} else 
				System.out.println("comment was not found");

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
