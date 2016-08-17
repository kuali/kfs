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
package edu.arizona.kfs.gl.batch.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.PosterEntriesStep;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.gl.GeneralLedgerConstants;
import edu.arizona.kfs.gl.dataaccess.BudgetAdjustmentTransactionDao;
import edu.arizona.kfs.gl.batch.service.BudgetAdjustmentCashTransferService;


@Transactional
public class BudgetAdjustmentCashTransferServiceImpl implements BudgetAdjustmentCashTransferService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentCashTransferServiceImpl.class);

    protected BudgetAdjustmentTransactionDao budgetAdjustmentTransactionDao;
    protected OptionsService optionsService;
    protected ParameterService parameterService;
    protected UniversityDateService universityDateService;
    protected String batchFileDirectoryName;
    //protected String inputFile;

    
    @Override
    public void extractAndSaveBudgetAdjustmentEntries() {
    	
    	//retrieve document type and origination code parameter values
    	Collection<String> documentTypeCodes = parameterService.getParameterValuesAsString(PosterEntriesStep.class, GeneralLedgerConstants.CASH_TRANSFER_DOC_TYPE_CODES);
    	Collection<String> originationCodes = parameterService.getParameterValuesAsString(PosterEntriesStep.class, GeneralLedgerConstants.CASH_TRANSFER_ORIGINATION_CODES);
      
        //get current year system options
    	 SystemOptions options = getOptionsService().getOptions(universityDateService.getCurrentFiscalYear());
         if (ObjectUtils.isNull(options)) {
             options = getOptionsService().getCurrentYearOptions();
         }
    }

    @Override
    public void generateBudgetAdjustmentCashTransferTransactions() {
      
    }
    
    public void setBudgetAdjustmentTransactionDao(BudgetAdjustmentTransactionDao budgetAdjustmentTransactionDao) {
        this.budgetAdjustmentTransactionDao = budgetAdjustmentTransactionDao;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
  
    public OptionsService getOptionsService() {
        return optionsService;
    }
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }
    
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
