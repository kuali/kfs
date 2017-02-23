package edu.arizona.kfs.module.tax.businessobject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.module.tax.TaxPropertyConstants;

public class ExtractHistory extends PersistableBusinessObjectBase implements Serializable {
    private static final long serialVersionUID = -5617102866483963271L;

    private Integer id;
    private Boolean replaceDataInd;
    private Integer payeesExtracted = 0;
    private Integer paymentsDeleted = 0;
    private Integer paymentsExtracted = 0;
    private Timestamp extractStartDt;
    private Timestamp extractEndDt;
    private Timestamp extractDt;
    private Integer taxYear;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getReplaceDataInd() {
        return replaceDataInd;
    }

    public void setReplaceDataInd(Boolean replaceDataInd) {
        this.replaceDataInd = replaceDataInd;
    }

    public Integer getPayeesExtracted() {
        return payeesExtracted;
    }

    public void setPayeesExtracted(Integer payeesExtracted) {
        this.payeesExtracted = payeesExtracted;
    }

    public Integer getPaymentsDeleted() {
        return paymentsDeleted;
    }

    public void setPaymentsDeleted(Integer paymentsDeleted) {
        this.paymentsDeleted = paymentsDeleted;
    }

    public Integer getPaymentsExtracted() {
        return paymentsExtracted;
    }

    public void setPaymentsExtracted(Integer paymentsExtracted) {
        this.paymentsExtracted = paymentsExtracted;
    }

    public int incrementPaymentsExtracted(int extractedCount) {
        if (paymentsExtracted == null) {
            paymentsExtracted = Integer.valueOf(extractedCount);
        } else {
            paymentsExtracted = Integer.valueOf(paymentsExtracted.intValue() + extractedCount);
        }
        return paymentsExtracted.intValue();
    }

    public int incrementPayeesExtracted() {
        if (payeesExtracted == null) {
            payeesExtracted = Integer.valueOf(1);
        } else {
            payeesExtracted = Integer.valueOf(payeesExtracted.intValue() + 1);
        }
        return payeesExtracted.intValue();
    }

    public Timestamp getExtractStartDt() {
        return extractStartDt;
    }

    public void setExtractStartDt(Timestamp extractStartDt) {
        this.extractStartDt = extractStartDt;
    }

    public Timestamp getExtractEndDt() {
        return extractEndDt;
    }

    public void setExtractEndDt(Timestamp extractEndDt) {
        this.extractEndDt = extractEndDt;
    }

    public Timestamp getExtractDt() {
        return extractDt;
    }

    public void setExtractDt(Timestamp extractDt) {
        this.extractDt = extractDt;
    }

    public Integer getTaxYear() {
        return taxYear;
    }

    public void setTaxYear(Integer taxYear) {
        this.taxYear = taxYear;
    }

    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> retval = new LinkedHashMap<String, String>();
        retval.put(TaxPropertyConstants.ExtractHistoryFields.EXTRACT_HISTORY_ID, getId().toString());
        retval.put(TaxPropertyConstants.ExtractHistoryFields.TAX_YEAR, getTaxYear().toString());
        return retval;
    }
}
