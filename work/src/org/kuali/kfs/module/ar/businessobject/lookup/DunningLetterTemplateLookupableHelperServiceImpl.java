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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.document.service.DunningLetterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

public class DunningLetterTemplateLookupableHelperServiceImpl extends TemplateLookupableHelperServiceImplBase {

    protected DunningLetterService dunningLetterService;

    /**
     * This method was overridden to add the links specific to the Dunning Letter Template Lookup Results
     * (edit, copy, upload, download) as appropriate.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();

        DunningLetterTemplate dunningLetterTemplate = (DunningLetterTemplate) businessObject;
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();

        boolean allowsMaintEdit = false;
        if (!StringUtils.isBlank(getMaintenanceDocumentTypeName())) {
            allowsMaintEdit = allowsMaintenanceEditAction(businessObject);
        }
        if (allowsMaintEdit) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintenanceNewOrCopyAction()) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintEdit) {
            htmlDataList.add(getTemplateUploadUrl(ArPropertyConstants.DunningLetterTemplateFields.DUNNING_LETTER_TEMPLATE_CODE, dunningLetterTemplate.getDunningLetterTemplateCode()));
        }
        if (allowsMaintEdit && StringUtils.isNotBlank(dunningLetterTemplate.getFilename()) && templateFileExists(dunningLetterTemplate.getFilename())) {
            htmlDataList.add(getTemplateDownloadUrl(dunningLetterTemplate.getFilename()));
        }

        return htmlDataList;
    }

    /**
     * @see org.kuali.kfs.module.ar.businessobject.lookup.TemplateLookupableHelperServiceImplBase#getAction()
     */
    @Override
    protected String getAction() {
        return ArConstants.UrlActions.ACCOUNTS_RECEIVABLE_DUNNING_LETTER_TEMPLATE_UPLOAD;
    }

    public DunningLetterService getDunningLetterService() {
        return dunningLetterService;
    }

    public void setDunningLetterService(DunningLetterService dunningLetterService) {
        this.dunningLetterService = dunningLetterService;
    }
}