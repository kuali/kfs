/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.financial.service;

import java.io.OutputStream;

import org.kuali.module.financial.document.CashReceiptDocument;


/**
 * Service for handling creation of the cover sheet of the <code>{@link CashReceiptDocument}</code>
 */
public interface CashReceiptCoverSheetService {

    /**
     * @param crDoc
     * @return true if coverSheet printing is allowed for the given CashReceiptDocument
     */
    public boolean isCoverSheetPrintingAllowed(CashReceiptDocument crDoc);


    /**
     * Generate a cover sheet for the <code>{@link CashReceiptDocument}</code>. An <code>{@link OutputStream}</code> is written
     * to for the coversheet.
     * 
     * @param document
     * @param searchPath
     * @param OutputStream
     * @exception DocumentException
     * @exception IOException
     */
    public void generateCoverSheet(CashReceiptDocument document, String searchPath, OutputStream outputStream) throws Exception;
}
