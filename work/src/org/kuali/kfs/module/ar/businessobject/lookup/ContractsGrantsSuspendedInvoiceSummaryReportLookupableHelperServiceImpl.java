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
import java.util.Set;
import java.util.TreeMap;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceSummaryReport;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.businessobject.SuspensionCategory;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a custom lookup for the Suspense Activity Report.
 */
public class ContractsGrantsSuspendedInvoiceSummaryReportLookupableHelperServiceImpl extends ContractsGrantsSuspendedInvoiceReportLookupableHelperServiceImplBase {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsSuspendedInvoiceSummaryReportLookupableHelperServiceImpl.class);
    protected DocumentService documentService;

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

        Collection<ContractsGrantsSuspendedInvoiceSummaryReport> displayList = new ArrayList<ContractsGrantsSuspendedInvoiceSummaryReport>();
        Map<String, List<String>> documentNumbersByCategory = new TreeMap<String, List<String>>();
        Map<String, String> suspensionCategoryDescriptions = new HashMap<String, String>();

        final String suspensionCategoryCodeFromLookup = (String)lookupFormFields.get(ArPropertyConstants.SuspensionCategory.SUSPENSION_CATEGORY_CODE);
        final Set<String> suspensionCategoryCodes = retrieveMatchingSuspensionCategories(suspensionCategoryCodeFromLookup);

        Map<String, String> lookupFields = new HashMap<>();
        final String processingDocumentStatuses = buildProcessingDocumentStatusesForLookup();
        lookupFields.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, processingDocumentStatuses);
        Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocuments = getLookupService().findCollectionBySearchHelper(ContractsGrantsInvoiceDocument.class, lookupFields, true);

        for (ContractsGrantsInvoiceDocument cgInvoiceDoc : cgInvoiceDocuments) {
            if (!ObjectUtils.isNull(cgInvoiceDoc.getInvoiceSuspensionCategories()) && !cgInvoiceDoc.getInvoiceSuspensionCategories().isEmpty()) { // only report on documents which have suspension categories associated
                for (InvoiceSuspensionCategory invoiceSuspensionCategory : cgInvoiceDoc.getInvoiceSuspensionCategories()) {
                    if (suspensionCategoryCodes.isEmpty() || suspensionCategoryCodes.contains(invoiceSuspensionCategory.getSuspensionCategoryCode())) {
                        if (!suspensionCategoryDescriptions.containsKey(invoiceSuspensionCategory.getSuspensionCategoryCode())) {
                            suspensionCategoryDescriptions.put(invoiceSuspensionCategory.getSuspensionCategoryCode(), invoiceSuspensionCategory.getSuspensionCategory().getSuspensionCategoryDescription());
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
                }
            }
        }

        for (String suspensionCategoryCode : documentNumbersByCategory.keySet()) {

            ContractsGrantsSuspendedInvoiceSummaryReport cgSuspendedInvoiceSummaryReport = new ContractsGrantsSuspendedInvoiceSummaryReport();
            cgSuspendedInvoiceSummaryReport.setSuspensionCategoryCode(suspensionCategoryCode);

            cgSuspendedInvoiceSummaryReport.setCategoryDescription(suspensionCategoryDescriptions.get(suspensionCategoryCode));
            cgSuspendedInvoiceSummaryReport.setTotalInvoicesSuspended(new Long(documentNumbersByCategory.get(suspensionCategoryCode).size()));

            displayList.add(cgSuspendedInvoiceSummaryReport);

        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }

    protected Map<String, String> buildSuspensionCategoryMap() {
        Map suspensionCategoryMap = new HashMap<String, String>();
        Collection<SuspensionCategory> suspensionCategories = getBusinessObjectService().findAll(SuspensionCategory.class);
        for (SuspensionCategory suspensionCategory : suspensionCategories) {
            suspensionCategoryMap.put(suspensionCategory.getSuspensionCategoryCode(), suspensionCategory.getSuspensionCategoryDescription());

        }
        return suspensionCategoryMap;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}