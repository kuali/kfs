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
package org.kuali.kfs.sys.document.authorization;

import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;

/**
 * The methods needed to answer questions about presentation viewing based on internal document state for FinancialSystemTransactionalDocuments
 */
public interface FinancialSystemTransactionalDocumentPresentationController extends TransactionalDocumentPresentationController {

    /**
     * Determines if the given document can be error corrected, based on internal document state
     * @param document the document to error correct
     * @return true if the document can be error corrected, false otherwise
     */
    public abstract boolean canErrorCorrect(FinancialSystemTransactionalDocument document);
}
