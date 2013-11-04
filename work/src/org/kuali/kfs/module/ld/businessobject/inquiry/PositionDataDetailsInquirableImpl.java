/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
