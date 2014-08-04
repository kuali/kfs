/*
 * Copyright 2014 The Kuali Foundation.
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
