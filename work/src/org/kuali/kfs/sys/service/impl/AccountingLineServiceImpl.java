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
package org.kuali.kfs.sys.service.impl;

import java.util.List;

import org.kuali.kfs.sys.dataaccess.AccountingLineDao;
import org.kuali.kfs.sys.service.AccountingLineService;
import org.kuali.kfs.sys.service.NonTransactional;

/**
 * This class is the service implementation for the AccountingLine structure. This has been created with polymorphism in mind so
 * that this service can be used for performing services for both the Source and Target AccountingLineBase structures. This is the
 * default, Kuali provided implementation.
 */

@NonTransactional
public class AccountingLineServiceImpl implements AccountingLineService {
    // set up logging
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingLineServiceImpl.class);

    private AccountingLineDao accountingLineDao;

    /**
     * Default constructor
     */
    public AccountingLineServiceImpl() {
        super();
    }

    /**
     * Retrieves an accounting line by its document header id. Will retrieve any object that extends AccountingLineBase (i.e. Source
     * and Target lines).
     *
     * @param Class The specific child class type to be retrieved.
     * @param Long
     */
    @Override
    public List getByDocumentHeaderId(Class clazz, String documentHeaderId) {
        // retrieve the line
        return getAccountingLineDao().findByDocumentHeaderId(clazz, documentHeaderId);
    }

    /**
     * Calls AccountingLineDao#findByDocumentHeaderIdAndLineType to find the appropriate lines
     * @see org.kuali.kfs.sys.service.AccountingLineService#getByDocumentHeaderIdAndLineType(java.lang.Class, java.lang.String, java.lang.String)
     */
    @Override
    public List getByDocumentHeaderIdAndLineType(Class clazz, String documentHeaderId, String lineType) {
        return getAccountingLineDao().findByDocumentHeaderIdAndLineType(clazz, documentHeaderId, lineType);
    }

    // needed for Spring injection
    /**
     * Sets the data access object
     *
     * @param d
     */
    public void setAccountingLineDao(AccountingLineDao d) {
        this.accountingLineDao = d;
    }

    /**
     * Retrieves a data access object
     */
    public AccountingLineDao getAccountingLineDao() {
        return accountingLineDao;
    }
}
