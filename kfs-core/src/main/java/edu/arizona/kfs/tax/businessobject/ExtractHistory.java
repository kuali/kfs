package edu.arizona.kfs.tax.businessobject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectExtensionBase;

public class ExtractHistory extends PersistableBusinessObjectExtensionBase implements Serializable {

    private Integer id;                    // EXTR_HIST_ID
    private Boolean replaceDataInd;        // REPLACE_DATA_IND
    private Integer payeesExtracted = 0;   // PAYEES_EXTR
    private Integer paymentsDeleted = 0;   // PAYMENTS_DEL
    private Integer paymentsExtracted = 0; // PAYMENTS_EXTR
    private Timestamp extractStartDt;      // EXTR_START_TS
    private Timestamp extractEndDt;        // EXTR_END_TS
    private Timestamp extractDt;           // EXTR_TS
    private Integer taxYear;               // TAX_YEAR
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

    protected LinkedHashMap<Object, Object> toStringMapper() {
        LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();

        map.put("id", getId());
        map.put("taxYear", getTaxYear());
        
        return map;
    }
}