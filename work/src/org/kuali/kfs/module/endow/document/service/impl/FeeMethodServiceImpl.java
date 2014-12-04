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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.KemidFee;
import org.kuali.kfs.module.endow.document.service.FeeMethodService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is the service implementation for the Fee Method. This is the default, Kuali provided implementation.
 */
public class FeeMethodServiceImpl implements FeeMethodService {

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.FeeMethodService#getFeeMethodsByNextProcessingDate(Date)
     */
    public Collection<FeeMethod> getFeeMethodsByNextProcessingDate(java.util.Date currentDate) {
        Collection<FeeMethod> feeMethods = new ArrayList();
        Map criteria = new HashMap();
    
        criteria.put(EndowPropertyConstants.FEE_METHOD_NEXT_PROCESS_DATE, currentDate);
        feeMethods = businessObjectService.findMatching(FeeMethod.class, criteria);
        
        return feeMethods;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.FeeMethodService#getFeeMethods()
     */
    public Collection<FeeMethod> getFeeMethods() {
        Collection<FeeMethod> feeMethods = new ArrayList();
        
        feeMethods = businessObjectService.findAll(FeeMethod.class);
        
        return feeMethods;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.FeeMethodService#getByPrimaryKey(java.lang.String)
     */
    public FeeMethod getByPrimaryKey(String feeMethodCode) {
        FeeMethod feeMethod = null;
        if (StringUtils.isNotBlank(feeMethodCode)) {
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put(EndowPropertyConstants.ENDOWCODEBASE_CODE, feeMethodCode);

            feeMethod = (FeeMethod) businessObjectService.findByPrimaryKey(FeeMethod.class, criteria);
        }
        return feeMethod;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.FeeMethodService#getFeeMethodNextProcessDate(java.lang.String)
     */
    public String getFeeMethodNextProcessDateForAjax(String feeMethodCode) {

        String nextProcessDateString = null;
        Date nextProcessDate = getFeeMethodNextProcessDate(feeMethodCode);

        if (ObjectUtils.isNotNull(nextProcessDate)) {
            nextProcessDateString = dateTimeService.toDateString(getFeeMethodNextProcessDate(feeMethodCode));
        }

        return nextProcessDateString;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.FeeMethodService#getFeeMethodNextProcessDate(java.lang.String)
     */
    public Date getFeeMethodNextProcessDate(String feeMethodCode) {

        FeeMethod feeMethod = getByPrimaryKey(feeMethodCode);
        Date nextProcessDate = null;

        if (ObjectUtils.isNotNull(feeMethod)) {
            nextProcessDate = feeMethod.getFeeNextProcessDate();
        }

        return nextProcessDate;
    }

    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the dateTimeService.
     * 
     * @return dateTimeService
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService.
     * 
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.service.FeeMethodService#isFeeMethodUsedOnAnyKemid(java.lang.String)
     */
    public boolean isFeeMethodUsedOnAnyKemid(String feeMethodCode) {
        boolean isUsed = false;
        int count = 0;
        Map<String, String> fieldValues = new HashMap<String, String>();

        fieldValues.put(EndowPropertyConstants.KEMID_FEE_MTHD_CD, feeMethodCode);
        count = businessObjectService.countMatching(KemidFee.class, fieldValues);

        if (count > 0) {
            isUsed = true;
        }

        return isUsed;
    }


}
