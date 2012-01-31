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
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceReport;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceReport;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Defines a lookupable helper class for Suspended Contracts and Grants Invoices Report.
 */
public class ContractsGrantsSuspendedInvoiceReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {

    private BusinessObjectService businessObjectService;
    private DocumentService documentService;

    private static final Log LOG = LogFactory.getLog(ContractsGrantsSuspendedInvoiceReportLookupableHelperServiceImpl.class);

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

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KNSConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KNSConstants.DOC_FORM_KEY));

        List<ContractsGrantsSuspendedInvoiceReport> displayList = new ArrayList<ContractsGrantsSuspendedInvoiceReport>();

        Collection<InvoiceSuspensionCategory> invoiceSuspensionCategories = businessObjectService.findAll(InvoiceSuspensionCategory.class);
        for (InvoiceSuspensionCategory invoiceSuspensionCategory : invoiceSuspensionCategories) {
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put("documentNumber", invoiceSuspensionCategory.getDocumentNumber());
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

                KualiWorkflowDocument workflowDocument = null;
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
                    isStatusFinalOrProcessed = workflowDocument.stateIsFinal() || workflowDocument.stateIsCanceled() || workflowDocument.stateIsDisapproved();
                }

                // If status of ContractsGrantsInvoiceDocument is final or processed, go to next doc
                if (isStatusFinalOrProcessed) {
                    continue;
                }

                ContractsGrantsSuspendedInvoiceReport cgSuspendedInvoiceReport = new ContractsGrantsSuspendedInvoiceReport();
                cgSuspendedInvoiceReport.setSuspensionCategoryCode(invoiceSuspensionCategory.getSuspensionCategoryCode());
                cgSuspendedInvoiceReport.setDocumentNumber(cgInvoiceDoc.getDocumentNumber());
                cgSuspendedInvoiceReport.setLetterOfCreditFundGroupCode(null);
                if (ObjectUtils.isNotNull(cgInvoiceDoc.getAward())) {
                    if (ObjectUtils.isNotNull(cgInvoiceDoc.getAward().getLetterOfCreditFund())) {
                        cgSuspendedInvoiceReport.setLetterOfCreditFundGroupCode(cgInvoiceDoc.getAward().getLetterOfCreditFund().getLetterOfCreditFundGroupCode());
                    }
                }
                Person fundManager;
                Person projectDirector;

                String fundManagerPrincipalName = "";
                String projectDirectorPrincipalName = "";

                ContractsAndGrantsCGBAward award = cgInvoiceDoc.getAward();
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
                cgSuspendedInvoiceReport.setAwardFundManager(fundManager);
                cgSuspendedInvoiceReport.setAwardProjectDirector(projectDirector);
                cgSuspendedInvoiceReport.setFundManagerPrincipalName(fundManagerPrincipalName);
                cgSuspendedInvoiceReport.setProjectDirectorPrincipalName(projectDirectorPrincipalName);

                cgSuspendedInvoiceReport.setAwardTotal(award.getAwardTotalAmount());

                if (ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgSuspendedInvoiceReport, "ContractsGrantsSuspendedInvoiceReport")) {
                    displayList.add(cgSuspendedInvoiceReport);
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
                    String url = ConfigContext.getCurrentContextConfig().getKEWBaseURL() + "/" + KEWConstants.DOC_HANDLER_REDIRECT_PAGE + "?" + KEWConstants.COMMAND_PARAMETER + "=" + KEWConstants.DOCSEARCH_COMMAND + "&" + KEWConstants.ROUTEHEADER_ID_PARAMETER + "=" + propValue;

                    Map<String, String> fieldList = new HashMap<String, String>();
                    fieldList.put(KFSPropertyConstants.DOCUMENT_NUMBER, propValue);
                    AnchorHtmlData a = new AnchorHtmlData(url, KNSConstants.EMPTY_STRING);
                    a.setTitle(HtmlData.getTitleText(createTitleText(ContractsGrantsInvoiceReport.class), ContractsGrantsInvoiceReport.class, fieldList));

                    col.setColumnAnchor(a);
                }
            }

            ResultRow row = new ResultRow(columns, "", ACTION_URLS_EMPTY);

            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }
            boolean rowReturnable = isResultReturnable(element);
            row.setRowReturnable(rowReturnable);
            if (rowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }
        lookupForm.setHasReturnableRow(hasReturnableRow);

    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

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
