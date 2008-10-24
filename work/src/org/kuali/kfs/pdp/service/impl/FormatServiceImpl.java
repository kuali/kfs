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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.pdp.GeneralUtilities;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.AchAccountNumber;
import org.kuali.kfs.pdp.businessobject.CustomerBank;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.kfs.pdp.businessobject.DisbursementType;
import org.kuali.kfs.pdp.businessobject.FormatProcess;
import org.kuali.kfs.pdp.businessobject.FormatResult;
import org.kuali.kfs.pdp.businessobject.FormatSelection;
import org.kuali.kfs.pdp.businessobject.PayeeAchAccount;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.pdp.businessobject.PaymentProcess;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.businessobject.PostFormatProcessSummary;
import org.kuali.kfs.pdp.businessobject.PreFormatProcessSummary;
import org.kuali.kfs.pdp.businessobject.ProcessSummary;
import org.kuali.kfs.pdp.dataaccess.FormatPaymentDao;
import org.kuali.kfs.pdp.dataaccess.PaymentDetailDao;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupDao;
import org.kuali.kfs.pdp.dataaccess.ProcessDao;
import org.kuali.kfs.pdp.exception.ConfigurationError;
import org.kuali.kfs.pdp.service.AchService;
import org.kuali.kfs.pdp.service.FormatService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.pdp.service.impl.exception.DisbursementRangeExhaustedException;
import org.kuali.kfs.pdp.service.impl.exception.MissingDisbursementRangeException;
import org.kuali.kfs.pdp.service.impl.exception.NoBankForCustomerException;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.service.KualiCodeService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiInteger;
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
    private KualiCodeService kualiCodeService;
    private PaymentGroupService paymentGroupService;
    private DateTimeService dateTimeService;

    /**
     * Constructs a FormatServiceImpl.java.
     */
    public FormatServiceImpl() {
        super();
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatProcessService#getDataForFormat(org.kuali.rice.kns.bo.user.UniversalUser)
     */
    public FormatSelection getDataForFormat(UniversalUser user) {

        String campusCode = user.getCampusCode();
        Date formatStartDate = getFormatProcessStartDate(campusCode);

        // create new FormatSelection object an set the campus code and the start date
        FormatSelection formatSelection = new FormatSelection();
        formatSelection.setCampus(campusCode);
        formatSelection.setStartDate(formatStartDate);

        //if format process not started yet, populate the other data as well
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
     * @see org.kuali.kfs.pdp.service.FormatService#startFormatProcess(org.kuali.rice.kns.bo.user.UniversalUser, java.lang.String, java.util.List, java.util.Date, java.lang.String)
     */
    public List startFormatProcess(UniversalUser user, String campus, List<CustomerProfile> customers, Date paydate, String paymentTypes) {
        LOG.debug("startFormatProcess() started");

        for (CustomerProfile element : customers) {
            LOG.debug("startFormatProcess() Customer: " + element);
        }

          // Create the process
        Date d = new Date();
        PaymentProcess paymentProcess = new PaymentProcess();
        paymentProcess.setCampus(campus);
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
        PreFormatProcessSummary preFormatProcessSummary = new PreFormatProcessSummary();

        Iterator iterator = this.paymentGroupService.getByProcess(paymentProcess);

        int count = 0;
        while (iterator.hasNext()) {
            PaymentGroup paymentGroup = (PaymentGroup) iterator.next();

            count++;
            preFormatProcessSummary.add(paymentGroup);
        }

        if (count == 0) {
            LOG.debug("startFormatProcess() No payments to process.  Format process ending");

            clearUnfinishedFormat(paymentProcess.getId().intValue());
            // endFormatProcess(campus);
        }

        return convertProcessSummary2FormatResult(preFormatProcessSummary.getProcessSummaryList());
    }

    /**
     * This method gets the maximum number of lines in a note.
     * @return the maximum number of lines in a note
     */
    private int getMaxNoteLines() {
        return GeneralUtilities.getParameterInteger(parameterService, ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.MAX_NOTE_LINES);
    }

    /**
     * This method returns the size of the format summary list.
     * @return the size of the format summary list
     */
    private int getFormatSummaryListSize() {
        return GeneralUtilities.getParameterInteger(parameterService, ParameterConstants.PRE_DISBURSEMENT_LOOKUP.class, PdpConstants.ApplicationParameterKeys.FORMAT_SUMMARY_ROWS, 40);
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#performFormat(java.lang.Integer)
     */
    public List<FormatResult> performFormat(Integer processId) {
        LOG.debug("performFormat() started");

        // get the PaymentProcess for the given id
       Map primaryKeys = new HashMap();
        primaryKeys.put(PdpPropertyConstants.PaymentProcess.PAYMENT_PROCESS_ID, processId);
        PaymentProcess paymentProcess = (PaymentProcess) this.businessObjectService.findByPrimaryKey(PaymentProcess.class, primaryKeys);
        
        if (paymentProcess == null) {
            LOG.error("performFormat() Invalid proc ID " + processId);
            throw new ConfigurationError("Invalid proc ID");
        }

        DisbursementType checkDisbursementType = (DisbursementType) kualiCodeService.getByCode(DisbursementType.class, PdpConstants.DisbursementTypeCodes.CHECK);
        DisbursementType achDisbursementType = (DisbursementType) kualiCodeService.getByCode(DisbursementType.class, PdpConstants.DisbursementTypeCodes.ACH);

        PaymentStatus extractedPaymentStatus = (PaymentStatus) kualiCodeService.getByCode(PaymentStatus.class, PdpConstants.PaymentStatusCodes.EXTRACTED);
        PaymentStatus pendingPaymentStatus = (PaymentStatus) kualiCodeService.getByCode(PaymentStatus.class, PdpConstants.PaymentStatusCodes.PENDING_ACH);

        // Step one, get ACH or Check, Bank info, ACH info, sorting
        Iterator paymentGroupIterator = this.paymentGroupService.getByProcess(paymentProcess);

        PostFormatProcessSummary postFormatProcessSummary = new PostFormatProcessSummary();
        
        while (paymentGroupIterator.hasNext()) {
            
            PaymentGroup paymentGroup = (PaymentGroup) paymentGroupIterator.next();
            LOG.debug("performFormat() Step 1 Payment Group ID " + paymentGroup.getId());

            // hold original bank code to log any change
            String originalBankCode = paymentGroup.getBankCode();
            
            // process payment group data
            processPaymentGroup(paymentGroup, paymentProcess, checkDisbursementType, achDisbursementType, extractedPaymentStatus, pendingPaymentStatus);
            
            // create payment history record if bank was changed
            if (!paymentGroup.getBankCode().equals(originalBankCode)) {
                
                PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();

                paymentGroupHistory.setPaymentChangeCode(PdpConstants.PaymentChangeCodes.BANK_CHNG_CD);
                paymentGroupHistory.setOrigBankCode(originalBankCode);
                paymentGroupHistory.setOrigPaymentStatus(paymentGroup.getPaymentStatus());
                paymentGroupHistory.setChangeUserId(KFSConstants.SYSTEM_USER);
                paymentGroupHistory.setPaymentGroup(paymentGroup);

                // save payment group history
                businessObjectService.save(paymentGroupHistory);
            }

            // save payment group
            this.businessObjectService.save(paymentGroup);
            
            // Add to summary information
            postFormatProcessSummary.add(paymentGroup);
        }

        // step 2 figure out if we combine checks into one
        LOG.debug("performFormat() Combining");
        
        combineChecksIntoOne(paymentProcess, checkDisbursementType);


        // step 3 now assign disbursement numbers
        LOG.debug("performFormat() Assigning disbursement numbers");
        pass2(paymentProcess.getCampus(), paymentProcess, postFormatProcessSummary);

        // step 4 save the summarizing info
        LOG.debug("performFormat() Save summarizing information");
        postFormatProcessSummary.save();

        // step 5 tell the extract batch job to start
        LOG.debug("performFormat() Start extract batch job");
        triggerExtract(processId);

        // step 6 end the format process for this campus
        LOG.debug("performFormat() End the format process for this campus");
        endFormatProcess(paymentProcess.getCampus());

        // step 7 return all the process summaries
        Map fieldValues = new HashMap();
        fieldValues.put(PdpPropertyConstants.ProcessSummary.PROCESS_SUMMARY_PROCESS_ID, paymentProcess);
        
        List processSummaryResults = (List) this.businessObjectService.findMatching(ProcessSummary.class, fieldValues);
        return convertProcessSummary2FormatResult(processSummaryResults);
    }
    
    /**
     * This method processes the payment group data.
     * @param paymentGroup
     * @param paymentProcess
     * @param checkDisbursementType
     * @param achDisbursementType
     * @param extractedPaymentStatus
     * @param pendingPaymentStatus
     */
    private void processPaymentGroup(PaymentGroup paymentGroup, PaymentProcess paymentProcess, DisbursementType checkDisbursementType, DisbursementType achDisbursementType, PaymentStatus extractedPaymentStatus, PaymentStatus pendingPaymentStatus) {
        
        CustomerProfile customer = paymentGroup.getBatch().getCustomerProfile();

        // Set the sort field to be saved in the database
        paymentGroup.setSortValue(paymentGroupService.getSortGroupId(paymentGroup));

        paymentGroup.setDisbursementDate(paymentProcess.getProcessTimestamp());
        paymentGroup.setPhysCampusProcessCd(paymentProcess.getCampus());
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

        // Attachments, Process Immediate & Special Handling are always checks
        // If there isn't a PSD Transaction code for the customer, don't even look to see if any payment is ACH
        // If the payment ID is X, it's always a check
        // If any one of the payment details in the group are negative, we always force a check
        PayeeAchAccount payeeAchAccount = null;
        boolean isCheck = true;
        if ((!PdpConstants.PayeeIdTypeCodes.OTHER.equals(paymentGroup.getPayeeIdTypeCd())) && (!"".equals(paymentGroup.getPayeeIdTypeCd())) && (paymentGroup.getPayeeIdTypeCd() != null) && (!"".equals(paymentGroup.getPayeeId())) && (paymentGroup.getPayeeId() != null) && (!paymentGroup.getPymtAttachment().booleanValue()) && (!paymentGroup.getProcessImmediate().booleanValue()) && (!paymentGroup.getPymtSpecialHandling().booleanValue()) && (customer.getPsdTransactionCode() != null) && (noNegativeDetails)) {
            LOG.debug("performFormat() Checking ACH");
            payeeAchAccount = achService.getAchInformation(paymentGroup.getPayeeIdTypeCd(), paymentGroup.getPayeeId(), customer.getPsdTransactionCode());
            isCheck = (payeeAchAccount == null);
        }

        if (isCheck) {
            PaymentStatus paymentStatus = extractedPaymentStatus;
            LOG.debug("performFormat() Check: " + paymentStatus);
            paymentGroup.setDisbursementType(checkDisbursementType);
            paymentGroup.setPaymentStatus(paymentStatus);

            // set bank, use group bank unless not given or not valid for checks
            if (paymentGroup.getBank() == null || !paymentGroup.getBank().isBankCheckIndicator()) {
                CustomerBank customerBank = customer.getCustomerBankByDisbursementType(PdpConstants.DisbursementTypeCodes.CHECK);
                if (customerBank != null && customerBank.isActive()) {
                    paymentGroup.setBankCode(customerBank.getBankCode());
                    paymentGroup.setBank(customerBank.getBank());
                }
            }

            if (paymentGroup.getBank() == null) {
                LOG.error("performFormat() A bank is needed for CHCK for customer: " + customer);
                throw new NoBankForCustomerException("A bank is needed for CHCK for customer: " + customer, customer.getChartCode() + "-" + customer.getOrgCode() + "-" + customer.getSubUnitCode());
            }
        }
        else {
            PaymentStatus paymentStatus = pendingPaymentStatus;
            LOG.debug("performFormat() ACH: " + paymentStatus);
            paymentGroup.setDisbursementType(achDisbursementType);
            paymentGroup.setPaymentStatus(paymentStatus);

            // set bank, use group bank unless not given or not valid for ACH
            if (paymentGroup.getBank() == null || !paymentGroup.getBank().isBankAchIndicator()) {
                CustomerBank customerBank = customer.getCustomerBankByDisbursementType(PdpConstants.DisbursementTypeCodes.ACH);
                if (customerBank != null && customerBank.isActive()) {
                    paymentGroup.setBankCode(customerBank.getBankCode());
                    paymentGroup.setBank(customerBank.getBank());
                }
            }

            if (paymentGroup.getBank() == null) {
                LOG.error("performFormat() A bank is needed for ACH for customer: " + customer);
                throw new NoBankForCustomerException("A bank is needed for ACH for customer: " + customer, customer.getChartCode() + "-" + customer.getOrgCode() + "-" + customer.getSubUnitCode());
            }

            paymentGroup.setAchBankRoutingNbr(payeeAchAccount.getBankRoutingNumber());
            paymentGroup.setAdviceEmailAddress(payeeAchAccount.getPayeeEmailAddress());
            paymentGroup.setAchAccountType(payeeAchAccount.getBankAccountTypeCode());

            AchAccountNumber achAccountNumber = new AchAccountNumber();
            achAccountNumber.setAchBankAccountNbr(payeeAchAccount.getBankAccountNumber());
            achAccountNumber.setId(paymentGroup.getId());
            paymentGroup.setAchAccountNumber(achAccountNumber);
        }
    }
    
    /**
     * This method...
     * @param paymentProcess
     * @param checkDisbursementType
     */
    private void combineChecksIntoOne(PaymentProcess paymentProcess, DisbursementType checkDisbursementType)
    {
        PaymentInfo lastPaymentInfo = new PaymentInfo();
       Iterator paymentGroupIterator = this.paymentGroupService.getByProcess(paymentProcess);
        
        int maxNoteLines = getMaxNoteLines();

        while (paymentGroupIterator.hasNext()) {
            PaymentGroup paymentGroup = (PaymentGroup) paymentGroupIterator.next();

            // Only look at checks
            if (checkDisbursementType.equals(paymentGroup.getDisbursementType())) {
                // Attachments, Special Handling and Immediates don't ever get combined
                // Also, don't combine if the XML file says not to do so
                if (paymentGroup.getPymtAttachment().booleanValue() || paymentGroup.getProcessImmediate().booleanValue() || paymentGroup.getPymtSpecialHandling().booleanValue() || (!paymentGroup.getCombineGroups().booleanValue())) {
                    // This one doesn't combine with the next one
                    LOG.debug("performFormat() This payment can't combine " + paymentGroup.getPymtAttachment() + " " + paymentGroup.getProcessImmediate() + " " + paymentGroup.getPymtSpecialHandling() + " " + paymentGroup.getCombineGroups());
                    lastPaymentInfo = null;
                }
                else {
                    PaymentInfo paymentInfo = new PaymentInfo();
                    paymentInfo.customer = paymentGroup.getBatch().getCustomerProfile();
                    paymentInfo.line1Address = paymentGroup.getLine1Address();
                    paymentInfo.payeeName = paymentGroup.getPayeeName();
                    paymentInfo.noteLines = paymentGroup.getNoteLines();
                    paymentInfo.payeeId = paymentGroup.getPayeeId();
                    paymentInfo.payeeIdType = paymentGroup.getPayeeIdTypeCd();
                    paymentInfo.bankCode = paymentGroup.getBankCode();
                    LOG.debug("performFormat() This payment might combine " + paymentInfo);

                    boolean combine = false;
                    if (lastPaymentInfo != null) {
                        if (lastPaymentInfo.equals(paymentInfo)) {
                            if (((lastPaymentInfo.noteLines + paymentInfo.noteLines) <= maxNoteLines)) {
                                LOG.debug("performFormat() Combining");
                                paymentGroup.setDisbursementNbr(new KualiInteger(PdpConstants.CHECK_NUMBER_PLACEHOLDER_VALUE)); // Mark it for later
                                lastPaymentInfo.noteLines += paymentInfo.noteLines;
                                combine = true;
                            }
                        }
                    }

                    if (!combine) {
                        LOG.debug("performFormat() Not combining");
                        lastPaymentInfo = paymentInfo;
                    }
                }
            }
        }
    }

    /**
     * This method...
     * 
     * @param procId
     */
    private void triggerExtract(Integer procId) {
        LOG.debug("triggerExtract() started");

        //saveProcessId(procId);
        String emailAddress = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.NO_PAYMENT_FILE_EMAIL);
        schedulerService.runJob("pdpExtractChecksJob", emailAddress);
    }

    /**
     * This method...
     * @param id
     */
    private void saveProcessId(Integer id) {
        /*Map fields = new HashMap();
         fields.put("parameterNamespaceCode", "KFS-PDP");
         fields.put("parameterDetailTypeCode", "All");
         fields.put("parameterName", PdpConstants.ApplicationParameterKeys.EXTRACT_PROCESS_ID);
         Parameter processParam = (Parameter) businessObjectService.findByPrimaryKey(Parameter.class, fields);
         if (processParam == null) {
         processParam = new Parameter();
         processParam.setParameterNamespaceCode("KFS-PDP");
         processParam.setParameterDetailTypeCode("All");
         processParam.setParameterTypeCode("CONFG");
         processParam.setParameterName(PdpConstants.ApplicationParameterKeys.EXTRACT_PROCESS_ID);
         processParam.setParameterConstraintCode("A");
         processParam.setParameterWorkgroupName("FP_OPERATIONS");
         }
         processParam.setParameterValue(id.toString());
         businessObjectService.save(processParam);*/

        //KFSMI-236
        //this.processDao.createProcessToRun(id);
    }

    /**
     * This method converts the ProcessSummary result list in a FormatResult list
     * @param processSummaryResults
     * @return
     */
    private List<FormatResult> convertProcessSummary2FormatResult(List<ProcessSummary> processSummaryResults) {
        List<FormatResult> results = new ArrayList();
        
        for (ProcessSummary processSummary : processSummaryResults) {
            
            FormatResult formatResult = new FormatResult(processSummary.getProcess().getId().intValue(), processSummary.getCustomer());
            
            formatResult.setSortGroupOverride(processSummary.getSortGroupId());
            formatResult.setAmount(processSummary.getProcessTotalAmount());
            formatResult.setPayments(processSummary.getProcessTotalCount().intValue());
            formatResult.setBeginDisbursementNbr(processSummary.getBeginDisbursementNbr().intValue());
            formatResult.setEndDisbursementNbr(processSummary.getEndDisbursementNbr().intValue());
            formatResult.setDisbursementType(processSummary.getDisbursementType());
            
            results.add(formatResult);
        }
        
        Collections.sort(results);
        
        return results;
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

        endFormatProcess(paymentProcess.getCampus());
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

        DynamicCollectionComparator.sort(customerProfileList, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_CHART_CODE, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_ORG_CODE, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_SUB_UNIT_CODE);

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
     * This is the second pass. It determines the
     * disbursement number and creates GL entries
     * @param campus
     * @param disbursementTypes
     * @param paymentStatusCodes
     * @param p
     * @param fps
     * @throws DisbursementRangeExhaustedException
     * @throws MissingDisbursementRangeException
     */
    private void pass2(String campus, PaymentProcess paymentProcess, PostFormatProcessSummary postFormatProcessSummary) throws DisbursementRangeExhaustedException, MissingDisbursementRangeException {
        LOG.debug("pass2() starting");

        List<DisbursementNumberRange> disbursementRanges = paymentDetailDao.getDisbursementNumberRanges(campus);

        int checkNumber = 0;

        Iterator payGroupIterator = this.paymentGroupService.getByProcess(paymentProcess);
        while (payGroupIterator.hasNext()) {
            PaymentGroup paymentGroup = (PaymentGroup) payGroupIterator.next();
            LOG.debug("performFormat() Payment Group ID " + paymentGroup.getId());

            DisbursementNumberRange range = getRange(disbursementRanges, paymentGroup.getBank(), paymentGroup.getDisbursementType().getCode(), dateTimeService.getCurrentDate());

            if (range == null) {
                String err = "No disbursement range for bank code=" + paymentGroup.getBank().getBankCode() + ", campus Id=" + campus;
                LOG.error("pass2() " + err);
                throw new MissingDisbursementRangeException(err);
            }

            if ("CHCK".equals(paymentGroup.getDisbursementType().getCode())) {
                if ((paymentGroup.getDisbursementNbr() != null) && (paymentGroup.getDisbursementNbr().intValue() == PdpConstants.CHECK_NUMBER_PLACEHOLDER_VALUE)) {
                    paymentGroup.setDisbursementNbr(new KualiInteger(checkNumber));
                }
                else {
                    int number = 1 + range.getLastAssignedDisbNbr().intValue();
                    checkNumber = number; // Save for next payment
                    if (number > range.getEndDisbursementNbr().intValue()) {
                        String err = "No more disbursement numbers for bank code=" + paymentGroup.getBank().getBankCode() + ", campus Id=" + campus;
                        LOG.error("pass2() " + err);
                        throw new MissingDisbursementRangeException(err);
                    }
                    paymentGroup.setDisbursementNbr(new KualiInteger(number));

                    range.setLastAssignedDisbNbr(new KualiInteger(number));

                    // Update the summary information
                    postFormatProcessSummary.setDisbursementNumber(paymentGroup, new Integer(number));
                }
            }
            else if ("ACH".equals(paymentGroup.getDisbursementType().getCode())) {
                int number = 1 + range.getLastAssignedDisbNbr().intValue();
                if (number > range.getEndDisbursementNbr().intValue()) {
                    String err = "No more disbursement numbers for bank code=" + paymentGroup.getBank().getBankCode() + ", campus Id=" + campus;
                    LOG.error("pass2() " + err);
                    throw new MissingDisbursementRangeException(err);
                }
                paymentGroup.setDisbursementNbr(new KualiInteger(number));

                range.setLastAssignedDisbNbr(new KualiInteger(number));

                // Update the summary information
                postFormatProcessSummary.setDisbursementNumber(paymentGroup, new Integer(number));
            }
            else {
                // if it isn't check or ach, we're in trouble
                LOG.error("pass2() Payment group " + paymentGroup.getId() + " must be CHCK or ACH.  It is: " + paymentGroup.getDisbursementType());
                throw new IllegalArgumentException("Payment group " + paymentGroup.getId() + " must be Check or ACH");
            }
            this.businessObjectService.save(paymentGroup);

            // Generate a GL entry for CHCK & ACH
            glPendingTransactionService.generatePaymentGeneralLedgerPendingEntry(paymentGroup);
        }

        // Update all the ranges
        LOG.debug("pass2() Save ranges");
        int savedRangesCount = 0;
        for (DisbursementNumberRange element : disbursementRanges) {
            savedRangesCount++;
            this.businessObjectService.save(element);
        }
        LOG.debug("pass2() " + savedRangesCount + " ranges saved");
    }

    /**
     * This method...
     * @param ranges
     * @param bank
     * @param disbursementTypeCode
     * @param today
     * @return
     */
    private DisbursementNumberRange getRange(List<DisbursementNumberRange> ranges, Bank bank, String disbursementTypeCode, Date today) {
        LOG.debug("getRange() Looking for bank = " + bank.getBankCode() + " for " + today);
        for (DisbursementNumberRange range : ranges) {
            Date disbursementNbrEffectiveDate = range.getDisbNbrEffectiveDt();
            Date disbursementNbrExpirationDate = range.getDisbNbrExpirationDt();

            if (range.getBank().getBankCode().equals(bank.getBankCode()) && range.getDisbursementTypeCode().equals(disbursementTypeCode) && (today.getTime() >= disbursementNbrEffectiveDate.getTime()) && (today.getTime() <= disbursementNbrExpirationDate.getTime())) {
                LOG.debug("getRange() Found match");
                return range;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#getFormatSummary(java.lang.Integer)
     */
    public List getFormatSummary(Integer procId) {
        LOG.debug("getFormatSummary() starting");
        
        Map fieldValues = new HashMap();
        fieldValues.put(PdpPropertyConstants.ProcessSummary.PROCESS_SUMMARY_PROCESS_ID, procId);
        
        List processSummaryResults = (List) this.businessObjectService.findMatching(ProcessSummary.class, fieldValues);
        return convertProcessSummary2FormatResult(processSummaryResults);
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#getMostCurrentProcesses()
     */
    public List getMostCurrentProcesses() {
        LOG.debug("getMostCurrent() starting");
        return processDao.getMostCurrentProcesses(new Integer(getFormatSummaryListSize()));
    }

    /**
     * This method...
     * @param fpd
     */
    public void setFormatPaymentDao(FormatPaymentDao fpd) {
        formatPaymentDao = fpd;
    }

    /**
     * This method...
     * @param gs
     */
    public void setGlPendingTransactionService(PendingTransactionService gs) {
        glPendingTransactionService = gs;
    }

    /**
     * This method...
     * @param as
     */
    public void setAchService(AchService as) {
        achService = as;
    }

    /**
     * This method...
     * @param pd
     */
    public void setProcessDao(ProcessDao pd) {
        processDao = pd;
    }

    /**
     * This method...
     * @param pgd
     */
    public void setPaymentGroupDao(PaymentGroupDao pgd) {
        paymentGroupDao = pgd;
    }

    /**
     * This method...
     * @param pdd
     */
    public void setPaymentDetailDao(PaymentDetailDao pdd) {
        paymentDetailDao = pdd;
    }

    /**
     * This method...
     * @param ss
     */
    public void setSchedulerService(SchedulerService ss) {
        schedulerService = ss;
    }

    /**
     * This method...
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * This method...
     * @param bos
     */
    public void setBusinessObjectService(BusinessObjectService bos) {
        this.businessObjectService = bos;
    }

    /**
     * This method...
     * @return
     */
    public KualiCodeService getKualiCodeService() {
        return kualiCodeService;
    }

    /**
     * This method...
     * @param kualiCodeService
     */
    public void setKualiCodeService(KualiCodeService kualiCodeService) {
        this.kualiCodeService = kualiCodeService;
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#setPaymentGroupService(org.kuali.kfs.pdp.service.PaymentGroupService)
     */
    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    /**
     * This class...
     */
    private class PaymentInfo {
        public CustomerProfile customer;
        public String payeeName;
        public String line1Address;
        public int noteLines;
        public String payeeId;
        public String payeeIdType;
        public String bankCode;

        public int hashCode() {
            return new HashCodeBuilder(3, 5).append(customer).append(payeeName).append(line1Address).toHashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof PaymentInfo)) {
                return false;
            }
            PaymentInfo o = (PaymentInfo) obj;
            return new EqualsBuilder().append(customer, o.customer).append(payeeName, o.payeeName).append(line1Address, o.line1Address).append(payeeId, o.payeeId).append(payeeIdType, o.payeeIdType).append(bankCode, o.bankCode).isEquals();
        }

        public String toString() {
            return new ToStringBuilder(this).append("customer", this.customer).append("payeeName", this.payeeName).append("line1Address", this.line1Address).append("noteLines", this.noteLines).append("payeeId", this.payeeId).append("payeeIdType", this.payeeIdType).append("bankCode", this.bankCode).toString();
        }
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
