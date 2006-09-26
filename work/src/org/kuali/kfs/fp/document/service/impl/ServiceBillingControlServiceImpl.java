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

import java.util.Collection;
import java.util.HashMap;

import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.financial.bo.ServiceBillingControl;
import org.kuali.module.financial.service.ServiceBillingControlService;

/**
 * This class implements ServiceBillingControlService.
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class ServiceBillingControlServiceImpl implements ServiceBillingControlService {

    private BusinessObjectService businessObjectService;

    /**
     * @see ServiceBillingControlService#getByPrimaryId(String, String)
     */
    public ServiceBillingControl getByPrimaryId(String chartOfAccountsCode, String accountNumber) {
        HashMap keys = new HashMap();
        keys.put(PropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(PropertyConstants.ACCOUNT_NUMBER, accountNumber);
        return (ServiceBillingControl) businessObjectService.findByPrimaryKey(ServiceBillingControl.class, keys);
    }

    /**
     * @see ServiceBillingControlService#getAll()
     */
    public ServiceBillingControl[] getAll() {
        Collection controls = businessObjectService.findAll(ServiceBillingControl.class);
        return (ServiceBillingControl[]) controls.toArray(new ServiceBillingControl[0]);
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
