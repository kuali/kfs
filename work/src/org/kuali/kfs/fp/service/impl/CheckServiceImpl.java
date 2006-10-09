/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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