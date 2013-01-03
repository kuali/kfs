/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.pdp.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.LoadPaymentStatus;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.pdp.service.PaymentFileService;
import org.kuali.kfs.pdp.service.PaymentFileValidationService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.InitiateDirectoryBase;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.pdp.service.PaymentFileService
 */
@Transactional
public class PaymentFileServiceImpl extends InitiateDirectoryBase implements PaymentFileService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentFileServiceImpl.class);

    private String outgoingDirectoryName;

    private ParameterService parameterService;
    private CustomerProfileService customerProfileService;
    private BatchInputFileService batchInputFileService;
    private PaymentFileValidationService paymentFileValidationService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private PdpEmailService paymentFileEmailService;
    private ConfigurationService kualiConfigurationService;

    public PaymentFileServiceImpl() {
        super();
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentFileService#processPaymentFiles(org.kuali.kfs.sys.batch.BatchInputFileType)
     */
    @Override
    public void processPaymentFiles(BatchInputFileType paymentInputFileType) {
        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(paymentInputFileType);

        for (String incomingFileName : fileNamesToLoad) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("processPaymentFiles() Processing " + incomingFileName);
                }

                // collect various information for status of load
                LoadPaymentStatus status = new LoadPaymentStatus();
                status.setMessageMap(new MessageMap());

                // process payment file
                PaymentFileLoad paymentFile = processPaymentFile(paymentInputFileType, incomingFileName, status.getMessageMap());
                if (paymentFile != null && paymentFile.isPassedValidation()) {
                    // load payment data
                    loadPayments(paymentFile, status, incomingFileName);

                    createOutputFile(status, incomingFileName);
                }else{
                    //if we encounter an error for the payment file, we will remove the .done file so it will not be parse again

                    LOG.warn("Encounter a problem while processing payment file: " + incomingFileName + " .  Removing the done file to stop re-process.");
                    removeDoneFile(incomingFileName);
                }
            }
            catch (RuntimeException e) {
                LOG.error("Caught exception trying to load payment file: " + incomingFileName, e);
                // swallow exception so we can continue processing files, the errors have been reported by email
            }
        }
    }

    /**
     * Attempt to parse the file, run validations, and store batch data
     *
     * @param paymentInputFileType <code>BatchInputFileType</code> for payment files
     * @param incomingFileName name of payment file
     * @param errorMap <code>Map</code> of errors
     * @return <code>LoadPaymentStatus</code> containing status data for load
     */
    protected PaymentFileLoad processPaymentFile(BatchInputFileType paymentInputFileType, String incomingFileName, MessageMap errorMap) {
        // parse xml, if errors found return with failure
        PaymentFileLoad paymentFile = parsePaymentFile(paymentInputFileType, incomingFileName, errorMap);

        if (errorMap.hasNoErrors()) {
            // do validation
            doPaymentFileValidation(paymentFile, errorMap);
        }

        return paymentFile;
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentFileService#doPaymentFileValidation(org.kuali.kfs.pdp.businessobject.PaymentFileLoad,
     *      org.kuali.rice.krad.util.MessageMap)
     */
    @Override
    public void doPaymentFileValidation(PaymentFileLoad paymentFile, MessageMap errorMap) {
        paymentFileValidationService.doHardEdits(paymentFile, errorMap);

        if (errorMap.hasErrors()) {
            paymentFileEmailService.sendErrorEmail(paymentFile, errorMap);
        }

        paymentFile.setPassedValidation(true);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentFileService#loadPayments(java.lang.String)
     */
    @Override
    public void loadPayments(PaymentFileLoad paymentFile, LoadPaymentStatus status, String incomingFileName) {
        status.setChart(paymentFile.getChart());
        status.setUnit(paymentFile.getUnit());
        status.setSubUnit(paymentFile.getSubUnit());
        status.setCreationDate(paymentFile.getCreationDate());
        status.setDetailCount(paymentFile.getActualPaymentCount());
        status.setDetailTotal(paymentFile.getCalculatedPaymentTotalAmount());

        // create batch record for payment load
        Batch batch = createNewBatch(paymentFile, getBaseFileName(incomingFileName));
        businessObjectService.save(batch);

        paymentFile.setBatchId(batch.getId());
        status.setBatchId(batch.getId());

        // do warnings and set defaults
        List<String> warnings = paymentFileValidationService.doSoftEdits(paymentFile);
        status.setWarnings(warnings);

        // store groups
        for (PaymentGroup paymentGroup : paymentFile.getPaymentGroups()) {
            businessObjectService.save(paymentGroup);
        }

        // send list of warnings
        paymentFileEmailService.sendLoadEmail(paymentFile, warnings);
        if (paymentFile.isTaxEmailRequired()) {
            paymentFileEmailService.sendTaxEmail(paymentFile);
        }

        removeDoneFile(incomingFileName);

        LOG.debug("loadPayments() was successful");
        status.setLoadStatus(LoadPaymentStatus.LoadStatus.SUCCESS);
    }

    /**
     * Calls <code>BatchInputFileService</code> to validate XML against schema and parse.
     *
     * @param paymentInputFileType <code>BatchInputFileType</code> for payment files
     * @param incomingFileName name of the payment file to parse
     * @param errorMap any errors encountered while parsing are adding to
     * @return <code>PaymentFile</code> containing the parsed values
     */
    protected PaymentFileLoad parsePaymentFile(BatchInputFileType paymentInputFileType, String incomingFileName, MessageMap errorMap) {
        FileInputStream fileContents;
        try {
            fileContents = new FileInputStream(incomingFileName);
        }
        catch (FileNotFoundException e1) {
            LOG.error("file to load not found " + incomingFileName, e1);
            throw new RuntimeException("Cannot find the file requested to be loaded " + incomingFileName, e1);
        }

        // do the parse
        PaymentFileLoad paymentFile = null;
        try {
            byte[] fileByteContent = IOUtils.toByteArray(fileContents);
            paymentFile = (PaymentFileLoad) batchInputFileService.parse(paymentInputFileType, fileByteContent);
        }
        catch (IOException e) {
            LOG.error("error while getting file bytes:  " + e.getMessage(), e);
            throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
        }
        catch (ParseException e1) {
            LOG.error("Error parsing xml " + e1.getMessage());

            errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML, new String[] { e1.getMessage() });
        }

        return paymentFile;
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentFileService#createOutputFile(org.kuali.kfs.pdp.businessobject.LoadPaymentStatus,
     *      java.lang.String)
     */
    @Override
    public boolean createOutputFile(LoadPaymentStatus status, String inputFileName) {

        //add a step to check for directory paths
        prepareDirectories(getRequiredDirectoryNames());

        // construct the outgoing file name
        String filename = outgoingDirectoryName + "/" + getBaseFileName(inputFileName);

        // set code-message indicating overall load status
        String code;
        String message;
        if (LoadPaymentStatus.LoadStatus.SUCCESS.equals(status.getLoadStatus())) {
            code = "SUCCESS";
            message = "Successful Load";
        }
        else {
            code = "FAIL";
            message = "Load Failed: ";
            List<ErrorMessage> errorMessages = status.getMessageMap().getMessages(KFSConstants.GLOBAL_ERRORS);
            for (ErrorMessage errorMessage : errorMessages) {
                String resourceMessage = kualiConfigurationService.getPropertyValueAsString(errorMessage.getErrorKey());
                resourceMessage = MessageFormat.format(resourceMessage, (Object[]) errorMessage.getMessageParameters());
                message += resourceMessage + ", ";
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(filename);
            PrintStream p = new PrintStream(out);

            p.println("<pdp_load_status>");
            p.println("  <input_file_name>" + inputFileName + "</input_file_name>");
            p.println("  <code>" + code + "</code>");
            p.println("  <count>" + status.getDetailCount() + "</count>");
            if (status.getDetailTotal() != null) {
                p.println("  <total>" + status.getDetailTotal() + "</total>");
            }
            else {
                p.println("  <total>0</total>");
            }

            p.println("  <description>" + message + "</description>");
            p.println("  <messages>");
            for (String warning : status.getWarnings()) {
                p.println("    <message>" + warning + "</message>");
            }
            p.println("  </messages>");
            p.println("</pdp_load_status>");

            p.close();
            out.close();
        }
        catch (FileNotFoundException e) {
            LOG.error("createOutputFile() Cannot create output file", e);
            return false;
        }
        catch (IOException e) {
            LOG.error("createOutputFile() Cannot write to output file", e);
            return false;
        }

        return true;
    }

    /**
     * Create a new <code>Batch</code> record for the payment file.
     *
     * @param paymentFile parsed payment file object
     * @param fileName payment file name (without path)
     * @return <code>Batch<code> object
     */
    protected Batch createNewBatch(PaymentFileLoad paymentFile, String fileName) {
        Timestamp now = dateTimeService.getCurrentTimestamp();

        Calendar nowPlus30 = Calendar.getInstance();
        nowPlus30.setTime(now);
        nowPlus30.add(Calendar.DATE, 30);

        Calendar nowMinus30 = Calendar.getInstance();
        nowMinus30.setTime(now);
        nowMinus30.add(Calendar.DATE, -30);

        Batch batch = new Batch();

        CustomerProfile customer = customerProfileService.get(paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit());
        batch.setCustomerProfile(customer);
        batch.setCustomerFileCreateTimestamp(new Timestamp(paymentFile.getCreationDate().getTime()));
        batch.setFileProcessTimestamp(now);
        batch.setPaymentCount(new KualiInteger(paymentFile.getPaymentCount()));

        if (fileName.length() > 30) {
            batch.setPaymentFileName(fileName.substring(0, 30));
        }
        else {
            batch.setPaymentFileName(fileName);
        }

        batch.setPaymentTotalAmount(paymentFile.getPaymentTotalAmount());
        batch.setSubmiterUserId(GlobalVariables.getUserSession().getPerson().getPrincipalId());

        return batch;
    }


    /**
     * @returns the file name from the file full path.
     */
    protected String getBaseFileName(String filename) {
        // Replace any backslashes with forward slashes. Works on Windows or Unix
        filename = filename.replaceAll("\\\\", "/");

        int startingPointer = filename.length() - 1;
        while ((startingPointer > 0) && (filename.charAt(startingPointer) != '/')) {
            startingPointer--;
        }

        return filename.substring(startingPointer + 1);
    }

    /**
     * Clears out the associated .done file for the processed data file
     *
     * @param dataFileName the name of date file with done file to remove
     */
    protected void removeDoneFile(String dataFileName) {
        File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
        if (doneFile.exists()) {
            doneFile.delete();
        }
    }

    /**
     * Sets the outgoingDirectoryName attribute value.
     *
     * @param outgoingDirectoryName The outgoingDirectoryName to set.
     */
    public void setOutgoingDirectoryName(String outgoingDirectoryName) {
        this.outgoingDirectoryName = outgoingDirectoryName;
    }

    /**
     * Sets the parameterService attribute value.
     *
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the customerProfileService attribute value.
     *
     * @param customerProfileService The customerProfileService to set.
     */
    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    /**
     * Sets the batchInputFileService attribute value.
     *
     * @param batchInputFileService The batchInputFileService to set.
     */
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    /**
     * Sets the paymentFileValidationService attribute value.
     *
     * @param paymentFileValidationService The paymentFileValidationService to set.
     */
    public void setPaymentFileValidationService(PaymentFileValidationService paymentFileValidationService) {
        this.paymentFileValidationService = paymentFileValidationService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the paymentFileEmailService attribute value.
     *
     * @param paymentFileEmailService The paymentFileEmailService to set.
     */
    public void setPaymentFileEmailService(PdpEmailService paymentFileEmailService) {
        this.paymentFileEmailService = paymentFileEmailService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     *
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.impl.InitiateDirectoryImpl#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return new ArrayList<String>() {{add(outgoingDirectoryName); }};
    }

}

