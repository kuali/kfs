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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceSummaryReport;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a custom lookup for the Suspense Activity Report.
 */
public class ContractsGrantsSuspendedInvoiceSummaryReportLookupableHelperServiceImpl extends ContractsGrantsSuspendedInvoiceReportLookupableHelperServiceImplBase {

    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsSuspendedInvoiceSummaryReportLookupableHelperServiceImpl.class);

    protected ConfigurationService configurationService;
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
        Map<String, String> lookupFields = new HashMap<>();
        Map<String, String> suspensionCategoryDescriptions = new HashMap<String, String>();

        final String suspensionCategoryCodeFromLookup = (String)lookupFormFields.get(ArPropertyConstants.SuspensionCategoryReportFields.SUSPENSION_CATEGORY_CODE);
        if (StringUtils.isNotBlank(suspensionCategoryCodeFromLookup)) {
            lookupFields.put(ArPropertyConstants.SuspensionCategoryReportFields.CONTRACTS_GRANTS_INVOICE_DOCUMENT_SUSPENSION_CATEGORY_CODE, suspensionCategoryCodeFromLookup);
        }

        final String processingDocumentStatuses = buildProcessingDocumentStatusesForLookup();
        lookupFields.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, processingDocumentStatuses);
        Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocuments = getLookupService().findCollectionBySearchHelper(ContractsGrantsInvoiceDocument.class, lookupFields, true);

        for (ContractsGrantsInvoiceDocument cgInvoiceDoc : cgInvoiceDocuments) {
            if (!ObjectUtils.isNull(cgInvoiceDoc.getInvoiceSuspensionCategories()) && !cgInvoiceDoc.getInvoiceSuspensionCategories().isEmpty()) { // only report on documents which have suspension categories associated
                for (InvoiceSuspensionCategory invoiceSuspensionCategory : cgInvoiceDoc.getInvoiceSuspensionCategories()) {
                    String suspensionCategoryCode = invoiceSuspensionCategory.getSuspensionCategoryCode();

                    Pattern suspensionCategoryCodePattern = null;
                    if (!StringUtils.isBlank(suspensionCategoryCodeFromLookup)) {
                        suspensionCategoryCodePattern = Pattern.compile(suspensionCategoryCodeFromLookup.replace("*", ".*"), Pattern.CASE_INSENSITIVE);
                    }

                    if (StringUtils.isBlank(suspensionCategoryCodeFromLookup) ||
                            (suspensionCategoryCodePattern != null && suspensionCategoryCodePattern.matcher(suspensionCategoryCode).matches())) {
                        if (!suspensionCategoryDescriptions.containsKey(suspensionCategoryCode)) {
                            suspensionCategoryDescriptions.put(suspensionCategoryCode, configurationService.getPropertyValueAsString(ArKeyConstants.INVOICE_DOCUMENT_SUSPENSION_CATEGORY + suspensionCategoryCode));
                        }

                        if (ObjectUtils.isNull(documentNumbersByCategory.get(suspensionCategoryCode))) {
                            List<String> documentNumbers = new ArrayList<String>();
                            documentNumbers.add(invoiceSuspensionCategory.getDocumentNumber());
                            documentNumbersByCategory.put(suspensionCategoryCode, documentNumbers);
                        }
                        else {
                            documentNumbersByCategory.get(suspensionCategoryCode).add(invoiceSuspensionCategory.getDocumentNumber());
                        }
                    }
                }
            }
        }

        for (String suspensionCategoryCode : documentNumbersByCategory.keySet()) {

            ContractsGrantsSuspendedInvoiceSummaryReport cgSuspendedInvoiceSummaryReport = new ContractsGrantsSuspendedInvoiceSummaryReport();
            cgSuspendedInvoiceSummaryReport.setSuspensionCategoryCode(suspensionCategoryCode);

            cgSuspendedInvoiceSummaryReport.setSuspensionCategoryDescription(suspensionCategoryDescriptions.get(suspensionCategoryCode));
            cgSuspendedInvoiceSummaryReport.setTotalInvoicesSuspended(new Long(documentNumbersByCategory.get(suspensionCategoryCode).size()));

            displayList.add(cgSuspendedInvoiceSummaryReport);

        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
