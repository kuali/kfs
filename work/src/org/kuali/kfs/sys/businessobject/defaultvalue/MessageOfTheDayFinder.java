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
package org.kuali.kfs.sys.businessobject.defaultvalue;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.MessageOfTheDay;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

public class MessageOfTheDayFinder implements ValueFinder {
//    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/MessageOfTheDayFinder";
//
//    @Cacheable(value=CACHE_NAME)
    @Override
    public String getValue() {
        try {
            Collection<MessageOfTheDay> collection = SpringContext.getBean(BusinessObjectService.class).findAll(MessageOfTheDay.class);
            if (collection != null && !collection.isEmpty()) {
                return collection.iterator().next().getFinancialSystemMessageOfTheDayText();
            }
        } catch ( Exception ex ) {
            Logger.getLogger(getClass()).error("Unable to retrieve the message of the day",ex);
        }
        return "unable to retrieve message of the day";
    }

}
