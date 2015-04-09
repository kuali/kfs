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
