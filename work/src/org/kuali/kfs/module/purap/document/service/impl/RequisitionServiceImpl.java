/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/document/service/impl/RequisitionServiceImpl.java,v $
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
