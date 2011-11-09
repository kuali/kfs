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
package org.kuali.kfs.sec.businessobject.inquiry;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;


/**
 * Sets inquiry for member name based on type
 */
public class SecurityModelMemberInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (SecPropertyConstants.MEMBER_ID.equals(attributeName)) {
            Properties parameters = new Properties();
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);

            Map<String, String> fieldList = new HashMap<String, String>();

            String memberId = (String) ObjectUtils.getPropertyValue(businessObject, SecPropertyConstants.MEMBER_ID);
            String memberTypeCode = (String) ObjectUtils.getPropertyValue(businessObject, SecPropertyConstants.MEMBER_TYPE_CODE);

            if (StringUtils.isNotBlank(memberId) && StringUtils.isNotBlank(memberTypeCode)) {
                if (KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)) {
                    parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Role.class.getName());
                    parameters.put(KimConstants.PrimaryKeyConstants.ROLE_ID, memberId);
                    fieldList.put(KimConstants.PrimaryKeyConstants.ROLE_ID, memberId.toString());
                }
                else if (KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)) {
                    parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Group.class.getName());
                    parameters.put(KimConstants.PrimaryKeyConstants.GROUP_ID, memberId);
                    fieldList.put(KimConstants.PrimaryKeyConstants.GROUP_ID, memberId.toString());
                }
                else {
                    parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Person.class.getName());
                    parameters.put(KimConstants.PrimaryKeyConstants.PRINCIPAL_ID, memberId);
                    fieldList.put(KimConstants.PrimaryKeyConstants.PRINCIPAL_ID, memberId.toString());
                }

                return getHyperLink(PayeeACHAccount.class, fieldList, UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, parameters));
            }
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }


}
