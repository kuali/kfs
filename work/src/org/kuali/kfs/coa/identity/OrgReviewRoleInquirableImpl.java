/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.coa.identity;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

public class OrgReviewRoleInquirableImpl extends KfsInquirableImpl {

    
    @Override
    public BusinessObject getBusinessObject(Map fieldValues) {
        OrgReviewRole orr = new OrgReviewRole();
        if ( StringUtils.isNotBlank( (String)fieldValues.get("orgReviewRoleMemberId") ) ) {
            if ( StringUtils.equals("true", (String)fieldValues.get("delegate") ) ) {
                SpringContext.getBean( OrgReviewRoleService.class ).populateOrgReviewRoleFromDelegationMember(orr, (String)fieldValues.get("orgReviewRoleMemberId") );
            } else {
                SpringContext.getBean( OrgReviewRoleService.class ).populateOrgReviewRoleFromRoleMember(orr, (String)fieldValues.get("orgReviewRoleMemberId") );
            }
        }
        return orr;
    }
    
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if ( StringUtils.equals( attributeName, "orgReviewRoleInquiryTitle") ) {
            Properties parameters = new Properties();
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "start");
            parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, OrgReviewRole.class.getName());
            if ( StringUtils.isNotBlank(((OrgReviewRole)businessObject).getDelegationMemberId()) ) {
                parameters.put("orgReviewRoleMemberId", ((OrgReviewRole)businessObject).getDelegationMemberId() );
                parameters.put("delegate", "true" );
            } else if ( StringUtils.isNotBlank(((OrgReviewRole)businessObject).getRoleMemberId()) ) {
                parameters.put("orgReviewRoleMemberId", ((OrgReviewRole)businessObject).getRoleMemberId() );
                parameters.put("delegate", "false" );
            }
            return getHyperLink(OrgReviewRole.class, Collections.EMPTY_MAP, UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, parameters));
        } else {
            return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
        }
    }
    
}
