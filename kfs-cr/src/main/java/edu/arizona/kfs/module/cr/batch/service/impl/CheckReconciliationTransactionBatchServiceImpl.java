package edu.arizona.kfs.module.cr.batch.service.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.sys.businessobject.Bank;

import edu.arizona.kfs.module.cr.batch.service.CheckReconciliationTransactionBatchService;
import edu.arizona.kfs.module.cr.businessobject.CheckReconciliation;
import edu.arizona.kfs.module.cr.dataaccess.CheckReconciliationDao;
import edu.arizona.kfs.module.cr.exception.BankNotFoundException;
import edu.arizona.kfs.module.cr.service.CheckReconciliationPendingTransactionService;

public class CheckReconciliationTransactionBatchServiceImpl implements CheckReconciliationTransactionBatchService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CheckReconciliationTransactionBatchServiceImpl.class);

    // Injected Objects
    private CheckReconciliationDao checkReconciliationDao;
    private CheckReconciliationPendingTransactionService checkReconciliationPendingTransactionService;

    // Service Objects
    private List<Bank> banks;

    // Spring Injectors

    public void setCheckReconciliationDao(CheckReconciliationDao checkReconciliationDao) {
        this.checkReconciliationDao = checkReconciliationDao;
    }

    public void setCheckReconciliationPendingTransactionService(CheckReconciliationPendingTransactionService checkReconciliationPendingTransactionService) {
        this.checkReconciliationPendingTransactionService = checkReconciliationPendingTransactionService;
    }

    // Public service methods

    @Override
    public boolean initializeServiceObjects() {
        banks = checkReconciliationDao.getAllBanks();
        if (banks.size() == 0) {
            throw new BankNotFoundException("No banks found.");
        }
        return true;
    }

    @Override
    public boolean processStoppedPayments() {
        List<CheckReconciliation> records = checkReconciliationDao.getUnprocessedPdpStoppedChecks();

        for (CheckReconciliation cr : records) {
            List<String> bankCodes = checkReconciliationDao.getBankCodesForShortName(cr.getBankAccountNumber());

            if (bankCodes.size() > 0) {
                List<PaymentGroup> paymentGroups = checkReconciliationDao.getAllPaymentGroupForSearchCriteria(cr.getCheckNumber(), bankCodes, cr.getCheckDate());
                if (paymentGroups.isEmpty()) {
                    LOG.warn("No payment group found id : " + cr.getId());
                }
                for (PaymentGroup paymentGroup : paymentGroups) {
                    checkReconciliationPendingTransactionService.generatePendingTransactionStop(paymentGroup);
                    updatePaymentGroup(paymentGroup, cr.getStatus());
                    LOG.info("Generated Stop GL Pending Transacation for Check Reconciliation " + cr.getId());
                }
                updateCheckReconciliation(cr);
            }
        }
        return true;
    }

    @Override
    public boolean processCancelledPayments() {
        List<CheckReconciliation> records = checkReconciliationDao.getUnprocessedPdpCancelledChecks();
        for (CheckReconciliation cr : records) {
            List<String> bankCodes = checkReconciliationDao.getBankCodesForShortName(cr.getBankAccountNumber());
            if (bankCodes.size() > 0) {
                List<PaymentGroup> paymentGroups = checkReconciliationDao.getAllPaymentGroupForSearchCriteria(cr.getCheckNumber(), bankCodes, cr.getCheckDate());
                if (paymentGroups.isEmpty()) {
                    LOG.warn("No payment group found id : " + cr.getId());
                }
                for (PaymentGroup paymentGroup : paymentGroups) {
                    checkReconciliationPendingTransactionService.generatePendingTransactionCancel(paymentGroup);
                    updatePaymentGroup(paymentGroup, cr.getStatus());
                    LOG.info("Generated Cancelled GL Pending Transacation for Check Reconciliation " + cr.getId());
                }
                updateCheckReconciliation(cr);
            }
        }
        return true;
    }

    @Override
    public boolean processStalePayments() {
        List<CheckReconciliation> records = checkReconciliationDao.getUnprocessedPdpStaleChecks();
        for (CheckReconciliation cr : records) {
            List<String> bankCodes = checkReconciliationDao.getBankCodesForShortName(cr.getBankAccountNumber());

            if (bankCodes.size() > 0) {
                List<PaymentGroup> paymentGroups = checkReconciliationDao.getAllPaymentGroupForSearchCriteria(cr.getCheckNumber(), bankCodes, cr.getCheckDate());
                if (paymentGroups.isEmpty()) {
                    LOG.warn("No payment group found id : " + cr.getId());
                }
                for (PaymentGroup paymentGroup : paymentGroups) {
                    checkReconciliationPendingTransactionService.generatePendingTransactionStale(paymentGroup);
                    updatePaymentGroup(paymentGroup, cr.getStatus());
                    LOG.info("Generated Stale GL Pending Transacation for Check Reconciliation " + cr.getId());
                }
                updateCheckReconciliation(cr);
            }
        }
        return true;
    }

    @Override
    public boolean processVoidedPayments() {
        List<CheckReconciliation> records = checkReconciliationDao.getUnprocessedPdpVoidedChecks();
        for (CheckReconciliation cr : records) {
            List<String> bankCodes = checkReconciliationDao.getBankCodesForShortName(cr.getBankAccountNumber());

            if (bankCodes.size() > 0) {
                List<PaymentGroup> paymentGroups = checkReconciliationDao.getAllPaymentGroupForSearchCriteria(cr.getCheckNumber(), bankCodes, cr.getCheckDate());

                if (paymentGroups.isEmpty()) {
                    LOG.warn("No payment group found id : " + cr.getId());
                }
                for (PaymentGroup paymentGroup : paymentGroups) {
                    updatePaymentGroup(paymentGroup, cr.getStatus());
                    LOG.info("Updated Voided Payment Group for Check Reconciliation " + cr.getId());
                }
                updateCheckReconciliation(cr);
            }
        }
        return true;
    }

    // Private methods

    private void updatePaymentGroup(PaymentGroup paymentGroup, String status) {
        PaymentStatus code = checkReconciliationDao.findMatchingPaymentStatus(status);
        paymentGroup.setPaymentStatus(code);
        paymentGroup.setLastUpdate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        checkReconciliationDao.save(paymentGroup);
    }

    private void updateCheckReconciliation(CheckReconciliation cr) {
        cr.setGlTransIndicator(Boolean.TRUE);
        checkReconciliationDao.save(cr);
        LOG.info("Updated Check Reconciliation " + cr.getId());
    }
}
