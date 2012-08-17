/*
 * Copyright 2007-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.cache.annotation.Cacheable;

/**
 * Default implementation of the <code>BankService</code> interface.
 *
 * @see org.kuali.kfs.fp.service.BankService
 */
public class BankServiceImpl implements BankService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected DataDictionaryService dataDictionaryService;
    protected ParameterService parameterService;
    protected ParameterEvaluatorService parameterEvaluatorService;

    /**
     * @see org.kuali.kfs.fp.service.BankService#getByPrimaryId(java.lang.String)
     */
    @Override
    @Cacheable(value=Bank.CACHE_NAME, key="'bankCode='+#p0")
    public Bank getByPrimaryId(String bankCode) {
        return businessObjectService.findBySinglePrimaryKey(Bank.class, bankCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.BankService#getDefaultBankByDocType(java.lang.String)
     */
    @Override
    @Cacheable(value=Bank.CACHE_NAME, key="'DefaultByDocType-'+#p0")
    public Bank getDefaultBankByDocType(String documentTypeCode) {
        if (parameterService.parameterExists(Bank.class, KFSParameterKeyConstants.DEFAULT_BANK_BY_DOCUMENT_TYPE)) {
            List<String> parmValues = new ArrayList<String>( parameterService.getSubParameterValuesAsString(Bank.class, KFSParameterKeyConstants.DEFAULT_BANK_BY_DOCUMENT_TYPE, documentTypeCode) );

            if (parmValues != null && !parmValues.isEmpty()) {
                String defaultBankCode = parmValues.get(0);

                Bank defaultBank = getByPrimaryId(defaultBankCode);

                // check active status, if not return continuation bank if active
                if ( defaultBank != null && !defaultBank.isActive() && defaultBank.getContinuationBank() != null && defaultBank.getContinuationBank().isActive()) {
                    return defaultBank.getContinuationBank();
                }

                return defaultBank;
            }
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.sys.service.BankService#getDefaultBankByDocType(java.lang.Class)
     */
    @Override
    @Cacheable(value=Bank.CACHE_NAME, key="'DefaultByDocClass-'+#p0")
    public Bank getDefaultBankByDocType(Class<?> documentClass) {
        String documentTypeCode = dataDictionaryService.getDocumentTypeNameByClass(documentClass);

        if (StringUtils.isBlank(documentTypeCode)) {
            throw new RuntimeException("Document type not found for document class: " + documentClass.getName());
        }
        return getDefaultBankByDocType(documentTypeCode);
    }

    /**
     * @see org.kuali.kfs.sys.service.BankService#isBankSpecificationEnabled()
     */
    @Override
    @Cacheable(value=Bank.CACHE_NAME, key="'isBankSpecificationEnabled'")
    public boolean isBankSpecificationEnabled() {
        return parameterService.getParameterValueAsBoolean(Bank.class, KFSParameterKeyConstants.ENABLE_BANK_SPECIFICATION_IND);
    }

    /**
     * @see org.kuali.kfs.sys.service.BankService#isBankSpecificationEnabledForDocument(java.lang.Class)
     */
    @Override
    @Cacheable(value=Bank.CACHE_NAME, key="'isBankSpecificationEnabled'+#p0")
    public boolean isBankSpecificationEnabledForDocument(Class<?> documentClass) {
        String documentTypeCode = dataDictionaryService.getDocumentTypeNameByClass(documentClass);
        if (ArrayUtils.contains(PERMANENT_BANK_SPECIFICATION_ENABLED_DOCUMENT_TYPES, documentTypeCode)) {
            return true;
        }
        ParameterEvaluator evaluator = SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Bank.class, KFSParameterKeyConstants.BANK_CODE_DOCUMENT_TYPES, documentTypeCode);
        return evaluator.evaluationSucceeds();
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
        this.parameterEvaluatorService = parameterEvaluatorService;
    }
}
