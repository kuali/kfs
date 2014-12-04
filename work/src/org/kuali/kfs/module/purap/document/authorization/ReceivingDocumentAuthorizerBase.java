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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.identity.PurapKimAttributes;
import org.kuali.kfs.module.purap.service.SensitiveDataService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;

public class ReceivingDocumentAuthorizerBase extends TransactionalDocumentAuthorizerBase {

    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        attributes.put(PurapKimAttributes.DOCUMENT_SENSITIVE, "false");
        ReceivingDocument purapDoc = (ReceivingDocument) businessObject;
        if (purapDoc.getAccountsPayablePurchasingDocumentLinkIdentifier() != null) {
            List<SensitiveData> sensitiveDataList = SpringContext.getBean(SensitiveDataService.class).getSensitiveDatasAssignedByRelatedDocId(purapDoc.getAccountsPayablePurchasingDocumentLinkIdentifier());
            StringBuffer sensitiveDataCodes = new StringBuffer();
            for (SensitiveData sensitiveData : sensitiveDataList) {
                sensitiveDataCodes.append(sensitiveData.getSensitiveDataCode()).append(";");
            }
            if (sensitiveDataCodes.length() > 0) {
                attributes.put(PurapKimAttributes.DOCUMENT_SENSITIVE, "true");
                attributes.put(PurapKimAttributes.SENSITIVE_DATA_CODE, sensitiveDataCodes.toString().substring(0, sensitiveDataCodes.length() - 1));
                attributes.put(PurapKimAttributes.ACCOUNTS_PAYABLE_PURCHASING_DOCUMENT_LINK_IDENTIFIER, purapDoc.getAccountsPayablePurchasingDocumentLinkIdentifier().toString());
            }
        }
    }
    
    @Override
    public boolean canEditDocumentOverview(Document document, Person user) {
        return isAuthorizedByTemplate(document,
                KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.EDIT_DOCUMENT,
                user.getPrincipalId());
    }

}
