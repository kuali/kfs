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
