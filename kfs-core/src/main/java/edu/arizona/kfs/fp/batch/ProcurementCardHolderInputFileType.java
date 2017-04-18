package edu.arizona.kfs.fp.batch;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;

/**
 * Batch input type for the procurement cardholder job.
 */
public class ProcurementCardHolderInputFileType extends XmlBatchInputFileTypeBase {

    private DateTimeService dateTimeService;

    public DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }

    /**
     * No additional information is added to procurement cardholder batch files.
     */
    @Override
    public String getFileName(String principalName, Object parsedFileContents, String userIdentifier) {
        String fileName = "pcdh_" + principalName;
        if (StringUtils.isNotBlank(userIdentifier)) {
            fileName += "_" + userIdentifier;
        }
        fileName += "_" + getDateTimeService().toDateTimeStringForFilename(getDateTimeService().getCurrentDate());

        // remove spaces in filename
        fileName = StringUtils.remove(fileName, " ");

        return fileName;
    }

    @Override
    public String getFileTypeIdentifer() {
        return KFSConstants.ProcurementCardholder.PCDH_FILE_TYPE_IDENTIFIER;
    }

    @Override
    public boolean validate(Object parsedFileContents) {
        return true;
    }

    @Override
    public String getAuthorPrincipalName(File file) {
        String[] fileNameParts = StringUtils.split(file.getName(), "_");
        if (fileNameParts.length >= 2) {
            return fileNameParts[1];
        }
        return null;
    }

    @Override
    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_PCDH;
    }

}
