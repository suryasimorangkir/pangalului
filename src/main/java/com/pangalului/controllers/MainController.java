package com.pangalului.controllers;

import com.pangalului.DokumenFile;
import com.pangalului.searcher.Indexer;
import com.pangalului.searcher.LuceneConstants;
import com.pangalului.searcher.Searcher;
import com.pangalului.searcher.TextFileFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by solo on 08/05/2018.
 */
@Controller
public class MainController {
    @Value("${spring.application.name}")
    private String aplicationName;

    @Value("${spring.application.title}")
    private  String titleSearch;


    @RequestMapping(value = {"/","/home"}, method = RequestMethod.GET)
    public  ModelAndView home(Model model) {
        model.addAttribute("applicationName", aplicationName);
        return new ModelAndView("pages/home");
    }
    @RequestMapping(value = {"createIndex"}, method = RequestMethod.GET)
    public  ModelAndView createIndex(Model model) {
        model.addAttribute("applicationName", aplicationName);
        long startTime=0;
        long endTime=0;
        try {
            startTime = System.currentTimeMillis();
            createIndex();
            endTime = System.currentTimeMillis();
            System.out.println("Time to index all document " +(endTime - startTime) + " ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModelAndView("pages/home");
    }

    @RequestMapping(value="search", method = RequestMethod.GET)
    public  @ResponseBody ModelAndView search(@RequestParam(value = "q", required = false) String query, Model model){
        ArrayList<DokumenFile> docFiles = new ArrayList<>();
        long startTime=0;
        long endTime=0;
        try {
            startTime = System.currentTimeMillis();
            docFiles = search(query);
            endTime = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.apache.lucene.queryparser.surround.parser.ParseException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        model.addAttribute("titleSearch", titleSearch);
        model.addAttribute("query",query);
        model.addAttribute("docFiles", docFiles);
        model.addAttribute("timeConsumed", (endTime - startTime));
        return new ModelAndView("pages/search-result");
    }



    @RequestMapping(value = "search/download/{path}", method = RequestMethod.GET)
    @ResponseBody
    public  void  downloadFile (@RequestParam("path") String path, HttpServletResponse response) throws IOException {
        File file = new File(path);


        if(!file.exists()){
            String errorMessage = "Sorry. The file you are looking for does not exist";
            System.out.println(errorMessage);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return;
        }

        String mimeType= URLConnection.guessContentTypeFromName(file.getName());
        if(mimeType==null){
            System.out.println("mimetype is not detectable, will take default");
            mimeType = "application/octet-stream";
        }

        System.out.println("mimetype : "+mimeType);

        response.setContentType(mimeType);

        /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));


        /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
        //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

        response.setContentLength((int)file.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        //Copy bytes from source to destination(outputstream in this example), closes both streams.
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }




//
    private void createIndex() throws IOException {
        Indexer indexer = new Indexer(LuceneConstants.INDEX_DIR);
        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(LuceneConstants.DATA_DIR, new TextFileFilter());
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed+" File indexed, time taken: "
                +(endTime-startTime)+" ms");
    }


    private ArrayList<DokumenFile> search(String searchQuery) throws IOException, ParseException, org.apache.lucene.queryparser.surround.parser.ParseException {
        Searcher searcher = new Searcher(LuceneConstants.INDEX_DIR);
        ArrayList<DokumenFile> docFiles = new ArrayList<>();
        TopDocs hits = searcher.search(searchQuery);
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            docFiles.add(new DokumenFile(doc.get(LuceneConstants.FILE_NAME), doc.get(LuceneConstants.FILE_PATH), scoreDoc.score, searcher.explanation(scoreDoc.doc).toString()));
        }
        searcher.close();
        return docFiles;
    }

}
