/*
 * Copyright 2007 The Kuali Foundation
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
