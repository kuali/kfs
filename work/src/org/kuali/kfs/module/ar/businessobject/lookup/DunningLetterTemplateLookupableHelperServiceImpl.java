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
