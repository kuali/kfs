package edu.arizona.kfs.pdp.batch;

import java.io.File;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.kuali.rice.core.api.datetime.DateTimeService;

import edu.arizona.kfs.pdp.PdpConstants;

/**
 * Batch input type for the PDP Prepaid checks payment file.
 */
public class PrepaidChecksInputFileType extends XmlBatchInputFileTypeBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PrepaidChecksInputFileType.class);

    private DateTimeService dateTimeService;
    
    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kim.api.identity.Person, java.lang.Object,
     *      java.lang.String)
     */
    @Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifer) {
        Timestamp currentTimestamp = dateTimeService.getCurrentTimestamp();

        String fileName = PdpConstants.PDP_FILE_UPLOAD_FILE_PREFIX  + "_" + principalName;
        if (StringUtils.isNotBlank(fileUserIdentifer)) {
            fileName += "_" + StringUtils.remove(fileUserIdentifer, " ");
        }
        fileName += "_" + dateTimeService.toString(currentTimestamp, "yyyyMMdd_HHmmss");

        // remove spaces in filename
        fileName = StringUtils.remove(fileName, " ");

        return fileName;
    }

    @Override
    public String getAuthorPrincipalName(File file) {
        String[] fileNameParts = StringUtils.split(file.getName(), "_");
        if (fileNameParts.length > 3) {
            return fileNameParts[2];
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    @Override
    public String getFileTypeIdentifer() {
        return PdpConstants.PrePaidChecksConstants.PREPAID_PAYMENT_FILE_TYPE_INDENTIFIER;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    @Override
    public boolean validate(Object parsedFileContents) {
    	 return true;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    @Override
    public String getTitleKey() {
        return PdpKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_PAYMENT;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileTypeBase#process(java.lang.String, java.lang.Object)
     */
    @Override
    public void process(String fileName, Object parsedFileContents) {
    	// do nothing purposefully
    }
   
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
  
}

