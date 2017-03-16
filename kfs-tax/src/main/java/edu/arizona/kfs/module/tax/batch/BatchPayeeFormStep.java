package edu.arizona.kfs.module.tax.batch;

import java.util.Date;
import java.util.List;

import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.module.tax.businessobject.Payee;
import edu.arizona.kfs.module.tax.service.TaxForm1099GeneratorService;
import edu.arizona.kfs.module.tax.service.TaxParameterHelperService;
import edu.arizona.kfs.module.tax.service.TaxPayeeService;

public class BatchPayeeFormStep extends AbstractStep {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchPayeeFormStep.class);
    private TaxPayeeService taxPayeeService;
    private TaxParameterHelperService taxParameterHelperService;
    private TaxForm1099GeneratorService taxForm1099GeneratorService;

    public void setTaxPayeeService(TaxPayeeService taxPayeeService) {
        this.taxPayeeService = taxPayeeService;
    }

    public void setTaxParameterHelperService(TaxParameterHelperService taxParameterHelperService) {
        this.taxParameterHelperService = taxParameterHelperService;
    }

    public void setTaxForm1099GeneratorService(TaxForm1099GeneratorService taxForm1099GeneratorService) {
        this.taxForm1099GeneratorService = taxForm1099GeneratorService;
    }

    @Override
    public boolean execute(String jobName, Date jobRunDate) {
        LOG.info("executing BatchPayeeFormStep");
        int year = taxParameterHelperService.getTaxYear();
        List<Payee> list = taxPayeeService.loadPayees(Integer.valueOf(year));
        boolean success = taxForm1099GeneratorService.generateBatchPayeeForms(year, list);
        return success;

    }

}
