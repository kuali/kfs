/*
 * Copyright 2006 The Kuali Foundation.
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
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.dao.CheckDao;
import org.kuali.module.financial.service.CheckService;

/**
 * Default implementation of the Check service.
 * 
 * 
 */

public class CheckServiceImpl implements CheckService {
    // set up logging
    private static Logger LOG = Logger.getLogger(CheckServiceImpl.class);

    private CheckDao checkDao;


    /**
     * Saves a check to the DB.
     * 
     * @param check
     * @return the check which was just saved
     */
    public Check save(Check check) {
        checkDao.save(check);

        return check;
    }

    /**
     * Deletes a check from the DB.
     * 
     * @param check
     */
    public void deleteCheck(Check check) {
        checkDao.deleteCheck(check);
    }

    /**
     * Retrieves a List of Checks by their document header id
     * 
     * @param documentHeaderId
     */
    public List getByDocumentHeaderId(String documentHeaderId) {
        // retrieve the check
        return checkDao.findByDocumentHeaderId(documentHeaderId);
    }


    // Spring injection
    public void setCheckDao(CheckDao d) {
        this.checkDao = d;
    }

    public CheckDao getCheckDao() {
        return checkDao;
    }
}