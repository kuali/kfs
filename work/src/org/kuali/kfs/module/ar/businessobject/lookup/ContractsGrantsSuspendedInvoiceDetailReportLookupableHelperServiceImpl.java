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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceReport;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceDetailReport;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a lookupable helper class for Suspended Invoice Detail Report.
 */
public class ContractsGrantsSuspendedInvoiceDetailReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {

    private BusinessObjectService businessObjectService;
    private DocumentService documentService;

    private static final Log LOG = LogFactory.getLog(ContractsGrantsSuspendedInvoiceDetailReportLookupableHelperServiceImpl.class);

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

        List<ContractsGrantsSuspendedInvoiceDetailReport> displayList = new ArrayList<ContractsGrantsSuspendedInvoiceDetailReport>();

        Collection<InvoiceSuspensionCategory> invoiceSuspensionCategories = businessObjectService.findAll(InvoiceSuspensionCategory.class);
        for (InvoiceSuspensionCategory invoiceSuspensionCategory : invoiceSuspensionCategories) {
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, invoiceSuspensionCategory.getDocumentNumber());
            Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocuments = businessObjectService.findMatching(ContractsGrantsInvoiceDocument.class, criteria);

            // Build search result fields
            for (ContractsGrantsInvoiceDocument cgInvoiceDoc : cgInvoiceDocuments) {

                ContractsGrantsInvoiceDocument cgInvoiceDocWithHeader;
                // Documentss that have a problem to get documentHeader won't be on the report
                try {
                    cgInvoiceDocWithHeader = (ContractsGrantsInvoiceDocument) documentService.getByDocumentHeaderId(cgInvoiceDoc.getDocumentNumber());
                }
                catch (WorkflowException e) {
                    LOG.debug("WorkflowException happened while retrives documentHeader");
                    continue;
                }

                WorkflowDocument workflowDocument = null;
                try {
                    workflowDocument = cgInvoiceDocWithHeader.getDocumentHeader().getWorkflowDocument();
                }
                catch (RuntimeException e) {
                    LOG.debug(e + " happened" + " : transient workflowDocument is null");
                    continue;
                }

                boolean isStatusFinalOrProcessed = false;
                // Check status of document
                if (ObjectUtils.isNotNull(workflowDocument)) {
                    isStatusFinalOrProcessed = workflowDocument.isFinal() || workflowDocument.isCanceled() || workflowDocument.isDisapproved();
                }

                // If status of ContractsGrantsInvoiceDocument is final or processed, go to next doc
                if (isStatusFinalOrProcessed) {
                    continue;
                }

                ContractsGrantsSuspendedInvoiceDetailReport cgSuspendedInvoiceDetailReport = new ContractsGrantsSuspendedInvoiceDetailReport();
                cgSuspendedInvoiceDetailReport.setSuspensionCategoryCode(invoiceSuspensionCategory.getSuspensionCategoryCode());
                cgSuspendedInvoiceDetailReport.setDocumentNumber(cgInvoiceDoc.getDocumentNumber());
                cgSuspendedInvoiceDetailReport.setLetterOfCreditFundGroupCode(null);
                if (ObjectUtils.isNotNull(cgInvoiceDoc.getAward())) {
                    if (ObjectUtils.isNotNull(cgInvoiceDoc.getAward().getLetterOfCreditFund())) {
                        cgSuspendedInvoiceDetailReport.setLetterOfCreditFundGroupCode(cgInvoiceDoc.getAward().getLetterOfCreditFund().getLetterOfCreditFundGroupCode());
                    }
                }
                Person fundManager;
                Person projectDirector;

                String fundManagerPrincipalName = "";
                String projectDirectorPrincipalName = "";

                ContractsAndGrantsBillingAward award = cgInvoiceDoc.getAward();
                try {
                    fundManager = award.getAwardPrimaryFundManager().getFundManager();
                    fundManagerPrincipalName = fundManager.getPrincipalName();
                }
                catch (NullPointerException e) {
                    fundManager = null;
                    LOG.debug("Null Pointer Exception happened while retrives fundManager.");
                }

                try {
                    projectDirector = award.getAwardPrimaryProjectDirector().getProjectDirector();
                    projectDirectorPrincipalName = projectDirector.getPrincipalName();
                }
                catch (NullPointerException e) {
                    projectDirector = null;
                    LOG.debug("Null Pointer Exception happened while retrives projectDirector.");
                }
                cgSuspendedInvoiceDetailReport.setAwardFundManager(fundManager);
                cgSuspendedInvoiceDetailReport.setAwardProjectDirector(projectDirector);
                cgSuspendedInvoiceDetailReport.setFundManagerPrincipalName(fundManagerPrincipalName);
                cgSuspendedInvoiceDetailReport.setProjectDirectorPrincipalName(projectDirectorPrincipalName);

                cgSuspendedInvoiceDetailReport.setAwardTotal(award.getAwardTotalAmount());

                if (ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgSuspendedInvoiceDetailReport, "ContractsGrantsSuspendedInvoiceDetailReport")) {
                    displayList.add(cgSuspendedInvoiceDetailReport);
                }
            }
        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }


    @Override
    protected void buildResultTable(LookupForm lookupForm, Collection displayList, Collection resultTable) {
        Person user = GlobalVariables.getUserSession().getPerson();
        boolean hasReturnableRow = false;
        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
                Column col = (Column) iterator.next();

                String propValue = ObjectUtils.getFormattedPropertyValue(element, col.getPropertyName(), col.getFormatter());
                Class propClass = getPropertyClass(element, col.getPropertyName());

                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                String propValueBeforePotientalMasking = propValue;
                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
                col.setPropertyValue(propValue);

                // Add url when property is documentNumber
                if (col.getPropertyName().equals("documentNumber")) {
                    String url = ConfigContext.getCurrentContextConfig().getKEWBaseURL() + "/" + KewApiConstants.DOC_HANDLER_REDIRECT_PAGE + "?" + KewApiConstants.COMMAND_PARAMETER + "=" + KewApiConstants.DOCSEARCH_COMMAND + "&" + KewApiConstants.DOCUMENT_ID_PARAMETER + "=" + propValue;

                    Map<String, String> fieldList = new HashMap<String, String>();
                    fieldList.put(KFSPropertyConstants.DOCUMENT_NUMBER, propValue);
                    AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                    a.setTitle(HtmlData.getTitleText(createTitleText(ContractsGrantsInvoiceReport.class), ContractsGrantsInvoiceReport.class, fieldList));

                    col.setColumnAnchor(a);
                }
            }

            ResultRow row = new ResultRow(columns, "", ACTION_URLS_EMPTY);

            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }
            boolean isRowReturnable = isResultReturnable(element);
            row.setRowReturnable(isRowReturnable);
            if (isRowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }
        lookupForm.setHasReturnableRow(hasReturnableRow);

    }

    @Override
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    @Override
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * Gets the documentService attribute value.
     *
     * @return the documentService
     */
    public DocumentService getDocumentService() {
        return documentService;
    }


    /**
     * Sets the documentService attribute value.
     *
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }


}
