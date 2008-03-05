/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.vendor.service.impl;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.vendor.bo.CommodityCode;
import org.kuali.module.vendor.service.CommodityCodeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the CommodityCodeService. 
 * This is the default, Kuali delivered implementation. It's currently used for dwr.
 */
@Transactional
public class CommodityCodeServiceImpl implements CommodityCodeService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CommodityCodeServiceImpl.class);

    private BusinessObjectService businessObjectService;
    
    /**
     * @see org.kuali.module.purap.service.CommodityCodeService#getByPrimaryId(java.lang.String)
     */
    public CommodityCode getByPrimaryId(String purchasingCommodityCode) {
        CommodityCode ccToBeRetrieved = new CommodityCode();
        ccToBeRetrieved.setPurchasingCommodityCode(purchasingCommodityCode);
        CommodityCode cc = (CommodityCode)businessObjectService.retrieve( ccToBeRetrieved );
        return cc;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}