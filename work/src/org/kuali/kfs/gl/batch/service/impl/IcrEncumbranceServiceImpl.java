/*
 * Copyright 2012 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.IcrEncumbranceService;
import org.kuali.kfs.gl.dataaccess.IcrEncumbranceDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class IcrEncumbranceServiceImpl implements IcrEncumbranceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IcrEncumbranceServiceImpl.class);

    protected IcrEncumbranceDao icrEncumbranceDao;
    protected ParameterService parameterService;
    protected UniversityDateService universityDateService;
    protected String batchFileDirectoryName;

    /**
     * @see org.kuali.kfs.gl.batch.service.IcrEncumbranceService#buildIcrEncumbranceFeed()
     */
    @Override
    public File buildIcrEncumbranceFeed() {
        File encumbranceFeedFile = null;

        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();
        String currentFiscalPeriod = universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod();

        //Get Expense Object Types and Cost Share Sub-Account Type
        ObjectTypeService objectTypeService = SpringContext.getBean(ObjectTypeService.class);
        String[] expenseObjectTypes = objectTypeService.getBasicExpenseObjectTypes(currentFiscalYear).toArray(new String[0]);
        String costShareSubAccountType = KFSConstants.SubAccountType.COST_SHARE;

        //Get ICR Encumbrance Origination Code and Balance Types
        String icrEncumbOriginCode = parameterService.getParameterValueAsString(KFSConstants.CoreModuleNamespaces.GL, GeneralLedgerConstants.PosterService.ICR_ENCUMBRANCE_FEED_PARM_TYP, GeneralLedgerConstants.PosterService.ICR_ENCUMBRANCE_ORIGIN_CODE_PARM_NM);
        Collection<String> icrEncumbBalanceTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(KFSConstants.CoreModuleNamespaces.GL, GeneralLedgerConstants.PosterService.ICR_ENCUMBRANCE_FEED_PARM_TYP, GeneralLedgerConstants.PosterService.ICR_ENCUMBRANCE_BALANCE_TYPE_PARM_NM) );
        if (StringUtils.isBlank(icrEncumbOriginCode) || icrEncumbBalanceTypes.isEmpty()) {
            throw new RuntimeException("ICR Encumbrance Origin Code or Balance Types parameter was blank, cannot create encumbrances without the configured Origin Code and Balance Types");
        }

        try {
            String enumbranceFeedFileName = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_ENCUMBRANCE_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            encumbranceFeedFile = new File(enumbranceFeedFileName);

            BufferedWriter fw = new BufferedWriter(new FileWriter(encumbranceFeedFile));
            try {
                icrEncumbranceDao.buildIcrEncumbranceFeed(currentFiscalYear, currentFiscalPeriod, icrEncumbOriginCode, icrEncumbBalanceTypes, expenseObjectTypes, costShareSubAccountType, fw);
            }
            finally {
                if (fw != null) {
                    fw.flush();
                    fw.close();
                }
            }
        }
        catch (Exception ex) {
            try {
                if (encumbranceFeedFile != null) {
                    encumbranceFeedFile.delete();
                }
            }
            catch (Exception ee) {
                LOG.error("Failed to delete even though process failed" + encumbranceFeedFile);
            }
            throw new RuntimeException("Error, see the stack trace.", ex);
        }

        return encumbranceFeedFile;
    }

    /**
     * Sets the icrEncumbranceDao attribute, allowing injection of an implementation of that data access object
     *
     * @param icrEncumbranceDao the icrEncumbranceDao implementation to set
     * @see org.kuali.kfs.gl.dataaccess.IcrEncumbranceDao
     */
    public void setIcrEncumbranceDao(IcrEncumbranceDao icrEncumbranceDao) {
        this.icrEncumbranceDao = icrEncumbranceDao;
    }

    /**
     * Sets the parameterService attribute, allowing injection of an implementation of that service
     *
     * @param parameterService the parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the unversityDateService attribute, allowing injection of an implementation of that service
     *
     * @param universityDateService the universityDateService implementation to set
     * @see org.kuali.kfs.sys.service.UniversityDateService
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     *
     * @param batchFileDirectoryName the batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
