/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.dao;

import java.util.List;

import org.kuali.module.purap.bo.PurApItem;

/**
 * PurApAccounting DAO Interface.
 */
public interface PurApAccountingDao {

    /**
     * Retrieves the accounting lines for a purap item.
     * 
     * @param item - purap item
     * @return - list of accounting lines
     */
    public List getAccountingLinesForItem(PurApItem item);

    /**
     * Deletes the summary accounts by purap document id.
     * 
     * @param purapDocumentIdentifier - purap document id
     */
    public void deleteSummaryAccounts(Integer purapDocumentIdentifier);
    
}
