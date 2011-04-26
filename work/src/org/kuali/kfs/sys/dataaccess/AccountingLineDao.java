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
package org.kuali.kfs.sys.dataaccess;

import java.util.ArrayList;

import org.kuali.kfs.sys.businessobject.AccountingLine;

/**
 * The data access interface for persisting AccountingLineBase objects.
 */
public interface AccountingLineDao {

    /**
     * Deletes an accounting line from the DB.
     * 
     * @param line
     */
    void deleteAccountingLine(AccountingLine line);

    /**
     * Retrieves a list of accounting lines (by class type) associated with a given document.
     * 
     * @param clazz
     * @param id
     * @return
     */
    public ArrayList findByDocumentHeaderId(Class clazz, String id);
}
