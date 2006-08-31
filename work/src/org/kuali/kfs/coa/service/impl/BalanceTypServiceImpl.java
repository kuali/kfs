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

import java.util.Collection;

import org.kuali.core.service.KualiCodeService;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.dao.BalanceTypeDao;
import org.kuali.module.chart.service.BalanceTypService;

/**
 * This service implementation is the default implementation of the BalanceTyp service that is delivered with Kuali. It uses the
 * balance typs that are defined in the Kuali database.
 * 
 * @author Kuali Financial Transactions Red Team (kualidev@oncourse.iu.edu)
 */
public class BalanceTypServiceImpl implements BalanceTypService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceTypServiceImpl.class);

    // balance type constants
    private static final String ACTUAL_BALANCE_TYPE = "AC";

    private KualiCodeService kualiCodeService;
    private BalanceTypeDao balanceTypeDao;

    /**
     * @see org.kuali.module.chart.service.BalanceTypService#getActualBalanceTyp()
     */
    public BalanceTyp getActualBalanceTyp() {
        return getBalanceTypByCode(ACTUAL_BALANCE_TYPE);
    }

    /**
     * 
     * @see org.kuali.module.chart.service.BalanceTypService#getEncumbranceBalanceTypes()
     */
    public Collection getEncumbranceBalanceTypes() {
        LOG.debug("getEncumbranceBalanceTypes() started");

        return balanceTypeDao.getEncumbranceBalanceTypes();
    }

    /**
     * This method retrieves a BalanceTyp instance from the Kuali database by its primary key - the balance typ's code.
     * 
     * @param code The primary key in the database for this data type.
     * @return A fully populated object instance.
     */
    public BalanceTyp getBalanceTypByCode(String code) {
        return (BalanceTyp) kualiCodeService.getByCode(BalanceTyp.class, code);
    }

    /**
     * @see org.kuali.module.chart.service.BalanceTypService#getAllBalanceTyps()
     */
    public Collection getAllBalanceTyps() {
        return kualiCodeService.getAll(BalanceTyp.class);
    }

    public void setKualiCodeService(KualiCodeService kualiCodeService) {
        this.kualiCodeService = kualiCodeService;
    }

    public void setBalanceTypeDao(BalanceTypeDao balanceTypeDao) {
        this.balanceTypeDao = balanceTypeDao;
    }
}