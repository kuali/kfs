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
package org.kuali.kfs.fp.service.impl;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.CheckBase;
import org.kuali.kfs.fp.dataaccess.CheckDao;
import org.kuali.kfs.fp.service.CheckService;
import org.kuali.kfs.sys.service.NonTransactional;

/**
 * 
 * This is the default implementation of the CheckService interface.
 */

@NonTransactional
public class CheckServiceImpl implements CheckService {
    // set up logging
    private static final Logger LOG = Logger.getLogger(CheckServiceImpl.class);

    protected CheckDao checkDao;

    /**
     * Retrieves a List of Checks by using the document header id given to retrieve a document and then 
     * retrieving all checks associated with that document.
     * 
     * @param documentHeaderId The document header id to use to find the associated collection of checks.
     * @return A collection of checks associated with a document with the provided document header id.
     */
    public Collection<CheckBase> getByDocumentHeaderId(String documentHeaderId) {
        // retrieve the check
        return checkDao.findByDocumentHeaderId(documentHeaderId);
    }

    // Spring injection
    /**
     * Sets the checkDao attribute.
     * @param The CheckDao to be set.
     */
    public void setCheckDao(CheckDao d) {
        this.checkDao = d;
    }

    /**
     * Gets the checkDao attribute.
     * @return An instance of the checkDao attribute.
     */
    public CheckDao getCheckDao() {
        return checkDao;
    }
}
