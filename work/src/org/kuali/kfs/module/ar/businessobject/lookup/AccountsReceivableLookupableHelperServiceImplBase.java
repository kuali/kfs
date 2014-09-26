/*
 * Copyright 2014 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public abstract class AccountsReceivableLookupableHelperServiceImplBase extends KualiLookupableHelperServiceImpl {

    /**
     * Overridden to setup document number link for document number properties and add Rice Path prefix
     * to the URL where necessary (cases where the Lookupable is called from the KFS context instead of the Rice context).
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        AnchorHtmlData inquiryHref = new AnchorHtmlData(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING);

        if (KFSPropertyConstants.DOCUMENT_NUMBER.equals(propertyName) ){
            String baseUrl = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.WORKFLOW_URL_KEY) + "/" + KFSConstants.DOC_HANDLER_ACTION;
            Properties parameters = new Properties();
            parameters.put(KFSConstants.PARAMETER_DOC_ID, ObjectUtils.getPropertyValue(bo, propertyName).toString());
            parameters.put(KFSConstants.PARAMETER_COMMAND, KFSConstants.METHOD_DISPLAY_DOC_SEARCH_VIEW);

            inquiryHref.setHref(UrlFactory.parameterizeUrl(baseUrl, parameters));
        } else {
            inquiryHref = (AnchorHtmlData)super.getInquiryUrl(bo, propertyName);
            if (StringUtils.startsWith(inquiryHref.getHref(), KRADConstants.INQUIRY_ACTION)) {
                inquiryHref.setHref(KFSConstants.RICE_PATH_PREFIX + inquiryHref.getHref());
            }
        }

        return inquiryHref;
    }

}
