/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.DataDictionaryBuilder;
import org.kuali.core.datadictionary.XmlErrorHandler;
import org.kuali.core.datadictionary.exception.InitException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.financial.bo.ProcurementCardTransaction;
import org.kuali.module.financial.exceptions.NoTransactionsException;
import org.kuali.module.financial.service.ProcurementCardLoadTransactionsService;

/**
 * Implementation of ProcurementCardDocumentService
 * 
 * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ProcurementCardLoadTransactionsServiceImpl implements ProcurementCardLoadTransactionsService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardLoadTransactionsServiceImpl.class);

    private final static String PCARD_DOCUMENT_PARAMETERS_SEC_GROUP = "PCardDocumentParameters";
    private final static String PCDO_FILE_DIRECTORY_PARM_NM = "pcdo.staging.directory";
    private final static String BACK_UP_INCOMING_FILES_IND_PARM_NM = "BACK_UP_INCOMOING_FILES_IND";
    private final static String PACKAGE_PREFIX = "/org/kuali/module/financial/batch/pcard/";

    private KualiConfigurationService kualiConfigurationService;
    private DictionaryValidationService dictionaryValidationService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;

    /**
     * @throws SAXException
     * @throws IOException
     * @see org.kuali.module.financial.service.ProcurementCardCreateDocumentService#loadProcurementCardDataFile()
     */
    public boolean loadProcurementCardDataFile() {
        // get input file to process
        File[] pcardLoadFiles = retrieveKualiPCardFiles();
        if (pcardLoadFiles == null) {
            LOG.warn("No PCard input files found.");
            throw new NoTransactionsException("No PCard input files found.");
        }

        // retrieve backup indicator parm value
        boolean backupIndicator = kualiConfigurationService.getApplicationParameterIndicator(PCARD_DOCUMENT_PARAMETERS_SEC_GROUP, BACK_UP_INCOMING_FILES_IND_PARM_NM);

        // clean transaction table from any previous loads
        LOG.info("Cleaning transaction temp table ...");
        cleanTransactionsTable();

        int totalTransactionsParsed = 0;
        for (int i = 0; i < pcardLoadFiles.length; i++) {
            File pcardLoadFile = pcardLoadFiles[i];
            LOG.info("Processing file: " + pcardLoadFile.getName());

            // setup digester for parsing the xml file
            Digester digester = buildDigester(pcardLoadFile.getName());

            Collection pcardTransactions;
            try {
                pcardTransactions = (Collection) digester.parse(pcardLoadFile);
            }
            catch (Exception e) {
                LOG.error("Error parsing xml " + e.getMessage());
                throw new RuntimeException("Error parsing xml " + e.getMessage());
            }

            if (pcardTransactions == null || pcardTransactions.isEmpty()) {
                LOG.warn("No PCard transactions in input file.");
                continue;
            }

            totalTransactionsParsed += pcardTransactions.size();

            // check transaction record against dd, validates length, required, and format
            boolean validationSuccessful = validateTransactionsDataFormat(pcardTransactions);
            if (!validationSuccessful) {
                LOG.error("Error validating transaction against dd file. " + GlobalVariables.getErrorMap().toString());
                throw new ValidationException("Error validating transaction against dd file. " + GlobalVariables.getErrorMap().toString());
            }

            // load new transactions into temp table
            LOG.info("Loading transaction temp table ...");
            loadTransactions((List) pcardTransactions);

            // backup input file
            String dateString = (new SimpleDateFormat("yyyy-MM-dd")).format(dateTimeService.getCurrentDate());
            String[] extSplit = StringUtils.split(pcardLoadFile.getAbsolutePath(), ".");
            File backupPCardFile = new File(extSplit[0] + dateString + "." + extSplit[1]);
            if (backupIndicator) {
                LOG.info("Backing up input file ...");
                pcardLoadFile.renameTo(backupPCardFile);
            }
            else {
                LOG.info("Not backing up input file ...");
            }
        }

        LOG.info("Total transactions loaded: " + Integer.toString(totalTransactionsParsed));
        return true;
    }

    private class PCardFilenameFilter implements FilenameFilter {
        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File dir, String name) {
            return name.endsWith(".xml");
        }
    }

    /**
     * Looks for the PCard input file using the APC parameters for the directory and name.
     * 
     * @return File object representing the input file or null if not found
     */
    private File[] retrieveKualiPCardFiles() {
        // retrieve configuration parm telling us where to find the incoming files
        String fileDirectory = kualiConfigurationService.getPropertyString(PCDO_FILE_DIRECTORY_PARM_NM);

        return (new File(fileDirectory)).listFiles(new PCardFilenameFilter());
    }

    /**
     * Validates each transaction against the data dictionary definition for correct size and format.
     * 
     * @param transactions
     * @return boolean
     */
    private boolean validateTransactionsDataFormat(Collection transactions) {
        boolean transactionsValid = true;

        for (Iterator iter = transactions.iterator(); iter.hasNext();) {
            ProcurementCardTransaction pcardTransaction = (ProcurementCardTransaction) iter.next();
            dictionaryValidationService.validateBusinessObject(pcardTransaction);
        }

        if (!GlobalVariables.getErrorMap().isEmpty()) {
            transactionsValid = false;
        }

        return transactionsValid;
    }

    /**
     * @return fully-initialized Digester used to process entry XML files
     */
    private Digester buildDigester(String sourceFileName) {
        Digester digester = new Digester();
        digester.setNamespaceAware(false);
        digester.setValidating(true);

        Rules rules = loadRules();

        digester.setRules(rules);
        digester.setErrorHandler(new XmlErrorHandler(sourceFileName));

        return digester;
    }

    /**
     * @return Rules loaded from the appropriate XML file
     */
    private final Rules loadRules() {
        // locate Digester rules
        URL rulesUrl = DataDictionaryBuilder.class.getResource(PACKAGE_PREFIX + "digesterRules.xml");
        if (rulesUrl == null) {
            throw new InitException("unable to locate digester rules file");
        }

        // create and init digester
        Digester digester = DigesterLoader.createDigester(rulesUrl);

        return digester.getRules();
    }

    /**
     * Calls businessObjectService to remove all the rows from the transaction load table.
     */
    private void cleanTransactionsTable() {
        businessObjectService.deleteMatching(ProcurementCardTransaction.class, new HashMap());
    }

    /**
     * Loads all the parsed xml transactions into the temp transaction table.
     * 
     * @param transactions - List of ProcurementCardTransaction to load.
     */
    private void loadTransactions(List transactions) {
        businessObjectService.save(transactions);
    }

    /**
     * @return Returns the kualiConfigurationService.
     */
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }


    /**
     * @return Returns the dictionaryValidationService.
     */
    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    /**
     * @param dictionaryValidationService The dictionaryValidationService to set.
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }


    /**
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}