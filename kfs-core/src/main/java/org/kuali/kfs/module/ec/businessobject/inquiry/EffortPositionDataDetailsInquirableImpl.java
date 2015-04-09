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
package org.kuali.kfs.module.ec.businessobject.inquiry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.ld.LaborLedgerPositionData;
import org.kuali.kfs.integration.ld.businessobject.inquiry.AbstractPositionDataDetailsInquirableImpl;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * This class is used to generate the URL for the user-defined attributes for the Position Inquiry screen. It extends the
 * AbstractPositionDataDetailsInquirableImpl class, so it covers both the default implementation and customized implementation.
 */
public class EffortPositionDataDetailsInquirableImpl extends AbstractPositionDataDetailsInquirableImpl {

    /**
     * @see PositionDataDetailsInquirableImpl#getUserDefinedAttributeMap()
     */
    @Override
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();
        userDefinedAttributeMap.put(KFSPropertyConstants.POSITION_NUMBER, KFSPropertyConstants.POSITION_NUMBER);
        userDefinedAttributeMap.put(EffortPropertyConstants.EFFECTIVE_DATE, EffortPropertyConstants.EFFECTIVE_DATE);

        return userDefinedAttributeMap;
    }

    @Override
    protected String getEffectiveDateKey() {
        return EffortPropertyConstants.EFFECTIVE_DATE;
    }

    @Override
    protected String getPositionNumberKeyValue() {
        return EffortConstants.DASH_POSITION_NUMBER;
    }

    @Override
    protected String getFinancialBalanceTypeCodeKeyValue() {
        return EffortConstants.BalanceInquiries.BALANCE_TYPE_AC_AND_A21;
    }

    @Override
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(LaborLedgerPositionData.class).createNewObjectFromExternalizableClass(LaborLedgerPositionData.class).getClass();
    }

    @Override
    public BusinessObject getBusinessObject(Map fieldValues) {
        List<LaborLedgerPositionData> positionList = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(LaborLedgerPositionData.class).getExternalizableBusinessObjectsList(LaborLedgerPositionData.class, fieldValues);
        SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");

        Date today = Calendar.getInstance().getTime();
        Date maxEffectiveDate = null;

        LaborLedgerPositionData lookupValue = null;
        for (LaborLedgerPositionData position : positionList) {
            Date effectiveDate = position.getEffectiveDate();
            if (effectiveDate.compareTo(today) <= 0 && (maxEffectiveDate == null || effectiveDate.compareTo(maxEffectiveDate) > 0)) {
                maxEffectiveDate = effectiveDate;
                lookupValue = position;
            }
        }

        return lookupValue;
    }

}
