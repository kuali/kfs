/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.vnd.batch.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.vnd.batch.dataaccess.DebarredVendorDao;
import org.kuali.kfs.vnd.batch.dataaccess.DebarredVendorMatchDao;
import org.kuali.kfs.vnd.batch.service.VendorExcludeService;
import org.kuali.kfs.vnd.businessobject.DebarredVendorDetail;
import org.kuali.kfs.vnd.businessobject.DebarredVendorMatch;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.transaction.annotation.Transactional;
@Transactional
public class VendorExcludeServiceImpl implements VendorExcludeService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VendorExcludeServiceImpl.class);

    private BatchInputFileService batchInputFileService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private BatchInputFileType batchInputFileType;
    private DebarredVendorDao debarredVendorDao;
    private DebarredVendorMatchDao debarredVendorMatchDao;

    /**
     * @see org.kuali.kfs.vnd.batch.service.VendorExcludeService.loadEplsFile()
     */
    @Override
    public boolean loadEplsFile() {
        //  create a list of the files to process
        List<String> fileNames = batchInputFileService.listInputFileNamesWithDoneFile(batchInputFileType);
        String fileName = null;
        long lastModified = 0;
        if(fileNames.size() == 0) {
            return true;
        }
        File tempFile;
        //Consider the latest file as the files are cumulative
        for (String name : fileNames) {
            tempFile = new File (name);
            if(tempFile.lastModified() > lastModified) {
                lastModified = tempFile.lastModified();
                fileName = name;
            }
        }
        if (fileName == null) {
            LOG.error("BatchInputFileService.listInputFileNamesWithDoneFile(" +
                    batchInputFileType.getFileTypeIdentifer() + ") returned NULL which should never happen.");
            throw new RuntimeException("BatchInputFileService.listInputFileNamesWithDoneFile(" +
                    batchInputFileType.getFileTypeIdentifer() + ") returned NULL which should never happen.");
        }
        byte[] fileByteContent;
        List debarredVendorList = new ArrayList<DebarredVendorDetail> ();
        try {
            fileByteContent = IOUtils.toByteArray(new FileInputStream(fileName));
        }
        catch (FileNotFoundException ex) {
            LOG.error("File not found [" + fileName + "]. " + ex.getMessage());
            throw new RuntimeException("File not found [" + fileName + "]. " + ex.getMessage());
        }
        catch (IOException ex) {
            LOG.error("IO Exception loading: [" + fileName + "]. " + ex.getMessage());
            throw new RuntimeException("IO Exception loading: [" + fileName + "]. " + ex.getMessage());
        }
        Object parsedObject = batchInputFileService.parse(batchInputFileType, fileByteContent);
        debarredVendorList = (List<DebarredVendorDetail>) parsedObject;

        purgeOldVendorRecords();
        businessObjectService.save(debarredVendorList);

        removeDoneFiles(fileNames);
        return true;
    }

    /**
     * Clears out associated .done files for the processed data files.
     */
    protected void removeDoneFiles(List<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }

    /**
     * @see org.kuali.kfs.vnd.batch.service.VendorExcludeService.matchVendors()
     */
    @Override
    public boolean matchVendors() {
        List<DebarredVendorMatch> matches = debarredVendorDao.match();
        businessObjectService.save(matches);
        return true;
    }

    /**
     * @see org.kuali.kfs.vnd.batch.service.VendorExcludeService.purgeOldVendorRecords()
     */
    @Override
    public void purgeOldVendorRecords() {
        businessObjectService.deleteMatching(DebarredVendorDetail.class, new HashMap());
    }

    /**
     * @see org.kuali.kfs.vnd.batch.service.VendorExcludeService.getDebarredVendorsUnmatched()
     */
    @Override
    public List<VendorDetail> getDebarredVendorsUnmatched() {
        return debarredVendorMatchDao.getDebarredVendorsUnmatched();
    }

    /**
     * @see org.kuali.kfs.vnd.batch.service.VendorExcludeService.confirmDebarredVendor()
     */
    @Override
    public void confirmDebarredVendor(int debarredVendorId) {
        DebarredVendorMatch match = debarredVendorMatchDao.getDebarredVendor(debarredVendorId);
        match.setConfirmStatusCode("C");
        match.setLastUpdatedPrincipalName(GlobalVariables.getUserSession().getPerson().getPrincipalName());
        match.setLastUpdatedTimeStamp(dateTimeService.getCurrentTimestamp());
        businessObjectService.save(match);
    }

    /**
     * @see org.kuali.kfs.vnd.batch.service.VendorExcludeService.denyDebarredVendor()
     */
    @Override
    public void denyDebarredVendor(int debarredVendorId) {
        DebarredVendorMatch match = debarredVendorMatchDao.getDebarredVendor(debarredVendorId);
        match.setConfirmStatusCode("D");
        match.setLastUpdatedPrincipalName(GlobalVariables.getUserSession().getPerson().getPrincipalName());
        match.setLastUpdatedTimeStamp(dateTimeService.getCurrentTimestamp());
        businessObjectService.save(match);
    }

    /**
     * Gets the batchInputFileService attribute.
     * @return Returns the batchInputFileService.
     */
    public BatchInputFileService getBatchInputFileService() {
        return batchInputFileService;
    }

    /**
     * Sets the batchInputFileService attribute value.
     * @param batchInputFileService The batchInputFileService to set.
     */
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    /**
     * Gets the businessObjectService attribute.
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the dateTimeService attribute.
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the batchInputFileType attribute.
     * @return Returns the batchInputFileType.
     */
    public BatchInputFileType getBatchInputFileType() {
        return batchInputFileType;
    }

    /**
     * Sets the batchInputFileType attribute value.
     * @param batchInputFileType The batchInputFileType to set.
     */
    public void setBatchInputFileType(BatchInputFileType batchInputFileType) {
        this.batchInputFileType = batchInputFileType;
    }

    /**
     * Gets the vendorExcludeDao attribute.
     * @return Returns the vendorExcludeDao.
     */
    public DebarredVendorDao getDebarredVendorDao() {
        return debarredVendorDao;
    }

    /**
     * Sets the vendorExcludeDao attribute value.
     * @param vendorExcludeDao The vendorExcludeDao to set.
     */
    public void setDebarredVendorDao(DebarredVendorDao debarredVendorDao) {
        this.debarredVendorDao = debarredVendorDao;
    }

    /**
     * Gets the debarredVendorMatchDao attribute.
     * @return Returns the debarredVendorMatchDao.
     */
    public DebarredVendorMatchDao getDebarredVendorMatchDao() {
        return debarredVendorMatchDao;
    }

    /**
     * Sets the debarredVendorMatchDao attribute value.
     * @param debarredVendorMatchDao The debarredVendorMatchDao to set.
     */
    public void setDebarredVendorMatchDao(DebarredVendorMatchDao debarredVendorMatchDao) {
        this.debarredVendorMatchDao = debarredVendorMatchDao;
    }
}
