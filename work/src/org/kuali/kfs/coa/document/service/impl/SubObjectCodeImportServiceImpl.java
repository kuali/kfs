package org.kuali.kfs.coa.document.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCodeImportDetail;
import org.kuali.kfs.coa.document.service.SubObjectCodeImportService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class is the service implementation for the SubAccountImport document structure.
 */

public class SubObjectCodeImportServiceImpl implements SubObjectCodeImportService {
    private static final Logger LOG = Logger.getLogger(SubObjectCodeImportServiceImpl.class);

    protected BusinessObjectService businessObjectService;

    @Override
    public void saveSubObjectCodeDetails(List<SubObjectCodeImportDetail> subObjectCodeImportDetails) {
        List<SubObjectCode> persistSubObjCds = new ArrayList<SubObjectCode>();
        SubObjectCode subObjectCd = null;

        for (SubObjectCodeImportDetail importLine : subObjectCodeImportDetails) {
            subObjectCd = new SubObjectCode();

            subObjectCd.setUniversityFiscalYear(importLine.getUniversityFiscalYear());
            subObjectCd.setChartOfAccountsCode(importLine.getChartOfAccountsCode());
            subObjectCd.setAccountNumber(importLine.getAccountNumber());
            subObjectCd.setFinancialObjectCode(importLine.getFinancialObjectCode());
            subObjectCd.setFinancialSubObjectCode(importLine.getFinancialSubObjectCode());
            subObjectCd.setActive(importLine.isActive());
            subObjectCd.setFinancialSubObjectCodeName(importLine.getFinancialSubObjectCodeName());
            subObjectCd.setFinancialSubObjectCdshortNm(importLine.getFinancialSubObjectCdshortNm());

            persistSubObjCds.add(subObjectCd);
        }
        businessObjectService.save(persistSubObjCds);

    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
