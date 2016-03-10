import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {
	
	private StandardAnalyzer analyzer;
	private Directory index;
	private FSDirectory dir;
	
	
	public Searcher(){
		
		Path p = Paths.get("C:/Users/Pareshan/Downloads/index");
		
		try {
			dir = FSDirectory.open(p);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	public StandardAnalyzer getAnalyzer()
	{
		Indexer indexerObject = new Indexer();
		return indexerObject.getAnalyzer();
		
	}
	
	
	
	public void searchQuery(String queryStrig, StandardAnalyzer analyzer) throws ParseException, IOException
	{
		Query q = new QueryParser("content", analyzer).parse(queryStrig);
		
		// Searching code
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
				    
		//	Code to display the results of search
		System.out.println("Found " + hits.length + " hits");
				    
		for(int i=0;i<hits.length;++i) 
		{
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " +  d.get("filename"));
		}
				    
		// reader can only be closed when there is no need to access the documents any more
			reader.close();
		
	}

	public static void main(String[] args) {
		
		
		
		
		Searcher searcherObject = new Searcher();
		
		searcherObject.getAnalyzer();
		
		
		
		
		String querystr = args.length > 0 ? args[0] : "+zeo";
		try {
			searcherObject.searchQuery(querystr, searcherObject.getAnalyzer());
		} catch (ParseException e) {
						e.printStackTrace();
		} catch (IOException e) {
						e.printStackTrace();
		}

	}

}
