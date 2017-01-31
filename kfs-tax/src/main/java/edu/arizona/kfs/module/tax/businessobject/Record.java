package edu.arizona.kfs.module.tax.businessobject;

import java.util.List;

public interface Record {

    /**
     * Validate Record
     * 
     * @return List
     */
    public List<ElectronicFileException> validateRecord();

    /**
     * Get Record String
     * 
     * @return String
     */
    public String getRecordString();

}
