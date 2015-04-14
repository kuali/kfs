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
package org.kuali.kfs.module.bc.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReasonCode;
import org.kuali.kfs.module.bc.service.BudgetConstructionAppointmentFundingReasonCodeService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * implements the service methods defined in BudgetConstructionAppointmentFundingReasonCodeService
 */
public class BudgetConstructionAppointmentFundingReasonCodeServiceImpl implements BudgetConstructionAppointmentFundingReasonCodeService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionAppointmentFundingReasonCodeServiceImpl.class);
    
    private BusinessObjectService businessObjectService;
    
    /**
     * @see org.kuali.kfs.module.bc.service.BudgetConstructionAppointmentFundingReasonCodeService#getByPrimaryId(java.lang.String)
     */
    public BudgetConstructionAppointmentFundingReasonCode getByPrimaryId(String reasonCode) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(BCPropertyConstants.APPOINTMENT_FUNDING_REASON_CODE, reasonCode);
        
        return (BudgetConstructionAppointmentFundingReasonCode)businessObjectService.findByPrimaryKey(BudgetConstructionAppointmentFundingReasonCode.class, primaryKeys);
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
