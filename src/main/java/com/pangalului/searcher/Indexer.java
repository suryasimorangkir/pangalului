package com.pangalului.searcher;

/**
 * Created by solo on 10 Mei 2018.
 */

import com.pangalului.searcher.documenthandler.PDFHandler;
import com.pangalului.searcher.documenthandler.PlainTextHandler;
import com.pangalului.searcher.documenthandler.WordHandler;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;

/**
 * Created by solo on 03/05/2018.
 */

public class Indexer {
    private IndexWriter writer;
    public Indexer(String indexDirectoryPath) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        Directory indexDirectory =
                FSDirectory.open(Paths.get(indexDirectoryPath));
        // 1. Create Lucene Index
        writer = new IndexWriter(indexDirectory,indexWriterConfig);

    }

    public void close() throws IOException {
        writer.close();
    }

    private Document getDocumentTXT(File file) throws  Exception {
        PlainTextHandler plainTextHandler = new PlainTextHandler();
        return plainTextHandler.getDocument(file);
    }

    private Document getDocumentPDF(File file) throws Exception{
        PDFHandler pdf = new PDFHandler();
        return pdf.getDocument(file);
    }

    private Document getDocumentDOCX(File file) throws Exception{
        WordHandler word = new WordHandler();
        return word.getDocument(file);
    }

    private void indexFile(File file) throws Exception {

        System.out.println("Indexing file : " + file.getCanonicalFile());
        Document document = getDocument(file);
        if(document != null)
            writer.addDocument(document);

        //update indexes for file contents
    }

    public void updateDocument(File file) throws Exception {
//        Directory directory = FSDirectory.open(Paths.get(LuceneConstants.INDEX_DIR));
//        writer = new IndexWriter(directory,
//                new StandardAnalyzer());
//        Document doc = getDocument(file);
//        writer.updateDocument(new Term(LuceneConstants.FILE_NAME, doc.get(LuceneConstants.FILE_NAME)), doc);

    }

    private Document getDocument(File file) throws Exception {
        Document document = null;
        System.out.println("Indexing file : " + file.getCanonicalFile());
        if (getExtension(file.getName()).equalsIgnoreCase(LuceneConstants.EXT_PDF)) {
            document = getDocumentPDF(file);
        }else if (getExtension(file.getName()).equalsIgnoreCase(LuceneConstants.EXT_TXT)) {
            document = getDocumentTXT(file);
        }else if (getExtension(file.getName()).equalsIgnoreCase(LuceneConstants.EXT_DOCX) ||
                getExtension(file.getName()).equalsIgnoreCase(LuceneConstants.EXT_DOC)) {
            document = getDocumentDOCX(file);
        }
        return document;
    }



    private String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf(".");
        return filename.substring(index+1);
    }

    public int createIndex(String dataDirPath, TextFileFilter filter)
            throws IOException{
        //get all files in the data directory
        File[] files = new File(dataDirPath).listFiles();
        int i =1;
        for (File file : files) {
            if(!file.isDirectory()
                    && !file.isHidden()
                    && file.exists()
                    && file.canRead()
                    && filter.accept(file)
                    ){
                try {
                    System.out.print(i+" : ");
                    indexFile(file);
                }catch (Exception e)  {
                    System.err.println(e.getMessage());
                }
            }
            i++;
        }

        return writer.numDocs();
    }



}
