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
package org.kuali.kfs.gl.batch;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.BusinessObjectStringParserFieldUtils;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This class has utility methods for parsing CollectorBatch from Strings for trailer record
 */
public class CollectorBatchTrailerRecordFieldUtil extends BusinessObjectStringParserFieldUtils {

    /**
     * Returns the class to parse into - CollectorBatch
     * @see org.kuali.kfs.sys.businessobject.BusinessObjectStringParserFieldUtils#getBusinessObjectClass()
     */
    @Override
    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return CollectorBatch.class;
    }

    /**
     * Returns the fields to be parsed from a String, in order, to form a CollectorBatch
     * @see org.kuali.kfs.sys.businessobject.BusinessObjectStringParserFieldUtils#getOrderedProperties()
     */
    @Override
    public String[] getOrderedProperties() {
        return new String[] {
                KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                KFSPropertyConstants.ORGANIZATION_CODE,
                KFSPropertyConstants.TRANSMISSION_DATE,
                KFSPropertyConstants.COLLECTOR_BATCH_RECORD_TYPE,
                KFSPropertyConstants.TRAILER_RECORD_FIRST_EMPTY_FIELD,
                KFSPropertyConstants.TOTAL_RECORDS,
                KFSPropertyConstants.TRAILER_RECORD_SECOND_EMPTY_FIELD,
                KFSPropertyConstants.TOTAL_AMOUNT
        };
    }
}
