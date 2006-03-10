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

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.dao.CashDrawerDao;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.service.CashDrawerService;
import org.kuali.module.financial.service.CashManagementService;


/**
 * Stock CashDrawerService implementation.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashDrawerServiceImpl implements CashDrawerService {
    private CashDrawerDao cashDrawerDao;
    private CashManagementService cashManagementService;


    /**
     * @see org.kuali.module.financial.service.CashDrawerService#closeCashDrawer(java.lang.String)
     */
    public void closeCashDrawer(String workgroupName) {
        CashDrawer drawer = getByWorkgroupName(workgroupName);
        if (drawer == null) {
            drawer = newCashDrawer(workgroupName);
        }
        drawer.setStatusCode(Constants.CashDrawerConstants.STATUS_CLOSED);

        save(drawer);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#openCashDrawer(java.lang.String)
     */
    public void openCashDrawer(String workgroupName) {
        CashDrawer drawer = getByWorkgroupName(workgroupName);
        if (drawer == null) {
            drawer = newCashDrawer(workgroupName);
        }
        drawer.setStatusCode(Constants.CashDrawerConstants.STATUS_OPEN);

        save(drawer);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#findByWorkgroupName(java.lang.String)
     */
    public CashDrawer getByWorkgroupName(String workgroupName) {
        if (StringUtils.isBlank(workgroupName)) {
            throw new IllegalArgumentException("invalid (blank) workgroupName");
        }

        return cashDrawerDao.findByWorkgroupName(workgroupName);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#save(org.kuali.module.financial.bo.CashDrawer)
     */
    public CashDrawer save(CashDrawer cashDrawer) {
        if (cashDrawer == null) {
            throw new IllegalArgumentException("invalid (null) cashDrawer");
        }

        return cashDrawerDao.save(cashDrawer);
    }

    /**
     * @see org.kuali.module.financial.service.CashDrawerService#delete(org.kuali.module.financial.bo.CashDrawer)
     */
    public void delete(CashDrawer cashDrawer) {
        if (cashDrawer == null) {
            throw new IllegalArgumentException("invalid (null) cashDrawer");
        }

        cashDrawerDao.delete(cashDrawer);
    }
    
    /**
     * If the passed in cash receipt is not yet associated with a verification unit (via campus code), 
     * then this method returns null.
     * 
     * @see org.kuali.module.financial.service.CashDrawerService#getByCashReceiptDocument(org.kuali.module.financial.document.CashReceiptDocument)
     */
    public CashDrawer getByCashReceiptDocument(CashReceiptDocument cashReceipt) {
        return getByWorkgroupName(cashManagementService.getCashReceiptVerificationUnitWorkgroupNameByCampusCode(
                cashReceipt.getCampusLocationCode()));
    }

    /**
     * @param workgroupName
     * @return CashDrawer
     */
    private CashDrawer newCashDrawer(String workgroupName) {
        CashDrawer drawer = new CashDrawer();
        drawer.setWorkgroupName(workgroupName);
        drawer.setStatusCode(Constants.CashDrawerConstants.STATUS_OPEN);

        return drawer;
    }


    // Spring injection
    /**
     * @param cashDrawerDao
     */
    public void setCashDrawerDao(CashDrawerDao cashDrawerDao) {
        this.cashDrawerDao = cashDrawerDao;
    }

    /**
     * @return CashDrawerDao
     */
    public CashDrawerDao getCashDrawerDao() {
        return cashDrawerDao;
    }

    /**
     * Gets the cashManagementService attribute. 
     * 
     * @return Returns the cashManagementService.
     */
    public CashManagementService getCashManagementService() {
        return cashManagementService;
    }

    /**
     * Sets the cashManagementService attribute value.
     * 
     * @param cashManagementService The cashManagementService to set.
     */
    public void setCashManagementService(CashManagementService cashManagementService) {
        this.cashManagementService = cashManagementService;
    }
}