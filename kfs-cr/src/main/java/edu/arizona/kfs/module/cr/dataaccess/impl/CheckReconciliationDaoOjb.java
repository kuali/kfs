package edu.arizona.kfs.module.cr.dataaccess.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentGroupHistory;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.module.cr.CrConstants;
import edu.arizona.kfs.module.cr.CrPropertyConstants;
import edu.arizona.kfs.module.cr.businessobject.CheckReconciliation;
import edu.arizona.kfs.module.cr.dataaccess.CheckReconciliationDao;

/**
 * Check Reconciliation DAO Implementation for OJB
 */
@Transactional
public class CheckReconciliationDaoOjb extends PlatformAwareDaoBaseOjb implements CheckReconciliationDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CheckReconciliationDaoOjb.class);

    private BusinessObjectService businessObjectService;

    // Spring Injectors

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    // Implemented Methods

    @Override
    public List<CheckReconciliation> getAll() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getAll() started");
        }

        List<CheckReconciliation> list = (List<CheckReconciliation>) businessObjectService.findAll(CheckReconciliation.class);
        return list;
    }

    @Override
    public List<CheckReconciliation> getNewCheckReconciliations(Collection<Bank> banks) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getNewCheckReconciliations() started");
        }

        List<CheckReconciliation> data = new ArrayList<CheckReconciliation>();
        String sql = getNewCheckReconciliationsSQL();

        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while (rs.next()) {
                CheckReconciliation cr = getCheckReconciliationFromResultSet(rs, banks);
                data.add(cr);
            }

            s.close();
        } catch (Exception e) {
            LOG.error("getNewCheckReconciliations", e);
        }

        return data;
    }

    @Override
    public List<CheckReconciliation> getBankFileCreatedCheckReconciliations(Integer checkNumber, KualiDecimal amount, String bankAccountNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getBankFileCreatedCheckReconciliations() starting");
        }

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CrPropertyConstants.CheckReconciliation.CHECK_NUMBER, checkNumber.toString());
        fieldValues.put(CrPropertyConstants.CheckReconciliation.BANK_ACCOUNT_NUMBER, bankAccountNumber);
        fieldValues.put(CrPropertyConstants.CheckReconciliation.AMOUNT, amount.toString());
        fieldValues.put(CrPropertyConstants.CheckReconciliation.SOURCE_CODE, CrConstants.CheckReconciliationSourceCodes.BFL_SRC);
        fieldValues.put(CrPropertyConstants.CheckReconciliation.STATUS, CrConstants.CheckReconciliationStatusCodes.EXCP);

        List<CheckReconciliation> retvals = (List<CheckReconciliation>) businessObjectService.findMatching(CheckReconciliation.class, fieldValues);
        return retvals;
    }

    @Override
    public List<CheckReconciliation> getAllCheckReconciliationForSearchCriteria(Date startDate, Date endDate, Set<String> statusCodes) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getAllCheckReconciliationForSearchCriteria() starting");
        }

        Criteria criteria = new Criteria();
        Criteria statusChangeDateCriteria = new Criteria();
        statusChangeDateCriteria.addIsNull(CrPropertyConstants.CheckReconciliation.STATUS_CHANGE_DATE);
        Criteria statusChangeDateGreaterThanEndDateCriteria = new Criteria();

        if (!(startDate == null)) {
            criteria.addGreaterOrEqualThan(CrPropertyConstants.CheckReconciliation.CHECK_DATE, startDate);
        }
        if (!(endDate == null)) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(endDate);
            gc.add(Calendar.DATE, 1);
            criteria.addLessThan(CrPropertyConstants.CheckReconciliation.CHECK_DATE, new Timestamp(gc.getTimeInMillis()));
            statusChangeDateGreaterThanEndDateCriteria.addGreaterThan(CrPropertyConstants.CheckReconciliation.STATUS_CHANGE_DATE, endDate);
            statusChangeDateCriteria.addOrCriteria(statusChangeDateGreaterThanEndDateCriteria);
        }

        criteria.addAndCriteria(statusChangeDateCriteria);
        if (CollectionUtils.isNotEmpty(statusCodes)) {
            criteria.addNotIn(CrPropertyConstants.CheckReconciliation.STATUS, statusCodes);
        }

        QueryByCriteria qbc = new QueryByCriteria(CheckReconciliation.class, criteria);
        qbc.addOrderBy(CrPropertyConstants.CheckReconciliation.BANK_ACCOUNT_NUMBER, true);
        qbc.addOrderBy(CrPropertyConstants.CheckReconciliation.CHECK_DATE, true);
        qbc.addOrderBy(CrPropertyConstants.CheckReconciliation.CHECK_NUMBER, true);
        if (LOG.isDebugEnabled()) {
            LOG.debug("getAllCheckReconciliationForSearchCriteria() Query = " + qbc.toString());
        }

        @SuppressWarnings("unchecked")
        List<CheckReconciliation> list = (List<CheckReconciliation>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return list;
    }

    @Override
    public List<PaymentGroup> getAllPaymentGroupForSearchCriteria(Integer disbNbr, Collection<String> bankCodes, Date disbursementDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getAllPaymentGroupForSearchCriteria() starting");
        }

        Criteria criteria = new Criteria();
        criteria.addEqualTo(CrPropertyConstants.PaymentGroup.DISBURSEMENT_NBR, disbNbr);
        criteria.addEqualTo(CrPropertyConstants.PaymentGroup.DISBURSEMENT_TYPE_CODE, CrConstants.DisbursementTypeCodes.CHECK);
        criteria.addIn(CrPropertyConstants.PaymentGroup.BANK_CODE, bankCodes);
        criteria.addEqualTo(CrPropertyConstants.PaymentGroup.DISBURSEMENT_DATE, disbursementDate);

        QueryByCriteria qbc = new QueryByCriteria(PaymentGroup.class, criteria);
        if (LOG.isDebugEnabled()) {
            LOG.debug("getAllPaymentGroupForSearchCriteria() Query = " + qbc.toString());
        }

        @SuppressWarnings("unchecked")
        List<PaymentGroup> list = (List<PaymentGroup>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return list;
    }

    @Override
    public List<CheckReconciliation> getCheckReconciliation(Integer checkNumber, String bankAccountNumber, KualiDecimal amount) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getCheckReconciliation() starting");
        }

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CrPropertyConstants.CheckReconciliation.CHECK_NUMBER, checkNumber.toString());
        fieldValues.put(CrPropertyConstants.CheckReconciliation.BANK_ACCOUNT_NUMBER, bankAccountNumber);
        fieldValues.put(CrPropertyConstants.CheckReconciliation.AMOUNT, amount.toString());
        fieldValues.put(CrPropertyConstants.CheckReconciliation.STATUS, CrConstants.CheckReconciliationStatusCodes.ISSUED);

        List<CheckReconciliation> retvals = (List<CheckReconciliation>) businessObjectService.findMatching(CheckReconciliation.class, fieldValues);
        return retvals;
    }

    @Override
    public List<PaymentGroup> getPdpPaymentGroups(CheckReconciliation checkReconciliation) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(CrPropertyConstants.PaymentGroup.DISBURSEMENT_NBR, checkReconciliation.getCheckNumber());
        fieldValues.put(CrPropertyConstants.PaymentGroup.DISBURSEMENT_DATE, checkReconciliation.getCheckDate());
        fieldValues.put(CrPropertyConstants.PaymentGroup.PAYEE_ID, checkReconciliation.getPayeeId());
        fieldValues.put(CrPropertyConstants.PaymentGroup.PAYEE_ID_TYPE_CODE, checkReconciliation.getPayeeTypeCode());
        fieldValues.put(CrPropertyConstants.PaymentGroup.BANK_CODE, getBankCodesForShortName(checkReconciliation.getBankAccountNumber()));
        fieldValues.put(CrPropertyConstants.PaymentGroup.DISBURSEMENT_TYPE_CODE, PdpConstants.DisbursementTypeCodes.CHECK);
        List<PaymentGroup> paymentGroups = (List<PaymentGroup>) businessObjectService.findMatching(PaymentGroup.class, fieldValues);
        return paymentGroups;
    }

    @Override
    public List<PaymentGroupHistory> getPdpPaymentGroupHistories(CheckReconciliation checkReconciliation) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(CrPropertyConstants.PaymentGroupHistory.ORIGIN_DISBURSEMENT_NUMBER, checkReconciliation.getCheckNumber());
        fieldValues.put(CrPropertyConstants.PaymentGroupHistory.ORIGIN_DISBURSE_DATE, checkReconciliation.getCheckDate());
        fieldValues.put(CrPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_PAYEE_ID, checkReconciliation.getPayeeId());
        fieldValues.put(CrPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_PAYEE_ID_TYPE_CODE, checkReconciliation.getPayeeTypeCode());
        fieldValues.put(CrPropertyConstants.PaymentGroupHistory.PAYMENT_GROUP_BANK_CODE, getBankCodesForShortName(checkReconciliation.getBankAccountNumber()));
        fieldValues.put(CrPropertyConstants.PaymentGroupHistory.DISBURSEMENT_TYPE_CODE, CrConstants.DisbursementTypeCodes.CHECK);
        List<PaymentGroupHistory> paymentGroupHistories = (List<PaymentGroupHistory>) businessObjectService.findMatching(PaymentGroupHistory.class, fieldValues);
        return paymentGroupHistories;
    }

    @Override
    public List<Bank> getAllBanks() {
        List<Bank> banks = (List<Bank>) businessObjectService.findAll(Bank.class);
        return banks;
    }

    @Override
    public CheckReconciliation save(CheckReconciliation cr) {
        CheckReconciliation saved = businessObjectService.save(cr);
        return saved;
    }

    @Override
    public PaymentGroup save(PaymentGroup paymentGroup) {
        PaymentGroup saved = businessObjectService.save(paymentGroup);
        return saved;
    }

    @Override
    public PaymentStatus findMatchingPaymentStatus(String checkStatus) {
        PaymentStatus retval = businessObjectService.findBySinglePrimaryKey(PaymentStatus.class, checkStatus);
        return retval;
    }

    @Override
    public List<CheckReconciliation> getUnprocessedPdpStoppedChecks() {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CrPropertyConstants.CheckReconciliation.GL_TRANS_INDICATOR, CrConstants.GlIndicatorOptions.NO);
        fieldValues.put(CrPropertyConstants.CheckReconciliation.STATUS, CrConstants.CheckReconciliationStatusCodes.STOP);
        fieldValues.put(CrPropertyConstants.CheckReconciliation.SOURCE_CODE, CrConstants.CheckReconciliationSourceCodes.PDP_SRC);
        List<CheckReconciliation> records = (List<CheckReconciliation>) businessObjectService.findMatching(CheckReconciliation.class, fieldValues);
        return records;

    }

    @Override
    public List<CheckReconciliation> getUnprocessedPdpCancelledChecks() {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CrPropertyConstants.CheckReconciliation.GL_TRANS_INDICATOR, CrConstants.GlIndicatorOptions.NO);
        fieldValues.put(CrPropertyConstants.CheckReconciliation.STATUS, CrConstants.CheckReconciliationStatusCodes.CANCELLED);
        fieldValues.put(CrPropertyConstants.CheckReconciliation.SOURCE_CODE, CrConstants.CheckReconciliationSourceCodes.PDP_SRC);

        List<CheckReconciliation> records = (List<CheckReconciliation>) businessObjectService.findMatching(CheckReconciliation.class, fieldValues);
        return records;
    }

    @Override
    public List<CheckReconciliation> getUnprocessedPdpStaleChecks() {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CrPropertyConstants.CheckReconciliation.GL_TRANS_INDICATOR, CrConstants.GlIndicatorOptions.NO);
        fieldValues.put(CrPropertyConstants.CheckReconciliation.STATUS, CrConstants.CheckReconciliationStatusCodes.STALE);
        fieldValues.put(CrPropertyConstants.CheckReconciliation.SOURCE_CODE, CrConstants.CheckReconciliationSourceCodes.PDP_SRC);

        List<CheckReconciliation> records = (List<CheckReconciliation>) businessObjectService.findMatching(CheckReconciliation.class, fieldValues);
        return records;
    }

    @Override
    public List<CheckReconciliation> getUnprocessedPdpVoidedChecks() {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CrPropertyConstants.CheckReconciliation.GL_TRANS_INDICATOR, CrConstants.GlIndicatorOptions.NO);
        fieldValues.put(CrPropertyConstants.CheckReconciliation.STATUS, CrConstants.CheckReconciliationStatusCodes.VOIDED);
        fieldValues.put(CrPropertyConstants.CheckReconciliation.SOURCE_CODE, CrConstants.CheckReconciliationSourceCodes.PDP_SRC);

        List<CheckReconciliation> records = (List<CheckReconciliation>) businessObjectService.findMatching(CheckReconciliation.class, fieldValues);
        return records;
    }

    // Private Methods

    private String getNewCheckReconciliationsSQL() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getNewCheckReconciliationsSQL() started");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT p.disb_nbr, p.disb_ts, SUM(d.net_pmt_amt), b.bnk_cd, p.payee_id, p.payee_id_typ_cd ");
        sb.append("  FROM pdp_pmt_grp_t p, pdp_pmt_dtl_t d, fp_bank_t b ");
        sb.append("  WHERE p.bnk_cd = b.bnk_cd");
        sb.append("    AND p.pmt_grp_id = d.pmt_grp_id");
        sb.append("    AND p.disb_typ_cd = 'CHCK' ");
        sb.append("    AND NOT EXISTS ( ");
        sb.append("      SELECT 'x' from check_reconciliation_t cr ");
        sb.append("        WHERE cr.check_nbr = p.disb_nbr ");
        sb.append("          AND cr.bank_account_nbr = b.bnk_shrt_nm ");
        sb.append("          AND cr.check_dt = p.disb_ts) ");
        sb.append("  GROUP BY p.disb_nbr, p.disb_ts, b.bnk_cd, p.payee_id, p.payee_id_typ_cd ");
        sb.append("  HAVING SUM(d.net_pmt_amt) > 0");

        String retval = sb.toString();
        return retval;
    }

    private CheckReconciliation getCheckReconciliationFromResultSet(ResultSet rs, Collection<Bank> banks) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getCheckReconciliationFromResultSet() started");
        }

        CheckReconciliation cr = new CheckReconciliation();
        try {
            cr.setCheckNumber(new Integer(rs.getInt(1)));
            cr.setCheckDate(new java.sql.Date(rs.getDate(2).getTime()));
            cr.setAmount(new KualiDecimal(rs.getDouble(3)));

            String bnkCd = rs.getString(4);
            for (Bank bank : banks) {
                if (bank.getBankCode().equals(bnkCd)) {
                    cr.setBankAccountNumber(bank.getBankShortName());
                }
            }

            cr.setPayeeId(rs.getString(5));
            cr.setPayeeTypeCode(rs.getString(6));
            cr.setGlTransIndicator(Boolean.FALSE);
            cr.setStatus(CrConstants.CheckReconciliationStatusCodes.ISSUED);
            cr.setSourceCode(CrConstants.CheckReconciliationSourceCodes.PDP_SRC);
        } catch (Exception e) {
            LOG.error("getCheckReconciliationFromResultSet", e);
        }
        return cr;
    }

    @Override
    public List<String> getBankCodesForShortName(String bankShortName) {
        List<String> codes = new ArrayList<String>();

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.BANK_SHORT_NAME, bankShortName);
        List<Bank> banks = (List<Bank>) businessObjectService.findMatching(Bank.class, fieldValues);

        for (Bank bank : banks) {
            codes.add(bank.getBankCode());
        }
        return codes;
    }

}
