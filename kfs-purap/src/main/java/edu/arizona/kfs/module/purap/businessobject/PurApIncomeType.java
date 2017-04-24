package edu.arizona.kfs.module.purap.businessobject;

import org.apache.ojb.broker.PersistenceBrokerAware;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

import edu.arizona.kfs.sys.businessobject.IncomeType;

public interface PurApIncomeType extends PersistableBusinessObject, PersistenceBrokerAware {

    public abstract String getChartOfAccountsCode();

    public abstract void setChartOfAccountsCode(String chartOfAccountsCode);

    public abstract KualiDecimal getAmount();

    public abstract void setAmount(KualiDecimal amount);

    public abstract String getIncomeTypeCode();

    public abstract void setIncomeTypeCode(String incomeTypeCode);

    public abstract Integer getIncomeTypeLineNumber();

    public abstract void setIncomeTypeLineNumber(Integer incomeTypeLineNumber);

    public abstract Chart getChart();

    public abstract void setChart(Chart chart);

    public abstract IncomeType getIncomeType();

    public abstract void setIncomeType(IncomeType incomeType);

    public abstract Integer getPurapDocumentIdentifier();

    public abstract void setPurapDocumentIdentifier(Integer purapDocumentIdentifier);

}
