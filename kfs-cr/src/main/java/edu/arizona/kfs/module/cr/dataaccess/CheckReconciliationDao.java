package edu.arizona.kfs.module.cr.dataaccess;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.module.cr.businessobject.CheckReconciliation;

/**
 * Check Reconciliation Dao
 */
public interface CheckReconciliationDao {

    /**
     * Get All CheckReconciliation objects
     */
    public List<CheckReconciliation> getAll();

    /**
     * Get New Check Reconciliations
     */
    public List<CheckReconciliation> getNewCheckReconciliations(Collection<Bank> banks);

    /**
     * Returns a list of {@link CheckReconciliation} objects that have a matching check number, amount, and bank with a source code of B and status of EXCP
     */
    public List<CheckReconciliation> getBankFileCreatedCheckReconciliations(Integer checkNumber, KualiDecimal amount, String bankAccountNumber);

    /**
     * Returns a list of {@link CheckReconciliation} objects with a checkDate between the startDate and endDate and who's status is not in statusCodes.
     */
    public List<CheckReconciliation> getAllCheckReconciliationForSearchCriteria(Date startDate, Date endDate, Set<String> statusCodes);

    /**
     * Get AllPaymentGroupForSearchCriteria
     */
    public List<PaymentGroup> getAllPaymentGroupForSearchCriteria(Integer disbNbr, Collection<String> bankCodes, Date disbursementDate);

    /**
     * Returns a list of {@link CheckReconciliation} objects by the given parameters.
     */
    public List<CheckReconciliation> getCheckReconciliation(Integer checkNumber, String bankAccountNumber, KualiDecimal amount);

    /**
     * Get all PdpPaymentGroup objects that match the CheckReconciliation
     */
    public List<PaymentGroup> getPdpPaymentGroups(CheckReconciliation checkReconciliation);

    /**
     * Get all PaymentGroupHistory objects that match the CheckReconciliation
     */
    public List<PaymentGroupHistory> getPdpPaymentGroupHistories(CheckReconciliation checkReconciliation);

    /**
     * Get all of the Bank objects in the database
     */
    public List<Bank> getAllBanks();

    /**
     * Save the CheckReconciliation object
     */
    public CheckReconciliation save(CheckReconciliation cr);

    /**
     * Save the PaymentGroup object
     */
    public PaymentGroup save(PaymentGroup paymentGroup);

    /**
     * Find the PaymentStatus object that matches with the checkStatus
     */
    public PaymentStatus findMatchingPaymentStatus(String checkStatus);

    /**
     * Returns a list of {@link CheckReconciliation} objects for unprocessed PDP Stopped checks.
     * 
     * @return
     */
    public List<CheckReconciliation> getUnprocessedPdpStoppedChecks();

    /**
     * Returns a list of {@link CheckReconciliation} objects for unprocessed PDP Cancelled checks.
     * 
     * @return
     */
    public List<CheckReconciliation> getUnprocessedPdpCancelledChecks();

    /**
     * Returns a list of {@link CheckReconciliation} objects for unprocessed PDP Stale checks.
     * 
     * @return
     */
    public List<CheckReconciliation> getUnprocessedPdpStaleChecks();

    /**
     * Returns a list of {@link CheckReconciliation} objects for unprocessed PDP Voided checks.
     * 
     * @return
     */
    public List<CheckReconciliation> getUnprocessedPdpVoidedChecks();

    /**
     * Returns the bank code(s) for a given short name, if any.
     * 
     * @return
     */
    public List<String> getBankCodesForShortName(String bankShortName);

}
