/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.defaultvalue;

import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.valuefinder.ValueFinder;

public class KEMIDDefaultProcessDateFinder implements ValueFinder {

    /**
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        KEMService kemService = SpringContext.getBean(KEMService.class);
        String processDate = kemService.getCurrentSystemProcessDate();

        return processDate;
    }

}
