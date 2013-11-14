/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.tem.identity.TemKimAttributes;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.KRADConstants;

/**
 *
 */
public class TemCTSCardApplicationDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {

    @Override
    public boolean canInitiate(String documentTypeName, Person user) {
        String nameSpaceCode = KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE;
        Map<String, String> permissionDetails = new HashMap<String, String>();
        Map<String, String> qualificationDetails = new HashMap<String, String>();
        qualificationDetails.put(TemKimAttributes.PROFILE_PRINCIPAL_ID, user.getPrincipalId());
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, documentTypeName);
        return getPermissionService().isAuthorizedByTemplate(user.getPrincipalId(), nameSpaceCode,
                KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT, permissionDetails, qualificationDetails);
    }

}
