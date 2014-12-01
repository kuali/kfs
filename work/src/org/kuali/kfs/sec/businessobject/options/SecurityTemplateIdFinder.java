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
