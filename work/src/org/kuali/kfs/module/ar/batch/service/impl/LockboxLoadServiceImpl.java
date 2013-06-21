package org.kuali.kfs.module.ar.batch.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.LockboxLoadStep;
import org.kuali.kfs.module.ar.batch.service.LockboxLoadService;
import org.kuali.kfs.module.ar.businessobject.Lockbox;
import org.kuali.kfs.module.ar.businessobject.LockboxDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.FlatFileInformation;
import org.kuali.kfs.sys.batch.FlatFileTransactionInformation;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.exception.InvalidAddressException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.MailService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 *
 * @author mramawat
 * @see org.kuali.kfs.module.ar.batch.service.LockboxLoadService
 * FSKD-688 iu-custom
 */
public class LockboxLoadServiceImpl implements LockboxLoadService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LockboxLoadServiceImpl.class);

    private BatchInputFileType batchInputFileType;
    private String reportsDirectory;
    private BatchInputFileService batchInputFileService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private MailService mailService;
    private ParameterService parameterService;



    @Override
    public boolean loadFile() {

        boolean resultInd = true;
        //List<LockboxLoadFileResult> fileResults = new ArrayList<LockboxLoadFileResult>();
        List<FlatFileInformation> flatFileInformationList = new ArrayList<FlatFileInformation>();
        //LockboxLoadFileResult fileResult = null;
        FlatFileInformation flatFileInformation = null;

        List<String> fileNamesToLoad = getListOfFilesToProcess();
        LOG.info("Found " + fileNamesToLoad.size() + " file(s) to process.");

        List<String> processedFiles = new ArrayList<String>();

        for (String inputFileName : fileNamesToLoad) {

            LOG.info("Beginning processing of filename: " + inputFileName + ".");
            flatFileInformation = new FlatFileInformation(inputFileName);
            flatFileInformationList.add(flatFileInformation);

            if (loadFile(inputFileName, flatFileInformation)) {
                processedFiles.add(inputFileName);
                flatFileInformation.addFileInfoMessage("File successfully completed processing.");
            }
            else {
                flatFileInformation.addFileErrorMessage("File failed to process successfully.");

            }

        }

        //  remove done files
        removeDoneFiles(processedFiles);

        // SendEmail
        sendLoadSummaryEmail(flatFileInformationList);

        return resultInd ;
    }

    public boolean loadFile(String fileName,FlatFileInformation flatFileInformation) {
        boolean isValid = true;
        //  load up the file into a byte array
        byte[] fileByteContent = safelyLoadFileBytes(fileName);

        //  parse the file against the configuration define in the spring file and load it into an object
        LOG.info("Attempting to parse the file ");
        Object parsedObject = null;

        try {
            parsedObject = batchInputFileService.parse(batchInputFileType, fileByteContent);
        }
        catch (ParseException e) {
            LOG.error("Error parsing batch file: " + e.getMessage());
            flatFileInformation.addFileErrorMessage("Error parsing batch file: " + e.getMessage());
            isValid = false;
            //    throw new ParseException(e.getMessage());
        }

        // validate the parsed data
        if (parsedObject != null ) {
            isValid = validate(parsedObject);
            copyAllMessage(parsedObject,flatFileInformation);
            if (isValid) {
                loadLockbox(parsedObject);
            }
        }
        return isValid ;

    }


    @Override
    public boolean validate(Object parsedFileContents) {
        // compare header with detail record
        boolean isValid = true;
        List<Lockbox> lockboxList = (List<Lockbox>)parsedFileContents;
        for (Lockbox lockbox : lockboxList) {
            if (! compareDetailsWithHeader(lockbox)) {
                isValid = false;
                break;
            }
        }

        return isValid;
    }



    /**
     * No processing
     */
    @Override
    public void process(String fileName, Object parsedFileContents) {}

    private boolean compareDetailsWithHeader(Lockbox lockbox) {
        boolean isHeaderMatchedDetails = true;
        KualiDecimal  headerTransBatchTotal = lockbox.getHeaderTransactionBatchTotal();
        Integer headerTransBatchCount   = lockbox.getHeaderTransactionBatchCount();
        KualiDecimal detailInvPaidTotal = new KualiDecimal(0);
        Integer totalDetailRecords = 0;
        for (LockboxDetail detail : lockbox.getLockboxDetails() ) {
            detailInvPaidTotal = detailInvPaidTotal.add(detail.getInvoicePaidOrAppliedAmount());
            totalDetailRecords++;
        }



        if (headerTransBatchTotal.compareTo(detailInvPaidTotal)== 0
                && headerTransBatchCount.compareTo(totalDetailRecords) == 0) {

            String message = "Good Transfer for lockbox number "  + lockbox.getLockboxNumber() + "."
            + " Transaction count : " + lockbox.getHeaderTransactionBatchCount()
            + " Transaction total amount : $ " + lockbox.getHeaderTransactionBatchTotal();
            lockbox.getFlatFileTransactionInformation().addInfoMessage(message);
            GlobalVariables.getMessageMap().putInfo(KFSConstants.GLOBAL_ERRORS,KFSKeyConstants.ERROR_CUSTOM, message);

        }


        if (headerTransBatchTotal.compareTo(detailInvPaidTotal)!= 0) {
            String message = "Bad Transmmission for lock box number " + lockbox.getLockboxNumber() + "."
            + " Detail does not match header control values "
            + " Header total : $ " + lockbox.getHeaderTransactionBatchTotal()
            + " Detail total : $ " + detailInvPaidTotal;
            lockbox.getFlatFileTransactionInformation().addErrorMessage(message);
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS,KFSKeyConstants.ERROR_CUSTOM, message);
            isHeaderMatchedDetails = false;
        }

        if (headerTransBatchCount.compareTo(totalDetailRecords) != 0 ) {
            String message = "Bad Transmmission for lock box number " + lockbox.getLockboxNumber() + "."
            + " Detail does not match header control values "
            + " Header Count : " + lockbox.getHeaderTransactionBatchCount()
            + " Detail total : " + totalDetailRecords;
            lockbox.getFlatFileTransactionInformation().addErrorMessage(message);
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS,KFSKeyConstants.ERROR_CUSTOM, message);
            isHeaderMatchedDetails  = false;
        }


        return isHeaderMatchedDetails;

    }


    /**
     * Send load lockbox file validation
     *
     * @param report
     */
    public void sendLoadSummaryEmail(List<FlatFileInformation> flatFileInformationList) {
        for(FlatFileInformation information : flatFileInformationList) {
            sendEmail(information);
        }
    }


    public void sendEmail(FlatFileInformation flatFileInformation) {
        LOG.debug("sendEmail() starting");


        MailMessage message = new MailMessage();

       // String returnAddress = parameterService.getParameterValueAsString(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE, ParameterConstants.BATCH_COMPONENT, "IU_FROM_EMAIL_ADDRESS");
       // if(StringUtils.isEmpty(returnAddress)) {
        String returnAddress = mailService.getBatchMailingList();
       // }
        message.setFromAddress(returnAddress);
        String subject = parameterService.getParameterValueAsString(LockboxLoadStep.class, ArConstants.Lockbox.SUMMARY_AND_ERROR_NOTIFICATION_EMAIL_SUBJECT);
        String fileName = StringUtils.substringAfterLast(flatFileInformation.getFileName(), "\\");
        message.setSubject(subject + "[ " + fileName + " ]");
        List<String> toAddressList = new ArrayList<String>( parameterService.getParameterValuesAsString(LockboxLoadStep.class, ArConstants.Lockbox.SUMMARY_AND_ERROR_NOTIFICATION_TO_EMAIL_ADDRESSES) );
        message.getToAddresses().addAll(toAddressList);
        String body = composeLockboxLoadBody(flatFileInformation);
        message.setMessage(body);

        try {
            mailService.sendMessage(message);
        }
        catch (InvalidAddressException e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
        catch (MessagingException me) {
            throw new RuntimeException("Could not send mail", me);
        }

    }

    private String composeLockboxLoadBody( FlatFileInformation flatFileInformation) {

        String contactText = parameterService.getParameterValueAsString(LockboxLoadStep.class, ArConstants.Lockbox.CONTACTS_TEXT);
        StringBuffer body = new StringBuffer();
        body.append(contactText);
        body.append("\n");

        for(Object object  : flatFileInformation.getFlatFileIdentifierToTransactionInfomationMap().values()) {
            for (String[] message : ((FlatFileTransactionInformation)object).getMessages()) {
                body.append(message[1]);
                body.append("\n");
            }
        }


        for(String[] resultMessage : flatFileInformation.getMessages()) {
            body.append(resultMessage[1]);
            body.append("\n");
        }

        return body.toString();
    }




    protected List<String> getListOfFilesToProcess() {

        //  create a list of the files to process
        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(batchInputFileType);

        if (fileNamesToLoad == null) {
            LOG.error("BatchInputFileService.listInputFileNamesWithDoneFile(" +
                    batchInputFileType.getFileTypeIdentifer() + ") returned NULL which should never happen.");
            throw new RuntimeException("BatchInputFileService.listInputFileNamesWithDoneFile(" +
                    batchInputFileType.getFileTypeIdentifer() + ") returned NULL which should never happen.");
        }

        //  filenames returned should never be blank/empty/null
        for (String inputFileName : fileNamesToLoad) {
            if (StringUtils.isBlank(inputFileName)) {
                LOG.error("One of the file names returned as ready to process [" + inputFileName +
                "] was blank.  This should not happen, so throwing an error to investigate.");
                throw new RuntimeException("One of the file names returned as ready to process [" + inputFileName +
                "] was blank.  This should not happen, so throwing an error to investigate.");
            }
        }

        return fileNamesToLoad;
    }

    /**
     *
     * Accepts a file name and returns a byte-array of the file name contents, if possible.
     *
     * Throws RuntimeExceptions if FileNotFound or IOExceptions occur.
     *
     * @param fileName String containing valid path & filename (relative or absolute) of file to load.
     * @return A Byte Array of the contents of the file.
     */
    protected byte[] safelyLoadFileBytes(String fileName) {

        InputStream fileContents;
        byte[] fileByteContent;
        try {
            fileContents = new FileInputStream(fileName);
        }
        catch (FileNotFoundException e1) {
            LOG.error("Batch file not found [" + fileName + "]. " + e1.getMessage());
            throw new RuntimeException("Batch File not found [" + fileName + "]. " + e1.getMessage());
        }
        try {
            fileByteContent = IOUtils.toByteArray(fileContents);
        }
        catch (IOException e1) {
            LOG.error("IO Exception loading: [" + fileName + "]. " + e1.getMessage());
            throw new RuntimeException("IO Exception loading: [" + fileName + "]. " + e1.getMessage());
        }
        return fileByteContent;
    }

    private void loadLockbox(Object parsedObject) {
        // create the lockbox object to load data
        List loadLockboxList = new ArrayList<Lockbox>();
        List<Lockbox> lockboxList = (List<Lockbox>)parsedObject;
        int batchSequenceNumber = 1;
        for (Lockbox lockbox : lockboxList) {
            setLockboxToLoad(loadLockboxList, lockbox, batchSequenceNumber);
            batchSequenceNumber ++;
        }

        // save lockbox data in AR_LOCKBOX_T
        businessObjectService.save(loadLockboxList);


    }


    /**
     * Clears out associated .done files for the processed data files.
     */
    protected void removeDoneFiles(List<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }


    private void setLockboxToLoad(List loadLockboxList ,Lockbox lockbox, int batchSequenceNumber ) {


        for (LockboxDetail detail : lockbox.getLockboxDetails()) {
            Lockbox lockboxToLoad = new Lockbox();
            lockboxToLoad.setLockboxNumber(lockbox.getLockboxNumber());
            lockboxToLoad.setScannedInvoiceDate(lockbox.getScannedInvoiceDate());
            lockboxToLoad.setCustomerNumber(detail.getCustomerNumber());
            lockboxToLoad.setBatchSequenceNumber(batchSequenceNumber);
            lockboxToLoad.setProcessedInvoiceDate(dateTimeService.getCurrentSqlDate());
            lockboxToLoad.setFinancialDocumentReferenceInvoiceNumber(detail.getFinancialDocumentReferenceInvoiceNumber());
            lockboxToLoad.setBillingDate(detail.getBillingDate());
            lockboxToLoad.setInvoiceTotalAmount(detail.getInvoiceTotalAmount());
            lockboxToLoad.setInvoicePaidOrAppliedAmount(detail.getInvoicePaidOrAppliedAmount());
            lockboxToLoad.setCustomerPaymentMediumCode(detail.getCustomerPaymentMediumCode());
            loadLockboxList.add(lockboxToLoad);
        }


    }

    private void copyAllMessage(Object parsedObject, FlatFileInformation flatFileInformation) {
        List<Lockbox> lockboxList = (List<Lockbox>)parsedObject;
        for (Lockbox lockbox : lockboxList) {
            FlatFileTransactionInformation information = lockbox.getFlatFileTransactionInformation();
            flatFileInformation.getOrAddFlatFileData(lockbox.getLockboxNumber(), information);
        }
    }

    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifier) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setBatchInputFileType(BatchInputFileType batchInputFileType) {
        this.batchInputFileType = batchInputFileType;
    }

    public void setReportsDirectory(String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }


    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


}
