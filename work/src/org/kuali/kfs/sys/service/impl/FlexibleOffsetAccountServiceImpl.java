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

import java.util.HashMap;

import org.kuali.Constants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;

/**
 * This class implements FlexibleOffsetAccountService.
 *
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class FlexibleOffsetAccountServiceImpl implements FlexibleOffsetAccountService {

    private BusinessObjectService businessObjectService;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * @see FlexibleOffsetAccountService#getByPrimaryIdIfEnabled
     */
    public OffsetAccount getByPrimaryIdIfEnabled(String chartOfAccountsCode, String accountNumber, String financialOffsetObjectCode) {
        if (!getEnabled()) {
            return null;
        }
        HashMap keys = new HashMap();
        keys.put("chartOfAccountsCode", chartOfAccountsCode);
        keys.put("accountNumber", accountNumber);
        keys.put("financialOffsetObjectCode", financialOffsetObjectCode);
        return (OffsetAccount) businessObjectService.findByPrimaryKey(OffsetAccount.class, keys);
    }

    /**
     * @see FlexibleOffsetAccountService#getEnabled
     */
    public boolean getEnabled() {
      return kualiConfigurationService.getRequiredApplicationParameterIndicator(Constants.ParameterGroups.SYSTEM,
            Constants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG);
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
