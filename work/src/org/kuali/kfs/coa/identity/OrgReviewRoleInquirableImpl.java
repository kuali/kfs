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
package org.kuali.kfs.coa.identity;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

public class OrgReviewRoleInquirableImpl extends KfsInquirableImpl {

    protected static final String ORG_REVIEW_ROLE_INQUIRY_TITLE = "orgReviewRoleInquiryTitle";
    protected static final String ORG_REVIEW_ROLE_MEMBER_ID = "orgReviewRoleMemberId";

    private static OrgReviewRoleService orgReviewRoleService;

    @Override
    public BusinessObject getBusinessObject(Map fieldValues) {
        OrgReviewRole orr = new OrgReviewRole();
        String roleMemberId = (String) fieldValues.get(ORG_REVIEW_ROLE_MEMBER_ID);
        if ( StringUtils.isNotBlank( roleMemberId ) ) {
            if ( Boolean.valueOf( (String)fieldValues.get(OrgReviewRole.DELEGATE_FIELD_NAME) ) ) {
                getOrgReviewRoleService().populateOrgReviewRoleFromDelegationMember(orr, null, roleMemberId );
            } else {
                getOrgReviewRoleService().populateOrgReviewRoleFromRoleMember(orr, roleMemberId );
            }
        }
        return orr;
    }

    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if ( StringUtils.equals( attributeName, ORG_REVIEW_ROLE_INQUIRY_TITLE) ) {
            Properties parameters = new Properties();
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, OrgReviewRole.class.getName());
            if ( StringUtils.isNotBlank(((OrgReviewRole)businessObject).getDelegationMemberId()) ) {
                parameters.put(ORG_REVIEW_ROLE_MEMBER_ID, ((OrgReviewRole)businessObject).getDelegationMemberId() );
                parameters.put(OrgReviewRole.DELEGATE_FIELD_NAME, "true" );
            } else if ( StringUtils.isNotBlank(((OrgReviewRole)businessObject).getRoleMemberId()) ) {
                parameters.put(ORG_REVIEW_ROLE_MEMBER_ID, ((OrgReviewRole)businessObject).getRoleMemberId() );
                parameters.put(OrgReviewRole.DELEGATE_FIELD_NAME, "false" );
            }
            return getHyperLink(OrgReviewRole.class, Collections.EMPTY_MAP, UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, parameters));
        } else {
            return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
        }
    }

    protected OrgReviewRoleService getOrgReviewRoleService() {
        if ( orgReviewRoleService == null ) {
            orgReviewRoleService = SpringContext.getBean( OrgReviewRoleService.class );
        }
        return orgReviewRoleService;
    }
}
