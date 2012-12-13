/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.lookup;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.service.TEMRoleService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.util.Utilities;

public abstract class DocumentActionBuilderBase {

    public static Logger LOG = Logger.getLogger(DocumentActionBuilderBase.class);

    /**
     *
     * @param travelDocumentIdentifier
     * @return
     */
    public String createReimbursementLink(String travelDocumentIdentifier) {
        final DocumentType docType = getDocumentTypeService().getDocumentTypeByName(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
        if (docType == null) {
            throw new RuntimeException(String.format("DocType with name %s does not exist!", TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT));
        }
        String linkPopup = "target=\"_blank\"";

        String userDisplaySortValue = TemConstants.TravelCustomSearchLinks.NEW_REIMBURSEMENT;
        String link = String.format("<a href=\"%s&travelDocumentIdentifier=%s&command=initiate&docTypeName=%s\" %s>%s</a>",
                docType.getResolvedDocumentHandlerUrl(),
                travelDocumentIdentifier,
                TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT,
                linkPopup,
                userDisplaySortValue);

        return link;
    }

    /**
     *
     * @param travelDocumentIdentifier
     * @return
     */
    public String createEntertainmentLink(String travelDocumentIdentifier, DocumentSearchResult documentSearchResult) {
        final DocumentType docType = getDocumentTypeService().getDocumentTypeByName(TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT);
        if (docType == null) {
            throw new RuntimeException(String.format("DocType with name %s does not exist!", TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT));
        }
        String linkPopup = "target=\"_blank\"";

        String link = String.format("<a href=\"%s&" + KFSPropertyConstants.DOCUMENT_NUMBER + "=%s&travelDocumentIdentifier=%s&command=initiate&docTypeName=%s\" %s>%s</a>",
                docType.getResolvedDocumentHandlerUrl(),
                documentSearchResult.getDocument().getDocumentId(),
                travelDocumentIdentifier,
                TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT,
                linkPopup,
                TemConstants.TravelCustomSearchLinks.NEW_ENTERTAINMENT);

        return link;
    }

    /**
     *
     * @param docCriteriaDTO
     * @param title
     * @return
     */
    public String createRequisitionLink(DocumentSearchResult documentSearchResult) {
        final DocumentType docType = getDocumentTypeService().getDocumentTypeByName(TemConstants.REQUISITION_DOCTYPE);

        if (docType == null) {
            throw new RuntimeException(String.format("DocType with name %s does not exist!", TemConstants.REQUISITION_DOCTYPE));
        }
        String linkPopup = "target=\"_blank\"";

        String unresolvedREQSURL = "${application.url}/" + TemConstants.TravelCustomSearchLinks.REQ_URL + documentSearchResult.getDocument().getDocumentId();

        String REQSURL = Utilities.substituteConfigParameters(docType.getApplicationId(), unresolvedREQSURL);
        String link = String.format("<a href=\"%s\" %s>%s</a>", REQSURL, linkPopup, TemConstants.TravelCustomSearchLinks.REQUISITION);
        return link;
    }

    /**
     *
     * @param docCriteriaDTO
     * @param title
     * @return
     */
    public String createDisbursementVoucherLink(DocumentSearchResult documentSearchResult) {
        final DocumentType docType = getDocumentTypeService().getDocumentTypeByName(TemConstants.DISBURSEMENT_VOUCHER_DOCTYPE);

        if (docType == null) {
            throw new RuntimeException(String.format("DocType with name %s does not exist!", TemConstants.DISBURSEMENT_VOUCHER_DOCTYPE));
        }
        String linkPopup = "target=\"_blank\"";

        String unresolvedDVURL = "${application.url}/" + TemConstants.TravelCustomSearchLinks.DV_URL + documentSearchResult.getDocument().getDocumentId();

        String DVURL = Utilities.substituteConfigParameters(docType.getApplicationId(), unresolvedDVURL);
        String link = String.format("<a href=\"%s\" %s>%s</a>", DVURL, linkPopup, TemConstants.TravelCustomSearchLinks.PRE_TRIP_VENDOR_PAYMENT);
        return link;
    }

    /**
     *
     * @param tripId
     * @return
     */
    public String createAgencySitesLinks(String tripId) {
        String links = "";
        if (getConfigurationService().getPropertyValueAsBoolean(TemKeyConstants.ENABLE_AGENCY_SITES_URL)){
            String agencySitesURL = getConfigurationService().getPropertyValueAsString(TemKeyConstants.AGENCY_SITES_URL);
            String linkPopup = "target=\"_blank\"";
            boolean passTripID = getConfigurationService().getPropertyValueAsBoolean(TemKeyConstants.PASS_TRIP_ID_TO_AGENCY_SITES);
            if(!StringUtils.isEmpty(agencySitesURL)){
                String[] sites = agencySitesURL.split(";");
                for (String site : sites){
                    String[] siteInfo = site.split("=");
                    String URL = siteInfo[1] + (passTripID?tripId:"");
                    links += (StringUtils.isEmpty(links)?"":"<br />") + String.format("<a href=\"http://%s\" %s>%s</a>", URL,linkPopup, siteInfo[0]);
                }
            }
        }
        return links;
    }

    /**
     *
     * @param documentSearchResult
     * @param tripID
     * @return
     */
    public String createPaymentsURL(DocumentSearchResult documentSearchResult, String tripId) {

        StrBuilder paymentHTML = new StrBuilder();
        paymentHTML.setNewLineText("<br/>");

        paymentHTML.appendln(createDisbursementVoucherLink(documentSearchResult));
        paymentHTML.appendln(createRequisitionLink(documentSearchResult));
        paymentHTML.appendln(createAgencySitesLinks(tripId));
        return paymentHTML.toString();
    }


    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    protected DocumentTypeService getDocumentTypeService() {
        return SpringContext.getBean(DocumentTypeService.class);
    }

    protected ConfigurationService getConfigurationService() {
        return SpringContext.getBean(ConfigurationService.class);
    }

    protected TEMRoleService getTemRoleService() {
        return SpringContext.getBean(TEMRoleService.class);
    }

}
