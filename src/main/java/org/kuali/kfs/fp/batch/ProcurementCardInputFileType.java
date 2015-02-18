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
package org.kuali.kfs.fp.batch;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Batch input type for the procurement card job.
 */
public class ProcurementCardInputFileType extends XmlBatchInputFileTypeBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardInputFileType.class);

    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return KFSConstants.PCDO_FILE_TYPE_INDENTIFIER;
    }

    /**
     * No additional information is added to procurment card batch files.
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kim.api.identity.Person, java.lang.Object,
     *      java.lang.String)
     */
    public String getFileName(String principalName, Object parsedFileContents, String userIdentifier) {
        String fileName = "pcdo_" + principalName;
        if (StringUtils.isNotBlank(userIdentifier)) {
            fileName += "_" + userIdentifier;
        }
        fileName += "_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());

        // remove spaces in filename
        fileName = StringUtils.remove(fileName, " ");

        return fileName;
    }
    
    public String getAuthorPrincipalName(File file) {
        String[] fileNameParts = StringUtils.split(file.getName(), "_");
        if (fileNameParts.length >= 2) {
            return fileNameParts[1];
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    public boolean validate(Object parsedFileContents) {
        List<ProcurementCardTransaction> pctrans = (List<ProcurementCardTransaction>)parsedFileContents;
        if (SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_ACCOUNTING_DEFAULT_PARAMETER_NAME)) {
            return true;  // we're using accounting defaults, don't worry about account numbers from the file...
        }

        boolean valid = true;
        // add validation for chartCode-accountNumber, as chartCode is not required in xsd due to accounts-cant-cross-charts option
        AccountService acctserv = SpringContext.getBean(AccountService.class);
        for (ProcurementCardTransaction pctran : pctrans) {
            // if chart code is empty while accounts cannot cross charts, then derive chart code from account number
            if (StringUtils.isEmpty(pctran.getChartOfAccountsCode())) {
                if (acctserv.accountsCanCrossCharts()) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_EMPTY_CHART, pctran.getAccountNumber());
                    valid = false;
                }
                else {
                    // accountNumber shall not be empty, otherwise won't pass schema validation
                    Account account = acctserv.getUniqueAccountForAccountNumber(pctran.getAccountNumber());
                    if (account != null) {
                        pctran.setChartOfAccountsCode(account.getChartOfAccountsCode());
                    }       
                    else {
                        GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_FILE_INVALID_ACCOUNT, pctran.getAccountNumber());
                        valid = false;
                    }
                }
            }
        }

        return valid;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getTitleKey()
     */
    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_PCDO;
    }

    /**
     * Gets the dateTimeService attribute.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}

