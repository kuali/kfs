package org.kuali.kfs.sys.batch;

import org.apache.commons.io.FileUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.io.File;
import java.io.IOException;

/**
 * Helper class to build up a batch directory and then tear it down when it is no longer needed.  To create a directory, call setUp; to remove
 * the directory, just call tearDown.
 */
public class BatchDirectoryHelper {
    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BatchDirectoryHelper.class);

    private String module;
    private String directoryName;
    private boolean batchFileDirectoryCreated = false;

    private volatile static ConfigurationService configurationService;

    public BatchDirectoryHelper(String module, String directoryName) {
        this.module = module;
        this.directoryName = directoryName;
    }

    public void createBatchDirectory() {
        File batchFileDirectory = new File(getBatchFileDirectoryName());
        if (!batchFileDirectory.exists()) {
            batchFileDirectory.mkdir();
            batchFileDirectoryCreated = true;
        }
    }

    public void removeBatchDirectory() {
        if (batchFileDirectoryCreated) {
            try {
                File batchDirectoryFile = new File(getBatchFileDirectoryName());
                FileUtils.deleteDirectory(batchDirectoryFile);
                batchFileDirectoryCreated = false;
            } catch (IOException e) {
                LOG.error("Could not remove batch directory for test");
                throw new RuntimeException(e);
            }
        }
    }

    public String getBatchFileDirectoryName() {
        String stagingDirectory = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory");
        return stagingDirectory + File.separator + this.module + File.separator + this.directoryName;
    }

    private ConfigurationService getConfigurationService() {
        if (this.configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        return configurationService;
    }
}
