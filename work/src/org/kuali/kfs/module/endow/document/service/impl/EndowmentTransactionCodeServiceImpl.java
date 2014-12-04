/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class is the service implementation for the EndowmentTransactionCodeService. This is the default, Kuali provided implementation.
 */
public class EndowmentTransactionCodeServiceImpl implements EndowmentTransactionCodeService {
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService#getByPrimaryKey(java.lang.String)
     */
    public EndowmentTransactionCode getByPrimaryKey(String eTranCode) {
        EndowmentTransactionCode endowmentTransactionCode = null;
        
        if (StringUtils.isNotBlank(eTranCode)) {
            Map criteria = new HashMap();

            if (SpringContext.getBean(DataDictionaryService.class).getAttributeForceUppercase(EndowmentTransactionCode.class, EndowPropertyConstants.KUALICODEBASE_CODE)) {
                eTranCode = eTranCode.toUpperCase();
            }
            
            criteria.put("code", eTranCode);

            endowmentTransactionCode = (EndowmentTransactionCode) businessObjectService.findByPrimaryKey(EndowmentTransactionCode.class, criteria);
        }
        return endowmentTransactionCode;
    }

    /**
     * Gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
