/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.tem.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.document.TravelArrangerDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Check for Traveler Derived Role base on document traveler (for Travel Document) or proflie (Travel Arranger Document)
 */
@SuppressWarnings("deprecation")
public class TravelerDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {

    private DocumentService documentService;

    /**
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#getRequiredAttributes()
     */
    @Override
    protected List<String> getRequiredAttributes() {
        final List<String> attrs = new ArrayList<String>(super.getRequiredAttributes());
        attrs.add(KimConstants.AttributeConstants.DOCUMENT_NUMBER);
        return Collections.unmodifiableList(attrs);
    }

    /**
     * @see org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase#getRoleMembersFromDerivedRole(java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        final List<RoleMembership> members = new ArrayList<RoleMembership>(1);
        if (qualification!=null && !qualification.isEmpty()) {

            final String documentNumber = qualification.get(KimConstants.AttributeConstants.DOCUMENT_NUMBER);
            if ( StringUtils.isNotBlank( documentNumber ) ) {

                try{
                    Document document = documentService.getByDocumentHeaderId(documentNumber);
                    if (document != null){
                        String memberId = "";
                        if(TravelDocTypes.TRAVEL_ARRANGER_DOCUMENT.equals(document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName())) {
                            memberId = ((TravelArrangerDocument)document).getProfile().getPrincipalId();
                        } else if (document instanceof TravelDocument && !ObjectUtils.isNull(((TravelDocument)document).getTraveler()) && !StringUtils.isBlank(((TravelDocument)document).getTraveler().getPrincipalId())) {
                            memberId = ((TravelDocument)document).getTraveler().getPrincipalId();
                        }
                        if (!StringUtils.isBlank(memberId)) {
                            members.add(RoleMembership.Builder.create("", "", memberId, MemberType.PRINCIPAL, null).build());
                        }
                    }
                } catch (WorkflowException e) {
                    throw new RuntimeException("Workflow problem while trying to get document using doc id '" + documentNumber + "'", e);
                }
            }
        }
        return members;
    }

    /**
     *
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
