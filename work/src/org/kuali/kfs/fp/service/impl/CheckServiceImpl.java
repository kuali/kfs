/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.dao.CheckDao;
import org.kuali.module.financial.service.CheckService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * This is the default implementation of the CheckService interface.
 */

@NonTransactional
public class CheckServiceImpl implements CheckService {
    // set up logging
    private static Logger LOG = Logger.getLogger(CheckServiceImpl.class);

    private CheckDao checkDao;

    /**
     * Retrieves a List of Checks by using the document header id given to retrieve a document and then 
     * retrieving all checks associated with that document.
     * 
     * @param documentHeaderId The document header id to use to find the associated collection of checks.
     * @return A collection of checks associated with a document with the provided document header id.
     */
    public List getByDocumentHeaderId(String documentHeaderId) {
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