package edu.arizona.kfs.module.tax.service;

import edu.arizona.kfs.module.tax.businessobject.ElectronicFile;

public interface ElectronicFilingService {

    /**
     * Get Electronic File
     * 
     * @param year
     * @return ElectronicFile
     * @throws Exception
     */
    public ElectronicFile getElectronicFile(Integer year) throws Exception;

}
