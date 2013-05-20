/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.InvoiceTemplate;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Helper service class for Invoice Template lookup
 */
public class InvoiceTemplateLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceTemplateLookupableHelperServiceImpl.class);

    /***
     * This method was overridden to remove the COPY link from the actions and to add in the REPORT link.
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        InvoiceTemplate invoiceTemplate = (InvoiceTemplate) businessObject;
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName()) && allowsMaintenanceEditAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintenanceNewOrCopyAction()) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        }
        if (invoiceTemplate.isValidOrganization())
            htmlDataList.add(getInvoiceTemplateUploadUrl(businessObject));
        if (ObjectUtils.isNotNull(invoiceTemplate.getFilename()) && StringUtils.isNotBlank(invoiceTemplate.getFilename()))
            htmlDataList.add(getInvoiceTemplateDownloadUrl(businessObject));
        return htmlDataList;
    }

    /**
     * This method helps in uploading the invoice templates.
     * 
     * @param bo
     * @return
     */
    private AnchorHtmlData getInvoiceTemplateUploadUrl(BusinessObject bo) {
        InvoiceTemplate invoiceTemplate = (InvoiceTemplate) bo;
        String href = "../cgContractsGrantsInvoiceTemplateUpload.do" + "?&methodToCall=start&invoiceTemplateCode=" + invoiceTemplate.getInvoiceTemplateCode() + "&docFormKey=88888888";
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, CGKeyConstants.AgencyConstants.ACTIONS_UPLOAD);
    }

    /**
     * This method helps in downloading the invoice templates.
     * 
     * @param bo
     * @return
     */
    private AnchorHtmlData getInvoiceTemplateDownloadUrl(BusinessObject bo) {
        InvoiceTemplate invoiceTemplate = (InvoiceTemplate) bo;
        Properties parameters = new Properties();
        if (ObjectUtils.isNotNull(invoiceTemplate.getFilename()) && ObjectUtils.isNotNull(invoiceTemplate.getFilepath())) {
            parameters.put("filePath", invoiceTemplate.getFilepath());
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "download");
        }
        String href = UrlFactory.parameterizeUrl("../cgContractsGrantsInvoiceTemplateUpload.do", parameters);
        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, CGKeyConstants.AgencyConstants.ACTIONS_DOWNLOAD);
    }
}
