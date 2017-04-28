package edu.arizona.kfs.pdp.batch;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.springframework.core.io.UrlResource;

import edu.arizona.kfs.pdp.batch.service.PrepaidChecksService;

/**
 * This step will call the <code>PrepaidChecksService</code> to pick up incoming Prepaid payment files and process.
 */
public class LoadPrepaidChecksStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoadPrepaidChecksStep.class);

    private PrepaidChecksService prepaidChecksService;
    private BatchInputFileType prepaidChecksInputFileType;

    /**
     * Picks up the required path from the batchInputFIleType as well as from the prepaid
     * checks service
     *
     * @see org.kuali.kfs.sys.batch.AbstractStep#getRequiredDirectoryNames()
     */
    @Override
    public List<String> getRequiredDirectoryNames() {
    	List<String> requiredDirectoryList = new ArrayList<String>();
        requiredDirectoryList.add(prepaidChecksInputFileType.getDirectoryPath());
        requiredDirectoryList.addAll(prepaidChecksService.getRequiredDirectoryNames());

        return requiredDirectoryList;
    }

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.debug("execute() started");
        //check if prepaidChecks.xsd exists. If not terminate at this point.
        if(prepaidChecksInputFileType instanceof XmlBatchInputFileTypeBase) {
            try {
                UrlResource schemaResource = new UrlResource(((XmlBatchInputFileTypeBase)prepaidChecksInputFileType).getSchemaLocation());
                if(!schemaResource.exists()) {
                    LOG.error(schemaResource.getFilename() + " file does not exist");
                    throw new RuntimeException("error getting schema stream from url: " + schemaResource.getFilename() + " file does not exist ");
                }
            }
            catch (MalformedURLException ex) {
                LOG.error("error getting schema url: " + ex.getMessage());
                throw new RuntimeException("error getting schema url:  " + ex.getMessage(), ex);
            }
        }

        return prepaidChecksService.processPrepaidChecks(prepaidChecksInputFileType);        
    }

    public void setPrepaidChecksService(PrepaidChecksService prepaidChecksService) {
        this.prepaidChecksService = prepaidChecksService;
    }

    public void setPrepaidChecksInputFileType(BatchInputFileType prepaidChecksInputFileType) {
        this.prepaidChecksInputFileType = prepaidChecksInputFileType;
    }

}
