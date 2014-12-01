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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.impl.ObjectCodeServiceImpl;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.integration.cg.dto.KcObjectCode;
import org.kuali.kfs.module.external.kc.service.KcObjectCodeService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class KcObjectCodeServiceImpl implements KcObjectCodeService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectCodeServiceImpl.class);
    
    private DocumentService documentService;
    private ParameterService parameterService;
    private DataDictionaryService dataDictionaryService;
    private BusinessObjectService businessObjectService;

 
    /**
     * @see org.kuali.kfs.module.external.kc.service.BudgetAdjustmentService#lookupObjectCodes(java.util.HashMap)
     */
    @Override
    public List<KcObjectCode> lookupObjectCodes(java.util.List<HashMapElement> searchCriteria) {
        HashMap <String, String> hashMap = new HashMap();
        List <ObjectCode> objCodeList = new ArrayList<ObjectCode>();       
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        
        if ((searchCriteria == null) || (searchCriteria.size() == 0)) {
            objCodeList = (List<ObjectCode>) boService.findAll(ObjectCode.class);
            
        } else {
            for (HashMapElement hashMapElement: searchCriteria) {
                hashMap.put(hashMapElement.getKey(), hashMapElement.getValue());
            }
            objCodeList = (List<ObjectCode>) (boService.findMatching(ObjectCode.class, hashMap));
        }
        List <KcObjectCode> kcObjectCodeList = new ArrayList();
        for (ObjectCode objectCode : objCodeList) {
            kcObjectCodeList.add( createKcObjectCode(objectCode));
        }
        return kcObjectCodeList;
    }
    
    /**
     * 
        @see org.kuali.kfs.module.external.kc.service.getObjectCode(String, String, String)
     */
    public KcObjectCode getObjectCode(String universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        Integer fiscalYear = new Integer(universityFiscalYear);
        ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        ObjectCode objectCode = (ObjectCode) objectCodeService.getByPrimaryId(fiscalYear, chartOfAccountsCode, financialObjectCode);
        return createKcObjectCode(objectCode);
    }
 
    protected KcObjectCode createKcObjectCode(ObjectCode objectCode) {
        KcObjectCode kcObjectCode = new KcObjectCode();
        kcObjectCode.setObjectCodeName(objectCode.getCode());
        kcObjectCode.setDescription(objectCode.getName());
        return kcObjectCode;
    }
    
    /**
     * Extracts errors for error report writing.
     * 
     * @return a list of error messages
     */
 
}
