package edu.arizona.kfs.pdp.batch;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.kuali.rice.core.api.datetime.DateTimeService;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;

/**
 * Batch input type for the shipping file load job.
 */
public class ShippingInputFileType extends XmlBatchInputFileTypeBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ShippingInputFileType.class);

    private DateTimeService dateTimeService;
    
    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return KFSConstants.SHIP_FILE_TYPE_IDENTIFIER;
    }
    
    /**
     * No additional information is added to shipping batch files.
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kim.bo.Person, java.lang.Object,
     *      java.lang.String)
     */
    public String getFileName(String principalName, Object parsedFileContents, String userIdentifier) {
        String fileName = "ship" + KFSConstants.SHIPPING_FILENAME_DELIMITER + principalName;     
        if (StringUtils.isNotBlank(userIdentifier)) {
            fileName += KFSConstants.SHIPPING_FILENAME_DELIMITER + userIdentifier;
        }
        fileName += KFSConstants.SHIPPING_FILENAME_DELIMITER + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());

        // remove spaces in filename
        fileName = StringUtils.remove(fileName, " ");

        return fileName;
    }
   
    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    public boolean validate(Object parsedFileContents) {
        return true;
    }

    public String getAuthorPrincipalName(File file) {
        String[] fileNameParts = StringUtils.split(file.getName(), KFSConstants.SHIPPING_FILENAME_DELIMITER);
        if (fileNameParts.length >= 2) {
            return fileNameParts[1];
        }
        return null;
    }

    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_SHIP;
    }
    
    /**
     * Gets the dateTimeService attribute.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
