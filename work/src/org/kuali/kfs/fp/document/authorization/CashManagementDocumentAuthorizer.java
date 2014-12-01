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
