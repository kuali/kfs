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
import org.kuali.kfs.pdp.dataaccess.FormatProcessDao;
import org.kuali.kfs.pdp.dataaccess.PaymentDetailDao;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupDao;
import org.kuali.kfs.pdp.dataaccess.ProcessDao;
import org.kuali.kfs.pdp.dataaccess.ProcessSummaryDao;
import org.kuali.kfs.pdp.exception.ConfigurationError;
import org.kuali.kfs.pdp.service.AchService;
import org.kuali.kfs.pdp.service.FormatService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PendingTransactionService;
import org.kuali.kfs.pdp.service.impl.exception.DisbursementRangeExhaustedException;
import org.kuali.kfs.pdp.service.impl.exception.MissingDisbursementRangeException;
import org.kuali.kfs.pdp.service.impl.exception.NoBankForCustomerException;
import org.kuali.kfs.pdp.util.CustomerProfileListComparator;
import org.kuali.kfs.pdp.util.DisbursementNumberRangeListComparator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.service.KualiCodeService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiInteger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class FormatServiceImpl implements FormatService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatServiceImpl.class);

    private FormatProcessDao formatProcessDao;
    private PaymentDetailDao paymentDetailDao;
    private PaymentGroupDao paymentGroupDao;
    private ProcessSummaryDao processSummaryDao;
    private ProcessDao processDao;
    private AchService achService;
    private PendingTransactionService glPendingTransactionService;
    private ParameterService parameterService;
    private FormatPaymentDao formatPaymentDao;
    private SchedulerService schedulerService;
    private BusinessObjectService businessObjectService;
    private KualiCodeService kualiCodeService;
    private PaymentGroupService paymentGroupService;

    /**
     * Constructs a FormatServiceImpl.java.
     */
    public FormatServiceImpl() {
        super();
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#formatSelectionAction(org.kuali.module.pdp.bo.PdpUser, boolean)
     */
    public FormatSelection formatSelectionAction(UniversalUser user, boolean clearFormat) {
        LOG.debug("formatSelectionAction() started");

        FormatSelection fs = new FormatSelection();
        String campus = user.getCampusCode();
        Date startDate = getFormatProcessStartDate(campus);

        fs.setCampus(campus);

        // If they want to clear the flag, do it
        if ((startDate != null) && clearFormat) {
            startDate = null;
            endFormatProcess(campus);
        }
        
        fs.setStartDate(startDate);

        if (startDate == null) {
            fs.setCustomerList(getAllCustomerProfiles());
            fs.setRangeList(getAllDisbursementNumberRanges());
        }
        
        return fs;
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#getFormatProcessStartDate(java.lang.String)
     */
    public Date getFormatProcessStartDate(String campus) {
        LOG.debug("getFormatProcessStartDate() started");
        
        Map primaryKeys = new HashMap();
        primaryKeys.put(PdpPropertyConstants.PHYS_CAMPUS_PROCESS_CODE, campus);
        FormatProcess fp = (FormatProcess) this.businessObjectService.findByPrimaryKey(FormatProcess.class, primaryKeys);
        
        if (fp != null) {
            LOG.debug("getFormatProcessStartDate() found");
            return new Date(fp.getBeginFormat().getTime());
        }
        else {
            LOG.debug("getFormatProcessStartDate() not found");
            return null;
        }
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
    public List<FormatResult> performFormat(Integer procId) {
        LOG.debug("performFormat() started");

        PaymentProcess proc = processDao.get(procId);
        if (proc == null) {
            LOG.error("performFormat() Invalid proc ID " + procId);
            throw new ConfigurationError("Invalid proc ID");
        }
        
        DisbursementType checkDisbursementType = (DisbursementType) kualiCodeService.getByCode(DisbursementType.class, PdpConstants.DisbursementTypeCodes.CHECK);
        DisbursementType achDisbursementType = (DisbursementType) kualiCodeService.getByCode(DisbursementType.class, PdpConstants.DisbursementTypeCodes. ACH);

        PaymentStatus extractedPaymentStatus = (PaymentStatus) kualiCodeService.getByCode(PaymentStatus.class, PdpConstants.PaymentStatusCodes.EXTRACTED);
        PaymentStatus pendingPaymentStatus = (PaymentStatus) kualiCodeService.getByCode(PaymentStatus.class, PdpConstants.PaymentStatusCodes.PENDING_ACH);


        int maxNoteLines = getMaxNoteLines();

        // Step one, get ACH or Check, Bank info, ACH info, sorting
        Iterator groups = paymentGroupDao.getByProcess(proc);

        PostFormatProcessSummary fps = new PostFormatProcessSummary();
        while (groups.hasNext()) {
            PaymentGroup pg = (PaymentGroup) groups.next();
            LOG.debug("performFormat() Step 1 Payment Group ID " + pg.getId());

            CustomerProfile customer = pg.getBatch().getCustomerProfile();

            // Set the sort field to be saved in the database
            pg.setSortValue(paymentGroupService.getSortGroupId(pg));

            pg.setDisbursementDate(proc.getProcessTimestamp());
            pg.setPhysCampusProcessCd(proc.getCampus());
            pg.setProcess(proc);

            // If any one of the payment details in the group are negative, we always force a check
            boolean noNegativeDetails = true;

            // If any one of the payment details in the group are negative, we always force a check
            List<PaymentDetail> paymentDetailsList = pg.getPaymentDetails();
            for (PaymentDetail pd : paymentDetailsList) {
                if (pd.getNetPaymentAmount().doubleValue() < 0) {
                    LOG.debug("performFormat() Payment Group " + pg + " has payment detail net payment amount " + pd.getNetPaymentAmount());
                    LOG.debug("performFormat() Forcing a Check for Group");
                    noNegativeDetails = false;
                    break;
                }
            }

            // hold original bank code to log any change
            String origBankCode = pg.getBankCode();

            // Attachments, Process Immediate & Special Handling are always checks
            // If there isn't a PSD Transaction code for the customer, don't even look to see if any payment is ACH
            // If the payment ID is X, it's always a check
            // If any one of the payment details in the group are negative, we always force a check
            PayeeAchAccount payeeAchAccount = null;
            boolean check = true;
            if ((!PdpConstants.PayeeIdTypeCodes.OTHER.equals(pg.getPayeeIdTypeCd())) && (!"".equals(pg.getPayeeIdTypeCd())) && (pg.getPayeeIdTypeCd() != null) && (!"".equals(pg.getPayeeId())) && (pg.getPayeeId() != null) && (!pg.getPymtAttachment().booleanValue()) && (!pg.getProcessImmediate().booleanValue()) && (!pg.getPymtSpecialHandling().booleanValue()) && (customer.getPsdTransactionCode() != null) && (noNegativeDetails)) {
                LOG.debug("performFormat() Checking ACH");
                payeeAchAccount = achService.getAchInformation(pg.getPayeeIdTypeCd(), pg.getPayeeId(), customer.getPsdTransactionCode());
                check = (payeeAchAccount == null);
            }

            if (check) {
                PaymentStatus ps = extractedPaymentStatus;
                LOG.debug("performFormat() Check: " + ps);
                pg.setDisbursementType(checkDisbursementType);
                pg.setPaymentStatus(ps);

                // set bank, use group bank unless not given or not valid for checks
                if (pg.getBank() == null || !pg.getBank().isBankCheckIndicator()) {
                    CustomerBank cb = customer.getCustomerBankByDisbursementType(PdpConstants.DisbursementTypeCodes.CHECK);
                    if (cb != null && cb.isActive()) {
                        pg.setBankCode(cb.getBankCode());
                        pg.setBank(cb.getBank());
                    }
                }

                if (pg.getBank() == null) {
                    LOG.error("performFormat() A bank is needed for CHCK for customer: " + customer);
                    throw new NoBankForCustomerException("A bank is needed for CHCK for customer: " + customer, customer.getChartCode() + "-" + customer.getOrgCode() + "-" + customer.getSubUnitCode());
                }
            }
            else {
                PaymentStatus ps = pendingPaymentStatus;
                LOG.debug("performFormat() ACH: " + ps);
                pg.setDisbursementType(achDisbursementType);
                pg.setPaymentStatus(ps);

                // set bank, use group bank unless not given or not valid for ACH
                if (pg.getBank() == null || !pg.getBank().isBankAchIndicator()) {
                    CustomerBank cb = customer.getCustomerBankByDisbursementType(PdpConstants.DisbursementTypeCodes.ACH);
                    if (cb != null && cb.isActive()) {
                        pg.setBankCode(cb.getBankCode());
                        pg.setBank(cb.getBank());
                    }
                }

                if (pg.getBank() == null) {
                    LOG.error("performFormat() A bank is needed for ACH for customer: " + customer);
                    throw new NoBankForCustomerException("A bank is needed for ACH for customer: " + customer, customer.getChartCode() + "-" + customer.getOrgCode() + "-" + customer.getSubUnitCode());
                }

                pg.setAchBankRoutingNbr(payeeAchAccount.getBankRoutingNumber());
                pg.setAdviceEmailAddress(payeeAchAccount.getPayeeEmailAddress());
                pg.setAchAccountType(payeeAchAccount.getBankAccountTypeCode());

                AchAccountNumber aan = new AchAccountNumber();
                aan.setAchBankAccountNbr(payeeAchAccount.getBankAccountNumber());
                aan.setId(pg.getId());
                pg.setAchAccountNumber(aan);
            }

            // create payment history record if bank was changed
            if (!pg.getBankCode().equals(origBankCode)) {
                PaymentGroupHistory paymentGroupHistory = new PaymentGroupHistory();

                paymentGroupHistory.setPaymentChangeCode(PdpConstants.PaymentChangeCodes.BANK_CHNG_CD);
                paymentGroupHistory.setOrigBankCode(origBankCode);
                paymentGroupHistory.setOrigPaymentStatus(pg.getPaymentStatus());
                paymentGroupHistory.setChangeUserId(KFSConstants.SYSTEM_USER);
                paymentGroupHistory.setPaymentGroup(pg);

                businessObjectService.save(paymentGroupHistory);
            }

            this.businessObjectService.save(pg);

            // Add to summary information
            fps.add(pg);
        }

        // step 2 figure out if we combine checks into one
        LOG.debug("performFormat() Combining");

        PaymentInfo lastPaymentInfo = new PaymentInfo();
        groups = paymentGroupDao.getByProcess(proc);

        while (groups.hasNext()) {
            PaymentGroup pg = (PaymentGroup) groups.next();

            // Only look at checks
            if (checkDisbursementType.equals(pg.getDisbursementType())) {
                // Attachments, Special Handling and Immediates don't ever get combined
                // Also, don't combine if the XML file says not to do so
                if (pg.getPymtAttachment().booleanValue() || pg.getProcessImmediate().booleanValue() || pg.getPymtSpecialHandling().booleanValue() || (!pg.getCombineGroups().booleanValue())) {
                    // This one doesn't combine with the next one
                    LOG.debug("performFormat() This payment can't combine " + pg.getPymtAttachment() + " " + pg.getProcessImmediate() + " " + pg.getPymtSpecialHandling() + " " + pg.getCombineGroups());
                    lastPaymentInfo = null;
                }
                else {
                    PaymentInfo pi = new PaymentInfo();
                    pi.customer = pg.getBatch().getCustomerProfile();
                    pi.line1Address = pg.getLine1Address();
                    pi.payeeName = pg.getPayeeName();
                    pi.noteLines = pg.getNoteLines();
                    pi.payeeId = pg.getPayeeId();
                    pi.payeeIdType = pg.getPayeeIdTypeCd();
                    pi.bankCode = pg.getBankCode();
                    LOG.debug("performFormat() This payment might combine " + pi);

                    boolean combine = false;
                    if (lastPaymentInfo != null) {
                        if (lastPaymentInfo.equals(pi)) {
                            if (((lastPaymentInfo.noteLines + pi.noteLines) <= maxNoteLines)) {
                                LOG.debug("performFormat() Combining");
                                pg.setDisbursementNbr(new KualiInteger(PdpConstants.CHECK_NUMBER_PLACEHOLDER_VALUE)); // Mark it for later
                                lastPaymentInfo.noteLines += pi.noteLines;
                                combine = true;
                            }
                        }
                    }

                    if (!combine) {
                        LOG.debug("performFormat() Not combining");
                        lastPaymentInfo = pi;
                    }
                }
            }
        }

        // step 3 now assign disbursement numbers
        LOG.debug("performFormat() Assigning disbursement numbers");
        pass2(proc.getCampus(), proc, fps);

        // step 4 save the summarizing info
        LOG.debug("performFormat() Save summarizing information");
        fps.save(processSummaryDao);

        // step 5 tell the extract batch job to start
        LOG.debug("performFormat() Start extract batch job");
        triggerExtract(procId);

        // step 6 end the format process for this campus
        LOG.debug("performFormat() End the format process for this campus");
        endFormatProcess(proc.getCampus());

        // step 7 return all the process summaries
        List processSummaryResults = processSummaryDao.getByPaymentProcess(proc);
        return convertProcessSummary2FormatResult(processSummaryResults);
    }

    /**
     * This method...
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
     * This method...
     * @param processSummaryResults
     * @return
     */
    private List<FormatResult> convertProcessSummary2FormatResult(List<ProcessSummary> processSummaryResults) {
        List<FormatResult> results = new ArrayList();
        for (ProcessSummary element : processSummaryResults) {
            FormatResult fr = new FormatResult(element.getProcess().getId().intValue(), element.getCustomer());
            fr.setSortGroupOverride(element.getSortGroupId());
            fr.setAmount(element.getProcessTotalAmount());
            fr.setPayments(element.getProcessTotalCount().intValue());
            fr.setBeginDisbursementNbr(element.getBeginDisbursementNbr().intValue());
            fr.setEndDisbursementNbr(element.getEndDisbursementNbr().intValue());
            fr.setDisbursementType(element.getDisbursementType());
            results.add(fr);
        }
        Collections.sort(results);
        return results;
    }

    // If the start format process was run and the user doesn't want to continue,
    // this needs to be run to set all payments back to open
    /**
     * @see org.kuali.kfs.pdp.service.FormatService#clearUnfinishedFormat(java.lang.Integer)
     */
    public void clearUnfinishedFormat(Integer procId) {
        LOG.debug("clearUnfinishedFormat() started");

        PaymentProcess proc = processDao.get(procId);
        LOG.debug("clearUnfinishedFormat() Process: " + proc);

        formatPaymentDao.unmarkPaymentsForFormat(proc);

        endFormatProcess(proc.getCampus());
    }

    // If the start format process was run and errored out,
    // this needs to be run to allow formats to continue to function
    /**
     * @see org.kuali.kfs.pdp.service.FormatService#resetFormatPayments(java.lang.Integer)
     */
    public void resetFormatPayments(Integer procId) {
        LOG.debug("resetFormatPayments() started");
        clearUnfinishedFormat(procId);
    }

    // Mark the process log so a format only happens once per campus. Mark all the
    // payments that will be formatted and return a summary
    /**
     * @see org.kuali.kfs.pdp.service.FormatService#startFormatProcess(org.kuali.rice.kns.bo.user.UniversalUser, java.lang.String, java.util.List, java.util.Date, java.lang.String)
     */
    public List startFormatProcess(UniversalUser user, String campus, List<CustomerProfile> customers, Date paydate, String paymentTypes) {
        LOG.debug("startFormatProcess() started");

        for (CustomerProfile element : customers) {
            LOG.debug("startFormatProcess() Customer: " + element);
        }

        PaymentStatus formatStatus = (PaymentStatus) kualiCodeService.getByCode(PaymentStatus.class, PdpConstants.PaymentStatusCodes.FORMAT);

        Date now = new Date();
        FormatProcess fp = new FormatProcess();
        fp.setPhysicalCampusProcessCode(campus);
        fp.setBeginFormat(new Timestamp(now.getTime()));
        this.businessObjectService.save(fp);
        
        // Create the process
        PaymentProcess p = processDao.createProcess(campus, user);

        // Mark all of them ready for format
        formatPaymentDao.markPaymentsForFormat(p, customers, paydate, paymentTypes);

        // summarize them
        PreFormatProcessSummary fps = new PreFormatProcessSummary();
        Iterator i = paymentGroupDao.getByProcess(p);

        int count = 0;
        while (i.hasNext()) {
            PaymentGroup pg = (PaymentGroup) i.next();

            count++;
            fps.add(pg);
        }

        if (count == 0) {
            LOG.debug("startFormatProcess() No payments to process.  Format process ending");
            endFormatProcess(campus);
        }

        return convertProcessSummary2FormatResult(fps.getProcessSummaryList());
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
        
        Collections.sort(customerProfileList, new CustomerProfileListComparator());
        
        return customerProfileList;
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#getAllDisbursementNumberRanges()
     */
    public List<DisbursementNumberRange> getAllDisbursementNumberRanges() {
        LOG.debug("getAllDisbursementNumberRanges() started");

        List<DisbursementNumberRange> disbursementNumberRangeList = (List<DisbursementNumberRange>) this.businessObjectService.findAll(DisbursementNumberRange.class);
        
        Collections.sort(disbursementNumberRangeList, new DisbursementNumberRangeListComparator());
        
        return disbursementNumberRangeList;
    }

    // This is the second pass. It determines the
    // disbursement number and creates GL entries
    /**
     * This method...
     * @param campus
     * @param disbursementTypes
     * @param paymentStatusCodes
     * @param p
     * @param fps
     * @throws DisbursementRangeExhaustedException
     * @throws MissingDisbursementRangeException
     */
    private void pass2(String campus, PaymentProcess p, PostFormatProcessSummary fps) throws DisbursementRangeExhaustedException, MissingDisbursementRangeException {
        LOG.debug("pass2() starting");

        // This is the date used for last update
        Date now = new Date();
        Timestamp nowTs = new Timestamp(now.getTime());

        List<DisbursementNumberRange> disbursementRanges = paymentDetailDao.getDisbursementNumberRanges(campus);

        int checkNumber = 0;

        Iterator payGroupIterator = paymentGroupDao.getByProcess(p);
        while (payGroupIterator.hasNext()) {
            PaymentGroup pg = (PaymentGroup) payGroupIterator.next();
            LOG.debug("performFormat() Payment Group ID " + pg.getId());

            DisbursementNumberRange range = getRange(disbursementRanges, pg.getBank(), pg.getDisbursementType().getCode(), now);

            if (range == null) {
                String err = "No disbursement range for bank code=" + pg.getBank().getBankCode() + ", campus Id=" + campus;
                LOG.error("pass2() " + err);
                throw new MissingDisbursementRangeException(err);
            }

            if ("CHCK".equals(pg.getDisbursementType().getCode())) {
                if ((pg.getDisbursementNbr() != null) && (pg.getDisbursementNbr().intValue() == PdpConstants.CHECK_NUMBER_PLACEHOLDER_VALUE)) {
                    pg.setDisbursementNbr(new KualiInteger(checkNumber));
                }
                else {
                    int number = 1 + range.getLastAssignedDisbNbr().intValue();
                    checkNumber = number; // Save for next payment
                    if (number > range.getEndDisbursementNbr().intValue()) {
                        String err = "No more disbursement numbers for bank code=" + pg.getBank().getBankCode() + ", campus Id=" + campus;
                        LOG.error("pass2() " + err);
                        throw new MissingDisbursementRangeException(err);
                    }
                    pg.setDisbursementNbr(new KualiInteger(number));

                    range.setLastAssignedDisbNbr(new KualiInteger(number));

                    // Update the summary information
                    fps.setDisbursementNumber(pg, new Integer(number));
                }
            }
            else if ("ACH".equals(pg.getDisbursementType().getCode())) {
                int number = 1 + range.getLastAssignedDisbNbr().intValue();
                if (number > range.getEndDisbursementNbr().intValue()) {
                    String err = "No more disbursement numbers for bank code=" + pg.getBank().getBankCode() + ", campus Id=" + campus;
                    LOG.error("pass2() " + err);
                    throw new MissingDisbursementRangeException(err);
                }
                pg.setDisbursementNbr(new KualiInteger(number));

                range.setLastAssignedDisbNbr(new KualiInteger(number));

                // Update the summary information
                fps.setDisbursementNumber(pg, new Integer(number));
            }
            else {
                // if it isn't check or ach, we're in trouble
                LOG.error("pass2() Payment group " + pg.getId() + " must be CHCK or ACH.  It is: " + pg.getDisbursementType());
                throw new IllegalArgumentException("Payment group " + pg.getId() + " must be Check or ACH");
            }
            this.businessObjectService.save(pg);

            // Generate a GL entry for CHCK & ACH
                     glPendingTransactionService.generatePaymentGeneralLedgerPendingEntry(pg);
            }

        // Update all the ranges
        LOG.debug("pass2() Save ranges");
        int rc = 0;
        for (DisbursementNumberRange element : disbursementRanges) {
            rc++;
            paymentDetailDao.saveDisbursementNumberRange(element);
        }
        LOG.debug("pass2() " + rc + " ranges saved");
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
        for (DisbursementNumberRange element : ranges) {
            Date eff = element.getDisbNbrEffectiveDt();
            Date exp = element.getDisbNbrExpirationDt();

            if (element.getBank().getBankCode().equals(bank.getBankCode()) && element.getDisbursementTypeCode().equals(disbursementTypeCode) && (today.getTime() >= eff.getTime()) && (today.getTime() <= exp.getTime())) {
                LOG.debug("getRange() Found match");
                return element;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.pdp.service.FormatService#getFormatSummary(java.lang.Integer)
     */
    public List getFormatSummary(Integer procId) {
        LOG.debug("getFormatSummary() starting");
        List processSummaryResults = processSummaryDao.getByProcessId(procId);
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
     * @param fpd
     */
    public void setFormatProcessDao(FormatProcessDao fpd) {
        formatProcessDao = fpd;
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
     * @param psd
     */
    public void setProcessSummaryDao(ProcessSummaryDao psd) {
        processSummaryDao = psd;
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

}
