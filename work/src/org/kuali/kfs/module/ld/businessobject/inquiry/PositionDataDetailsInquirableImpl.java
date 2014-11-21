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
package org.kuali.kfs.module.ld.businessobject.inquiry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.ld.businessobject.inquiry.AbstractPositionDataDetailsInquirableImpl;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.PositionData;
import org.kuali.rice.krad.bo.BusinessObject;



/**
 * This class is used to generate the URL for the user-defined attributes for the Position Inquiry screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class PositionDataDetailsInquirableImpl extends AbstractPositionDataDetailsInquirableImpl {

    @Override
    protected String getPositionNumberKeyValue() {
        return LaborConstants.getDashPositionNumber();
    }

    @Override
    protected String getFinancialBalanceTypeCodeKeyValue() {
        return LaborConstants.BalanceInquiries.BALANCE_TYPE_AC_AND_A21;
    }

    @Override
    protected String getEffectiveDateKey() {
        return LaborPropertyConstants.EFFECTIVE_DATE;
    }

    @Override
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return PositionData.class;
    }

    @Override
    public BusinessObject getBusinessObject(Map fieldValues) {
        List<PositionData> positionList = new ArrayList<PositionData>(getLookupService().findCollectionBySearch(PositionData.class, fieldValues));
        SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");

        Date today = Calendar.getInstance().getTime();
        Date maxEffectiveDate = null;

        PositionData lookupValue = null;
        for (PositionData position : positionList) {
            Date effectiveDate = position.getEffectiveDate();
            if (effectiveDate.compareTo(today) <= 0 && (maxEffectiveDate == null || effectiveDate.compareTo(maxEffectiveDate) > 0)) {
                maxEffectiveDate = effectiveDate;
                lookupValue = position;
            }
        }

        return lookupValue;
    }

}
