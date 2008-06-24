/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.web.struts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.AmountTotaling;

/**
 * This class is the custom KFS maintenance document authorizer base class
 */
public class FinancialSystemMaintenanceDocumentAuthorizerBase extends MaintenanceDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(FinancialSystemMaintenanceDocumentAuthorizerBase.class);

    /**
     * Adds settings for KFS maintenance-document-specific flags.
     * 
     * @see org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public FinancialSystemDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        LOG.debug("calling FinancialSystemMaintenanceDocumentAuthorizerBase.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPersonUserIdentifier() + "'");
        FinancialSystemDocumentActionFlags flags =  new FinancialSystemDocumentActionFlags(super.getDocumentActionFlags(document, user));

        // if document implements AmountTotaling interface, then we should display the total
        if (document instanceof AmountTotaling) {
            flags.setHasAmountTotal(true);
        }
        else {
            flags.setHasAmountTotal(false);
        }

        return flags;
    }

}
