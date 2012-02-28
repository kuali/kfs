/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.gl.businessobject;

import java.util.Map;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.BusinessObjectStringParserFieldUtils;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This class has utility methods for parsing OriginEntries from Strings
 */
public class CollectorDetailFieldUtil extends BusinessObjectStringParserFieldUtils {

    /**
     * Returns the class to parse into - OriginEntryFull
     * @see org.kuali.kfs.sys.businessobject.BusinessObjectStringParserFieldUtils#getBusinessObjectClass()
     */
    @Override
    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return CollectorDetail.class;
    }

    /**
     * Returns the fields to be parsed from a String, in order, to form an OriginEntryFull
     * @see org.kuali.kfs.sys.businessobject.BusinessObjectStringParserFieldUtils#getOrderedProperties()
     */
    @Override
    public String[] getOrderedProperties() {
        return new String[] {
                KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                KFSPropertyConstants.ACCOUNT_NUMBER,
                KFSPropertyConstants.SUB_ACCOUNT_NUMBER,
                KFSPropertyConstants.FINANCIAL_OBJECT_CODE,
                KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE,
                KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE,
                KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE,
                KFSPropertyConstants.COLLECTOR_DETAIL_SEQUENCE_NUMBER,
                KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE,
                KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE,
                KFSPropertyConstants.DOCUMENT_NUMBER,
                KFSPropertyConstants.COLLECTOR_DETAIL_AMOUNT,
                KFSPropertyConstants.COLLECTOR_DETAIL_GL_CREDIT_CODE,
                KFSPropertyConstants.COLLECTOR_DETAIL_NOTE_TEXT
        };
    }

    /**
     *
     */

    public int getDetailLineTotalLength() {
        int totalLength = 0;
        Map<String, Integer> lengthMap = getFieldLengthMap();
        for (String property : getOrderedProperties()) {
            totalLength += lengthMap.get(property).intValue();
        }
        return totalLength;
    }
}
