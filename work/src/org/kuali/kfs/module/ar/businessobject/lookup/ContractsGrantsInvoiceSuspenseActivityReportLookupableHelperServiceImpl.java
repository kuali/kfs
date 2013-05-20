/*
 * Copyright 2008 The Kuali Foundation
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceSuspenseActivityReport;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.SuspensionCategory;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kew.api.WorkflowDocument;

/**
 * Defines a custom lookup for the Suspense Activity Report.
 */
public class ContractsGrantsInvoiceSuspenseActivityReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {

    private BusinessObjectService businessObjectService;
    private DocumentService documentService;

    private static final Log LOG = LogFactory.getLog(ContractsGrantsInvoiceSuspenseActivityReportLookupableHelperServiceImpl.class);

    /**
     * This method performs the lookup and returns a collection of lookup items
     * 
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        Collection<ContractsGrantsInvoiceSuspenseActivityReport> displayList = new ArrayList<ContractsGrantsInvoiceSuspenseActivityReport>();

        List<InvoiceSuspensionCategory> invoiceSuspensionCategories = (List<InvoiceSuspensionCategory>) businessObjectService.findAll(InvoiceSuspensionCategory.class);
        Map<String, String> suspensionCategoryMap = buildSuspensionCategoryMap();

        TreeMap<String, List<String>> documentNumbersByCategory = new TreeMap<String, List<String>>();

        for (InvoiceSuspensionCategory invoiceSuspensionCategory : invoiceSuspensionCategories) {

            ContractsGrantsInvoiceDocument cgInvoiceDocument;
            try {
                cgInvoiceDocument = (ContractsGrantsInvoiceDocument) documentService.getByDocumentHeaderId(invoiceSuspensionCategory.getDocumentNumber());
            }
            catch (WorkflowException e) {
                LOG.debug("WorkflowException happened while retrives documentHeader");
                continue;
            }

            WorkflowDocument workflowDocument = null;
            try {
                workflowDocument = cgInvoiceDocument.getDocumentHeader().getWorkflowDocument();
            }
            catch (RuntimeException e) {
                LOG.debug(e + " happened" + " : transient workflowDocument is null");
                continue;
            }

            boolean isStatusFinalOrProcessed = false;
            // check status of document
            if (ObjectUtils.isNotNull(workflowDocument)) {
                isStatusFinalOrProcessed = workflowDocument.isFinal() || workflowDocument.isCanceled() || workflowDocument.isDisapproved();
            }

            // if status of ContractsGrantsInvoiceDocument is final or processed, go to next doc
            if (isStatusFinalOrProcessed) {
                continue;
            }


            if (ObjectUtils.isNull(documentNumbersByCategory.get(invoiceSuspensionCategory.getSuspensionCategoryCode()))) {
                List<String> documentNumbers = new ArrayList<String>();
                documentNumbers.add(invoiceSuspensionCategory.getDocumentNumber());
                documentNumbersByCategory.put(invoiceSuspensionCategory.getSuspensionCategoryCode(), documentNumbers);
            }
            else {

                documentNumbersByCategory.get(invoiceSuspensionCategory.getSuspensionCategoryCode()).add(invoiceSuspensionCategory.getDocumentNumber());
            }
        }

        for (String suspensionCategoryCode : documentNumbersByCategory.keySet()) {

            ContractsGrantsInvoiceSuspenseActivityReport cgInvoiceSuspenseActivityReport = new ContractsGrantsInvoiceSuspenseActivityReport();
            cgInvoiceSuspenseActivityReport.setSuspensionCategoryCode(suspensionCategoryCode);

            cgInvoiceSuspenseActivityReport.setCategoryDescription(suspensionCategoryMap.get(suspensionCategoryCode));
            cgInvoiceSuspenseActivityReport.setTotalInvoicesSuspended(new Long(documentNumbersByCategory.get(suspensionCategoryCode).size()));

            if (ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgInvoiceSuspenseActivityReport, "ContractsGrantsInvoiceSuspenseActivityReport")) {
                displayList.add(cgInvoiceSuspenseActivityReport);
            }

        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    private Map<String, String> buildSuspensionCategoryMap() {
        Map suspensionCategoryMap = new HashMap<String, String>();
        Collection<SuspensionCategory> suspensionCategories = businessObjectService.findAll(SuspensionCategory.class);
        for (SuspensionCategory suspensionCategory : suspensionCategories) {
            suspensionCategoryMap.put(suspensionCategory.getSuspensionCategoryCode(), suspensionCategory.getSuspensionCategoryDescription());

        }
        return suspensionCategoryMap;
    }
}
