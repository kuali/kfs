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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.tem.document.TravelArrangerDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.krad.util.ObjectUtils;


public class TravelArrangerAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase#addRoleQualification(org.kuali.rice.kns.bo.BusinessObject,java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> qualification) {
        super.addRoleQualification(dataObject, qualification);
        if (dataObject instanceof TravelArrangerDocument) {
         TravelArrangerDocument document = (TravelArrangerDocument)dataObject;
            if (ObjectUtils.isNotNull(document.getProfile())) {
                qualification.put(KfsKimAttributes.PROFILE_PRINCIPAL_ID, document.getProfile().getPrincipalId());
            }
        }
    }

}
