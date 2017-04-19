package edu.arizona.kfs.pdp.batch.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.DisbursementType;
import org.kuali.kfs.pdp.businessobject.LoadPaymentStatus;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.pdp.service.PaymentFileValidationService;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.pdp.service.impl.PaymentFileServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import au.com.bytecode.opencsv.CSVWriter;
import edu.arizona.kfs.pdp.PdpConstants;
import edu.arizona.kfs.pdp.batch.service.PrepaidChecksService;
import edu.arizona.kfs.pdp.service.PdpEmailService;

@Transactional
public class PrepaidChecksServiceImpl extends PaymentFileServiceImpl implements PrepaidChecksService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PrepaidChecksServiceImpl.class);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
    private ParameterService parameterService;
    private CustomerProfileService customerProfileService;
    private BatchInputFileService batchInputFileService;
    private PaymentFileValidationService paymentFileValidationService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private PdpEmailService pdpEmailService;
    private ConfigurationService kualiConfigurationService;    
    private PendingTransactionService glPendingTransactionService;
    private String outgoingDirectoryName;
    private String payeeFileNamePrefix;

    public PrepaidChecksServiceImpl() {
        super();
        super.setParameterService(parameterService);
        super.setCustomerProfileService(customerProfileService);
        super.setBatchInputFileService(batchInputFileService);
        super.setPaymentFileValidationService(paymentFileValidationService);
        super.setBusinessObjectService(businessObjectService);
        super.setDateTimeService(dateTimeService);
        super.setConfigurationService(kualiConfigurationService);
        super.setOutgoingDirectoryName(outgoingDirectoryName);        
    }
    
    public boolean processPrepaidChecks(BatchInputFileType prepaidChecksInputFileType) {        
        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(prepaidChecksInputFileType);

        boolean succeeded = true;
        for (String incomingFileName : fileNamesToLoad) {
            try {
            	if (LOG.isDebugEnabled()) {
            		LOG.debug("processPrepaidChecks() Processing " + incomingFileName);
            	}
                super.removeDoneFile(incomingFileName);
              
                // collect various information for status of load
                LoadPaymentStatus status = new LoadPaymentStatus();
                status.setMessageMap(new MessageMap());

                // process payment file
                PaymentFileLoad prepaidChecksFile = processPaymentFile(prepaidChecksInputFileType, incomingFileName, status.getMessageMap());
                if (prepaidChecksFile != null && prepaidChecksFile.isPassedValidation()) {
                    // load payment data
                	loadPayments(prepaidChecksFile, status, incomingFileName);
                    super.createOutputFile(status, incomingFileName);
                    writeAuditReport(prepaidChecksFile, status, incomingFileName);
                    writePayeeDetailLog(prepaidChecksFile, incomingFileName);                    
                } 
                else { 
                	succeeded = false;
                }                               
            }            
            catch (RuntimeException e) {
                LOG.error("Caught exception trying to load payment file: " + incomingFileName, e);
                // swallow exception so we can continue processing files, the errors have been reported by email
                succeeded = false;
            }
        }       
        return succeeded;
    }
    
    @Override
    protected PaymentFileLoad processPaymentFile(BatchInputFileType paymentInputFileType, String incomingFileName, MessageMap errorMap) {
        // parse xml, if errors found return with failure
        PaymentFileLoad paymentFile = parsePaymentFile(paymentInputFileType, incomingFileName, errorMap);

        // if no parsing error, do further validation
        if (errorMap.hasNoErrors()) {
            super.doPaymentFileValidation(paymentFile, errorMap);
        }
        
        // if any error from parsing or post-parsing validation, send error email notice
        if (errorMap.hasErrors()) {
        	pdpEmailService.sendPrepaidChecksErrorEmail(paymentFile, errorMap, incomingFileName);
        }

        return paymentFile;
    }
    
    @Override
    public void loadPayments(PaymentFileLoad prepaidChecksFile, LoadPaymentStatus status, String incomingFileName) {   
        status.setChart(prepaidChecksFile.getChart());
        status.setUnit(prepaidChecksFile.getUnit());
        status.setSubUnit(prepaidChecksFile.getSubUnit());
        status.setCreationDate(prepaidChecksFile.getCreationDate());
        status.setDetailCount(prepaidChecksFile.getActualPaymentCount());
        status.setDetailTotal(prepaidChecksFile.getCalculatedPaymentTotalAmount());

        // create batch record for payment load
        Batch batch = super.createNewBatch(prepaidChecksFile, super.getBaseFileName(incomingFileName));
        businessObjectService.save(batch);

        prepaidChecksFile.setBatchId(batch.getId());
        status.setBatchId(batch.getId());
        
        // do warnings and set defaults
        List<String> warnings = paymentFileValidationService.doSoftEdits(prepaidChecksFile);
        status.setWarnings(warnings);
        
        // set needsTaxEmail to FALSE for all 
        prepaidChecksFile.setTaxEmailRequired(false);
        
        // retrieve PaymentStatus BO for EXTR
        PaymentStatus paymentStatus = (PaymentStatus) businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.EXTRACTED);
        
        // retrieve DisbursementType BO for CHCK
        DisbursementType disbursementType = businessObjectService.findBySinglePrimaryKey(DisbursementType.class, PdpConstants.DisbursementTypeCodes.CHECK);
                
        // store groups
        for (PaymentGroup paymentGroup : prepaidChecksFile.getPaymentGroups()) {
            // wire the batch in
            paymentGroup.setBatchId(batch.getId());
            paymentGroup.setBatch(batch);
            
            // refresh bank
            paymentGroup.refreshReferenceObject(PdpPropertyConstants.CUSTOMER_BANK);
            
            // set paymentStatusCode to EXTR for all groups
            paymentGroup.setPaymentStatusCode(PdpConstants.PaymentStatusCodes.EXTRACTED);            
            paymentGroup.setPaymentStatus(paymentStatus);    
                       
            // force the disbursement type to CHCK
            paymentGroup.setDisbursementTypeCode(PdpConstants.DisbursementTypeCodes.CHECK);
            paymentGroup.setDisbursementType(disbursementType);
            if (ObjectUtils.isNull(paymentGroup.getDisbursementType())) {
                throw new RuntimeException("The paymentGroup's disbursementTypeCode ["+paymentGroup.getDisbursementTypeCode()+"] doesn't map " + 
                        "to any DisbursementType object in the database.  This will cause the glPending Entries to be generated incorrectly, so " + 
                        "we fail here.");
            }
            
            businessObjectService.save(paymentGroup);

            // Generate GL entries for the payments
            glPendingTransactionService.generatePaymentGeneralLedgerPendingEntry(paymentGroup);
        }

        // send list of warnings
        pdpEmailService.sendPrepaidChecksLoadEmail(prepaidChecksFile, warnings, incomingFileName);

        LOG.debug("loadPrepaidChecks() was successful");
        status.setLoadStatus(LoadPaymentStatus.LoadStatus.SUCCESS);
    }
        
    @Override
    protected PaymentFileLoad parsePaymentFile(BatchInputFileType paymentInputFileType, String incomingFileName, MessageMap errorMap) {
    	PaymentFileLoad paymentFile = null;
    	FileInputStream fileContents;
	    try {
	        fileContents = new FileInputStream(incomingFileName);
	    }
	    catch (FileNotFoundException e1) {
	    	pdpEmailService.sendPrepaidChecksErrorEmail(paymentFile, errorMap, incomingFileName);
	        LOG.error("file to load not found " + incomingFileName, e1);
	        throw new RuntimeException("Cannot find the file requested to be loaded " + incomingFileName, e1);
	    }
	    
	    // do the parse
        try {
            byte[] fileByteContent = IOUtils.toByteArray(fileContents);
            paymentFile = (PaymentFileLoad) batchInputFileService.parse(paymentInputFileType, fileByteContent);
        }
        catch (IOException e) {
        	pdpEmailService.sendPrepaidChecksErrorEmail(paymentFile, errorMap, incomingFileName);
            LOG.error("error while getting file bytes:  " + e.getMessage(), e);
            throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
        }
        catch (ParseException e1) {
            LOG.error("Error parsing xml " + e1.getMessage());
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_PARSING_XML, new String[] { e1.getMessage() });
            pdpEmailService.sendPrepaidChecksErrorEmail(paymentFile, errorMap, incomingFileName);
            throw new RuntimeException("Error parsing input file [" + incomingFileName + "].", e1);
        }

        return paymentFile;       
    }
    
    protected void writeAuditReport(PaymentFileLoad prepaidChecksFile, LoadPaymentStatus status, String incomingFileName) {
    	String dateTime = dateTimeService.toDateTimeString(dateTimeService.getCurrentDate());
        String reportFile = outgoingDirectoryName + PdpConstants.PrePaidChecksConstants.SLASH + getBaseFileName(incomingFileName) + PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_FILE_EXTENSION;
        
        try {
            FileOutputStream out = new FileOutputStream(reportFile);
            PrintStream p = new PrintStream(out);          
            p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_HEADING + dateTime);
            p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_SEPARATOR);
            p.println("");
            p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_SUB_HEADING + incomingFileName);
            p.println("");
            
            //  errors
            List<ErrorMessage> errorMessages = status.getMessageMap().getMessages(KFSConstants.GLOBAL_ERRORS);
            if (errorMessages != null && !errorMessages.isEmpty()) {                      
                p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_SUB_HEADING_ERRORS);
                p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_SEPARATOR_SINGLE);
                for (ErrorMessage errorMessage : errorMessages) {
                	String resourceMessage = kualiConfigurationService.getPropertyValueAsString(errorMessage.getErrorKey());
                    resourceMessage = MessageFormat.format(resourceMessage, (Object[]) errorMessage.getMessageParameters());
                    p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_ASTERISK + resourceMessage);                    
                }                
                p.println("");               
            }
            
            //  warnings
            List<String> warnings = status.getWarnings();
            if (warnings != null && !warnings.isEmpty()) {
                p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_SUB_HEADING_WARNINGS);
                p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_SEPARATOR_SINGLE);
                for (String warning : warnings) {
                    p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_ASTERISK + warning);                    
                }
                p.println("");
            }
            
            //  checks
            p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_SUB_HEADING_CHECKS);
            p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_SEPARATOR_CHECKS);            
            p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_TABLE_HEADING_CHECKS);
            p.println(PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_SEPARATOR_CHECKS);
            p.println("");
            
            List<PaymentGroup> paymentGroups = prepaidChecksFile.getPaymentGroups();
            for (PaymentGroup paymentGroup : paymentGroups) {
                p.print(StringUtils.rightPad(paymentGroup.getDisbursementNbr().toString(),                      16, " ") + PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_BAR);
                p.print(StringUtils.rightPad(paymentGroup.getDisbursementDate().toString(),                     13, " ") + PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_BAR);
                p.print(StringUtils.rightPad(paymentGroup.getPayeeIdTypeCd() + "-" + paymentGroup.getPayeeId(), 11, " ") + PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_BAR);
                p.print(StringUtils.rightPad(paymentGroup.getPayeeName(),                                       26, " ") + PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_BAR);
                p.print(StringUtils.leftPad(paymentGroup.getNetPaymentAmount().toString(),                      13, " ") + PdpConstants.PrePaidChecksConstants.AUDIT_REPORT_BAR);
                p.print(StringUtils.rightPad(paymentGroup.getPaymentStatusCode(),                                5, " "));
                p.println();
            }
            
            p.close();
            out.close();
        }
        catch (FileNotFoundException e) {
            LOG.error("writeAuditReport() Cannot create output file", e);
            throw new RuntimeException("writeAuditReport() Cannot create output file", e);
        }
        catch (IOException e) {
            LOG.error("writeAuditReport() Cannot write to output file", e);
            throw new RuntimeException("writeAuditReport() Cannot write to output file", e);
        }
    }
       
    /**
     * 
     * writes out a file of payee names and email addresses for successful payments
     * @param prepaidChecksFile
     * @param inputFileName
     */
    private void writePayeeDetailLog(PaymentFileLoad prepaidChecksFile, String inputFileName) {
        CSVWriter csvw = null;
        try {
            Map <KualiInteger, KualiDecimal> disbTotalMap = new HashMap<KualiInteger, KualiDecimal>();
            
            for(PaymentGroup pg : prepaidChecksFile.getPaymentGroups()) {
                if (pg.getDisbursementNbr() != null) {
                    for (PaymentDetail pd : pg.getPaymentDetails()) {
                        if (pd.getNetPaymentAmount() != null) {
                            KualiDecimal tot = disbTotalMap.get(pg.getDisbursementNbr());

                            if (tot == null) {
                                disbTotalMap.put(pg.getDisbursementNbr(), new KualiDecimal(pd.getNetPaymentAmount().bigDecimalValue()));
                            } else {
                                disbTotalMap.put(pg.getDisbursementNbr(), tot.add(pd.getNetPaymentAmount()));
                            }
                        }
                    }
                }
            }
           
            csvw = new CSVWriter(new FileWriter(getPayeeDetailLogFileName(inputFileName)));

            csvw.writeNext(new String[] {
            	PdpConstants.PrePaidChecksConstants.PAYEE_DETAIL_TABLE_HEADING_0,
            	PdpConstants.PrePaidChecksConstants.PAYEE_DETAIL_TABLE_HEADING_1,
            	PdpConstants.PrePaidChecksConstants.PAYEE_DETAIL_TABLE_HEADING_2,
            	PdpConstants.PrePaidChecksConstants.PAYEE_DETAIL_TABLE_HEADING_3,
            	PdpConstants.PrePaidChecksConstants.PAYEE_DETAIL_TABLE_HEADING_4,
            	PdpConstants.PrePaidChecksConstants.PAYEE_DETAIL_TABLE_HEADING_5,
            	PdpConstants.PrePaidChecksConstants.PAYEE_DETAIL_TABLE_HEADING_6,
            	PdpConstants.PrePaidChecksConstants.PAYEE_DETAIL_TABLE_HEADING_7,
            	PdpConstants.PrePaidChecksConstants.PAYEE_DETAIL_TABLE_HEADING_8,
            	PdpConstants.PrePaidChecksConstants.PAYEE_DETAIL_TABLE_HEADING_9,
            	PdpConstants.PrePaidChecksConstants.PAYEE_DETAIL_TABLE_HEADING_10,
            	PdpConstants.PrePaidChecksConstants.PAYEE_DETAIL_TABLE_HEADING_11
            });
            
            for(PaymentGroup pg : prepaidChecksFile.getPaymentGroups()) {
                for (PaymentDetail pd : pg.getPaymentDetails()) {
                    csvw.writeNext(getPayeeDetailLine(pg, pd, disbTotalMap));
                }
            }

        }
        
        catch (IOException e) {
            LOG.error("writePayeeDetailLog() Cannot write to output file", e);
            throw new RuntimeException("writePayeeDetailLog() Cannot write to output file", e);
        }
        
        finally {
            if (csvw != null) {
                try {
                    csvw.close();
                }
                
                catch (Exception ex) {};
            }
        }
        
    }
    
    /**
     * build filename for output payee details
     * @param inputFileName
     * @return
     */
    private String getPayeeDetailLogFileName(String inputFileName) {
        StringBuilder retval = new StringBuilder(128);
        
        retval.append(outgoingDirectoryName);
        retval.append(PdpConstants.PrePaidChecksConstants.SLASH);
        retval.append(payeeFileNamePrefix);
        retval.append(PdpConstants.PrePaidChecksConstants.UNDERSCORE);
        
        String basename = getBaseFileName(inputFileName);
        
        int pos = basename.lastIndexOf(KFSConstants.DELIMITER);
        
        if (pos > -1) {
            retval.append(basename.subSequence(0, pos));
        } else {
            retval.append(basename);
        }

        retval.append(KFSConstants.ReportGeneration.CSV_FILE_EXTENSION);
        
        return retval.toString();
    }
    
    private String[] getPayeeDetailLine(PaymentGroup pg, PaymentDetail pd, Map <KualiInteger, KualiDecimal> disbTotalMap) {
        String[] retval = new String[12];

        retval[0] = pd.getCustPaymentDocNbr();
        
        if (pd.getId() != null) {
            retval[1] = pd.getId().toString();
        }
        
        retval[2] = pd.getInvoiceNbr();
        retval[3] = pg.getPayeeName();

        if (PdpConstants.PayeeIdTypeCodes.VENDOR_ID.equals(pg.getPayeeIdTypeCd())) {
            retval[4] = pg.getPayeeId();
        }
        
        if (pg.getPaymentDate() != null) {
            retval[5] = dateFormat.format(pg.getPaymentDate());
        }
        
        if (pg.getDisbursementDate() != null) {
            retval[6] = dateFormat.format(pg.getDisbursementDate());
        }
        
        if (pg.getPaymentStatus() != null) {
            retval[7] = pg.getPaymentStatus().getName();
        }
        
        if (pg.getDisbursementType() != null) {
            retval[8] = pg.getDisbursementType().getName();
        }
        
        if (pg.getDisbursementNbr() != null) {
            retval[9] = pg.getDisbursementNbr().toString();
            
            KualiDecimal tot = disbTotalMap.get(pg.getDisbursementNbr());
            if (tot != null) {
                retval[10] = tot.toString();
            }
        }
        
        if (pd.getNetPaymentAmount() != null) {
            retval[11] = pd.getNetPaymentAmount().toString();
        }
        
        
        return retval;
    }
              
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
        super.setParameterService(parameterService);
    }

    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
        super.setCustomerProfileService(customerProfileService);
    }
    
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
        super.setBatchInputFileService(batchInputFileService);
    }

    public void setPaymentFileValidationService(PaymentFileValidationService paymentFileValidationService) {
        this.paymentFileValidationService = paymentFileValidationService;
        super.setPaymentFileValidationService(paymentFileValidationService);
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
        super.setBusinessObjectService(businessObjectService);
    }
    
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
        super.setDateTimeService(dateTimeService);
    }
    
    public void setPdpEmailService(PdpEmailService pdpEmailService) {
        this.pdpEmailService = pdpEmailService;
        super.setPaymentFileEmailService(pdpEmailService);
    }
    
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
        super.setConfigurationService(kualiConfigurationService);
    }
    
    public void setGlPendingTransactionService(PendingTransactionService glPendingTransactionService) {
        this.glPendingTransactionService = glPendingTransactionService;
    }
      
    public void setOutgoingDirectoryName(String outgoingDirectoryName) {
        this.outgoingDirectoryName = outgoingDirectoryName;
        super.setOutgoingDirectoryName(outgoingDirectoryName);
    }
     
    public void setPayeeFileNamePrefix(String payeeFileNamePrefix) {
        this.payeeFileNamePrefix = payeeFileNamePrefix;
    }
    
    /**
     * @see org.kuali.kfs.sys.batch.service.impl.InitiateDirectoryImpl#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
        return new ArrayList<String>() {{add(outgoingDirectoryName); }};
    }
    
}
