/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.web.struts;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;


/**
 * Form class for Contracts Grants Invoice Report Lookup screen.
 */
public class ContractsGrantsInvoiceDocumentErrorLogReportForm extends ContractsGrantsReportLookupForm {
    private static volatile ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * Constructor.
     */
    public ContractsGrantsInvoiceDocumentErrorLogReportForm() {
        setHtmlFormAction("contractsGrantsInvoiceDocumentErrorLogReport");
    }

    @Override
    public Map<String, String> getFieldsForLookup() {
        Map<String, String> fieldValues = super.getFieldsForLookup();

        // Add wildcard character to start and end of accounts field so users can search for single account
        // within the delimited list of accounts without having to add the wildcards explicitly themselves.
        String accounts = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ACCOUNTS);
        if (StringUtils.isNotBlank(accounts)) {
            // only add wildcards if they haven't already been added (for some reason this method gets called twice when generating the pdf report)
            if (!StringUtils.startsWith(accounts, KFSConstants.WILDCARD_CHARACTER)) {
                accounts = KFSConstants.WILDCARD_CHARACTER + accounts;
            }
            if (!StringUtils.endsWith(accounts, KFSConstants.WILDCARD_CHARACTER)) {
                accounts += KFSConstants.WILDCARD_CHARACTER;
            }
        }
        fieldValues.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ACCOUNTS, accounts);

        // Add time component to error date since it's stored as both a date and time in the database and without this
        // records with an error date of the date being searched for won't show up in the results
        // (ex: 7/29/2014 09:35 AM is not <= 7/29/2014, but it is <= 7/29/2014 23:59:59)
        String errorDate = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE);
        errorDate = getContractsGrantsReportHelperService().appendEndTimeToDate(errorDate);

        fieldValues.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE, errorDate);

        return fieldValues;
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        if (contractsGrantsReportHelperService == null) {
            contractsGrantsReportHelperService = SpringContext.getBean(ContractsGrantsReportHelperService.class);
        }
        return contractsGrantsReportHelperService;
    }
}
