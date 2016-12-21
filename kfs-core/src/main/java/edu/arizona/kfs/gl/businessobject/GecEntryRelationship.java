package edu.arizona.kfs.gl.businessobject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.sys.KFSPropertyConstants;


public class GecEntryRelationship extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 1107325740786032980L;
    private static final String TO_STRING_FORMAT = "GecEntryRelationship(enrtryId: %d, gecFdocNumber: %s, gecDocRouteStatusCode: %s, fdocLineTypeCode: %s, acctLineSeqNum: %d)";

    private Long entryId;
    private String gecDocumentNumber;
    private Integer gecAcctLineSeqNumber;
    private String gecDocRouteStatus;
    private String gecFdocLineTypeCode;
    private Entry entry;


    public GecEntryRelationship() {
        // OJB requires an empty constructor
    }


    public GecEntryRelationship(Long entryId, String gecDocumentNumber, Integer gecAcctLineSeqNumber, String gecFdocLineTypeCode, String gecDocumentStatus) {
        this.entryId = entryId;
        this.gecDocumentNumber = gecDocumentNumber;
        this.gecAcctLineSeqNumber = gecAcctLineSeqNumber;
        this.gecFdocLineTypeCode = gecFdocLineTypeCode;
        this.gecDocRouteStatus = gecDocumentStatus;
    }


    public GecEntryRelationship(Long entryId, String gecDocumentNumber, Integer gecAcctLineSeqNumber, String gecFdocLineTypeCode, String gecDocumentStatus, Entry entry) {
        this(entryId, gecDocumentNumber, gecAcctLineSeqNumber, gecFdocLineTypeCode, gecDocumentStatus);
        this.entry = entry;
    }


    public Long getEntryId() {
        return entryId;
    }


    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }


    public String getGecDocumentNumber() {
        return gecDocumentNumber;
    }


    public void setGecDocumentNumber(String gecDocumentNumber) {
        this.gecDocumentNumber = gecDocumentNumber;
    }


    public Integer getGecAcctLineSeqNumber() {
        return gecAcctLineSeqNumber;
    }


    public void setGecAcctLineSeqNumber(Integer gecAcctLineSeqNumber) {
        this.gecAcctLineSeqNumber = gecAcctLineSeqNumber;
    }


    public String getGecDocRouteStatus() {
        return gecDocRouteStatus;
    }


    public void setGecDocRouteStatus(String gecDocRouteStatus) {
        this.gecDocRouteStatus = gecDocRouteStatus;
    }


    public void setEntry(Entry entry) {
        this.entry = entry;
    }


    public Entry getEntry() {
        if (entry == null && getEntryId() != null) {
            Map<String, Object> queryCriteria = new HashMap<String, Object>();
            queryCriteria.put(KFSPropertyConstants.ENTRY_ID, getEntryId());
            Collection tmpEntries = SpringContext.getBean(BusinessObjectService.class).findMatching(Entry.class, queryCriteria);
            if (tmpEntries != null && tmpEntries.size() == 1) {
                entry = (Entry) tmpEntries.iterator().next();
            }
        }

        return entry;
    }


    public String getGecFdocLineTypeCode() {
        return gecFdocLineTypeCode;
    }


    public void setGecFdocLineTypeCode(String gecFdocLineTypeCode) {
        this.gecFdocLineTypeCode = gecFdocLineTypeCode;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result +
                ((getEntryId() == null) ? 0 : getEntryId().hashCode());
        result = prime
                * result
                + ((getGecDocumentNumber() == null) ? 0 : getGecDocumentNumber().hashCode());
        result = prime
                * result
                + ((getGecFdocLineTypeCode() == null) ? 0 : getGecFdocLineTypeCode().hashCode());
        result = prime
                * result
                + ((getGecAcctLineSeqNumber() == null) ? 0 : getGecAcctLineSeqNumber().hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }

        GecEntryRelationship other = (GecEntryRelationship) obj;
        if (getEntryId() == null && other.entryId != null) {
            return false;
        } else if (!getEntryId().equals(other.entryId)) {
            return false;
        } else if (getGecDocumentNumber() == null && other.getGecDocumentNumber() != null) {
            return false;
        } else if (!getGecDocumentNumber().equals(other.getGecDocumentNumber())) {
            return false;
        } else if (getGecFdocLineTypeCode() == null && other.getGecFdocLineTypeCode() != null) {
            return false;
        } else if (!getGecFdocLineTypeCode().equals(other.getGecFdocLineTypeCode())) {
            return false;
        } else if (getGecAcctLineSeqNumber() == null && other.getGecAcctLineSeqNumber() != null) {
            return false;
        } else if (!getGecAcctLineSeqNumber().equals(other.getGecAcctLineSeqNumber())) {
            return false;
        }

        return true;
    }


    @Override
    public String toString () {
        return String.format(TO_STRING_FORMAT,
                getEntryId(), getGecDocumentNumber(), getGecDocRouteStatus(),
                getGecFdocLineTypeCode(), getGecAcctLineSeqNumber());
    }

}
