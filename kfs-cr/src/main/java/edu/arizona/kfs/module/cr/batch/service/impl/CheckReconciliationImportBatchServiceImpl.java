package edu.arizona.kfs.module.cr.batch.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.gl.GeneralLedgerConstants;
import edu.arizona.kfs.module.cr.CrConstants;
import edu.arizona.kfs.module.cr.CrParameterConstants;
import edu.arizona.kfs.module.cr.batch.service.CheckReconciliationImportBatchService;
import edu.arizona.kfs.module.cr.businessobject.CheckReconError;
import edu.arizona.kfs.module.cr.businessobject.CheckReconciliation;
import edu.arizona.kfs.module.cr.comparator.CheckReconciliationChronologicalComparator;
import edu.arizona.kfs.module.cr.dataaccess.CheckReconciliationDao;
import edu.arizona.kfs.module.cr.exception.BankNotFoundException;
import edu.arizona.kfs.module.cr.filefilter.DoneFilenameFilter;
import edu.arizona.kfs.sys.KFSConstants;

public class CheckReconciliationImportBatchServiceImpl implements CheckReconciliationImportBatchService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CheckReconciliationImportBatchService.class);

    // Injected Objects

    private CheckReconciliationDao checkReconciliationDao;
    private ParameterService parameterService;
    private String reportsDirectory;
    private String stagingDirectory;

    // Service Objects

    private Map<String, String> statusMap;
    List<CheckReconError> checkReconErrorRecords;
    private Collection<Bank> banks;

    // Spring Injectors

    public void setCheckReconciliationDao(CheckReconciliationDao checkReconciliationDao) {
        this.checkReconciliationDao = checkReconciliationDao;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setReportsDirectory(String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    public void setStagingDirectory(String stagingDirectory) {
        this.stagingDirectory = stagingDirectory;
    }

    // Implemented Methods

    @Override
    public boolean initializeServiceObjects() {
        checkReconErrorRecords = new ArrayList<CheckReconError>();

        Map<String, String> clrdMap = getStatusMapFromParameter(CrParameterConstants.CheckReconciliationImportStep.CLRD_STATUS, CrConstants.CheckReconciliationStatusCodes.CLEARED);
        Map<String, String> cnclMap = getStatusMapFromParameter(CrParameterConstants.CheckReconciliationImportStep.CNCL_STATUS, CrConstants.CheckReconciliationStatusCodes.CANCELLED);
        Map<String, String> issdMap = getStatusMapFromParameter(CrParameterConstants.CheckReconciliationImportStep.ISSD_STATUS, CrConstants.CheckReconciliationStatusCodes.ISSUED);
        Map<String, String> stalMap = getStatusMapFromParameter(CrParameterConstants.CheckReconciliationImportStep.STAL_STATUS, CrConstants.CheckReconciliationStatusCodes.STALE);
        Map<String, String> stopMap = getStatusMapFromParameter(CrParameterConstants.CheckReconciliationImportStep.STOP_STATUS, CrConstants.CheckReconciliationStatusCodes.STOP);
        Map<String, String> voidMap = getStatusMapFromParameter(CrParameterConstants.CheckReconciliationImportStep.VOID_STATUS, CrConstants.CheckReconciliationStatusCodes.VOIDED);

        statusMap = new HashMap<String, String>();
        statusMap.putAll(clrdMap);
        statusMap.putAll(cnclMap);
        statusMap.putAll(issdMap);
        statusMap.putAll(stalMap);
        statusMap.putAll(stopMap);
        statusMap.putAll(voidMap);

        banks = checkReconciliationDao.getAllBanks();

        return true;
    }

    @Override
    public boolean importPdpPayments() {
        Collection<CheckReconciliation> checkReconciliations = getNewCheckReconciliations();

        for (CheckReconciliation checkReconciliation : checkReconciliations) {
            List<CheckReconciliation> bankFileCheckReconciliations = checkReconciliationDao.getBankFileCreatedCheckReconciliations(checkReconciliation.getCheckNumber(), checkReconciliation.getAmount(), checkReconciliation.getBankAccountNumber());
            if (bankFileCheckReconciliations.size() == 1) {
                Date checkDate = checkReconciliation.getCheckDate();
                String payeeId = checkReconciliation.getPayeeId();
                String payeeTypeCode = checkReconciliation.getPayeeTypeCode();

                checkReconciliation = bankFileCheckReconciliations.get(0);
                checkReconciliation.setCheckDate(checkDate);
                checkReconciliation.setClearedDate(checkDate);
                checkReconciliation.setSourceCode(CrConstants.CheckReconciliationSourceCodes.PDP_SRC);
                checkReconciliation.setStatus(CrConstants.CheckReconciliationStatusCodes.CLEARED);
                checkReconciliation.setPayeeId(payeeId);
                checkReconciliation.setPayeeTypeCode(payeeTypeCode);
            } else if (bankFileCheckReconciliations.size() > 1) {
                LOG.warn("Multiple Bank File Check Reconciliations found for check " + checkReconciliation.getCheckNumber() + ", bank: " + checkReconciliation.getBankAccountNumber() + ", amount: " + checkReconciliation.getAmount());
            }
            checkReconciliationDao.save(checkReconciliation);
        }

        LOG.info("Found (" + checkReconciliations.size() + ") New PDP payments.");
        return true;
    }

    @Override
    public boolean processDataFiles() throws Exception {
        boolean success = true;
        List<File> files = getDataFileList();

        for (File file : files) {
            try {
                LOG.info("Processing file " + file.getAbsolutePath());
                deleteDoneFile(file);
                processFile(file);
            } catch (BankNotFoundException e) {
                // swallow the exceptions, and try to parse the other files
                LOG.error("Bank account number could not be matched in: " + file.getAbsolutePath() + ". This file's processing has been rolledback.", e);
                writeDoneFile(file);
            } catch (Exception e) {
                LOG.error("Exception occured during loading of " + file.getAbsolutePath() + ". This file's processing has been rolledback.", e);
                throw new Exception(e);
            }
        }
        writeLog();
        return success;
    }

    // Private Methods

    private Map<String, String> getStatusMapFromParameter(String parameterName, String statusCode) {
        Map<String, String> retval = new HashMap<String, String>();
        String parameterString = getParameter(parameterName);

        StringTokenizer parser = new StringTokenizer(parameterString, KFSConstants.MULTI_VALUE_SEPERATION_CHARACTER, false);
        while (parser.hasMoreTokens()) {
            String temp = parser.nextToken();
            retval.put(temp, statusCode);
        }

        return retval;
    }

    private String getParameter(String parameterName) {
        String retval = parameterService.getParameterValueAsString(CrParameterConstants.CR_NAMESPACE_CODE, CrParameterConstants.CHECK_RECONCILIATION_IMPORT_STEP_COMPONENT, parameterName);
        if (StringUtils.isBlank(retval)) {
            throw new RuntimeException("System parameter " + parameterName + " is null.");
        }
        return retval;
    }

    private Collection<CheckReconciliation> getNewCheckReconciliations() {
        Collection<CheckReconciliation> retvals = checkReconciliationDao.getNewCheckReconciliations(banks);
        return retvals;
    }

    private void writeDoneFile(File file) {
        String strippedOfName = FilenameUtils.removeExtension(file.getAbsolutePath());
        String doneFileName = strippedOfName + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;
        File doneFile = new File(doneFileName);
        try {
            doneFile.createNewFile();
        } catch (IOException e) {
            LOG.error("Unable to recreate .done file: " + doneFileName);
        }
    }

    private List<File> getDataFileList() {
        List<File> fileList = new ArrayList<File>();
        List<File> doneFileList = new ArrayList<File>();
        File file = new File(stagingDirectory);

        if (file.isDirectory()) {
            doneFileList = Arrays.asList(file.listFiles(new DoneFilenameFilter()));
        }

        for (File donefile : doneFileList) {
            String fileName = donefile.getAbsolutePath();
            fileName = FilenameUtils.removeExtension(fileName) + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            fileList.add(new File(fileName));
        }

        return fileList;
    }

    private void deleteDoneFile(File dataFile) {
        String strippedOfName = FilenameUtils.removeExtension(dataFile.getAbsolutePath());
        String doneFile = strippedOfName + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;

        try {
            FileUtils.deleteQuietly(new File(doneFile));
        } catch (Exception e) {
            LOG.info("Error in CheckReconciliationBatchService in method deleteDoneFile");
            throw new RuntimeException(e);
        }
    }

    @Transactional
    private void processFile(File file) throws Exception {
        LOG.info("Parsing File : " + file.getName());

        int totalLinesProcessed = 0;
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        try {
            line = br.readLine();
        } catch (Exception e) {
            LOG.error("Error loading first line of file " + file.getName());
            // end this method now if there's a failure at this point.
            throw e;
        } finally {
            IOUtils.closeQuietly(br);
        }

        while (line != null) {

            CheckReconciliation cr = null;
            try {
                cr = parseDelimitedLine(line);
            } catch (BankNotFoundException e) {
                CheckReconError cre = generateCheckReconError(e.getErrorCheckReconciliation(), "Error parsing line: " + e.getMessage() + ", loading of file " + file.getName() + " has been rolled back.");
                checkReconErrorRecords.add(cre);
                // end processing and close the stream now if there is an error at this point. No finally because we want to continue processing if there is no exception.
                IOUtils.closeQuietly(br);
                throw e;
            }

            // Find existing check record
            Collection<CheckReconciliation> existingRecords = checkReconciliationDao.getCheckReconciliation(cr.getCheckNumber(), cr.getBankAccountNumber(), cr.getAmount());
            if (existingRecords == null || existingRecords.isEmpty()) {
                updateCheckReconciliationAsException(cr);
                LOG.error("Check Record Not Found. Check Number: " + cr.getCheckNumber() + ". Bank Account: " + cr.getBankAccountNumber() + ". Amount: " + cr.getAmount() + ".");
            } else {
                updateCheckReconciliation(cr, existingRecords);
            }

            totalLinesProcessed++;
            try {
                line = br.readLine();
            } catch (Exception e) {
                LOG.error("Error loading a line from file " + file.getName());
                // end the loop and this method now if there's an I/O failure at this point.
                line = null;
            }
        }

        IOUtils.closeQuietly(br);
        LOG.info("Processed Records : " + totalLinesProcessed);
    }

    private void updateCheckReconciliation(CheckReconciliation cr, Collection<CheckReconciliation> existingRecords) {
        List<CheckReconciliation> reconList = new ArrayList<CheckReconciliation>(existingRecords);
        Collections.sort(reconList, new CheckReconciliationChronologicalComparator());

        CheckReconciliation firstRecord = reconList.remove(0);
        String checkStatus = validateCheckStatus(cr);
        Date statusChangeDate = updateStatusChangeDate(firstRecord.getStatus(), checkStatus, cr.getStatusChangeDate());

        if (CrConstants.CheckReconciliationSourceCodes.PDP_SRC.equals(firstRecord.getSourceCode()) && !CrConstants.CheckReconciliationStatusCodes.EXCP.equals(checkStatus)) {
            checkStatus = updatePdpCheckStatus(cr, checkStatus);
        }
        firstRecord.setStatus(checkStatus);
        firstRecord.setStatusChangeDate(statusChangeDate);
        firstRecord.setLastUpdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        if (CrConstants.CheckReconciliationStatusCodes.CLEARED.equals(checkStatus)) {
            firstRecord.setClearedDate(cr.getClearedDate());
        }
        checkReconciliationDao.save(firstRecord);

        for (CheckReconciliation extraRecord : reconList) {
            extraRecord.setStatus(CrConstants.CheckReconciliationStatusCodes.EXCP);
            extraRecord.setLastUpdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            checkReconciliationDao.save(extraRecord);

            CheckReconError cre = generateCheckReconError(extraRecord, "Check was a duplicate to check ID " + firstRecord.getId() + " checks based on check number, bank short name, and amount.  This check has been marked as EXCP.");
            checkReconErrorRecords.add(cre);
            LOG.info("Updated Check Recon Record to exception because of multiple matching records: " + firstRecord.getId());
        }
    }

    private String validateCheckStatus(CheckReconciliation cr) {
        String checkStatus = statusMap.get(cr.getStatus());
        if (checkStatus != null) {
            return checkStatus;
        }

        LOG.warn("Invalid status ( " + cr.getStatus() + ") ID : " + cr.getId());
        return CrConstants.CheckReconciliationStatusCodes.EXCP;
    }

    private String updatePdpCheckStatus(CheckReconciliation cr, String checkStatus) {

        KualiDecimal totalNetPaymentAmount = KualiDecimal.ZERO;
        Set<String> paymentSourceDocumentNumbers = new TreeSet<String>();

        List<String> bankCodes = generateBankCodes(cr);
        if (bankCodes.size() == 0) {
            String message = "Invalid Bank Account Number : " + cr.getBankAccountNumber();
            CheckReconError cre = generateCheckReconError(cr, message);
            checkReconErrorRecords.add(cre);
            return CrConstants.CheckReconciliationStatusCodes.EXCP;
        }

        Collection<PaymentGroup> paymentGroups = checkReconciliationDao.getAllPaymentGroupForSearchCriteria(cr.getCheckNumber(), bankCodes, cr.getCheckDate());
        if (paymentGroups == null || paymentGroups.size() == 0) {
            LOG.info("No Payments Found : " + cr.getBankAccountNumber() + "-" + cr.getCheckNumber());
            CheckReconError cre = generateCheckReconError(cr, "No payment groups found for check. Status changed to EXCP.");
            checkReconErrorRecords.add(cre);
            return CrConstants.CheckReconciliationStatusCodes.EXCP;
        }

        for (PaymentGroup paymentGroup : paymentGroups) {
            for (PaymentDetail paymentDetail : paymentGroup.getPaymentDetails()) {
                paymentSourceDocumentNumbers.add(paymentDetail.getCustPaymentDocNbr());
                totalNetPaymentAmount.add(paymentDetail.getNetPaymentAmount());
            }
            PaymentStatus paymentStatus = checkReconciliationDao.findMatchingPaymentStatus(checkStatus);
            if (!paymentGroup.getPaymentStatus().equals(paymentStatus)) {
                paymentGroup.setPaymentStatus(paymentStatus);
            }
            checkReconciliationDao.save(paymentGroup);
            LOG.info("Updated Payment Group : " + paymentGroup.getId());
        }

        if (!cr.getAmount().equals(totalNetPaymentAmount)) {
            String message = generatePdpInvalidAmountMessage(cr.getAmount(), totalNetPaymentAmount, paymentSourceDocumentNumbers);
            CheckReconError cre = generateCheckReconError(cr, message);
            checkReconErrorRecords.add(cre);
            return CrConstants.CheckReconciliationStatusCodes.EXCP;
        }

        return checkStatus;
    }

    private String generatePdpInvalidAmountMessage(KualiDecimal amount, KualiDecimal totalNetPaymentAmount, Set<String> paymentSourceDocumentNumbers) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("The check amount in the file and total net payment amount do not match. " + "Amount in file is $");
        stringBuilder.append(amount);
        stringBuilder.append(", amount in reconciliation table is $");
        stringBuilder.append(totalNetPaymentAmount);
        stringBuilder.append(" for payment source document number(s): ");
        for (String source : paymentSourceDocumentNumbers) {
            stringBuilder.append(source);
            stringBuilder.append(", ");
        }
        stringBuilder.setLength(stringBuilder.length() - 2);
        return stringBuilder.toString();
    }

    private List<String> generateBankCodes(CheckReconciliation cr) {
        List<String> retvals = new ArrayList<String>();
        for (Bank bank : banks) {
            if (bank.getBankShortName().equals(cr.getBankAccountNumber())) {
                retvals.add(bank.getBankCode());
            }
        }
        return retvals;
    }

    private void updateCheckReconciliationAsException(CheckReconciliation cr) {
        cr.setLastUpdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        cr.setStatus(CrConstants.CheckReconciliationStatusCodes.EXCP);
        cr.setGlTransIndicator(Boolean.FALSE);
        cr.setSourceCode(CrConstants.CheckReconciliationSourceCodes.BFL_SRC);
        checkReconciliationDao.save(cr);
        CheckReconError cre = generateCheckReconError(cr, "The bank record does not exist in reconciliation table. ");
        checkReconErrorRecords.add(cre);
    }

    private CheckReconciliation parseDelimitedLine(String line) throws ParseException {
        String[] crArray = line.split(KFSConstants.COMMA);

        Integer checkNumber = convertStringToInteger(crArray[CrConstants.CheckReconciliationImportColumns.CHECK_NUMBER]);
        KualiDecimal amount = convertStringToKualiDecimal(crArray[CrConstants.CheckReconciliationImportColumns.CHECK_AMOUNT]);
        Date checkDate = convertStringToDate(crArray[CrConstants.CheckReconciliationImportColumns.CHECK_DATE]);
        String accountNumber = crArray[CrConstants.CheckReconciliationImportColumns.ACCOUNT_NUMBER];
        String status = crArray[CrConstants.CheckReconciliationImportColumns.STATUS];

        CheckReconciliation cr = new CheckReconciliation();
        cr.setCheckNumber(checkNumber);
        cr.setAmount(amount);
        cr.setStatus(status);
        cr.setGlTransIndicator(Boolean.FALSE);
        cr.setCheckDate(checkDate);

        try {
            String bankShortName = findBankShortNameForBankAccountNumber(accountNumber);
            cr.setBankAccountNumber(bankShortName);
        } catch (BankNotFoundException e) {
            cr.setBankAccountNumber(CrConstants.MASKED_BANK_ACCOUNT_NUMBER);
            e.setErrorCheckReconciliation(cr);
            throw e;
        }
        return cr;
    }

    private Integer convertStringToInteger(String string) {
        Integer retval = new Integer(string);
        return retval;
    }

    private Date convertStringToDate(String string) throws ParseException {
        Date retval = new Date(CrConstants.CHECK_SIMPLE_DATE_FORMAT.parse(string).getTime());

        return retval;
    }

    private KualiDecimal convertStringToKualiDecimal(String value) {
        value = StringUtils.leftPad(value, 3, '0');
        int length = value.length();
        String temp = value.substring(0, length - 2) + "." + value.substring(length - 2);
        KualiDecimal retval = new KualiDecimal(temp);
        return retval;
    }

    private String findBankShortNameForBankAccountNumber(String accountNumber) {
        for (Bank bank : banks) {
            if (StringUtils.equals(accountNumber, bank.getBankAccountNumber())) {
                return bank.getBankShortName();
            }
        }
        throw new BankNotFoundException("No banks found for account number");
    }

    private CheckReconError generateCheckReconError(CheckReconciliation cr, String message) {
        CheckReconError retval = new CheckReconError();
        retval.setBankAcctNum(cr.getBankAccountNumber());
        retval.setCheckNum(cr.getCheckNumber().toString());
        retval.setCheckDate(cr.getCheckDate().toString());
        retval.setMessage(message);
        return retval;
    }

    private Date updateStatusChangeDate(String oldStatus, String newStatus, Date currStatusUpdateDate) {
        if (CrConstants.CheckReconciliationStatusCodes.ISSUED.equals(oldStatus) && !CrConstants.CheckReconciliationStatusCodes.ISSUED.equals(newStatus)) {
            return new Date(Calendar.getInstance().getTimeInMillis());
        }
        return currStatusUpdateDate;
    }

    private void writeLog() {
        File folder = new File(reportsDirectory);
        if (!folder.exists()) {
            boolean created = folder.mkdir();

            if (created) {
                LOG.info("Created new CR log folder : " + folder.getAbsolutePath());
            } else {
                LOG.warn("Unable to create log folder : " + folder.getAbsolutePath());
                return;
            }
        }
        if (!folder.isDirectory()) {
            LOG.warn("'" + reportsDirectory + "' is not a folder.");
            return;
        }

        String logFile = folder.getAbsolutePath() + "/cr_" + CrConstants.FILE_TIMESTAMP.format(new Date(Calendar.getInstance().getTimeInMillis())) + ".txt";

        Writer output = null;

        try {
            output = new BufferedWriter(new FileWriter(logFile));
            output.write((new Date(Calendar.getInstance().getTimeInMillis())) + "\n");
            output.write("Check Reconciliation Error Report\n\n");
            output.write("Account Number Check Number ");
            output.write(StringUtils.rightPad("Date", 30));
            output.write("Comment\n");

            for (CheckReconError temp : checkReconErrorRecords) {
                output.write(StringUtils.rightPad(temp.getBankAcctNum(), 15));
                output.write(StringUtils.rightPad(temp.getCheckNum(), 13));
                output.write(StringUtils.rightPad(temp.getCheckDate(), 30));
                output.write(temp.getMessage());
                output.write(KFSConstants.NEWLINE);
            }
        } catch (Exception err) {
            LOG.error("writeLog", err);
        } finally {
            try {
                output.close();
            } catch (Exception e) {
            }
        }

    }
}
