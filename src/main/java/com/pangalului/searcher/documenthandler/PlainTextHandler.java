package com.pangalului.searcher.documenthandler;

import com.pangalului.searcher.LuceneConstants;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;

import javax.print.Doc;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by solo on 16 Mei 2018.
 */
public class PlainTextHandler implements DocumentHandler{
    @Override
    public Document getDocument(File file) throws Exception {
        Document document = new Document();
        FileInputStream fis = new FileInputStream(file);
        byte[] bodyText = new byte[(int) file.length()];
        if (bodyText.length > 0) {
            fis.read(bodyText);
            fis.close();
            Field fileContent = new Field(LuceneConstants.CONTENTS, new String(bodyText, "UTF-8"), new FieldType(TextField.TYPE_STORED));
            Field fileName = new Field(LuceneConstants.FILE_NAME, file.getName(), new FieldType(TextField.TYPE_STORED));
            Field filePath = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), new FieldType(TextField.TYPE_STORED));
            Field isRelevant = new Field(LuceneConstants.FILE_IS_RELEVANT, "false", new FieldType(TextField.TYPE_STORED));
            document.add(isRelevant);
            document.add(fileContent);
            document.add(fileName);
            document.add(filePath);
            return document;
        }
        return null;
    }
}
