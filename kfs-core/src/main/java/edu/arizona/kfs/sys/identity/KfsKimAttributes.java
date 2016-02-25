package edu.arizona.kfs.sys.identity;

import org.kuali.kfs.coa.businessobject.ObjectSubType;
import org.kuali.kfs.sys.KFSPropertyConstants;

public class KfsKimAttributes extends org.kuali.kfs.sys.identity.KfsKimAttributes {
    public static final String OBJECT_SUB_TYPE_CODE = KFSPropertyConstants.FINANCIAL_OBJECT_SUB_TYPE_CODE;
        
    protected String financialObjectSubTypeCode;
    protected ObjectSubType objectSubType;

    /**
     * Gets the financialObjectSubTypeCode attribute. 
     * @return Returns the financialObjectSubTypeCode.
     */
    public String getFinancialObjectSubTypeCode() {
        return financialObjectSubTypeCode;
    }

    /**
     * Sets the financialObjectSubTypeCode attribute value.
     * @param financialObjectSubTypeCode The financialObjectSubTypeCode to set.
     */
    public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
        this.financialObjectSubTypeCode = financialObjectSubTypeCode;
    }

    /**
     * Gets the objectSubType attribute. 
     * @return Returns the objectSubType.
     */
    public ObjectSubType getObjectSubType() {
        return objectSubType;
    }

    /**
     * Sets the objectSubType attribute value.
     * @param objectSubType The objectSubType to set.
     */
    public void setObjectSubType(ObjectSubType objectSubType) {
        this.objectSubType = objectSubType;
    }
}

