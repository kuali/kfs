package edu.arizona.kfs.module.tax.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.module.tax.service.TaxBatchExtractService;

public class PayeeMasterExtractStep extends AbstractStep {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeMasterExtractStep.class);

    private TaxBatchExtractService taxBatchExtractService;

    public void setTaxBatchExtractService(TaxBatchExtractService taxBatchExtractService) {
        this.taxBatchExtractService = taxBatchExtractService;
    }

    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.info("started master payee extraction step of job " + jobName + "at " + new Date());
        boolean retval = false;
        retval = taxBatchExtractService.extractPayees();
        return retval;
    }

}
