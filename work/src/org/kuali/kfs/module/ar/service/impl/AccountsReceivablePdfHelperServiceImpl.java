/*
 * Copyright 2014 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
