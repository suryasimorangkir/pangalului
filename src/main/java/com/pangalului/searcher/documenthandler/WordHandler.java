package com.pangalului.searcher.documenthandler;

import com.pangalului.searcher.LuceneConstants;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by solo on 16 Mei 2018.
 */
public class WordHandler implements DocumentHandler {
    @Override
    public Document getDocument(File file) throws Exception {
        String bodyText = null;
        InputStream is = new FileInputStream(file.getPath());

        if(getExtension(file.getName()).equalsIgnoreCase(LuceneConstants.EXT_DOC)) {
            WordExtractor we = new WordExtractor(is);
            String[] paragraphs = we.getParagraphText();
            for(String para : paragraphs) {
                bodyText += para;
            }
            we.close();
        }else if(getExtension(file.getName()).equalsIgnoreCase(LuceneConstants.EXT_DOCX)) {
            XWPFDocument wordDocument = new XWPFDocument(is);
            XWPFWordExtractor we = new XWPFWordExtractor(wordDocument);
            bodyText = we.getText();
            wordDocument.close();
            we.close();
        }

        if (bodyText != null) {
            Document doc = new Document();
            Field fileContent = new Field(LuceneConstants.CONTENTS, bodyText,  new FieldType(TextField.TYPE_STORED));
            Field fileName = new Field(LuceneConstants.FILE_NAME, file.getName(), new FieldType(TextField.TYPE_STORED));
            Field filePath = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), new FieldType(TextField.TYPE_STORED));
            Field isRelevant = new Field(LuceneConstants.FILE_IS_RELEVANT, "false", new FieldType(TextField.TYPE_STORED));
            doc.add(isRelevant);
            doc.add(fileName);
            doc.add(fileContent);
            doc.add(filePath);
            return doc;
        }
        return null;
    }

    private String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = filename.lastIndexOf(".");
        return filename.substring(index+1);
    }
}
