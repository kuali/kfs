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
package org.kuali.kfs.module.purap.util;

import org.kuali.kfs.module.purap.document.AccountsPayableDocument;

/**
 * Purchasing Accounts Payable Question Callback
 * Defines a callback method for post processing handling in the question interface.
 */
public interface PurQuestionCallback {

    /**
     * Hooks for doing processing on the document after a question has been performed.
     * 
     * @param document - AP document
     * @param noteText - user entered note
     */
    public AccountsPayableDocument doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception;
}
