

package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormDueDateType extends PersistableBusinessObjectBase {

	private String documentNumber;
	private String dueDateTypeCode;
    
    private DueDateType dueDateType;

	/**
	 * Default constructor.
	 */
	public RoutingFormDueDateType() {

	}

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the dueDateTypeCode attribute.
	 * 
	 * @return Returns the dueDateTypeCode
	 * 
	 */
	public String getDueDateTypeCode() { 
		return dueDateTypeCode;
	}

	/**
	 * Sets the dueDateTypeCode attribute.
	 * 
	 * @param dueDateTypeCode The dueDateTypeCode to set.
	 * 
	 */
	public void setDueDateTypeCode(String dueDateTypeCode) {
		this.dueDateTypeCode = dueDateTypeCode;
	}

    /**
     * Gets the dueDateType attribute.
     * 
     * @return Returns the dueDateType
     * 
     */
	public DueDateType getDueDateType() {
        return dueDateType;
    }

    /**
     * Sets the dueDateType attribute.
     * 
     * @param dueDateType The dueDateType to set.
     * 
     */
    public void setDueDateType(DueDateType dueDateType) {
        this.dueDateType = dueDateType;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("documentNumber", this.documentNumber);
        m.put("dueDateTypeCode", this.dueDateTypeCode);
	    return m;
    }
}
