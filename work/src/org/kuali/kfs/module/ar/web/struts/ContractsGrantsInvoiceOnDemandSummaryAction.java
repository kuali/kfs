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
package org.kuali.kfs.module.ar.web.struts;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceOnDemandLookupResult;
import org.kuali.kfs.module.ar.businessobject.lookup.ContractsGrantsInvoiceOnDemandLookupUtil;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.action.KualiAction;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Action class for Contracts Grants Invoice On Demand Summary.
 */
public class ContractsGrantsInvoiceOnDemandSummaryAction extends KualiAction {


    /**
     * 1. This method passes the control from Contracts Grants Invoice On Demand lookup to the Contracts Grants Invoice On Demand
     * Summary page. 2. Retrieves the list of selected awards by agency for creating invoices.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ContractsGrantsInvoiceOnDemandSummaryForm contractsGrantsInvoiceOnDemandSummaryForm = (ContractsGrantsInvoiceOnDemandSummaryForm) form;
        String lookupResultsSequenceNumber = contractsGrantsInvoiceOnDemandSummaryForm.getLookupResultsSequenceNumber();
        if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
            String personId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
            Collection<ContractsGrantsInvoiceOnDemandLookupResult> contractsGrantsInvoiceOnDemandLookupResults = ContractsGrantsInvoiceOnDemandLookupUtil.getContractsGrantsInvoiceOnDemandResultsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId);

            contractsGrantsInvoiceOnDemandSummaryForm.setContractsGrantsInvoiceOnDemandLookupResults(contractsGrantsInvoiceOnDemandLookupResults);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method would create invoices for the list of awards. It calls the batch process to reuse the functionality to create the
     * invoices.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward createInvoices(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ContractsGrantsInvoiceOnDemandSummaryForm contractsGrantsInvoiceOnDemandSummaryForm = (ContractsGrantsInvoiceOnDemandSummaryForm) form;
        ContractsGrantsInvoiceCreateDocumentService cgInvoiceDocumentCreateService = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class);
        Person person = GlobalVariables.getUserSession().getPerson();
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        String lookupResultsSequenceNumber = "";
        String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            lookupResultsSequenceNumber = StringUtils.substringBetween(parameterName, ".number", ".");
        }

        Collection<ContractsGrantsInvoiceOnDemandLookupResult> lookupResults = ContractsGrantsInvoiceOnDemandLookupUtil.getContractsGrantsInvoiceOnDemandResultsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        // To retrieve the batch file directory name as "reports/cg"
        ModuleConfiguration systemConfiguration = SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode("KFS-AR").getModuleConfiguration();

        String destinationFolderPath = StringUtils.EMPTY;
        List<String> batchFileDirectories = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories();

        if ( CollectionUtils.isNotEmpty(batchFileDirectories)){
            destinationFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories().get(0);
        }

        String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());
        contractsGrantsInvoiceOnDemandSummaryForm.setAwardInvoicedInd(true);// set to false before creating invoices
        // Create Invoices from list of Awards.
        for (ContractsGrantsInvoiceOnDemandLookupResult contractsGrantsInvoiceOnDemandLookupResult : lookupResults) {

            String errOutputFile1 = destinationFolderPath + File.separator + ArConstants.BatchFileSystem.ONDEMAND_VALIDATION_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
            String errOutputFile2 = destinationFolderPath + File.separator + ArConstants.BatchFileSystem.ONDEMAND_CREATION_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
            Collection<ContractsAndGrantsBillingAward> awards = contractsGrantsInvoiceOnDemandLookupResult.getAwards();
            awards = cgInvoiceDocumentCreateService.validateAwards(awards, errOutputFile1);

            cgInvoiceDocumentCreateService.createCGInvoiceDocumentsByAwards(awards, errOutputFile2);

        }
        // set isInvoiced to true

        KNSGlobalVariables.getMessageList().add(ArKeyConstants.ContractsGrantsInvoiceConstants.MESSAGE_CONTRACTS_GRANTS_INVOICE_BATCH_SENT);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * To cancel the document, invoices are not created when the cancel method is called.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_CANCEL);
    }
}
