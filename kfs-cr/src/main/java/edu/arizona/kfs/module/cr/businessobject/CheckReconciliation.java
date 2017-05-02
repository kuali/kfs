package edu.arizona.kfs.module.cr.businessobject;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.NoteService;

import edu.arizona.kfs.module.cr.CrPropertyConstants;
import edu.arizona.kfs.module.cr.service.CheckReconciliationPayeeService;

/**
 * Check Reconciliation business object
 */
public class CheckReconciliation extends PersistableBusinessObjectBase implements Serializable {
    private static final long serialVersionUID = 4917199086426043829L;

    private Integer id;
    private Integer checkNumber;
    private String bankAccountNumber;
    private Date checkDate;
    private KualiDecimal amount;
    private String status;
    private Timestamp lastUpdate;
    private Boolean glTransIndicator = Boolean.FALSE;
    private String sourceCode;
    private String payeeId;
    private String payeeTypeCode;
    private Date clearedDate;
    private Date statusChangeDate;
    private List<Note> boNotes = new ArrayList<Note>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(Integer checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public KualiDecimal getAmount() {
        return amount;
    }

    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Boolean getGlTransIndicator() {
        return glTransIndicator;
    }

    public void setGlTransIndicator(Boolean glTransIndicator) {
        this.glTransIndicator = glTransIndicator;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public String getPayeeTypeCode() {
        return payeeTypeCode;
    }

    public void setPayeeTypeCode(String payeeTypeCode) {
        this.payeeTypeCode = payeeTypeCode;
    }

    public Date getClearedDate() {
        return clearedDate;
    }

    public void setClearedDate(Date clearedDate) {
        this.clearedDate = clearedDate;
    }

    public Date getStatusChangeDate() {
        return statusChangeDate;
    }

    public void setStatusChangeDate(Date statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    public LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put(CrPropertyConstants.CheckReconciliation.ID, id.toString());
        map.put(CrPropertyConstants.CheckReconciliation.CHECK_NUMBER, checkNumber.toString());
        map.put(CrPropertyConstants.CheckReconciliation.BANK_ACCOUNT_NUMBER, bankAccountNumber);
        map.put(CrPropertyConstants.CheckReconciliation.CHECK_DATE, checkDate.toString());
        map.put(CrPropertyConstants.CheckReconciliation.AMOUNT, amount.toString());
        map.put(CrPropertyConstants.CheckReconciliation.STATUS, status);
        map.put(CrPropertyConstants.CheckReconciliation.LAST_UPDATE, lastUpdate.toString());
        map.put(CrPropertyConstants.CheckReconciliation.GL_TRANS_INDICATOR, glTransIndicator.toString());
        map.put(CrPropertyConstants.CheckReconciliation.SOURCE_CODE, sourceCode);
        map.put(CrPropertyConstants.CheckReconciliation.PAYEE_ID, payeeId);
        map.put(CrPropertyConstants.CheckReconciliation.PAYEE_TYPE_CODE, payeeTypeCode);
        map.put(CrPropertyConstants.CheckReconciliation.CLEARED_DATE, clearedDate.toString());
        map.put(CrPropertyConstants.CheckReconciliation.STATUS_CHANGE_DATE, statusChangeDate.toString());
        return map;
    }

    public String getPayeeName() {
        String payeeName = SpringContext.getBean(CheckReconciliationPayeeService.class).getCheckPayeeName(this);
        return payeeName;
    }

    public List<Note> getBoNotes() {
        if (!StringUtils.isEmpty(getObjectId())) {
            boNotes = SpringContext.getBean(NoteService.class).getByRemoteObjectId(getObjectId());
        }

        if (boNotes == null) {
            boNotes = new ArrayList<Note>();
        }
        return boNotes;
    }

}
