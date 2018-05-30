package com.pangalului.searcher.documenthandler;

import org.apache.lucene.document.Document;

import java.io.File;

/**
 * Created by solo on 12 Mei 2018.
 */
public interface DocumentHandler {
    Document getDocument(File file) throws Exception;

}