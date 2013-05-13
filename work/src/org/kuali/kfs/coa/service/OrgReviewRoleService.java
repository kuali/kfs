/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.coa.service;

import java.util.List;

import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.krad.exception.ValidationException;

public interface OrgReviewRoleService {

    void populateOrgReviewRoleFromRoleMember( OrgReviewRole orr, String roleMemberId );
    void populateOrgReviewRoleFromDelegationMember( OrgReviewRole orr, String roleMemberId, String delegationMemberId );

    boolean isValidDocumentTypeForOrgReview(String documentTypeName);

    void validateDocumentType(String documentTypeName) throws ValidationException;
    List<String> getRolesToConsider(String documentTypeName) throws ValidationException;

    boolean hasAccountingOrganizationHierarchy(final String documentTypeName);
    boolean hasOrganizationHierarchy(final String documentTypeName);
    String getClosestOrgReviewRoleParentDocumentTypeName(final String documentTypeName);

    RoleMember getRoleMemberFromKimRoleService( String roleMemberId );

    void saveOrgReviewRoleToKim( OrgReviewRole orr );
}
