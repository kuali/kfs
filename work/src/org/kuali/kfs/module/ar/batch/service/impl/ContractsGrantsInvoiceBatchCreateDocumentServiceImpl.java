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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceBatchCreateDocumentService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.springframework.util.CollectionUtils;

/**
 * Default implementation of the ContractsGrantsInvoiceBatchCreateDocumentServiceImpl interface
 */
public class ContractsGrantsInvoiceBatchCreateDocumentServiceImpl implements ContractsGrantsInvoiceBatchCreateDocumentService {
    protected ContractsGrantsInvoiceCreateDocumentService contractsGrantsInvoiceCreateDocumentService;
    protected ConfigurationService configurationService;

    /**
     * @see org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceDocumentCreateService#createCGInvoiceDocumentsByAwards(java.lang.String)
     */
    @Override
    public void createCGInvoiceDocumentsByAwards(Collection<ContractsAndGrantsBillingAward> awards, String errOutputFileName) {
        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();

        List<ErrorMessage> errorMessages = getContractsGrantsInvoiceCreateDocumentService().createCGInvoiceDocumentsByAwards(awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH);

        if (!CollectionUtils.isEmpty(errorMessages)) {
            File errOutPutfile = new File(errOutputFileName);
            PrintStream outputFileStream = null;

            try {
                outputFileStream = new PrintStream(errOutPutfile);
                for (ErrorMessage errorMessage : errorMessages) {
                    outputFileStream.printf("%s\n", MessageFormat.format(getConfigurationService().getPropertyValueAsString(errorMessage.getErrorKey()), (Object[])errorMessage.getMessageParameters()));
                }
            } catch (IOException ex) {
                throw new RuntimeException("Could not write error entries for batch Contracts and Grants Invoice document creation", ex);
            } finally {
                if (outputFileStream != null) {
                    outputFileStream.close();
                }
            }
        }
    }

    /**
     * Retrieves the awards, validates them, and then creates documents for all valid awards
     * @see org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService#processBatchInvoiceDocumentCreation(java.lang.String, java.lang.String)
     */
    @Override
    public void processBatchInvoiceDocumentCreation(String validationErrorOutputFileName, String invoiceDocumentErrorOutputFileName) {
        final Collection<ContractsAndGrantsBillingAward> awards = getContractsGrantsInvoiceCreateDocumentService().retrieveNonLOCAwards();
        final Collection<ContractsAndGrantsBillingAward> validAwards = getContractsGrantsInvoiceCreateDocumentService().validateAwards(awards, null, validationErrorOutputFileName, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode());
        createCGInvoiceDocumentsByAwards(validAwards, invoiceDocumentErrorOutputFileName);
    }

    public ContractsGrantsInvoiceCreateDocumentService getContractsGrantsInvoiceCreateDocumentService() {
        return contractsGrantsInvoiceCreateDocumentService;
    }

    public void setContractsGrantsInvoiceCreateDocumentService(ContractsGrantsInvoiceCreateDocumentService contractsGrantsInvoiceCreateDocumentService) {
        this.contractsGrantsInvoiceCreateDocumentService = contractsGrantsInvoiceCreateDocumentService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
