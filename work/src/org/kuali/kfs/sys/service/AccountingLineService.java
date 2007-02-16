/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.kfs.service;

import java.util.List;

import org.kuali.kfs.bo.AccountingLineBase;

/**
 * This interface defines methods that an AccountingLine service implementation must provide.
 * 
 * 
 */
public interface AccountingLineService {
    /**
     * Retrieves a list of accounting lines for a given group (i.e. Target/Source) given the associated document id.
     * 
     * @param clazz
     * @param documentHeaderId
     * @return A list of AccountingLines... to be casted to the appropriate class.
     * @throws Exception
     */
    public List getByDocumentHeaderId(Class clazz, String documentHeaderId);

    /**
     * Saves an accounting line.
     * 
     * @param line
     * @return The saved accounting line
     * @throws Exception
     */
    public AccountingLineBase save(AccountingLineBase line);


    /**
     * Deletes an accounting line.
     * 
     * @param line
     * @throws Exception
     */
    public void deleteAccountingLine(AccountingLineBase line);
}