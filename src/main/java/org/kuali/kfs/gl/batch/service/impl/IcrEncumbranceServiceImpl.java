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
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class IcrEncumbranceServiceImpl implements IcrEncumbranceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IcrEncumbranceServiceImpl.class);

    protected IcrEncumbranceDao icrEncumbranceDao;
    protected ObjectTypeService objectTypeService;
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
        String[] expenseObjectTypes = objectTypeService.getBasicExpenseObjectTypes(currentFiscalYear).toArray(new String[0]);
        String costShareSubAccountType = KFSConstants.SubAccountType.COST_SHARE;

        //Get ICR Cost Types to exclude
        Collection<String> icrCostTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.INDIRECT_COST_TYPES_PARAMETER) );

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
                icrEncumbranceDao.buildIcrEncumbranceFeed(currentFiscalYear, currentFiscalPeriod, icrEncumbOriginCode, icrEncumbBalanceTypes, icrCostTypes, expenseObjectTypes, costShareSubAccountType, fw);
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
     * Sets the objectTypeService attribute, allowing injection of an implementation of that service
     *
     * @param objectTypeService the objectTypeService to set.
     */
    public void setObjectTypeService(ObjectTypeService objectTypeService) {
        this.objectTypeService = objectTypeService;
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
