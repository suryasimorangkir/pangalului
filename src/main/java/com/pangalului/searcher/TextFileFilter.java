package com.pangalului.searcher;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileFilter;

/**
 * Created by solo on 03/05/2018.
 */
public class TextFileFilter implements FileFilter {

    @Override
    public boolean accept(File file) {
        FileNameExtensionFilter filter =
                new FileNameExtensionFilter("Document file : pdf, txt, Reach Text File , ",
                        LuceneConstants.EXT_PDF,
                        LuceneConstants.EXT_TXT,
                        LuceneConstants.EXT_DOC,
                        LuceneConstants.EXT_DOCX,
                        LuceneConstants.EXT_XLS);
        return filter.accept(file);
    }
}