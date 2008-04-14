/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.lookup.valuefinder;

import java.util.Collection;

import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.spring.CacheNoCopy;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.bo.MessageOfTheDay;

public class MessageOfTheDayFinder implements ValueFinder {

    @CacheNoCopy
    public String getValue() {
        String motd = "unable to retrieve message of the day";
        Collection collection = SpringContext.getBean(BusinessObjectService.class).findAll(MessageOfTheDay.class);
        if (collection != null && !collection.isEmpty()) {
            motd = ((MessageOfTheDay) collection.iterator().next()).getFinancialSystemMessageOfTheDayText();
        }
        return motd;
    }

}
