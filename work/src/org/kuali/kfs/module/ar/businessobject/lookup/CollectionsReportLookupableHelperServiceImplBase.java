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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.businessobject.CollectionsReport;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

public abstract class CollectionsReportLookupableHelperServiceImplBase extends ContractsGrantsReportLookupableHelperServiceImplBase {

    protected ConfigurationService configurationService;
    protected IdentityManagementService identityManagementService;

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> urls = super.getCustomActionUrls(businessObject, pkNames);

        CollectionsReport collectionsReport = (CollectionsReport) businessObject;
        String url = getContractsGrantsReportHelperService().getInitiateCollectionActivityDocumentUrl(collectionsReport.getProposalNumber().toString(), collectionsReport.getInvoiceNumber());
        Map<String, String> fieldList = new HashMap<String, String>();
        fieldList.put(KFSPropertyConstants.PROPOSAL_NUMBER, collectionsReport.getProposalNumber().toString());
        AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
        a.setHref(url);
        a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));
        a.setDisplayText(getConfigurationService().getPropertyValueAsString(ArKeyConstants.ContractsGrantsCollectionActivityDocumentConstants.TITLE_PROPERTY));
        urls.add(a);

        Map<String, String> permissionDetails = new HashMap<String, String>();
        Map<String, String> qualificationDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, ArConstants.ArDocumentTypeCodes.COLLECTION_EVENT);

        if (getIdentityManagementService().isAuthorizedByTemplateName(GlobalVariables.getUserSession().getPrincipalId(), KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE, KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT, permissionDetails, null)) {
            Properties parameters = new Properties();
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, CollectionEvent.class.getName());
            parameters.put(KFSConstants.OVERRIDE_KEYS, ArPropertyConstants.CollectionEventFields.ID);
            parameters.put(KFSConstants.REFRESH_CALLER, ArPropertyConstants.CollectionEventFields.ID + "::" + collectionsReport.getEventId());
            parameters.put(ArPropertyConstants.CollectionEventFields.ID, collectionsReport.getEventId().toString());
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);

            url = UrlFactory.parameterizeUrl(KFSConstants.RICE_PATH_PREFIX + KFSConstants.MAINTENANCE_ACTION, parameters);
            AnchorHtmlData anchorHtmlData = new AnchorHtmlData(url, KFSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, KFSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
            urls.add(anchorHtmlData);
        }

        return urls;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public IdentityManagementService getIdentityManagementService() {
        return identityManagementService;
    }

    public void setIdentityManagementService(IdentityManagementService identityManagementService) {
        this.identityManagementService = identityManagementService;
    }


}
