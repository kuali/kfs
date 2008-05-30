/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.document.EquipmentLoanOrReturnDocument;
import org.kuali.module.cams.service.EquipmentLoanInfoService;

/**
 * Implements EquipmentLoanInfoService
 */
public class EquipmentLoanInfoServiceImpl implements EquipmentLoanInfoService {
    private BusinessObjectService businessObjectService;

    public void setEquipmentLoanInfo(Asset asset) {

        if (asset.getExpectedReturnDate() != null && asset.getLoanReturnDate() == null) {
            Map<String, Long> params = new HashMap<String, Long>();
            params.put(CamsPropertyConstants.EquipmentLoanOrReturnDocument.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber());
            Collection<EquipmentLoanOrReturnDocument> matchingDocs = getBusinessObjectService().findMatching(EquipmentLoanOrReturnDocument.class, params);

            List<EquipmentLoanOrReturnDocument> sortableList = new ArrayList<EquipmentLoanOrReturnDocument>();

            for (EquipmentLoanOrReturnDocument equipmentLoanOrReturn : matchingDocs) {
                equipmentLoanOrReturn.refreshReferenceObject(CamsPropertyConstants.EquipmentLoanOrReturnDocument.DOCUMENT_HEADER);
                if (equipmentLoanOrReturn.getDocumentHeader() != null && KFSConstants.DocumentStatusCodes.APPROVED.equals(equipmentLoanOrReturn.getDocumentHeader().getFinancialDocumentStatusCode())) {
                    sortableList.add(equipmentLoanOrReturn);
                }
            }
            Comparator<EquipmentLoanOrReturnDocument> comparator = new Comparator<EquipmentLoanOrReturnDocument>() {
                public int compare(EquipmentLoanOrReturnDocument o1, EquipmentLoanOrReturnDocument o2) {
                    // sort descending based on loan date
                    return o2.getLoanDate().compareTo(o1.getLoanDate());
                }
            };
            Collections.sort(sortableList, comparator);

            if (!sortableList.isEmpty()) {
                asset.setLoanOrReturnInfo(sortableList.get(0));
            }
        }
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
