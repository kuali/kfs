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

import org.kuali.core.service.KualiUserService;
import org.kuali.module.chart.bo.PriorYearAccount;
import org.kuali.module.chart.dao.PriorYearAccountDao;
import org.kuali.module.chart.service.PriorYearAccountService;

/**
 */
public class PriorYearAccountServiceImpl implements PriorYearAccountService {

    private PriorYearAccountDao priorYearAccountDao;
    private KualiUserService kualiUserService;
    
    public PriorYearAccountServiceImpl() {
        super();
    }

    /**
     * @param priorYearAccountDao The priorYearAccountDao to set.
     */
    public void setPriorYearAccountDao(PriorYearAccountDao priorYearAccountDao) {
        this.priorYearAccountDao = priorYearAccountDao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.chart.service.PriorYearAccountService#getByPrimaryKey(java.lang.String, java.lang.String)
     */
    public PriorYearAccount getByPrimaryKey(String chartCode, String accountNumber) {
        return priorYearAccountDao.getByPrimaryId(chartCode, accountNumber);
    }

    public void setKualiUserService(KualiUserService kualiUserService) {
        this.kualiUserService = kualiUserService;
    }    
    
}
