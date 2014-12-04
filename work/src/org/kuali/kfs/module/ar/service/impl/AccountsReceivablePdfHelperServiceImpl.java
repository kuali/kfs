/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.service.AccountsReceivablePdfHelperService;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

public class AccountsReceivablePdfHelperServiceImpl implements AccountsReceivablePdfHelperService {

    @Override
    public ByteArrayOutputStream buildPdfOutputStream(byte[] content) throws IOException, DocumentException, BadPdfFormatException {
        List<byte[]> contents = new ArrayList<>();
        contents.add(content);
        return buildPdfOutputStream(contents);
    }

    @Override
    public ByteArrayOutputStream buildPdfOutputStream(List<byte[]> contents) throws IOException, DocumentException, BadPdfFormatException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ArrayList<PdfReader> master = new ArrayList<>();
        Document document = null;
        PdfCopy writer = null;
        boolean createDocument = true;

        for (byte[] content:contents) {
            // create a reader for the document
            PdfReader reader = new PdfReader(content);
            reader.consolidateNamedDestinations();

            // retrieve the total number of pages
            int n = reader.getNumberOfPages();
            List<PdfReader> bookmarks = SimpleBookmark.getBookmark(reader);
            if (bookmarks != null) {
                master.addAll(bookmarks);
            }

            if (createDocument) {
                // step 1: create a document-object
                document = new Document(reader.getPageSizeWithRotation(1));
                // step 2: create a writer that listens to the document
                writer = new PdfCopy(document, baos);
                // step 3: open the document
                document.open();
            }
            // step 4: add content
            PdfImportedPage page;
            for (int i = 0; i < n; ) {
                ++i;
                page = writer.getImportedPage(reader, i);
                writer.addPage(page);
            }
            writer.freeReader(reader);
            createDocument = false;
        }

        if (!master.isEmpty()) {
            writer.setOutlines(master);
        }

        // step 5: we close the document
        document.close();

        return baos;
    }

}
