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
package org.kuali.kfs.pdp.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.batch.service.ExtractPaymentService;
import org.kuali.kfs.pdp.businessobject.AchAccountNumber;
import org.kuali.kfs.pdp.businessobject.CustomerBank;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.kfs.pdp.businessobject.DisbursementType;
import org.kuali.kfs.pdp.businessobject.FormatProcess;
import org.kuali.kfs.pdp.businessobject.FormatProcessSummary;
import org.kuali.kfs.pdp.businessobject.FormatSelection;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.pdp.businessobject.PaymentChangeCode;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.pdp.businessobject.PaymentProcess;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.dataaccess.FormatPaymentDao;
import org.kuali.kfs.pdp.dataaccess.PaymentDetailDao;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupDao;
import org.kuali.kfs.pdp.dataaccess.ProcessDao;
import org.kuali.kfs.pdp.service.AchService;
import org.kuali.kfs.pdp.service.FormatService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.pdp.service.impl.exception.FormatException;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FormatServiceImpl implements FormatService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatServiceImpl.class);

    private PaymentDetailDao paymentDetailDao;
    private PaymentGroupDao paymentGroupDao;
    private ProcessDao processDao;
    private AchService achService;
    private PendingTransactionService glPendingTransactionService;
    private ParameterService parameterService;
    private FormatPaymentDao formatPaymentDao;
    private SchedulerService schedulerService;
    private BusinessObjectService businessObjectService;
    private PaymentGroupService paymentGroupService;
    private DateTimeService dateTimeService;
    private ExtractPaymentService extractPaymentService;

    /**
     * Constructs a FormatServiceImpl.java.
     */
    public FormatServiceImpl() {
        super();
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatProcessService#getDataForFormat(org.kuali.rice.kim.bo.Person)
     */
    public FormatSelection getDataForFormat(Person user) {

        String campusCode = user.getCampusCode();
        Date formatStartDate = getFormatProcessStartDate(campusCode);

        // create new FormatSelection object an set the campus code and the start date
        FormatSelection formatSelection = new FormatSelection();
        formatSelection.setCampus(campusCode);
        formatSelection.setStartDate(formatStartDate);

        // if format process not started yet, populate the other data as well
        if (formatStartDate == null) {
            formatSelection.setCustomerList(getAllCustomerProfiles());
            formatSelection.setRangeList(getAllDisbursementNumberRanges());
        }

        return formatSelection;
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#getFormatProcessStartDate(java.lang.String)
     */
    public Date getFormatProcessStartDate(String campus) {
        LOG.debug("getFormatProcessStartDate() started");

        Map primaryKeys = new HashMap();
        primaryKeys.put(PdpPropertyConstants.PHYS_CAMPUS_PROCESS_CODE, campus);
        FormatProcess formatProcess = (FormatProcess) this.businessObjectService.findByPrimaryKey(FormatProcess.class, primaryKeys);

        if (formatProcess != null) {
            LOG.debug("getFormatProcessStartDate() found");
            return new Date(formatProcess.getBeginFormat().getTime());
        }
        else {
            LOG.debug("getFormatProcessStartDate() not found");
            return null;
        }
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#startFormatProcess(org.kuali.rice.kim.bo.Person, java.lang.String,
     *      java.util.List, java.util.Date, java.lang.String)
     */
    public FormatProcessSummary startFormatProcess(Person user, String campus, List<CustomerProfile> customers, Date paydate, String paymentTypes) {
        LOG.debug("startFormatProcess() started");

        for (CustomerProfile element : customers) {
            LOG.debug("startFormatProcess() Customer: " + element);
        }

        // Create the process
        Date d = new Date();
        PaymentProcess paymentProcess = new PaymentProcess();
        paymentProcess.setCampusCode(campus);
        paymentProcess.setProcessUser(user);
        paymentProcess.setProcessTimestamp(new Timestamp(d.getTime()));

        this.businessObjectService.save(paymentProcess);

        // add an entry in the format process table (to lock the format process)
        FormatProcess formatProcess = new FormatProcess();

        formatProcess.setPhysicalCampusProcessCode(campus);
        formatProcess.setBeginFormat(dateTimeService.getCurrentTimestamp());
        formatProcess.setPaymentProcIdentifier(paymentProcess.getId().intValue());

        this.businessObjectService.save(formatProcess);

        // Mark all of them ready for format
        formatPaymentDao.markPaymentsForFormat(paymentProcess, customers, paydate, paymentTypes);

        // summarize them
        FormatProcessSummary preFormatProcessSummary = new FormatProcessSummary();
        Iterator iterator = this.paymentGroupService.getByProcess(paymentProcess);

        while (iterator.hasNext()) {
            PaymentGroup paymentGroup = (PaymentGroup) iterator.next();
            preFormatProcessSummary.add(paymentGroup);
        }

        // if no payments found for format clear the format process
        if (preFormatProcessSummary.getProcessSummaryList().size() == 0) {
            LOG.debug("startFormatProcess() No payments to process.  Format process ending");
            clearUnfinishedFormat(paymentProcess.getId().intValue());// ?? maybe call end format process
        }

        return preFormatProcessSummary;
    }


    /**
     * This method gets the maximum number of lines in a note.
     * 
     * @return the maximum number of lines in a note
     */
    protected int getMaxNoteLines() {
        String maxLines = parameterService.getParameterValue(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.MAX_NOTE_LINES);
        if (StringUtils.isBlank(maxLines)) {
            throw new RuntimeException("System parameter for max note lines is blank");
        }

        return Integer.parseInt(maxLines);
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#performFormat(java.lang.Integer)
     */
    public boolean performFormat(Integer processId) {
        boolean successful = true;
        LOG.debug("performFormat() started");

        // get the PaymentProcess for the given id
        Map primaryKeys = new HashMap();
        primaryKeys.put(PdpPropertyConstants.PaymentProcess.PAYMENT_PROCESS_ID, processId);
        PaymentProcess paymentProcess = (PaymentProcess) this.businessObjectService.findByPrimaryKey(PaymentProcess.class, primaryKeys);

        if (paymentProcess == null) {
            LOG.error("performFormat() Invalid proc ID " + processId);
            throw new RuntimeException("Invalid proc ID");
        }
        String campus = paymentProcess.getCampusCode();

        //get disbursement types, payment statuses
        DisbursementType checkDisbursementType = (DisbursementType) businessObjectService.findBySinglePrimaryKey(DisbursementType.class, PdpConstants.DisbursementTypeCodes.CHECK);
        DisbursementType achDisbursementType = (DisbursementType) businessObjectService.findBySinglePrimaryKey(DisbursementType.class, PdpConstants.DisbursementTypeCodes.ACH);

        PaymentStatus extractedPaymentStatus = (PaymentStatus) businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.EXTRACTED);
        PaymentStatus pendingPaymentStatus = (PaymentStatus) businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, PdpConstants.PaymentStatusCodes.PENDING_ACH);

        // step 1 get ACH or Check, Bank info, ACH info, sorting
        Iterator paymentGroupIterator = this.paymentGroupService.getByProcess(paymentProcess);

        FormatProcessSummary postFormatProcessSummary = new FormatProcessSummary();

        while (paymentGroupIterator.hasNext()) {

            PaymentGroup paymentGroup = (PaymentGroup) paymentGroupIterator.next();
            LOG.debug("performFormat() Step 1 Payment Group ID " + paymentGroup.getId());

            // hold original bank code to log any change
            String originalBankCode = paymentGroup.getBankCode();
            Bank originalBank = paymentGroup.getBank();

            // process payment group data
            successful &= processPaymentGroup(paymentGroup, paymentProcess, checkDisbursementType, achDisbursementType, extractedPaymentStatus, pendingPaymentStatus);

            // create payment history record if bank was changed
            if (StringUtils.isNotBlank(originalBankCode) && !paymentGroup.getBankCode().equals(originalBankCode)) {
                PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();

                PaymentChangeCode paymentChangeCode = (PaymentChangeCode) businessObjectService.findBySinglePrimaryKey(PaymentChangeCode.class, PdpConstants.PaymentChangeCodes.BANK_CHNG_CD);
                paymentGroupHistory.setPaymentChange(paymentChangeCode);
                paymentGroupHistory.setOrigBankCode(originalBankCode);
                paymentGroupHistory.setBank(originalBank);
                paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
                Person changeUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPerson(KFSConstants.SYSTEM_USER);
                paymentGroupHistory.setChangeUser(changeUser);
                paymentGroupHistory.setPaymentGroup(paymentGroup);
                paymentGroupHistory.setChangeTime(new Timestamp(new Date().getTime()));

                // save payment group history
                businessObjectService.save(paymentGroupHistory);
            }

            // save payment group
            this.businessObjectService.save(paymentGroup);

            // Add to summary information
            postFormatProcessSummary.add(paymentGroup);
        }

        // step 2 assign disbursement numbers and combine checks into one if possible
        successful &= assignDisbursementNumbersAndCombineChecks(campus, paymentProcess, postFormatProcessSummary);
        if (successful) {
            // step 3 save the summarizing info
            LOG.debug("performFormat() Save summarizing information");
            postFormatProcessSummary.save();

            // step 4 set formatted indicator to true and save in the db
            paymentProcess.setFormattedIndicator(true);
            businessObjectService.save(paymentProcess);

            // step 5 end the format process for this campus
            LOG.debug("performFormat() End the format process for this campus");
            endFormatProcess(campus);

            // step 6 tell the extract batch job to start
            LOG.debug("performFormat() Start extract");
            extractChecks();
        }   
        return successful;
    }

    /**
     * This method processes the payment group data.
     * 
     * @param paymentGroup
     * @param paymentProcess
     * @param checkDisbursementType
     * @param achDisbursementType
     * @param extractedPaymentStatus
     * @param pendingPaymentStatus
     */
    protected boolean processPaymentGroup(PaymentGroup paymentGroup, PaymentProcess paymentProcess, DisbursementType checkDisbursementType, DisbursementType achDisbursementType, PaymentStatus extractedPaymentStatus, PaymentStatus pendingPaymentStatus) {
        boolean successful = true;
        CustomerProfile customer = paymentGroup.getBatch().getCustomerProfile();

        // Set the sort field to be saved in the database
        paymentGroup.setSortValue(paymentGroupService.getSortGroupId(paymentGroup));

        paymentGroup.setDisbursementDate(new java.sql.Date(paymentProcess.getProcessTimestamp().getTime()));
        paymentGroup.setPhysCampusProcessCd(paymentProcess.getCampusCode());
        paymentGroup.setProcess(paymentProcess);

        // If any one of the payment details in the group are negative, we always force a check
        boolean noNegativeDetails = true;

        // If any one of the payment details in the group are negative, we always force a check
        List<PaymentDetail> paymentDetailsList = paymentGroup.getPaymentDetails();
        for (PaymentDetail paymentDetail : paymentDetailsList) {
            if (paymentDetail.getNetPaymentAmount().doubleValue() < 0) {
                LOG.debug("performFormat() Payment Group " + paymentGroup + " has payment detail net payment amount " + paymentDetail.getNetPaymentAmount());
                LOG.debug("performFormat() Forcing a Check for Group");
                noNegativeDetails = false;
                break;
            }
        }

        // determine whether payment should be ACH or Check
        PayeeACHAccount payeeAchAccount = null;
        boolean isCheck = true;
        if (PdpConstants.PayeeIdTypeCodes.VENDOR_ID.equals(paymentGroup.getPayeeIdTypeCd()) || PdpConstants.PayeeIdTypeCodes.EMPLOYEE.equals(paymentGroup.getPayeeIdTypeCd()) || PdpConstants.PayeeIdTypeCodes.ENTITY.equals(paymentGroup.getPayeeIdTypeCd())) {
            if (StringUtils.isNotBlank(paymentGroup.getPayeeId()) && !paymentGroup.getPymtAttachment() && !paymentGroup.getProcessImmediate() && !paymentGroup.getPymtSpecialHandling() && (customer.getAchTransactionType() != null) && noNegativeDetails) {
                LOG.debug("performFormat() Checking ACH");
                payeeAchAccount = achService.getAchInformation(paymentGroup.getPayeeIdTypeCd(), paymentGroup.getPayeeId(), customer.getAchTransactionType());
                isCheck = (payeeAchAccount == null);
            }
        }

        if (isCheck) {
            PaymentStatus paymentStatus = extractedPaymentStatus;
            LOG.debug("performFormat() Check: " + paymentStatus);
            paymentGroup.setDisbursementType(checkDisbursementType);
            paymentGroup.setPaymentStatus(paymentStatus);

            // set bank, use group bank unless not given or not valid for checks
            if (ObjectUtils.isNull(paymentGroup.getBank()) || !paymentGroup.getBank().isBankCheckIndicator() || !paymentGroup.getBank().isActive()) {
                CustomerBank customerBank = customer.getCustomerBankByDisbursementType(PdpConstants.DisbursementTypeCodes.CHECK);
                if (ObjectUtils.isNotNull(customerBank) && customerBank.isActive() && ObjectUtils.isNotNull(customerBank.getBank()) && customerBank.getBank().isActive()) {
                    paymentGroup.setBankCode(customerBank.getBankCode());
                    paymentGroup.setBank(customerBank.getBank());
                }
                else if (ObjectUtils.isNotNull(customerBank) && ObjectUtils.isNotNull(customerBank.getBank()) && ObjectUtils.isNotNull(customerBank.getBank().getContinuationBank()) && customerBank.getBank().getContinuationBank().isActive()) {
                    paymentGroup.setBankCode(customerBank.getBank().getContinuationBank().getBankCode());
                    paymentGroup.setBank(customerBank.getBank().getContinuationBank());                 
                }
            }

            if (ObjectUtils.isNull(paymentGroup.getBank())) {
                LOG.error("performFormat() A bank is needed for CHCK for customer: " + customer);
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.Format.ErrorMessages.ERROR_FORMAT_BANK_MISSING, customer.getCustomerShortName());
                successful = false;
                return successful;
               // throw new FormatException("A bank is needed for CHCK for customer: " + customer.getChartCode() + "-" + customer.getUnitCode() + "-" + customer.getSubUnitCode());
            }
        }
        else {
            PaymentStatus paymentStatus = pendingPaymentStatus;
            LOG.debug("performFormat() ACH: " + paymentStatus);
            paymentGroup.setDisbursementType(achDisbursementType);
            paymentGroup.setPaymentStatus(paymentStatus);

            // set bank, use group bank unless not given or not valid for ACH
            if (ObjectUtils.isNull(paymentGroup.getBank()) || !paymentGroup.getBank().isBankAchIndicator() || !paymentGroup.getBank().isActive()) {
                CustomerBank customerBank = customer.getCustomerBankByDisbursementType(PdpConstants.DisbursementTypeCodes.ACH);
                if (ObjectUtils.isNotNull(customerBank) && customerBank.isActive() && ObjectUtils.isNotNull(customerBank.getBank()) && customerBank.getBank().isActive()) {
                    paymentGroup.setBankCode(customerBank.getBankCode());
                    paymentGroup.setBank(customerBank.getBank());
                }
                else if (ObjectUtils.isNotNull(customerBank) && ObjectUtils.isNotNull(customerBank.getBank()) && ObjectUtils.isNotNull(customerBank.getBank().getContinuationBank()) && customerBank.getBank().getContinuationBank().isActive()) {
                    paymentGroup.setBankCode(customerBank.getBank().getContinuationBank().getBankCode());
                    paymentGroup.setBank(customerBank.getBank().getContinuationBank());                 
                }
            }

            if (ObjectUtils.isNull(paymentGroup.getBank())) {
                LOG.error("performFormat() A bank is needed for ACH for customer: " + customer);
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.Format.ErrorMessages.ERROR_FORMAT_BANK_MISSING, customer.getCustomerShortName());
                successful = false;
                return successful;
            //    throw new FormatException("A bank is needed for ACH for customer: " + customer.getChartCode() + "-" + customer.getUnitCode() + "-" + customer.getSubUnitCode());
            }

            paymentGroup.setAchBankRoutingNbr(payeeAchAccount.getBankRoutingNumber());
            paymentGroup.setAdviceEmailAddress(payeeAchAccount.getPayeeEmailAddress());
            paymentGroup.setAchAccountType(payeeAchAccount.getBankAccountTypeCode());

            AchAccountNumber achAccountNumber = new AchAccountNumber();
            achAccountNumber.setAchBankAccountNbr(payeeAchAccount.getBankAccountNumber());
            achAccountNumber.setId(paymentGroup.getId());
            paymentGroup.setAchAccountNumber(achAccountNumber);
        }
        return successful;
    }

    /**
     * This method assigns disbursement numbers and tries to combine payment groups with disbursement type check if possible.
     * @param campus
     * @param paymentProcess
     * @param postFormatProcessSummary
     */
    protected boolean assignDisbursementNumbersAndCombineChecks(String campus, PaymentProcess paymentProcess, FormatProcessSummary postFormatProcessSummary) {
        boolean successful = true;
        
        // keep a map with paymentGroupKey and PaymentInfo (disbursementNumber, noteLines)
        Map<String, PaymentInfo> combinedChecksMap = new HashMap<String, PaymentInfo>();

        List<DisbursementNumberRange> disbursementRanges = paymentDetailDao.getDisbursementNumberRanges(campus);
        Iterator paymentGroupIterator = this.paymentGroupService.getByProcess(paymentProcess);
        int maxNoteLines = getMaxNoteLines();

        while (paymentGroupIterator.hasNext()) {
            PaymentGroup paymentGroup = (PaymentGroup) paymentGroupIterator.next();
            LOG.debug("performFormat() Payment Group ID " + paymentGroup.getId());

            DisbursementNumberRange range = getRange(disbursementRanges, paymentGroup.getBank(), paymentGroup.getDisbursementType().getCode());

            if (range == null) {
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.Format.ErrorMessages.ERROR_FORMAT_DISBURSEMENT_MISSING, campus, paymentGroup.getBank().getBankCode(), paymentGroup.getDisbursementType().getCode());
                successful = false;
                return successful;
               // throw new FormatException("No disbursement range for bank code " + paymentGroup.getBank().getBankCode() + " and disbursement type code " + paymentGroup.getDisbursementType().getCode());
            }

            if (PdpConstants.DisbursementTypeCodes.CHECK.equals(paymentGroup.getDisbursementType().getCode())) {

                if (paymentGroup.getPymtAttachment().booleanValue() || paymentGroup.getProcessImmediate().booleanValue() || paymentGroup.getPymtSpecialHandling().booleanValue() || (!paymentGroup.getCombineGroups())) {
                    assignDisbursementNumber(campus, range, paymentGroup, postFormatProcessSummary);
                }
                else {
                    String paymentGroupKey = paymentGroup.toStringKey();
                    // check if there was another paymentGroup we can combine with
                    if (combinedChecksMap.containsKey(paymentGroupKey)) {
                        PaymentInfo paymentInfo = combinedChecksMap.get(paymentGroupKey);
                        paymentInfo.noteLines = paymentInfo.noteLines.add(new KualiInteger(paymentGroup.getNoteLines()));

                        // if noteLines don't excede the maximum assign the same disbursementNumber
                        if (paymentInfo.noteLines.intValue() <= maxNoteLines) {
                            KualiInteger checkNumber = paymentInfo.disbursementNumber;
                            paymentGroup.setDisbursementNbr(checkNumber);
                            
                            // update payment info for new noteLines value
                            combinedChecksMap.put(paymentGroupKey, paymentInfo);
                        }
                        // it noteLines more than maxNoteLines we remove the old entry and get a new disbursement number
                        else {
                            //remove old entry for this paymentGroupKey
                            combinedChecksMap.remove(paymentGroupKey);
                            
                            // get a new check number and the paymentGroup noteLines
                            KualiInteger checkNumber = assignDisbursementNumber(campus, range, paymentGroup, postFormatProcessSummary);
                            int noteLines = paymentGroup.getNoteLines();
                            
                            // create new payment info with these two
                            paymentInfo = new PaymentInfo(checkNumber, new KualiInteger(noteLines));
                            
                            // add new entry in the map for this paymentGroupKey
                            combinedChecksMap.put(paymentGroupKey, paymentInfo);

                        }
                    }
                    // if no entry in the map for this payment group we create a new one
                    else {
                        // get a new check number and the paymentGroup noteLines
                        KualiInteger checkNumber = assignDisbursementNumber(campus, range, paymentGroup, postFormatProcessSummary);
                        int noteLines = paymentGroup.getNoteLines();
                        
                        // create new payment info with these two
                        PaymentInfo paymentInfo = new PaymentInfo(checkNumber, new KualiInteger(noteLines));
                        
                        // add new entry in the map for this paymentGroupKey
                        combinedChecksMap.put(paymentGroupKey, paymentInfo);
                    }
                }
            }
            else if (PdpConstants.DisbursementTypeCodes.ACH.equals(paymentGroup.getDisbursementType().getCode())) {
                assignDisbursementNumber(campus, range, paymentGroup, postFormatProcessSummary);
            }
            else {
                // if it isn't check or ach, we're in trouble
                LOG.error("assignDisbursementNumbers() Payment group " + paymentGroup.getId() + " must be CHCK or ACH.  It is: " + paymentGroup.getDisbursementType());
                throw new IllegalArgumentException("Payment group " + paymentGroup.getId() + " must be Check or ACH");
            }

            this.businessObjectService.save(paymentGroup);

            // Generate a GL entry for CHCK & ACH
            glPendingTransactionService.generatePaymentGeneralLedgerPendingEntry(paymentGroup);
        }

        // Update all the ranges
        LOG.debug("assignDisbursementNumbers() Save ranges");
        for (DisbursementNumberRange element : disbursementRanges) {
            this.businessObjectService.save(element);
        }
        return successful;
    }

    /**
     * This method gets a new disbursement number and sets it on the payment group and process summary.
     * @param campus
     * @param range
     * @param paymentGroup
     * @param postFormatProcessSummary
     * @return
     */
    protected KualiInteger assignDisbursementNumber(String campus, DisbursementNumberRange range, PaymentGroup paymentGroup, FormatProcessSummary postFormatProcessSummary) {
        KualiInteger disbursementNumber = new KualiInteger(1 + range.getLastAssignedDisbNbr().intValue());

        if (disbursementNumber.isGreaterThan(range.getEndDisbursementNbr())) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.Format.ErrorMessages.ERROR_FORMAT_DISBURSEMENT_EXHAUSTED, campus, paymentGroup.getBank().getBankCode(), paymentGroup.getDisbursementType().getCode());

            throw new FormatException("No more disbursement numbers for bank code " + paymentGroup.getBank().getBankCode() + " and disbursement type code " + paymentGroup.getDisbursementType().getCode());
        }

        paymentGroup.setDisbursementNbr(disbursementNumber);
        range.setLastAssignedDisbNbr(disbursementNumber);

        // Update the summary information
        postFormatProcessSummary.setDisbursementNumber(paymentGroup, disbursementNumber.intValue());

        return disbursementNumber;
    }

    /**
     * runs the extract process.
     */
    protected void extractChecks() {
        LOG.debug("extractChecks() started");
        
        extractPaymentService.extractChecks();
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#clearUnfinishedFormat(java.lang.Integer)
     */
    public void clearUnfinishedFormat(Integer processId) {
        LOG.debug("clearUnfinishedFormat() started");

        Map primaryKeys = new HashMap();
        primaryKeys.put(PdpPropertyConstants.PaymentProcess.PAYMENT_PROCESS_ID, processId);
        PaymentProcess paymentProcess = (PaymentProcess) this.businessObjectService.findByPrimaryKey(PaymentProcess.class, primaryKeys);
        LOG.debug("clearUnfinishedFormat() Process: " + paymentProcess);

        formatPaymentDao.unmarkPaymentsForFormat(paymentProcess);

        endFormatProcess(paymentProcess.getCampusCode());
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#resetFormatPayments(java.lang.Integer)
     */
    public void resetFormatPayments(Integer processId) {
        LOG.debug("resetFormatPayments() started");
        clearUnfinishedFormat(processId);
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#endFormatProcess(java.lang.String)
     */
    public void endFormatProcess(String campus) {
        LOG.debug("endFormatProcess() starting");

        Map primaryKeys = new HashMap();
        primaryKeys.put(PdpPropertyConstants.PHYS_CAMPUS_PROCESS_CODE, campus);

        this.businessObjectService.deleteMatching(FormatProcess.class, primaryKeys);
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#getAllCustomerProfiles()
     */
    public List getAllCustomerProfiles() {
        LOG.debug("getAllCustomerProfiles() started");

        List<CustomerProfile> customerProfileList = (List<CustomerProfile>) this.businessObjectService.findAll(CustomerProfile.class);

        DynamicCollectionComparator.sort(customerProfileList, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_CHART_CODE, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_UNIT_CODE, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_SUB_UNIT_CODE);

        return customerProfileList;
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#getAllDisbursementNumberRanges()
     */
    public List<DisbursementNumberRange> getAllDisbursementNumberRanges() {
        LOG.debug("getAllDisbursementNumberRanges() started");

        List<DisbursementNumberRange> disbursementNumberRangeList = (List<DisbursementNumberRange>) this.businessObjectService.findAll(DisbursementNumberRange.class);
        DynamicCollectionComparator.sort(disbursementNumberRangeList, PdpPropertyConstants.DisbursementNumberRange.DISBURSEMENT_NUMBER_RANGE_PHYS_CAMPUS_PROC_CODE, PdpPropertyConstants.DisbursementNumberRange.DISBURSEMENT_NUMBER_RANGE_TYPE_CODE);

        return disbursementNumberRangeList;
    }

    /**
     * Given the List of disbursement number ranges for the processing campus, finds matches for the bank code and disbursement type
     * code. If more than one match is found, the range with the latest start date (before or equal to today) will be returned.
     * 
     * @param ranges List of disbursement ranges to search (already filtered to processing campus, active, and start date before or
     *        equal to today)
     * @param bank bank code to find range for
     * @param disbursementTypeCode disbursement type code to find range for
     * @return found <code>DisbursementNumberRange</code or null if one was not found
     */
    protected DisbursementNumberRange getRange(List<DisbursementNumberRange> ranges, Bank bank, String disbursementTypeCode) {
        LOG.debug("getRange() Looking for bank = " + bank.getBankCode() + " and disbursement type " + disbursementTypeCode);

        List<DisbursementNumberRange> rangeMatches = new ArrayList<DisbursementNumberRange>();
        for (DisbursementNumberRange range : ranges) {
            if (range.getBank().getBankCode().equals(bank.getBankCode()) && range.getDisbursementTypeCode().equals(disbursementTypeCode)) {
                rangeMatches.add(range);
            }
        }

        // if more than one match we need to take the range with the latest start date
        if (rangeMatches.size() > 0) {
            DisbursementNumberRange maxStartDateRange = rangeMatches.get(0);
            for (DisbursementNumberRange range : rangeMatches) {
                if (range.getDisbNbrRangeStartDt().compareTo(maxStartDateRange.getDisbNbrRangeStartDt()) > 0) {
                    maxStartDateRange = range;
                }
            }

            return maxStartDateRange;
        }

        return null;
    }

    /**
     * This method sets the formatPaymentDao
     * 
     * @param fpd
     */
    public void setFormatPaymentDao(FormatPaymentDao fpd) {
        formatPaymentDao = fpd;
    }

    /**
     * This method sets the glPendingTransactionService
     * 
     * @param gs
     */
    public void setGlPendingTransactionService(PendingTransactionService gs) {
        glPendingTransactionService = gs;
    }

    /**
     * This method sets the achService
     * 
     * @param as
     */
    public void setAchService(AchService as) {
        achService = as;
    }

    /**
     * This method sets the processDao
     * 
     * @param pd
     */
    public void setProcessDao(ProcessDao pd) {
        processDao = pd;
    }

    /**
     * This method sets the paymentGroupDao
     * 
     * @param pgd
     */
    public void setPaymentGroupDao(PaymentGroupDao pgd) {
        paymentGroupDao = pgd;
    }

    /**
     * This method sets the paymentDetailDao
     * 
     * @param pdd
     */
    public void setPaymentDetailDao(PaymentDetailDao pdd) {
        paymentDetailDao = pdd;
    }

    /**
     * This method sets the schedulerService
     * 
     * @param ss
     */
    public void setSchedulerService(SchedulerService ss) {
        schedulerService = ss;
    }

    /**
     * This method sets the parameterService
     * 
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param bos
     */
    public void setBusinessObjectService(BusinessObjectService bos) {
        this.businessObjectService = bos;
    }

    /**
     * This method sets the paymentGroupService
     * @param paymentGroupService
     */
    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    /**
     * This method sets the dateTimeService
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
    /**
     * Gets the extractPaymentService attribute. 
     * @return Returns the extractPaymentService.
     */
    protected ExtractPaymentService getExtractPaymentService() {
        return extractPaymentService;
    }

    /**
     * Sets the extractPaymentService attribute value.
     * @param extractPaymentService The extractPaymentService to set.
     */
    public void setExtractPaymentService(ExtractPaymentService extractPaymentService) {
        this.extractPaymentService = extractPaymentService;
    }

    /**
     * This class holds disbursement number and noteLines info for payment group disbursement number assignment and combine checks.
     */
    protected class PaymentInfo {
        public KualiInteger disbursementNumber;
        public KualiInteger noteLines;

        public PaymentInfo(KualiInteger disbursementNumber, KualiInteger noteLines) {
            this.disbursementNumber = disbursementNumber;
            this.noteLines = noteLines;
        }
    }

}
