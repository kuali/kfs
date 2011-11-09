/*
 * Copyright 2006 The Kuali Foundation
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
