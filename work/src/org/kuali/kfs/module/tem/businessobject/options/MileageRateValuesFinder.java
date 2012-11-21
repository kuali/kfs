/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class MileageRateValuesFinder extends KeyValuesBase {

    protected static Logger LOG = Logger.getLogger(MileageRateValuesFinder.class);

    private String queryDate;

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        java.util.Date javaDate = null;
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if(StringUtils.isNotEmpty(queryDate)) {
            try {
                javaDate = df.parse(queryDate);
            } catch (ParseException ex) {
                LOG.error("unable to parse date: " + queryDate);
            }
        }
        Date searchDate = null;
        try {
            searchDate = getDateTimeService().convertToSqlDate(df.format(javaDate));
        }
        catch (ParseException ex) {
            LOG.error("unable to convert date: " + queryDate);
        }
        return getTravelDocumentService().getMileageRateKeyValues(searchDate);
    }

    /**
     * Gets the queryDate attribute.
     * @return Returns the queryDate.
     */
    public String getQueryDate() {
        return queryDate;
    }

    /**
     * Sets the queryDate attribute value.
     * @param queryDate The queryDate to set.
     */
    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate;
    }

    private TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    private DateTimeService getDateTimeService() {
        return SpringContext.getBean(DateTimeService.class);
    }

}
