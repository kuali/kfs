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
package org.kuali.module.purap.service.impl;

import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.purap.bo.OrganizationParameter;
import org.kuali.module.purap.bo.VendorContract;
import org.kuali.module.purap.bo.VendorContractOrganization;
import org.kuali.module.purap.service.RequisitionService;
import org.kuali.module.purap.service.VendorService;

public class RequisitionServiceImpl implements RequisitionService {
    private BusinessObjectService businessObjectService;

    public void setBusinessObjectService(BusinessObjectService boService) {
        this.businessObjectService = boService;    
    }

    /**
     * This method gets the org_auto_po_limit for a given requisition from PUR_AP_ORG_PARM_T.
     *
    */
    public KualiDecimal getApoLimit(String chart, String org) {
        KualiDecimal apoLimit = new KualiDecimal(0);
        
        if ( ObjectUtils.isNull(ObjectUtils.isNull(chart)) || ObjectUtils.isNull(org) ) {
            return null;
        }
        
        OrganizationParameter orgParameter = new OrganizationParameter();
        orgParameter.setChartOfAccountsCode(chart);
        orgParameter.setOrganizationCode(org);
        Map parameterKeys = SpringServiceLocator.getPersistenceService().getPrimaryKeyFieldValues(orgParameter);
        orgParameter = (OrganizationParameter) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(OrganizationParameter.class, parameterKeys);
        if (ObjectUtils.isNull(orgParameter)) {
            return null;
        }

        return orgParameter.getOrganizationAutomaticPurchaseOrderLimit(); // Note, it may be null.
    }
    
}
