package util;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import static java.lang.System.out;


public class MethodFromComment2
{
	/* Run from command line: java -jar [file-name].jar file/path "TODO comment" */
	public static void main(String[] args)
	{
		try
		{
			/** For testing purposes: */
//			BufferedReader reader = new BufferedReader(new FileReader("src/util/maldonados200.txt"));
//			ArrayList<String[]> sc = new ArrayList<String[]>();
//			String ln = reader.readLine();
//			while (ln != null) {
//				sc.add(ln.split(","));
//				ln = reader.readLine();
//			}
//			reader.close();
//			int num = 6;
//			BufferedReader br = new BufferedReader(new FileReader(
//					"/Users/martapancaldi/Documents/Gauss/jmeter/BugResults200/" +sc.get(num)[0]+ "/first_version.java"));
//			String comm = sc.get(num)[2];
//			out.println(comm);
			/** Bash script runs jar file with arguments */
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String comm = args[1];
			/******/
			String line = br.readLine();
			List<String> content = new ArrayList<String>();
			
			int commIndex = 0;
			while (line != null) {
				content.add(line);
				line = br.readLine();
			}
			br.close();
			for (int i = 0; i < content.size(); i++) {
				if (((String)content.get(i)).contains(comm)) {
					commIndex = i;
					break;
				}
			}
			/* Search for comment inside file */
			if (commIndex != 0) {
				Pattern declaration = Pattern.compile("(private|public|protected|static)(\\s)+((class|synchronized|static|final) )*");
				Pattern semicolon = Pattern.compile(";");
				Pattern open = Pattern.compile("\\{");
				Pattern close = Pattern.compile("\\}");
				int openCount = 0;int closeCount = 0;int startIndex = 0;int endIndex = 0;

				/*CASE 1: Normal case when the TODO comment is inside a method*/
				for (int i = commIndex; i > 0; i--) { 
					//Parse the code backwards until a method declaration is found
					Matcher methodMatcher = declaration.matcher(content.get(i));
					Matcher scMatcher = semicolon.matcher(content.get(i));
					if (methodMatcher.find() && !scMatcher.find()) {
						startIndex = i;
						break;
					}
				}
				/* If the comment is outside the class (i.e. startIndex remained 0), 
				* skip initial license and imports when searching for a block */
				if (startIndex == 0) startIndex = commIndex-1; 

				for (int i = startIndex; i < content.size(); i++) {	
					/* Once a declaration is found, go forward to parse the entire method block 
					 * (count open and closed {} braces) */
					Matcher openMatcher = open.matcher(content.get(i));
					while (openMatcher.find())
						openCount++;
					Matcher closeMatcher = close.matcher(content.get(i));
					while (closeMatcher.find())
						closeCount++;
					if ((openCount != 0) && (openCount == closeCount)) {
						endIndex = i;
						break;
					}
				}
				
				/* CASE 1.5: if the block that includes the comment is a class (or interface),
				 * the SATD may refer to a global variable declaration or a method below the class */
				Pattern classPattern = Pattern.compile("(public|private|protected)(\\s)+(class|interface)(\\s)+");
				Matcher classMatcher = classPattern.matcher(content.get(startIndex));
				
				/* CASE 2: Case when the TODO is not inside a block but refers to a method that starts immediately below the comment.
				 * In this case, the previous code parsed the entire method block that preceeds the comment,
				 * therefore the comment itself is not included in the parsed block.
				 * Vice versa, if the comment is inside the method, the previous code parsed it successfully.*/
				
				openCount = 0; closeCount = 0;
				int a = startIndex;
				boolean containsComment = false;
				while (a <= endIndex) {
					if (((String)content.get(a)).contains(comm))
						containsComment = true;
					a++;
				}

				if (!containsComment || classMatcher.find()) { 
//					System.out.println("Comment was not inside method. Retry...");
//					for (int k = commIndex + 1; k < content.size(); k++) {
//						Matcher methodMatcher = declaration.matcher(content.get(k));
//						if (methodMatcher.find()) {
//							startIndex = k;
//							break;
//						}
//					}
					startIndex = commIndex;
					Pattern inLineComment = Pattern.compile("[\\s\\d\\w\\(\\)\\=\\[\\]\\<\\>;]+ " + comm);
					for (int i = startIndex; i < content.size(); i++)
					{
						/* Case when the satd refers not to a method but to a single statement (ex: var declaration)*/
						if (content.get(i).contains(";") && openCount == 0) { 
							endIndex = i;
							break;
						}
						/* Case when the satd is in the same line of the statement it refers to */
						Matcher inLineCommentMatcher = inLineComment.matcher(content.get(i));
						if (inLineCommentMatcher.find()) {
							out.println("inlinematch");
							endIndex = i;
							break;
						}
						Matcher openMatcher = open.matcher(content.get(i));
						while (openMatcher.find())
							openCount++;
						Matcher closeMatcher = close.matcher(content.get(i));
						while (closeMatcher.find())
							closeCount++;
						if (openCount > 0 && (openCount == closeCount)) {
							endIndex = i;
							break;
						}
					}
					startIndex = commIndex;
				}
				System.out.println("Start block index: " + startIndex);
				System.out.println("End block index: " + endIndex);
				int j = startIndex;
				while (j <= endIndex) {
					System.out.println((String)content.get(j));
					j++;
				}
			} else {
				System.out.println("comment was not found");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}