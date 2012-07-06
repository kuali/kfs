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

import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TravelRelocationFields.TRIP_ID_PROPERTY;

import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelCustomSearchLinks;
import org.kuali.kfs.module.tem.TemConstants.TravelRelocationStatusCodeKeys;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.document.service.TravelRelocationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchResultProcessor;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.Column;

/**
 * Produces custom search results for {@link TravelRelocationDocument}
 *
 * @author Eswara Gadde (egadde [at] rsmart.com)
 */
public class TravelRelocationDocumentSearchResultProcessor extends AbstractDocumentSearchResultProcessor implements TravelDocumentSearchResultsProcessor {

	private String createPaymentsURL(DocSearchDTO docCriteriaDTO, String tripID) {
        String links = createDisbursementVoucherLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.VENDOR_PAYMENT);
        links += "<br />" + createRequisitionLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.REQUISITION);
        String agencyLinks = createAgencySitesLinks(tripID);
        links += (StringUtils.isEmpty(agencyLinks)?"":"<br />") + agencyLinks;
        return links;
    }
	
	public String createRelocationLink(DocSearchDTO docCriteriaDTO) {
        final DocumentType docType = getDocumentTypeService().findByName(TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT);
        final String travelDocumentIdentifier = docCriteriaDTO.getSearchableAttribute(TRVL_IDENTIFIER_PROPERTY).getUserDisplayValue();
        if (docType == null) {
            throw new RuntimeException(String.format("DocType with name %s does not exist!", TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT));
        }
        String linkPopup = "target=\"_blank\"";
        
        
        String link = String.format("<a href=\"%s&travelDocumentIdentifier=%s&command=initiate&docTypeName=%s\" %s>%s</a>", 
                docType.getDocHandlerUrl(), 
                travelDocumentIdentifier,
                TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT,
                linkPopup, 
                TravelCustomSearchLinks.NEW_RELOCATION);
        return link;
    }

    @Override
    public DocumentSearchResult addActionsColumn(DocSearchDTO docCriteriaDTO, DocumentSearchResult result) {
        final String columnName = TemPropertyConstants.TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS;
        String tripID = docCriteriaDTO.getSearchableAttribute(TRVL_IDENTIFIER_PROPERTY).getUserDisplayValue();
        String actionsHTML = "";
        actionsHTML += createRelocationLink(docCriteriaDTO);
        actionsHTML += "<br />";
        actionsHTML += createPaymentsURL(docCriteriaDTO, tripID);
        final KeyValueSort actions = new KeyValueSort(columnName, "", actionsHTML, actionsHTML,null);
        result.getResultContainers().add(0, actions); 
        
        return result;
    }
    
    @Override
    public boolean filterSearchResult(DocSearchDTO docCriteriaDTO) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    protected DocumentTypeService getDocumentTypeService() {
        return SpringContext.getBean(DocumentTypeService.class);
    }
    
    protected TravelRelocationService getTravelRelocationService() {
        return SpringContext.getBean(TravelRelocationService.class);
    }
}
