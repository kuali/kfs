package edu.arizona.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.sys.KFSPropertyConstants;

public class IncomeType extends PersistableBusinessObjectBase implements MutableInactivatable {
    private static final long serialVersionUID = -1816078823019930401L;

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
        retval.put(KFSPropertyConstants.IncomeTypeFields.INCOME_TYPE_CODE, this.incomeTypeCode);
        retval.put(KFSPropertyConstants.IncomeTypeFields.INCOME_TYPE_DESCRIPTION, this.incomeTypeDescription);
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
