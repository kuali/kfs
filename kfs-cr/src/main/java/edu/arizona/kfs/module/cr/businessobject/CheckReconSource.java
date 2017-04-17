package edu.arizona.kfs.module.cr.businessobject;

import java.io.Serializable;
import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.module.cr.CrPropertyConstants;

/**
 * CheckReconSource
 * Represents a Check Reconciliation Source.
 */
public class CheckReconSource extends PersistableBusinessObjectBase implements Serializable {
    private static final long serialVersionUID = 5621116178337978383L;

    private Integer id;
    private String sourceCode;
    private String sourceName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put(CrPropertyConstants.CheckReconSource.ID, id.toString());
        map.put(CrPropertyConstants.CheckReconSource.SOURCE_CODE, sourceCode);
        map.put(CrPropertyConstants.CheckReconSource.SOURCE_NAME, sourceName);
        return map;
    }

}
