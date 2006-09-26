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
package org.kuali.module.chart.service.impl;

import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.dao.SubObjectCodeDao;
import org.kuali.module.chart.service.SubObjectCodeService;

/**
 * This class is the service implementation for the SubObjectCode structure. This is the default implementation that gets delivered
 * with Kuali.
 * 
 * @author Kuali Nervous System Team ()
 */
public class SubObjectCodeServiceImpl implements SubObjectCodeService {
    private SubObjectCodeDao subObjectCodeDao;

    /**
     * @see org.kuali.module.chart.service.SubObjectCodeService#getByPrimaryId(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public SubObjCd getByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode) {
        return subObjectCodeDao.getByPrimaryId(universityFiscalYear, chartOfAccountsCode, accountNumber, financialObjectCode, financialSubObjectCode);
    }

    /**
     * @return Returns the SubObjectCodeDao
     */
    public SubObjectCodeDao getSubObjectCodeDao() {
        return subObjectCodeDao;
    }

    /**
     * @param subObjectCodeDao The SubObjectCode object to set
     */
    public void setSubObjectCodeDao(SubObjectCodeDao subObjectCodeDao) {
        this.subObjectCodeDao = subObjectCodeDao;
    }
}