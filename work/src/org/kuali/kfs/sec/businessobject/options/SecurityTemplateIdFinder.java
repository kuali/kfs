/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.sec.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;


/**
 * Returns list of security template Ids
 */
public class SecurityTemplateIdFinder extends KeyValuesBase {

    protected static List<KeyValue> OPTIONS;
    
    protected void buildOptionsList() {
        List<KeyValue> temp = new ArrayList<KeyValue>();
        temp.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getInquiryWithFieldValueTemplate().getId(), "Balance Inquiry"));
        temp.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getEditAccountingLineWithFieldValueTemplate().getId(), "Edit Accounting Line"));
        temp.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getEditDocumentWithFieldValueTemplate().getId(), "Edit Document"));
        temp.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getLookupWithFieldValueTemplate().getId(), "Lookup Records"));
        temp.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getViewAccountingLineWithFieldValueTemplate().getId(), "View Accounting Line"));
        temp.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getViewDocumentWithFieldValueTemplate().getId(), "View Document"));
        temp.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getViewNotesAttachmentsWithFieldValueTemplate().getId(), "View Notes/Attachments"));
        OPTIONS = temp;
    }
    
    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        if ( OPTIONS == null ) {
            buildOptionsList();
        }
        return OPTIONS;
    }
}
