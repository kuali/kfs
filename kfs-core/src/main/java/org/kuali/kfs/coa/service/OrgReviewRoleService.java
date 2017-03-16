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
    boolean hasOrganizationFundReview(final String documentTypeName);
    String getClosestOrgReviewRoleParentDocumentTypeName(final String documentTypeName);

    RoleMember getRoleMemberFromKimRoleService( String roleMemberId );

    void saveOrgReviewRoleToKim( OrgReviewRole orr );
}
