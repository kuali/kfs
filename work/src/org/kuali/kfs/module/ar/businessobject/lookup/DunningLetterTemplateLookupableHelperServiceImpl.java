/*
 * Copyright 2012 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class DunningLetterTemplateLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DunningLetterTemplateLookupableHelperServiceImpl.class);

    /***
     * This method was overridden to remove the COPY link from the actions and to add in the REPORT link.
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        DunningLetterTemplate letterTemplate = (DunningLetterTemplate) businessObject;
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName()) && allowsMaintenanceEditAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintenanceNewOrCopyAction()) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        }
        if (letterTemplate.isValidOrganization() || !letterTemplate.isAccessRestricted()) // if the access is not restricted, anyone
                                                                                          // can upload file and do changes.
            htmlDataList.add(getDunningLetterTemplateUploadUrl(businessObject));
        if (ObjectUtils.isNotNull(letterTemplate.getFilename()) && StringUtils.isNotBlank(letterTemplate.getFilename()))
            htmlDataList.add(getDunningLetterTemplateDownloadUrl(businessObject));
        return htmlDataList;
    }

    /**
     * This method helps in uploading the dunning letter templates.
     * 
     * @param bo
     * @return
     */
    private AnchorHtmlData getDunningLetterTemplateUploadUrl(BusinessObject bo) {
        DunningLetterTemplate letterTemplate = (DunningLetterTemplate) bo;
        String href = "../arAccountsReceivableLetterTemplateUpload.do" + "?&methodToCall=start&letterTemplateCode=" + letterTemplate.getLetterTemplateCode() + "&docFormKey=88888888";
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArKeyConstants.ACTIONS_UPLOAD);
    }

    /**
     * This method helps in downloading the dunning letter templates.
     * 
     * @param bo
     * @return
     */
    private AnchorHtmlData getDunningLetterTemplateDownloadUrl(BusinessObject bo) {
        DunningLetterTemplate letterTemplate = (DunningLetterTemplate) bo;
        Properties parameters = new Properties();
        if (ObjectUtils.isNotNull(letterTemplate.getFilename()) && ObjectUtils.isNotNull(letterTemplate.getFilepath())) {
            parameters.put("filePath", letterTemplate.getFilepath());
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "download");
        }
        String href = UrlFactory.parameterizeUrl("../arAccountsReceivableLetterTemplateUpload.do", parameters);
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, ArKeyConstants.ACTIONS_DOWNLOAD);
    }
}
