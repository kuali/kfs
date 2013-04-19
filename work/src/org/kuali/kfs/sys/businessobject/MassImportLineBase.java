package org.kuali.kfs.sys.businessobject;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is the detail business object for Sub Account Import Global Maintenance Document
 */
public abstract class MassImportLineBase extends PersistableBusinessObjectBase {

    private static final Logger LOG = Logger.getLogger(MassImportLineBase.class);
    // Mass upload sub-account
    protected Integer sequenceNumber;

    /**
     * Gets the sequenceNumber attribute.
     *
     * @return Returns the sequenceNumber.
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the sequenceNumber attribute value.
     *
     * @param sequenceNumber The sequenceNumber to set.
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
