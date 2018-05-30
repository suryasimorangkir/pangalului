package com.pangalului.searcher;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.surround.parser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by solo on 05/05/2018.
 */

public class Searcher {
    IndexSearcher indexSearcher;
    DirectoryReader ireader;
    QueryParser queryParser;
    Query query;

    public Searcher(String indexDirectoryPath)
            throws IOException {
        Directory indexDirectory =
                FSDirectory.open(Paths.get(indexDirectoryPath));
        ireader = DirectoryReader.open(indexDirectory);
        indexSearcher = new IndexSearcher(ireader);
        Analyzer analyzer = new StandardAnalyzer();
        queryParser = new QueryParser(LuceneConstants.CONTENTS, analyzer);
    }

    public TopDocs search(String searchQuery)
            throws IOException, ParseException,
            org.apache.lucene.queryparser.classic.ParseException {

        query = queryParser.parse(searchQuery);
        return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
    }

    public Explanation explanation(int doc) throws IOException {
        Explanation explanation = indexSearcher.explain(query, doc);

        return explanation;
    }

    public Document getDocument(ScoreDoc scoreDoc)
            throws CorruptIndexException, IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void close() throws IOException {
        ireader.close();

    }
}
