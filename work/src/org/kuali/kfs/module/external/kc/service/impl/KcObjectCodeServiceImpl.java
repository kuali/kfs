/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.integration.cg.dto.KcObjectCode;
import org.kuali.kfs.module.external.kc.service.KcObjectCodeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class KcObjectCodeServiceImpl implements KcObjectCodeService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KcObjectCodeServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected ObjectCodeService objectCodeService;

    /**
     * @see org.kuali.kfs.module.external.kc.service.BudgetAdjustmentService#lookupObjectCodes(java.util.HashMap)
     */
    @Override
    public List<KcObjectCode> lookupObjectCodes(java.util.List<HashMapElement> searchCriteria) {
        HashMap <String, String> hashMap = new HashMap();
        List <ObjectCode> objCodeList = new ArrayList<ObjectCode>();

        if ((searchCriteria == null) || (searchCriteria.size() == 0)) {
            objCodeList = (List<ObjectCode>) businessObjectService.findAll(ObjectCode.class);

        } else {
            for (HashMapElement hashMapElement: searchCriteria) {
                hashMap.put(hashMapElement.getKey(), hashMapElement.getValue());
            }
            objCodeList = (List<ObjectCode>) (businessObjectService.findMatching(ObjectCode.class, hashMap));
        }
        List <KcObjectCode> kcObjectCodeList = new ArrayList();
        for (ObjectCode objectCode : objCodeList) {
            kcObjectCodeList.add( createKcObjectCode(objectCode));
        }
        return kcObjectCodeList;
    }

    /**
     * @see org.kuali.kfs.module.external.kc.service.getObjectCode(String, String, String)
     */
    @Override
    public KcObjectCode getObjectCode(String universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        Integer fiscalYear = new Integer(universityFiscalYear);
        ObjectCode objectCode = objectCodeService.getByPrimaryId(fiscalYear, chartOfAccountsCode, financialObjectCode);
        return createKcObjectCode(objectCode);
    }

    protected KcObjectCode createKcObjectCode(ObjectCode objectCode) {
        KcObjectCode kcObjectCode = new KcObjectCode();
        kcObjectCode.setObjectCodeName(objectCode.getCode());
        kcObjectCode.setDescription(objectCode.getName());
        return kcObjectCode;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ObjectCodeService getObjectCodeService() {
        return objectCodeService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

}
