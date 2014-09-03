/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BadPdfFormatException;

/**
 * Defines utility methods web tier.
 */
public interface AccountsReceivablePdfHelperService {

    /**
     * Builds a PDF ByteArrayOutputStream that can be returned in the response.
     *
     * @param content byte[] used to build the PDF output stream
     * @return PDF ByteArrayOutputStream
     * @throws IOException
     * @throws DocumentException
     * @throws BadPdfFormatException
     */
    public ByteArrayOutputStream buildPdfOutputStream(byte[] content) throws IOException, DocumentException, BadPdfFormatException;

    /**
     * Builds a PDF ByteArrayOutputStream that can be returned in the response.
     *
     * @param contents List of byte arrays used to build the PDF output stream
     * @return PDF ByteArrayOutputStream
     * @throws IOException
     * @throws DocumentException
     * @throws BadPdfFormatException
     */
    public ByteArrayOutputStream buildPdfOutputStream(List<byte[]> contents) throws IOException, DocumentException, BadPdfFormatException;

}
