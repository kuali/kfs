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
package org.kuali.kfs.fp.document.service;


import java.io.IOException;
import java.io.OutputStream;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;

import com.lowagie.text.DocumentException;

/**
 * This service interface defines the methods that a DisbursementVoucherCoverSheetService implementation must provide.
 * 
 */
public interface DisbursementVoucherCoverSheetService {

    /**
     * Generates a disbursement voucher cover sheet.
     * 
     * @param templateDirectory The file system directory which contains the template.
     * @param templateName The name of the template file used to generate the cover sheet.
     * @param document The DisbursementVoucherDocument used to generate the cover sheet.
     * @param outputStream The stream to write out the new cover sheet.
     * @throws DocumentException Thrown if there is a problem pulling data from the document to create the cover sheet.
     * @throws IOException Thrown if there is a problem writing the cover sheet to the output stream.
     */
    public void generateDisbursementVoucherCoverSheet(String templateDirectory, String templateName, DisbursementVoucherDocument document, OutputStream outputStream) throws DocumentException, IOException;

    /**
     * checks the status of the document to see if the cover sheet is printable
     * 
     * @param document submitted document
     * @return true if document is not canceled, initiated, disapproved, exception, or saved
     */
    public boolean isCoverSheetPrintable(DisbursementVoucherDocument document);
}
