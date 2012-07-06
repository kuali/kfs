/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.lookup;

import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementStatusCodeKeys;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchResultProcessor;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeQueryService;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.web.ui.Column;


public class TravelReimbursementDocumentSearchResultProcessor extends AbstractDocumentSearchResultProcessor implements TravelDocumentSearchResultsProcessor {

    /**
     * This method determines whether a reimbursement has been completely approved.
     * In order to accurately keep track on disencumbrance, the appDocStatus needs to be Department Approved.
     * @param docCriteriaDTO
     * @return
     */
    private boolean showReimbursementURL(DocSearchDTO docCriteriaDTO) {
        final String documentStatus = docCriteriaDTO.getDocRouteStatusCode();
        return (documentStatus.equals(KEWConstants.ROUTE_HEADER_FINAL_CD) || documentStatus.equals(KEWConstants.ROUTE_HEADER_PROCESSED_CD))
            && docCriteriaDTO.getAppDocStatus().equals(TravelReimbursementStatusCodeKeys.DEPT_APPROVED);
    }
    
    private boolean otherPaymentMethodsAllowed(DocSearchDTO docCriteriaDTO) {
        final String documentStatus = docCriteriaDTO.getDocRouteStatusCode();
        return (getParameterService().getIndicatorParameter(TemConstants.PARAM_NAMESPACE, TemConstants.TravelReimbursementParameters.PARAM_DTL_TYPE, TemConstants.TravelReimbursementParameters.ENABLE_VENDOR_PAYMENT_BEFORE_FINAL_TR_APPROVAL_IND) 
                || documentStatus.equals(KEWConstants.ROUTE_HEADER_FINAL_CD) 
                || documentStatus.equals(KEWConstants.ROUTE_HEADER_PROCESSED_CD));
    }
    
    private String createPaymentsURL(DocSearchDTO docCriteriaDTO, String tripID) {
        String links = createDisbursementVoucherLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.VENDOR_PAYMENT);
        links += "<br />" + createRequisitionLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.REQUISITION);
        String agencyLinks = createAgencySitesLinks(tripID);
        links += (StringUtils.isEmpty(agencyLinks)?"":"<br />") + agencyLinks;
        return links;
    }
    
    @Override
    public DocumentSearchResult addActionsColumn(DocSearchDTO docCriteriaDTO, DocumentSearchResult result) {
        final String columnName = TemPropertyConstants.TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS;
        String tripID = docCriteriaDTO.getSearchableAttribute(TRVL_IDENTIFIER_PROPERTY).getUserDisplayValue();
        String actionsHTML = "";
        if (showReimbursementURL(docCriteriaDTO)){
            actionsHTML += createTravelReimbursementLink(tripID);
        }
        if (otherPaymentMethodsAllowed(docCriteriaDTO)) {
            if (!StringUtils.isBlank(actionsHTML)) {
                actionsHTML += "<br />";
            }
            actionsHTML += createPaymentsURL(docCriteriaDTO, tripID);
        }
   
        
        final KeyValueSort actions = new KeyValueSort(columnName, "", actionsHTML, actionsHTML,null);
        result.getResultContainers().add(0, actions); 
        
        return result;
    }

    @Override
    public boolean filterSearchResult(DocSearchDTO docCriteriaDTO) {
        // TODO Auto-generated method stub
        return filterByUser(docCriteriaDTO);
    }

    private ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);

    }
    
    @Override
    protected DocumentTypeService getDocumentTypeService() {
        return SpringContext.getBean(DocumentTypeService.class);
    }
}
