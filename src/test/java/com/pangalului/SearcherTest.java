package com.pangalului;

import com.pangalului.searcher.Indexer;
import com.pangalului.searcher.LuceneConstants;
import com.pangalului.searcher.Searcher;
import com.pangalului.searcher.TextFileFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.surround.parser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by solo on 21 Mei 2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SearcherTest {

    @Test
    public void updateIndex() throws Exception {
        Indexer indexer = new Indexer(LuceneConstants.INDEX_DIR);
        File file = new File("D:\\kuliah\\Semester_8_End\\Sistem_Temu_Balik_Informasi\\Projek\\implementasi\\pangalului\\repository\\data\\Comprehensive-Safety-and-Health-Program-General-Industry.doc");
        long startTime = System.currentTimeMillis();
        indexer.updateDocument(file);
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(file.getName()+" has been updated, time taken: "
                +(endTime-startTime)+" ms");
    }


    @Test
    public void SearcherQuery() throws IOException {
        Searcher searcher = new Searcher(LuceneConstants.INDEX_DIR);
        String searchQuery = "The attached Health and Safety Program";
        ArrayList<String> resultPath = new ArrayList<>();
        TopDocs hits = null;
        Long startTime = System.currentTimeMillis();
        try {
            hits = searcher.search(searchQuery);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            e.printStackTrace();
        }
        Long endTime = System.currentTimeMillis();
        System.out.println("Queri = "+ searchQuery);
        System.out.println(hits.totalHits +
                " documents found. Time :" + (endTime - startTime));
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
//            resultPath.add(doc.get(LuceneConstants.FILE_NAME));
            System.out.println(doc.get(LuceneConstants.FILE_NAME));
//            System.out.println(doc.get(LuceneConstants.CONTENTS));
        }
        searcher.close();
    }
}
