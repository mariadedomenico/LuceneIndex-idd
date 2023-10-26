package Lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;


public class LuceneIndex {
    
    public static void createIndex(Directory directory, Path path) throws Exception {
    	
        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();
        
        perFieldAnalyzers.put("nome", new StandardAnalyzer());
        perFieldAnalyzers.put("contenuto", new ItalianAnalyzer());
        
        Analyzer analyzer = new PerFieldAnalyzerWrapper(new ItalianAnalyzer(), perFieldAnalyzers);
        
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        Codec codec = new SimpleTextCodec();
        config.setCodec(codec);
        
        IndexWriter writerIndex = new IndexWriter(directory, config);
        writerIndex.deleteAll(); 

        long startTime = System.currentTimeMillis();
        try {
        	File dir = new File(path.toString());
            File[] files = dir.listFiles();
            if (files != null) {
	            for (File file : files) {
	                Document document = new Document();
	                document.add(new TextField("nome",file.getName(), Field.Store.YES));
	                Reader reader = new FileReader(file.getCanonicalPath());
	                document.add(new TextField("contenuto", reader));
	                writerIndex.addDocument(document);
	                reader.close();
	            }
            }
            writerIndex.commit();
            writerIndex.close();

            long endTime = System.currentTimeMillis();
            long indexingTime = endTime - startTime;
            System.out.println("Tempo di indicizzazione: " + indexingTime + " ms\n");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
	public static void runQuery(IndexSearcher searcher, Query query, int numDoc) throws IOException {
        TopDocs hits = searcher.search(query, numDoc);
        System.out.println("Sono stati trovati " + hits.scoreDocs.length + " " + "documenti");
        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("doc "+scoreDoc.doc + ": "+ doc.get("nome") + " (" + scoreDoc.score +")");
        }
        System.out.print("\n");
    }
    

}
