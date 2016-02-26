import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

// Developer : Roshan Shetty

///The Scanner class is used to check all the filenames in the data store and return a <Map> //
public class Scanner {
	 
	// the HashMap will save filenames and last modified as a <key,value> pair.
	// the <key,value> pair will be used to decide whether to index content of a file again or not
	
	
	
	private HashMap<String,Long> filesInFolder = new HashMap<String, Long>(); 
	private HashMap<String,Long> filesInIndexFile = new HashMap<String, Long>(); 
	private static String  indexFile = "C:/Users/Pareshan/Documents/Roshan MS/Second Semester Spring 2016/Information Systems/Project - Resume Mining/Programs/index file/index.txt";
	private static String datastore = "C:/Users/Pareshan/Downloads"; 
	
	 public void listFilesInFolder(File folder) {
		  
	     for (File fileEntry : folder.listFiles()) {
	    	 
	         if (fileEntry.isDirectory()) {
	        	 listFilesInFolder(fileEntry);
	        	 
	         } else {
	        	 filesInFolder.put(fileEntry.getName(), fileEntry.lastModified());
	            
	         }
	     }
	 }

	 
	
	 // this function will compare both the current file index and the previously scanned file index to check for recently added files.
	 public void compareIndexes()
	 {
		 for(Entry<String, Long> filename: filesInFolder.entrySet())
		 {
			 String key = filename.getKey().toString();
			 Long value = filename.getValue();
			 if (filesInIndexFile.containsKey(key))
			 {
				 continue;
			 }
			 else
			 {
				 System.out.println(key + " is eligible for indexing");
			 }
		 }
	 }
	 
	 // this function will compare both the current file index and the previously scanned file index to check for recently deleted files.
	 public void checkForDeletedFiles()
	 {
		 for(Entry<String, Long> filename: filesInIndexFile.entrySet())
		 {
			 String key = filename.getKey().toString();
			 Long value = filename.getValue();
			 if (filesInFolder.containsKey(key))
			 {
				 continue;
			 }
			 else
			 {
				 System.out.println(key + " is a deleted file");
				 
			 }
		 }
		 
	 }
	 
	 //This function displays the current files in the data store. Not used currently, but can be used for debugging. 
	 public void displayMap()
	 {
		 for(Entry<String, Long> filename: filesInFolder.entrySet())
		 {
			 String key = filename.getKey().toString();
			 Long value = filename.getValue();
			 System.out.println("key: " + key + " | value: " + value);
			 System.out.println("---------------------------------------------------------------------");
			 System.out.println(key.length() + key + value.SIZE);
		 }
		 
	 }
	 
	 // this function creates , updates and stores the index file.
	 public void saveMap()
	 {
		 
		 try {

			 	
				File file = new File(indexFile);

				// if file doesn't exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				
				for(Entry<String, Long> filename: filesInFolder.entrySet())
				 {
					 String key = filename.getKey().toString();
					 Long value = filename.getValue();
					 String line = key.length() +"|" + key + value;
					 bw.write(line);
					 bw.newLine();
				 }
				
				bw.close();

				System.out.println("file names saved successfully");

			} catch (IOException e) {
				e.printStackTrace();
			}
		 
	 }

	 // this function checks for modified files.
	 public void compareMap(String keyInFile, Long valueInFile)
	 {
		 
		 if (filesInFolder.containsKey(keyInFile))
		 {
			 Long currentValue = filesInFolder.get(keyInFile);
			 //System.out.println(oldKey);
			// System.out.println(valueInFile);
			 
			 if (!currentValue.equals(valueInFile))
			 {
				 System.out.println(keyInFile + " is eligible for indexing");
				 filesInFolder.put(keyInFile, currentValue);
			 }
		 }
		 
	 }
	 
	 
	 public void readFile()
	 {
		 BufferedReader br = null;

			try {

				String sCurrentLine;

				br = new BufferedReader(new FileReader(indexFile));

				while ((sCurrentLine = br.readLine()) != null) {
					
					int pos = sCurrentLine.indexOf('|');
					int length = Integer.parseInt(sCurrentLine.substring(0, pos));
					String key = sCurrentLine.substring((pos+1), ((pos+1) + length));
					Long value = Long.parseLong((sCurrentLine.substring((((pos+1) + length)))));
					
					compareMap(key, value);
					filesInIndexFile.put(key, value);
					
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
	 }
	 
	//Starter Class 
	public static void main(String Args[])
	{
		long startTime = System.nanoTime();
		// Data Store location
		File folder = new File(datastore);
		Scanner obj1 = new Scanner();
		obj1.listFilesInFolder(folder);
		
		// checking for index file already present.
		File file = new File(indexFile);
		// if file doesn't exists, then create it
		if (file.exists()) {
			obj1.readFile();	
			obj1.compareIndexes();
			obj1.checkForDeletedFiles();
		}
		
		//System.out.println("Indexing algorithm will run");
		obj1.saveMap();
		long endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime) + " ns"); 
		System.out.println("Took "+(endTime - startTime)/ 1000000000.0 + " s");		
	}

}// end of class
