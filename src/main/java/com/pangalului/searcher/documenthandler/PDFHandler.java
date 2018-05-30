package com.pangalului.searcher.documenthandler;

import com.pangalului.searcher.LuceneConstants;
import com.sun.xml.internal.ws.handler.HandlerException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

/**
 * Created by solo on 16 Mei 2018.
 */
public class PDFHandler implements DocumentHandler {

    @Override
    public Document getDocument(File file) throws Exception {
        COSDocument cosDoc = null;
        // Load input stream into memory
        try{
            cosDoc = parseDocument(new RandomAccessFile(file, "r"));

        }catch (IOException e) {
            closeCOSDocument(cosDoc);
            System.out.println("Can't parser PDF document"+e.getMessage());
        }

        // Extract PDD Document's textual content
        String docText = null;
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            docText = stripper.getText(new PDDocument(cosDoc));

        }catch (IOException e) {
            closeCOSDocument(cosDoc);
            throw new HandlerException(
                    "Cannot parse PDF document", e);
        }


        Document doc = new Document();
        if (docText != null) {
            Field fileContent = new Field(LuceneConstants.CONTENTS, docText,  new FieldType(TextField.TYPE_STORED));
            Field fileName = new Field(LuceneConstants.FILE_NAME, file.getName(), new FieldType(TextField.TYPE_STORED));
            Field filePath = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), new FieldType(TextField.TYPE_STORED));
            Field isRelevant = new Field(LuceneConstants.FILE_IS_RELEVANT, "false", new FieldType(TextField.TYPE_STORED));
            doc.add(isRelevant);
            doc.add(fileName);
            doc.add(fileContent);
            doc.add(filePath);
            closeCOSDocument(cosDoc);
            return  doc;
        }

//        // Extract pdf document's meta data
//        PDDocument pdDocument = new PDDocument(cosDoc);
//
//        try {
//            PDDocumentInformation documentInformation = pdDocument.getDocumentInformation();
//            String author = documentInformation.getAuthor();
//            String title = documentInformation.getTitle();
//            String keywords = documentInformation.getKeywords();
//            String summary = documentInformation.getSubject();
//            if((author != null) && !author.equals("")) {
//                doc.add(new Field("author", author, new FieldType(TextField.TYPE_STORED)));
//            }
//            if((title != null) && !title.equals("")) {
//                doc.add(new Field("title", title, new FieldType(TextField.TYPE_STORED)));
//            }
//            if((keywords != null) && !keywords.equals("")) {
//                doc.add(new Field("keywords", keywords, new FieldType(TextField.TYPE_STORED)));
//            }
//            if((summary != null) && !summary.equals("")) {
//                doc.add(new Field("summary", summary, new FieldType(TextField.TYPE_STORED)));
//            }
//        }catch (Exception e) {
//            closeCOSDocument(cosDoc);
//            closePDDocument(pdDocument);
//            System.err.println("Cannot get PDF document meta-data : "+e.getMessage());
//        }
        closeCOSDocument(cosDoc);
//        closePDDocument(pdDocument);
        return null;
    }
    private static  COSDocument parseDocument(RandomAccessRead is) throws IOException{
        PDFParser parser = new PDFParser(is);
        parser.parse();
        return  parser.getDocument();
    }

    private void closeCOSDocument(COSDocument cosDoc) {
        if(cosDoc != null) {
            try {
                cosDoc.close();
            }catch (IOException e) {
                System.err.println(e.toString());
            }
        }
    }

    private void closePDDocument(PDDocument pdDocument) {
        if(pdDocument != null) {
            try {
                pdDocument.close();
            }catch (IOException e) {
                System.err.println(e.toString());
            }
        }
    }


}
