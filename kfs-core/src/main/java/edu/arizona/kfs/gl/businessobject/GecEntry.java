package edu.arizona.kfs.gl.businessobject;

import org.kuali.kfs.gl.businessobject.Entry;

import edu.arizona.kfs.gl.businessobject.lookup.GecEntryHelperServiceImpl;

/**
 * This class is used to retrieve the data in the table for Entry objects, add a the
 * gecDocumentNumber field, and provide dynamically generated objectIds since they do
 * not exist in the database for use in GEC Lookups.
 *
 * @author Adam Kost <kosta@email.arizona.edu> with some code adapted from UCI
 */

public class GecEntry extends Entry {

    private static final long serialVersionUID = 7915615641174612210L;

    // transient, dynamically generated fields
    private transient String gecDocumentNumber;
    private transient String generatedId;

    /**
     * Note: If gecDocumentNumber is an empty String (KFSConstants.EMPTY_STRING) it means that
     * the Entry is not associated with a Correcting GEC Document. If the value is null, then
     * the system has not searched for a Correcting GEC Document yet.
     *
     * @return
     */
    public String getGecDocumentNumber() {
        return gecDocumentNumber;
    }

    /**
     * Note: If gecDocumentNumber is an empty String (KFSConstants.EMPTY_STRING) it means that
     * the Entry is not associated with a Correcting GEC Document. If the value is null, then
     * the system has not searched for a Correcting GEC Document yet.
     *
     * @return
     */
    public void setGecDocumentNumber(String gecDocumentNumber) {
        this.gecDocumentNumber = gecDocumentNumber;
    }

    public String getGeneratedId() {
        if (generatedId == null) {
            generatedId = GecEntryHelperServiceImpl.generateObjectId(this);
        }
        return generatedId;
    }

    public void setGeneratedId(String generatedId) {
        this.generatedId = generatedId;
    }

    /**
     * In the foundation database the objectId field does not exist, and when an Entry is
     * retrieved from the database, the objectId field will always be null. This prevents
     * Entry objects from being individually returnable via lookups. A dynamically generated
     * Id based on the composite key fields for the Entry objects needs to be used to allow
     * this to function properly.
     * Due to OJB reflection voodoo, overriding the setObjectId() and constructor(s) to
     * populate this field with the dynamically generated value does not work.
     * If the GL_ENTRY_T is ever amended to include an objectId field, super.getObjectId()
     * will never be null and there is no need for it to be generated/temporarily stored.
     *
     * @return the objectId of this object, or a dynamically generated Id if it has not been set yet.
     */

    @Override
    public String getObjectId() {
        if (super.getObjectId() == null) {
            return this.getGeneratedId();
        }
        return super.getObjectId();
    }

}
