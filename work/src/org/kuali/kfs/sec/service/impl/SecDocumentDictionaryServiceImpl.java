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
package org.kuali.kfs.sec.service.impl;

import org.kuali.kfs.sec.document.validation.impl.AccessSecurityAccountingDocumentRuleBase;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.service.impl.DocumentDictionaryServiceImpl;

public class SecDocumentDictionaryServiceImpl extends DocumentDictionaryServiceImpl {
    private AccessSecurityService accessSecurityService;

    protected AccessSecurityService getAccessSecurityService() {
        if ( accessSecurityService == null ) {
            accessSecurityService = SpringContext.getBean(AccessSecurityService.class);
        }
        return accessSecurityService;
    }

    /**
     * @see org.kuali.rice.krad.service.DocumentDictionaryService#getBusinessRulesClass
     */
    @Override
    public Class<? extends BusinessRule> getBusinessRulesClass(Document document) {
        String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        if (getAccessSecurityService().isAccessSecurityControlledDocumentType(documentType)) {
            return AccessSecurityAccountingDocumentRuleBase.class;
        }

        return super.getBusinessRulesClass(document);
    }
}
