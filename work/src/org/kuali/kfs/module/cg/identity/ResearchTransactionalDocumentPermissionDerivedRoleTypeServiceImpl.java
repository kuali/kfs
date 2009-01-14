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
package org.kuali.kfs.module.cg.identity;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.AdhocPerson;
import org.kuali.kfs.module.cg.document.service.ResearchDocumentPermissionsService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.PassThruRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

public class ResearchTransactionalDocumentPermissionDerivedRoleTypeServiceImpl extends PassThruRoleTypeServiceBase {
    private ResearchDocumentPermissionsService researchDocumentPermissionsService;

    @Override
    public AttributeSet convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, AttributeSet qualification) {
        AdhocPerson adhocPerson = getResearchDocumentPermissionsService().getAdHocPerson(qualification.get(KfsKimAttributes.DOCUMENT_NUMBER), qualification.get(KimConstants.PropertyNames.PRINCIPAL_ID));
        if ((adhocPerson == null) || CGConstants.RoutingFormPermissionTypes.PERMISSION_READ_CODE.equals(adhocPerson.getPermissionCode())) {
            AttributeSet unmatchableQualification = new AttributeSet();
            unmatchableQualification.put(KfsKimAttributes.DOCUMENT_NUMBER, UNMATCHABLE_QUALIFICATION);
            return unmatchableQualification;
        }
        return qualification;
    }

    protected ResearchDocumentPermissionsService getResearchDocumentPermissionsService() {
        if (researchDocumentPermissionsService == null) {
            researchDocumentPermissionsService = SpringContext.getBean(ResearchDocumentPermissionsService.class);
        }
        return researchDocumentPermissionsService;
    }
}