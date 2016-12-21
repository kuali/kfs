package edu.arizona.kfs.module.purap.document.service;

import edu.arizona.kfs.tax.businessobject.ElectronicFile;

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
