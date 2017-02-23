package edu.arizona.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.module.purap.PurapPropertyConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.sys.businessobject.IncomeType;

public class PurApIncomeTypeBase extends PersistableBusinessObjectBase implements PurApIncomeType {
    private static final long serialVersionUID = 385969063916820658L;

    private String chartOfAccountsCode;
    private KualiDecimal amount = new KualiDecimal(0);
    private String incomeTypeCode;
    private Integer incomeTypeLineNumber;
    private Integer purapDocumentIdentifier;
    private Chart chart;
    private IncomeType incomeType;

    protected LinkedHashMap<String, Object> toStringMapper() {
        Map<String, Object> retval = new LinkedHashMap<String, Object>();
        retval.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getChartOfAccountsCode());
        retval.put(KFSPropertyConstants.AMOUNT, getAmount());
        retval.put(KFSPropertyConstants.IncomeTypeFields.INCOME_TYPE_CODE, getIncomeTypeCode());
        retval.put(PurapPropertyConstants.INCOME_TYPE_LINE_NUMBER, getIncomeTypeLineNumber());
        retval.put(PurapPropertyConstants.PURAP_DOC_ID, getPurapDocumentIdentifier());
        return (LinkedHashMap<String, Object>) retval;
    }

    @Override
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    @Override
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    @Override
    public KualiDecimal getAmount() {
        return amount;
    }

    @Override
    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String getIncomeTypeCode() {
        return incomeTypeCode;
    }

    @Override
    public void setIncomeTypeCode(String incomeTypeCode) {
        this.incomeTypeCode = incomeTypeCode;
    }

    @Override
    public Integer getIncomeTypeLineNumber() {
        return incomeTypeLineNumber;
    }

    @Override
    public void setIncomeTypeLineNumber(Integer incomeTypeLineNumber) {
        this.incomeTypeLineNumber = incomeTypeLineNumber;
    }

    @Override
    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    @Override
    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    @Override
    public Chart getChart() {
        return chart;
    }

    @Override
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    @Override
    public IncomeType getIncomeType() {
        return incomeType;
    }

    @Override
    public void setIncomeType(IncomeType incomeType) {
        this.incomeType = incomeType;
    }

}
