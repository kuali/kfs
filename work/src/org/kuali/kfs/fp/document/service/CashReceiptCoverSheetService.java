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
