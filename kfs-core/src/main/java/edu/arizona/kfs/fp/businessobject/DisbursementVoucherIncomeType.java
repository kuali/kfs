package edu.arizona.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.sys.businessobject.DocumentIncomeType;
import edu.arizona.kfs.sys.businessobject.IncomeType;

public class DisbursementVoucherIncomeType extends PersistableBusinessObjectBase implements DocumentIncomeType<String> {
    private static final long serialVersionUID = 1843150667863117645L;

    private String documentNumber;
    private Integer sequenceNumber;
    private String chartOfAccountsCode;
    private KualiDecimal amount = new KualiDecimal(0);
    private String incomeTypeCode;

    private Chart chart;
    private IncomeType incomeType;

    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> retval = new LinkedHashMap<String, Object>();

        retval.put(KFSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        retval.put(KFSPropertyConstants.SEQUENCE_NUMBER, getSequenceNumber());
        retval.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getChartOfAccountsCode());
        retval.put(KFSPropertyConstants.AMOUNT, getAmount());
        retval.put(KFSPropertyConstants.IncomeTypeFields.INCOME_TYPE_CODE, getIncomeTypeCode());

        return retval;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public KualiDecimal getAmount() {
        if (amount == null) {
            amount = new KualiDecimal(0);
        }

        return amount;
    }

    public void setAmount(KualiDecimal amount) {
        if (amount == null) {
            this.amount = new KualiDecimal(0);
        } else {
            this.amount = amount;
        }
    }

    public IncomeType getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(IncomeType incomeType) {
        this.incomeType = incomeType;
    }

    public String getIncomeTypeCode() {
        return incomeTypeCode;
    }

    public void setIncomeTypeCode(String incomeTypeCode) {
        this.incomeTypeCode = incomeTypeCode;
    }

    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }

    @Override
    public Integer getLineNumber() {
        return getSequenceNumber();
    }

    @Override
    public void setLineNumber(Integer lineNumber) {
        this.setSequenceNumber(lineNumber);
    }

    @Override
    public String getDocumentIdentifier() {
        return getDocumentNumber();
    }

    @Override
    public void setDocumentIdentifier(String documentIdentifier) {
        setDocumentNumber(documentIdentifier);
    }

}
