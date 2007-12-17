/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.pdp.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.mail.InvalidAddressException;
import org.kuali.core.mail.MailMessage;
import org.kuali.core.service.MailService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.dao.PaymentFileLoadDao;
import org.kuali.module.pdp.exception.PaymentLoadException;
import org.kuali.module.pdp.service.CustomerProfileService;
import org.kuali.module.pdp.service.EnvironmentService;
import org.kuali.module.pdp.service.LoadPaymentStatus;
import org.kuali.module.pdp.service.PaymentFileService;
import org.kuali.module.pdp.utilities.GeneralUtilities;
import org.kuali.module.pdp.xml.PaymentFileParser;
import org.kuali.module.pdp.xml.XmlHeader;
import org.kuali.module.pdp.xml.XmlTrailer;
import org.kuali.module.pdp.xml.impl.DataLoadHandler;
import org.kuali.module.pdp.xml.impl.HardEditHandler;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PaymentFileServiceImpl implements PaymentFileService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentFileServiceImpl.class);

    private MailService mailService;
    private ParameterService parameterService;
    private CustomerProfileService customerProfileService;
    private EnvironmentService environmentService;
    private PaymentFileLoadDao paymentFileLoadDao;
    private UniversalUserService universalUserService;
    private String incomingDirectoryName;
    private String outgoingDirectoryName;

    public PaymentFileServiceImpl() {
        super();
    }

    public void processPaymentFiles() {
        LOG.debug("processPaymentFiles() started");

        PdpUser pusr = null;
        try {
            UniversalUser uu = universalUserService.getUniversalUserByAuthenticationUserId("KULUSER");
            pusr = new PdpUser(uu);
        }
        catch (UserNotFoundException u) {
            LOG.error("processPaymentFiles() Unable to find requested user", u);
            throw new IllegalArgumentException("Unable to find user");
        }

        // Look for *.done in the incoming folder
        File dir = new File(incomingDirectoryName);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".done");
            }
        };
        String[] files = dir.list(filter);
        for (int i = 0; i < files.length; i++) {
            String doneFilename = files[i];
            LOG.debug("processPaymentFiles() Found done file: " + doneFilename);

            String inputDoneFullPath = incomingDirectoryName + "/" + doneFilename;
            String inputXmlFullPath = incomingDirectoryName + "/" + getFilename(doneFilename) + ".xml";
            String outputDoneFullPath = outgoingDirectoryName + "/" + doneFilename;

            LOG.debug("processPaymentFiles() inputDoneFullPath: " + inputDoneFullPath);
            LOG.debug("processPaymentFiles() inputXmlFullPath: " + inputXmlFullPath);
            LOG.debug("processPaymentFiles() outputDoneFullPath: " + outputDoneFullPath);

            File f = new File(inputXmlFullPath);
            if (f.exists()) {
                LOG.info("processPaymentFiles() Processing " + inputXmlFullPath);
                try {
                    LoadPaymentStatus status = loadPayments(inputXmlFullPath, pusr);

                    // Create the output file
                    createOutputFile(outputDoneFullPath, "SUCCESS", "Successful Load", status.getDetailCount(), status.getDetailTotal(), status.getWarnings(), doneFilename);

                    // Delete the file now that we have processed it
                    f.delete();
                }
                catch (PaymentLoadException ple) {
                    String msg = ple.getMessage() == null ? "" : ple.getMessage();
                    createOutputFile(outputDoneFullPath, "FAIL", "Load Failed " + msg, 0, null, null, doneFilename);
                }

                // Delete the done file
                File df = new File(inputDoneFullPath);
                df.delete();
            }
            else {
                LOG.error("processPaymentFiles() Done file exists without xml file: " + inputXmlFullPath);
            }
        }
    }

    public boolean createOutputFile(String filename, String code, String message, int count, BigDecimal total, List messages, String inputFileName) {
        FileOutputStream out;
        PrintStream p;

        try {
            out = new FileOutputStream(filename);
            p = new PrintStream(out);

            p.println("<pdp_load_status>");
            p.println("  <input_file_name>" + inputFileName + "</input_file_name>");
            p.println("  <code>" + code + "</code>");
            if ("SUCCESS".equals(code)) {
                p.println("  <count>" + count + "</count>");
                p.println("  <total>" + total + "</total>");
            }
            else {
                p.println("  <count>0</count>");
                p.println("  <total>0</total>");
            }
            p.println("  <description>" + message + "</description>");
            if (messages != null) {
                p.println("  <messages>");
                for (Iterator iter = messages.iterator(); iter.hasNext();) {
                    String element = (String) iter.next();
                    p.println("    <message>" + element + "</message>");
                }
                p.println("  </messages>");
            }
            p.println("</pdp_load_status>");

            p.close();
            out.close();
            return true;
        }
        catch (FileNotFoundException e) {
            LOG.error("createOutputFile() Cannot create output file", e);
            return false;
        }
        catch (IOException e) {
            LOG.error("createOutputFile() Cannot write to output file", e);
            return false;
        }
    }

    public boolean createDoneFile(String filename) {
        FileOutputStream out;
        PrintStream p;

        try {
            out = new FileOutputStream(filename);
            p = new PrintStream(out);
            p.println("This file intentionally left blank");
            p.close();
            out.close();
            return true;
        }
        catch (FileNotFoundException e) {
            LOG.error("createDoneFile() Cannot create done file", e);
            return false;
        }
        catch (IOException e) {
            LOG.error("createDoneFile() Cannot write to done file", e);
            return false;
        }
    }

    private String getFilename(String doneFile) {
        return doneFile.replaceAll("\\.done", "");
    }

    private int getMaxNoteLines() {
        return GeneralUtilities.getParameterInteger(parameterService, ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.MAX_NOTE_LINES);
    }

    private String getBaseFileName(String filename) {

        // Replace any backslashes with forward slashes. This makes it work on
        // Windows or Unix
        filename = filename.replaceAll("\\\\", "/");

        int startingPointer = filename.length() - 1;
        while ((startingPointer > 0) && (filename.charAt(startingPointer) != '/')) {
            startingPointer--;
        }
        return filename.substring(startingPointer + 1);
    }

    public void saveBatch(Batch batch) {
        paymentFileLoadDao.createBatch(batch);
    }

    // This will parse the file for hard edit problems, then copy the file to
    // the batch directory. The batch job will actually load the file into the
    // database
    public LoadPaymentStatus loadPayments(String filename, PdpUser user) throws PaymentLoadException {
        PaymentFileParser paymentFileParser = SpringContext.getBean(PaymentFileParser.class);

        HardEditHandler hardEditHandler;

        LoadPaymentStatus status = new LoadPaymentStatus();

        // Try to do the hard edits
        LOG.debug("loadPayments() hard edit check");
        hardEditHandler = new HardEditHandler();
        hardEditHandler.clear();
        hardEditHandler.setMaxNoteLines(getMaxNoteLines());

        paymentFileParser.setFileHandler(hardEditHandler);
        try {
            LOG.debug("loadPayments() begin hard edit parsing");
            paymentFileParser.parse(filename);
            LOG.debug("loadPayments() done hard edit parsing");
        }
        catch (Exception e) {
            LOG.error("loadPayments() Exception when parsing XML", e);

            List errors = new ArrayList();
            errors.add(e.getMessage());

            // Send error email
            sendErrorEmail(hardEditHandler.getHeader(), hardEditHandler.getTrailer(), errors);

            PaymentLoadException ple = new PaymentLoadException();
            ple.setErrors(errors);
            throw ple;
        }

        List errors = hardEditHandler.getErrorMessages();
        if (errors.size() > 0) {
            LOG.debug("loadPayments() There were hard errors in the file");

            // Send error email
            sendErrorEmail(hardEditHandler.getHeader(), hardEditHandler.getTrailer(), errors);

            PaymentLoadException ple = new PaymentLoadException();
            ple.setErrors(errors);
            throw ple;
        }
        status.setDetailCount(hardEditHandler.getActualPaymentCount());
        status.setDetailTotal(hardEditHandler.getCalculatedPaymentTotalAmount());

        DataLoadHandler dataLoadHandler;

        dataLoadHandler = new DataLoadHandler(hardEditHandler.getTrailer());
        dataLoadHandler.setUser(user);
        dataLoadHandler.setFilename(getBaseFileName(filename));
        dataLoadHandler.setMaxNoteLines(getMaxNoteLines());
        paymentFileParser.setFileHandler(dataLoadHandler);
        try {
            paymentFileParser.parse(filename);
        }
        catch (Exception e) {
            LOG.error("loadPayments() Exception when parsing XML for load", e);

            List lerrors = new ArrayList();
            lerrors.add(e.getMessage());

            // Send error email
            sendErrorEmail(hardEditHandler.getHeader(), hardEditHandler.getTrailer(), errors);

            PaymentLoadException ple = new PaymentLoadException();
            ple.setErrors(lerrors);
            throw ple;
        }

        // Send list of warnings
        // sendLoadEmail(dataLoadHandler.getBatch().getId(),hardEditHandler.getHeader(), hardEditHandler.getTrailer(),
        // dataLoadHandler.getErrorMessages());
        sendLoadEmail(dataLoadHandler, hardEditHandler.getHeader(), hardEditHandler.getTrailer());
        if (dataLoadHandler.isTaxEmailRequired()) {
            sendTaxEmail(dataLoadHandler, hardEditHandler.getHeader());
        }

        LOG.debug("loadPayments() parse was successful");
        status.setWarnings(dataLoadHandler.getErrorMessages());
        status.setHeader(hardEditHandler.getHeader());
        status.setBatchId(dataLoadHandler.getBatch().getId());
        return status;
    }

    private void sendErrorEmail(XmlHeader header, XmlTrailer trailer, List errors) {
        LOG.debug("sendErrorEmail() starting");

        // To send email or not send email
        boolean noEmail = false;
        if (parameterService.parameterExists(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.NO_PAYMENT_FILE_EMAIL)) {
            noEmail = parameterService.getIndicatorParameter(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.NO_PAYMENT_FILE_EMAIL);
        }
        if (noEmail) {
            LOG.debug("sendErrorEmail() sending payment file email is disabled");
            return;
        }

        CustomerProfile customer = null;
        MailMessage message = new MailMessage();

        if (environmentService.isProduction()) {
            message.setSubject("PDP --- Payment file NOT loaded");
        }
        else {
            String env = environmentService.getEnvironment();
            message.setSubject(env + "-PDP --- Payment file NOT loaded");
        }
        StringBuffer body = new StringBuffer();

        String ccAddresses = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.HARD_EDIT_CC);
        String ccAddressList[] = ccAddresses.split(",");

        if (header == null) {
            LOG.error("sendErrorEmail() Header is null.  Sending email to CC addresses");
            if (ccAddressList.length == 0) {
                LOG.error("sendErrorEmail() No HARD_EDIT_CC addresses.  No email sent");
                return;
            }
            for (int i = 0; i < ccAddressList.length; i++) {
                if (ccAddressList[i] != null) {
                    message.addToAddress(ccAddressList[i].trim());
                }
            }

            body.append("A file was uploaded that was so messed up, we don't even know who sent it:\n\n");
        }
        else {
            // Get customer
            customer = customerProfileService.get(header.getChart(), header.getOrg(), header.getSubUnit());
            if (customer == null) {
                LOG.error("sendErrorEmail() Invalid Customer.  Sending email to CC addresses");

                if (ccAddressList.length == 0) {
                    LOG.error("sendErrorEmail() No HARD_EDIT_CC addresses.  No email sent");
                    return;
                }
                for (int i = 0; i < ccAddressList.length; i++) {
                    if (ccAddressList[i] != null) {
                        message.addToAddress(ccAddressList[i].trim());
                    }
                }

                body.append("A file was uploaded for an invalid customer:\n\n");
            }
            else {
                String toAddresses = customer.getProcessingEmailAddr();
                String toAddressList[] = toAddresses.split(",");

                if (toAddressList.length > 0) {
                    for (int i = 0; i < toAddressList.length; i++) {
                        if (toAddressList[i] != null) {
                            message.addToAddress(toAddressList[i].trim());
                        }
                    }
                }
                // message.addToAddress(customer.getProcessingEmailAddr());

                for (int i = 0; i < ccAddressList.length; i++) {
                    if (ccAddressList[i] != null) {
                        message.addCcAddress(ccAddressList[i].trim());
                    }
                }
            }
        }

        String fromAddressList[] = {mailService.getBatchMailingList()};

        if(fromAddressList.length > 0) {
            for (int i = 0; i < fromAddressList.length; i++) {
                if (fromAddressList[i] != null) {
                    message.setFromAddress(fromAddressList[i].trim());
                }
            }
        }
        
        if (header != null) {
            body.append("The following payment file was NOT loaded\n\n");
            body.append("Chart: " + header.getChart() + "\n");
            body.append("Organization: " + header.getOrg() + "\n");
            body.append("Sub Unit: " + header.getSubUnit() + "\n");
            body.append("Creation Date: " + header.getCreationDate() + "\n");
        }

        if (trailer != null) {
            body.append("\nPayment Count: " + trailer.getPaymentCount() + "\n");
            body.append("Payment Total Amount: " + trailer.getPaymentTotalAmount() + "\n");
        }

        body.append("\nThe following error messages were generated:\n");
        for (Iterator iter = errors.iterator(); iter.hasNext();) {
            String msg = (String) iter.next();
            body.append(msg + "\n\n");
        }

        message.setMessage(body.toString());
        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address.  Message not sent", e);
        }
    }

    private void sendLoadEmail(DataLoadHandler dataLoadHandler, XmlHeader header, XmlTrailer trailer) {
        LOG.debug("sendLoadEmail() starting");

        Integer batchId = dataLoadHandler.getBatch().getId();
        List errors = dataLoadHandler.getErrorMessages();

        // To send email or not send email
        boolean noEmail = parameterService.getIndicatorParameter(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.NO_PAYMENT_FILE_EMAIL);
        if (noEmail) {
            LOG.debug("sendLoadEmail() sending payment file email is disabled");
            return;
        }

        CustomerProfile customer = null;
        MailMessage message = new MailMessage();

        if (environmentService.isProduction()) {
            message.setSubject("PDP --- Payment file loaded");
        }
        else {
            String env = environmentService.getEnvironment();
            message.setSubject(env + "-PDP --- Payment file loaded");
        }

        StringBuffer body = new StringBuffer();

        String ccAddresses = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.SOFT_EDIT_CC);
        String ccAddressList[] = ccAddresses.split(",");

        // Get customer
        customer = customerProfileService.get(header.getChart(), header.getOrg(), header.getSubUnit());
        String toAddresses = customer.getProcessingEmailAddr();
        String toAddressList[] = toAddresses.split(",");

        if (toAddressList.length > 0) {
            for (int i = 0; i < toAddressList.length; i++) {
                if (toAddressList[i] != null) {
                    message.addToAddress(toAddressList[i].trim());
                }
            }
        }
        // message.addToAddress(customer.getProcessingEmailAddr());

        for (int i = 0; i < ccAddressList.length; i++) {
            if (ccAddressList[i] != null) {
                message.addCcAddress(ccAddressList[i].trim());
            }
        }

        String fromAddressList[] = {mailService.getBatchMailingList()};

        if(fromAddressList.length > 0) {
            for (int i = 0; i < fromAddressList.length; i++) {
                if (fromAddressList[i] != null) {
                    message.setFromAddress(fromAddressList[i].trim());
                }
            }
        }
        
        body.append("The following payment file was loaded\n\n");
        body.append("Batch ID: " + batchId + "\n");
        body.append("Chart: " + header.getChart() + "\n");
        body.append("Organization: " + header.getOrg() + "\n");
        body.append("Sub Unit: " + header.getSubUnit() + "\n");
        body.append("Creation Date: " + header.getCreationDate() + "\n");
        body.append("\nPayment Count: " + trailer.getPaymentCount() + "\n");
        body.append("Payment Total Amount: " + trailer.getPaymentTotalAmount() + "\n");

        body.append("\nThe following warning messages were generated:\n");
        for (Iterator iter = errors.iterator(); iter.hasNext();) {
            String msg = (String) iter.next();
            body.append(msg + "\n\n");
        }

        message.setMessage(body.toString());
        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
        if (dataLoadHandler.getFileThreshold().booleanValue()) {
            sendThresholdEmail("file", dataLoadHandler, customer, header, trailer);
        }
        if (dataLoadHandler.getDetailThreshold().booleanValue()) {
            sendThresholdEmail("detail", dataLoadHandler, customer, header, trailer);
        }
    }

    private void sendThresholdEmail(String type, DataLoadHandler dataLoadHandler, CustomerProfile customer, XmlHeader header, XmlTrailer trailer) {
        MailMessage message = new MailMessage();

        if (environmentService.isProduction()) {
            message.setSubject("PDP --- Payment file loaded with Threshold Warnings");
        }
        else {
            String env = environmentService.getEnvironment();
            message.setSubject(env + "-PDP --- Payment file loaded with Threshold Warnings");
        }

        StringBuffer body = new StringBuffer();

        body.append("The following payment file was loaded\n\n");
        body.append("Batch ID: " + dataLoadHandler.getBatch().getId() + "\n");
        body.append("Chart: " + header.getChart() + "\n");
        body.append("Organization: " + header.getOrg() + "\n");
        body.append("Sub Unit: " + header.getSubUnit() + "\n");
        body.append("Creation Date: " + header.getCreationDate() + "\n");
        body.append("\nPayment Count: " + trailer.getPaymentCount() + "\n");
        body.append("Payment Total Amount: " + trailer.getPaymentTotalAmount() + "\n");

        // Hard Coded - Can only be 'file' or 'detail'
        if ("file".equals(type)) {
            String toAddresses = customer.getFileThresholdEmailAddress();
            String toAddressList[] = toAddresses.split(",");

            if (toAddressList.length > 0) {
                for (int i = 0; i < toAddressList.length; i++) {
                    if (toAddressList[i] != null) {
                        message.addToAddress(toAddressList[i].trim());
                    }
                }
            }
            // message.addToAddress(customer.getFileThresholdEmailAddress());
            body.append("\n" + dataLoadHandler.getFileThresholdMessage());
        }
        else {
            String toAddresses = customer.getPaymentThresholdEmailAddress();
            String toAddressList[] = toAddresses.split(",");

            if (toAddressList.length > 0) {
                for (int i = 0; i < toAddressList.length; i++) {
                    if (toAddressList[i] != null) {
                        message.addToAddress(toAddressList[i].trim());
                    }
                }
            }
            // message.addToAddress(customer.getPaymentThresholdEmailAddress());
            body.append("\nThe Detail Threshold Limit (" + customer.getPaymentThresholdAmount() + ") was exceeded with the following payments:\n\n");
            for (Iterator iter = dataLoadHandler.getDetailThresholdMessages().iterator(); iter.hasNext();) {
                String msg = (String) iter.next();
                body.append(msg + "\n");
            }
        }

        String fromAddressList[] = {mailService.getBatchMailingList()};

        if(fromAddressList.length > 0) {
            for (int i = 0; i < fromAddressList.length; i++) {
                if (fromAddressList[i] != null) {
                    message.setFromAddress(fromAddressList[i].trim());
                }
            }
        }
        
        message.setMessage(body.toString());
        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    private void sendTaxEmail(DataLoadHandler dataLoadHandler, XmlHeader header) {
        LOG.debug("sendTaxEmail() starting");

        Integer batchId = dataLoadHandler.getBatch().getId();

        CustomerProfile customer = null;
        MailMessage message = new MailMessage();

        if (environmentService.isProduction()) {
            message.setSubject("PDP --- Payment file loaded with payment(s) held for Tax");
        }
        else {
            String env = environmentService.getEnvironment();
            message.setSubject(env + "-PDP --- Payment file loaded with payment(s) held for Tax");
        }

        StringBuffer body = new StringBuffer();

        String taxEmail = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.TAX_GROUP_EMAIL_ADDRESS);
        if (GeneralUtilities.isStringEmpty(taxEmail)) {
            LOG.error("No Tax E-mail Application Setting found to send notification e-mail");
            return;
        }
        else {
            message.addToAddress(taxEmail);
        }

        String fromAddressList[] = {mailService.getBatchMailingList()};

        if(fromAddressList.length > 0) {
            for (int i = 0; i < fromAddressList.length; i++) {
                if (fromAddressList[i] != null) {
                    message.setFromAddress(fromAddressList[i].trim());
                }
            }
        }
        
        body.append("The following payment file was loaded with payment(s) held for Tax\n\n");
        body.append("Batch ID: " + batchId + "\n");
        body.append("Chart: " + header.getChart() + "\n");
        body.append("Organization: " + header.getOrg() + "\n");
        body.append("Sub Unit: " + header.getSubUnit() + "\n");
        body.append("Creation Date: " + header.getCreationDate() + "\n");

        body.append("\nPlease go to the PDP system to view the payments.\n");

        message.setMessage(body.toString());
        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * @see org.kuali.module.pdp.service.PaymentFileService#sendLoadEmail(org.kuali.module.pdp.bo.Batch)
     */
    public void sendLoadEmail(Batch batch) {
        LOG.debug("sendLoadEmail() starting");

        CustomerProfile customer = batch.getCustomerProfile();

        // To send email or not send email
        boolean noEmail = parameterService.getIndicatorParameter(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.NO_PAYMENT_FILE_EMAIL);
        if (noEmail) {
            LOG.debug("sendLoadEmail() sending payment file email is disabled");
            return;
        }

        MailMessage message = new MailMessage();

        if (environmentService.isProduction()) {
            message.setSubject("PDP --- Payment file loaded");
        }
        else {
            String env = environmentService.getEnvironment();
            message.setSubject(env + "-PDP --- Payment file loaded");
        }

        StringBuffer body = new StringBuffer();

        String ccAddresses = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.SOFT_EDIT_CC);
        String ccAddressList[] = ccAddresses.split(",");

        // Get customer
        String toAddresses = customer.getProcessingEmailAddr();
        String toAddressList[] = toAddresses.split(",");

        if (toAddressList.length > 0) {
            for (int i = 0; i < toAddressList.length; i++) {
                if (toAddressList[i] != null) {
                    message.addToAddress(toAddressList[i].trim());
                }
            }
        }

        for (int i = 0; i < ccAddressList.length; i++) {
            if (ccAddressList[i] != null) {
                message.addCcAddress(ccAddressList[i].trim());
            }
        }

        String fromAddressList[] = {mailService.getBatchMailingList()};

        if(fromAddressList.length > 0) {
            for (int i = 0; i < fromAddressList.length; i++) {
                if (fromAddressList[i] != null) {
                    message.setFromAddress(fromAddressList[i].trim());
                }
            }
        }
        
        body.append("The following payment file was loaded\n\n");
        body.append("Batch ID: " + batch.getId() + "\n");
        body.append("Chart: " + customer.getChartCode() + "\n");
        body.append("Organization: " + customer.getOrgCode() + "\n");
        body.append("Sub Unit: " + customer.getSubUnitCode() + "\n");
        body.append("Creation Date: " + batch.getCustomerFileCreateTimestamp() + "\n");
        body.append("\nPayment Count: " + batch.getPaymentCount() + "\n");
        body.append("Payment Total Amount: " + batch.getPaymentTotalAmount() + "\n");

        message.setMessage(body.toString());
        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setPaymentFileLoadDao(PaymentFileLoadDao pfld) {
        paymentFileLoadDao = pfld;
    }

    public void setEnvironmentService(EnvironmentService es) {
        environmentService = es;
    }

    public void setCustomerProfileService(CustomerProfileService cps) {
        customerProfileService = cps;
    }

    public void setMailService(MailService ms) {
        mailService = ms;
    }

    public void setUniversalUserService(UniversalUserService uus) {
        universalUserService = uus;
    }

    public void setIncomingDirectoryName(String idn) {
        incomingDirectoryName = idn;
    }

    public void setOutgoingDirectoryName(String odn) {
        outgoingDirectoryName = odn;
    }
}
