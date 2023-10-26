package Lucene;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MainClass {

    public static void main(String args[]) throws Exception {

        Directory directory = null;
        Path path = Paths.get("C:\\Users\\Maria\\Desktop\\uni\\idd");
        Path indexPath = Paths.get("C:\\Users\\Maria\\Desktop\\index");
        System.out.println(path.toString());
        
        Scanner scanner = new Scanner(System.in);
        boolean continueQuery = true;

        try {
            directory = FSDirectory.open(indexPath);
            LuceneIndex.createIndex(directory, path);
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            
            
            while (continueQuery) {
                System.out.print("Inserisci una query oppure 'exit' per uscire: ");
                String queryReader = scanner.nextLine();
                String[] querySplitted = queryReader.split(":");
                String field = querySplitted[0].trim();
                QueryParser queryParser = new QueryParser(field, new StandardAnalyzer());

                Query query;
                
                if (field.equals("nome")) {
                    query = queryParser.parse(querySplitted[1] + ".txt");
                    LuceneIndex.runQuery(searcher, query, 1);
                } else if (field.equals("contenuto")) {
                    query = queryParser.parse(querySplitted[1]);
                    LuceneIndex.runQuery(searcher, query, 5);
                } else if (field.equals("exit")) {
                    continueQuery = false;
                } else {
                    System.out.println("Campo non valido");
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	scanner.close();
            directory.close();
        }
    }

}