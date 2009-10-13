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

import java.io.OutputStream;

import org.kuali.kfs.fp.document.CashReceiptDocument;


/**
 * Service for handling creation of the cover sheet of the <code>{@link CashReceiptDocument}</code>
 * 
 */
public interface CashReceiptCoverSheetService {

    /**
     * This method determines whether or not cover sheet printing is allowed for the provided cash receipt document.
     * 
     * @param crDoc The document that the cover sheet will be printed for.
     * @return True if coverSheet printing is allowed for the given CashReceiptDocument, false otherwise.
     */
    public boolean isCoverSheetPrintingAllowed(CashReceiptDocument crDoc);


    /**
     * Generate a cover sheet for the <code>{@link CashReceiptDocument}</code>. An <code>{@link OutputStream}</code> is written
     * to for the coversheet.
     * 
     * @param document The <code>{@link CashReceiptDocument}</code> the cover sheet is being generated for.
     * @param searchPath A directory path used to identify the path to the template that will be used for creating this cover sheet.
     * @param OutputStream The output stream that the printable cover sheet will be piped to.
     * @exception Exception Thrown if there are any problems generating the cover sheet.
     */
    public void generateCoverSheet(CashReceiptDocument document, String searchPath, OutputStream outputStream) throws Exception;
}
