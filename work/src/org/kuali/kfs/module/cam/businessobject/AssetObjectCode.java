package org.kuali.module.cams.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjSubTyp;
import org.kuali.module.chart.bo.ObjectCode;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetObjectCode extends PersistableBusinessObjectBase {

	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String financialObjectSubTypeCode;
	private String capitalizationFinancialObjectCode;
	private String accumulatedDepreciationFinancialObjectCode;
	private String depreciationExpenseFinancialObjectCode;

    private ObjectCode accumulatedDepreciationFinancialObject;
	private ObjectCode capitalizationFinancialObject;
	private ObjectCode depreciationExpenseFinancialObject;
	private Chart chartOfAccounts;
    private ObjSubTyp financialObjectSubType;
    
	/**
	 * Default constructor.
	 */
	public AssetObjectCode() {

	}

	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the financialObjectSubTypeCode attribute.
	 * 
	 * @return Returns the financialObjectSubTypeCode
	 * 
	 */
	public String getFinancialObjectSubTypeCode() { 
		return financialObjectSubTypeCode;
	}

	/**
	 * Sets the financialObjectSubTypeCode attribute.
	 * 
	 * @param financialObjectSubTypeCode The financialObjectSubTypeCode to set.
	 * 
	 */
	public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
		this.financialObjectSubTypeCode = financialObjectSubTypeCode;
	}


	/**
	 * Gets the capitalizationFinancialObjectCode attribute.
	 * 
	 * @return Returns the capitalizationFinancialObjectCode
	 * 
	 */
	public String getCapitalizationFinancialObjectCode() { 
		return capitalizationFinancialObjectCode;
	}

	/**
	 * Sets the capitalizationFinancialObjectCode attribute.
	 * 
	 * @param capitalizationFinancialObjectCode The capitalizationFinancialObjectCode to set.
	 * 
	 */
	public void setCapitalizationFinancialObjectCode(String capitalizationFinancialObjectCode) {
		this.capitalizationFinancialObjectCode = capitalizationFinancialObjectCode;
	}


	/**
	 * Gets the accumulatedDepreciationFinancialObjectCode attribute.
	 * 
	 * @return Returns the accumulatedDepreciationFinancialObjectCode
	 * 
	 */
	public String getAccumulatedDepreciationFinancialObjectCode() { 
		return accumulatedDepreciationFinancialObjectCode;
	}

	/**
	 * Sets the accumulatedDepreciationFinancialObjectCode attribute.
	 * 
	 * @param accumulatedDepreciationFinancialObjectCode The accumulatedDepreciationFinancialObjectCode to set.
	 * 
	 */
	public void setAccumulatedDepreciationFinancialObjectCode(String accumulatedDepreciationFinancialObjectCode) {
		this.accumulatedDepreciationFinancialObjectCode = accumulatedDepreciationFinancialObjectCode;
	}


	/**
	 * Gets the depreciationExpenseFinancialObjectCode attribute.
	 * 
	 * @return Returns the depreciationExpenseFinancialObjectCode
	 * 
	 */
	public String getDepreciationExpenseFinancialObjectCode() { 
		return depreciationExpenseFinancialObjectCode;
	}

	/**
	 * Sets the depreciationExpenseFinancialObjectCode attribute.
	 * 
	 * @param depreciationExpenseFinancialObjectCode The depreciationExpenseFinancialObjectCode to set.
	 * 
	 */
	public void setDepreciationExpenseFinancialObjectCode(String depreciationExpenseFinancialObjectCode) {
		this.depreciationExpenseFinancialObjectCode = depreciationExpenseFinancialObjectCode;
	}


	/**
	 * Gets the accumulatedDepreciationFinancialObject attribute.
	 * 
	 * @return Returns the accumulatedDepreciationFinancialObject
	 * 
	 */
	public ObjectCode getAccumulatedDepreciationFinancialObject() { 
		return accumulatedDepreciationFinancialObject;
	}

	/**
	 * Sets the accumulatedDepreciationFinancialObject attribute.
	 * 
	 * @param accumulatedDepreciationFinancialObject The accumulatedDepreciationFinancialObject to set.
	 * @deprecated
	 */
	public void setAccumulatedDepreciationFinancialObject(ObjectCode accumulatedDepreciationFinancialObject) {
		this.accumulatedDepreciationFinancialObject = accumulatedDepreciationFinancialObject;
	}

	/**
	 * Gets the capitalizationFinancialObject attribute.
	 * 
	 * @return Returns the capitalizationFinancialObject
	 * 
	 */
	public ObjectCode getCapitalizationFinancialObject() { 
		return capitalizationFinancialObject;
	}

	/**
	 * Sets the capitalizationFinancialObject attribute.
	 * 
	 * @param capitalizationFinancialObject The capitalizationFinancialObject to set.
	 * @deprecated
	 */
	public void setCapitalizationFinancialObject(ObjectCode capitalizationFinancialObject) {
		this.capitalizationFinancialObject = capitalizationFinancialObject;
	}

	/**
	 * Gets the depreciationExpenseFinancialObject attribute.
	 * 
	 * @return Returns the depreciationExpenseFinancialObject
	 * 
	 */
	public ObjectCode getDepreciationExpenseFinancialObject() { 
		return depreciationExpenseFinancialObject;
	}

	/**
	 * Sets the depreciationExpenseFinancialObject attribute.
	 * 
	 * @param depreciationExpenseFinancialObject The depreciationExpenseFinancialObject to set.
	 * @deprecated
	 */
	public void setDepreciationExpenseFinancialObject(ObjectCode depreciationExpenseFinancialObject) {
		this.depreciationExpenseFinancialObject = depreciationExpenseFinancialObject;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param chartOfAccounts The chartOfAccounts to set.
	 * @deprecated
	 */
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

    /**
     * Gets the financialObjectSubType attribute. 
     * @return Returns the financialObjectSubType.
     */
    public ObjSubTyp getFinancialObjectSubType() {
        return financialObjectSubType;
    }

    /**
     * Sets the financialObjectSubType attribute value.
     * @param financialObjectSubType The financialObjectSubType to set.
     * @deprecated
     */
    public void setFinancialObjectSubType(ObjSubTyp financialObjectSubType) {
        this.financialObjectSubType = financialObjectSubType;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("financialObjectSubTypeCode", this.financialObjectSubTypeCode);
        return m;
    }    

}
