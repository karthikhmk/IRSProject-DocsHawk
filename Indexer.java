import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.FileSwitchDirectory;
import org.apache.lucene.store.NativeFSLockFactory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;



public class Indexer 
{
	
	
	private static String datastore = "C:/Users/Pareshan/Downloads/datastore"; 
	
	private IndexWriter w = null;
	private StandardAnalyzer analyzer;
	private Directory index;
	private FSDirectory dir;
	
	public Indexer(){
		
		try{
			
//			Specify the analyzer for tokenizing text.
		    //	The same analyzer should be used for indexing and searching
			analyzer = new StandardAnalyzer();
			
			//	Code to create the index
			Path p = Paths.get("C:/Users/Pareshan/Downloads/index");
			dir = FSDirectory.open(p);
			
			
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			
			w = new IndexWriter(dir, config);
			
			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	
	
	 public StandardAnalyzer getAnalyzer()
	 {
		 return this.analyzer;
	 }
	
	
	 public void listFilesInFolder(File folder) throws CorruptIndexException, IOException {
		  
	     for (File fileEntry : folder.listFiles()) {
	    	 
	         if (fileEntry.isDirectory()) {
	        	 listFilesInFolder(fileEntry);
	        	 
	         } else {
	        	 
	        	         	 
	        	 indexWriter(fileEntry);   
	        	 
	        	 
	            
	         }
	     }
	    
	 }

	 public void closeIndexWriter() 
	 {
		 try {
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 @SuppressWarnings("deprecation")
	public void indexWriter(File file) throws CorruptIndexException, IOException {
		 
		 Document doc = new Document();
		 
		 
		 if (file.getName().endsWith(".txt")
			 && !file.isHidden()
             && file.exists()
             && file.canRead()
             && file.length() > 0.0 )			 
				 			 
    	 {	 
			 doc.add(new Field("content", new FileReader(file)));
		     doc.add(new Field("filename", file.getName(), Field.Store.YES, Field.Index.ANALYZED));
		     if (doc != null) 
		     { w.addDocument(doc); }
    	   
    	 }
    	 else if( file.getName().endsWith(".pdf")
    			 && !file.isHidden()
                 && file.exists()
                 && file.canRead()
                 && file.length() > 0.0 ) 
    	 {
    		 	 
    			    	 
    		 System.out.println(file.getName().toString() + " is a .pdf file" );
    		 
         	
    			 PDDocument pdfdoc=PDDocument.load(file);
    			 String content= new PDFTextStripper().getText(pdfdoc);
    			 
    			 doc.add(new Field("content", content,Field.Store.YES, Field.Index.ANALYZED));	 
    			 doc.add(new Field("filename", file.getName(), Field.Store.YES, Field.Index.ANALYZED));
    		     if (doc != null) 
    		     { w.addDocument(doc); }
         		 
    			 
         	 
    	 }
		 
    	 else if (
    			 (file.getName().endsWith(".doc") || file.getName().endsWith(".docx")) 
    			 && !file.isHidden()
                 && file.exists()
                 && file.canRead()
                 && file.length() > 0.0)
    	 {
    		 
    		 System.out.println(file.getName().toString() + " doc/docx file" );
    		    XWPFDocument docx = new XWPFDocument(new FileInputStream(file));
    		    //using XWPFWordExtractor Class
    		    XWPFWordExtractor we = new XWPFWordExtractor(docx);
    		    
    		 doc.add(new Field("content", we.getText(),Field.Store.YES, Field.Index.ANALYZED));	 
   			 doc.add(new Field("filename", file.getName(), Field.Store.YES, Field.Index.ANALYZED));
   		     if (doc != null) 
   		     { w.addDocument(doc); }
    		    
    		 
    	 }
		 
    	 else {
    		 
    		 System.out.println(file.getName().toString() + " is neither a .doc, .docx, .pdf file"  );
    		 
    		 
    	 }
		
		 
		 
	 }
	
	
	
	 
	 
	public static void main(String[] args) throws CorruptIndexException, IOException
	{
		long start = System.currentTimeMillis();
		Indexer l = new Indexer();
		File file = new File(datastore);
		l.listFilesInFolder(file);
		l.closeIndexWriter();
		long end = System.currentTimeMillis();
		
		
		
	}
}
