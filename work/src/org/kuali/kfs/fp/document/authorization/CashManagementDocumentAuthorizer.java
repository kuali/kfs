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
package org.kuali.kfs.fp.document.authorization;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kim.api.KimConstants;

/**
 * DocumentAuthorizer containing authorization code for CashManagement documents
 */
public class CashManagementDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {
    protected static Log LOG = LogFactory.getLog(CashManagementDocumentAuthorizer.class);

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentAuthorizerBase#addRoleQualification(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> attributes) {
        CashManagementDocument cashManagementDocument = (CashManagementDocument) dataObject;
        attributes.put(KimConstants.AttributeConstants.CAMPUS_CODE, cashManagementDocument.getCampusCode());

        super.addRoleQualification(dataObject, attributes);
    }
}
