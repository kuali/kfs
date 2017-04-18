package edu.arizona.kfs.sys.businessobject;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public interface DocumentIncomeType<T> {

    public String getChartOfAccountsCode();

    public void setChartOfAccountsCode(String chartOfAccountsCode);

    public KualiDecimal getAmount();

    public void setAmount(KualiDecimal amount);

    public String getIncomeTypeCode();

    public void setIncomeTypeCode(String incomeTypeCode);

    public Integer getLineNumber();

    public void setLineNumber(Integer lineNumber);

    public Chart getChart();

    public void setChart(Chart chart);

    public IncomeType getIncomeType();

    public void setIncomeType(IncomeType incomeType);

    public T getDocumentIdentifier();

    public void setDocumentIdentifier(T documentIdentifier);

}
