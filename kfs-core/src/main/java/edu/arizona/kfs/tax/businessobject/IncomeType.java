package edu.arizona.kfs.tax.businessobject;

import java.util.LinkedHashMap;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class IncomeType extends PersistableBusinessObjectBase implements MutableInactivatable {
	private String incomeTypeCode;
	private String incomeTypeDescription;
	private String incomeTypeBox;
	private boolean active;

	public String getIncomeTypeCode() {
		return incomeTypeCode;
	}

	public void setIncomeTypeCode(String incomeTypeCode) {
		this.incomeTypeCode = incomeTypeCode;
	}

	public String getIncomeTypeBox() {
		return incomeTypeBox;
	}

	public void setIncomeTypeBox(String incomeTypeBox) {
		this.incomeTypeBox = incomeTypeBox;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> retval = new LinkedHashMap<String, String>();
		retval.put("incomeTypeCode", this.incomeTypeCode);
		retval.put("incomeTypeDescription", this.incomeTypeDescription);
		return retval;
	}

	public String getIncomeTypeDescription() {
		return incomeTypeDescription;
	}

	public void setIncomeTypeDescription(String incomeTypeDescription) {
		this.incomeTypeDescription = incomeTypeDescription;
	}

	public String getFullDescription() {
		return getIncomeTypeCode() + " - " + getIncomeTypeDescription();
	}
}
