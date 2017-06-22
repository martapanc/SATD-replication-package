package util;

import java.io.*;
import java.util.*;

public class RemoveDupSATD {

	public static void main(String[] args) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("src/files/emfComments")));
			String comm = br.readLine();
			ArrayList<String> list = new ArrayList<>();
			while (comm != null) {
				list.add(comm);
				comm = br.readLine();
			}
			System.out.println(list.size() + "\n");
			
			Set<String> set = new HashSet<String>(list);
			for (String s : set)
				System.out.println(s);
			
			System.out.println("\n" + set.size());
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {	
			e.printStackTrace();
		}
	}

}
