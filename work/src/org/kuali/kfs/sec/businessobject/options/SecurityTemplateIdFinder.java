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
package org.kuali.kfs.sec.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;


/**
 * Returns list of security template Ids
 */
public class SecurityTemplateIdFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();

        activeLabels.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getInquiryWithFieldValueTemplateId(), "Balance Inquiry"));
        activeLabels.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getEditAccountingLineWithFieldValueTemplateId(), "Edit Accounting Line"));
        activeLabels.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getEditDocumentWithFieldValueTemplateId(), "Edit Document"));
        activeLabels.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getLookupWithFieldValueTemplateId(), "Lookup Records"));
        activeLabels.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getViewAccountingLineWithFieldValueTemplateId(), "View Accounting Line"));
        activeLabels.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getViewDocumentWithFieldValueTemplateId(), "View Document"));
        activeLabels.add(new ConcreteKeyValue(SpringContext.getBean(AccessSecurityService.class).getViewNotesAttachmentsWithFieldValueTemplateId(), "View Notes/Attachments"));

        return activeLabels;
    }
}
