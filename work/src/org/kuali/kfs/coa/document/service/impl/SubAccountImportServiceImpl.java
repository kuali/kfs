package org.kuali.kfs.coa.document.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubAccountImportDetail;
import org.kuali.kfs.coa.document.service.SubAccountImportService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class is the service implementation for the SubAccountImport document structure.
 */

public class SubAccountImportServiceImpl implements SubAccountImportService {
    private static final Logger LOG = Logger.getLogger(SubAccountImportServiceImpl.class);

    protected BusinessObjectService businessObjectService;


    /**
     * save sub-accounts
     */
    @Override
    public void saveSubAccounts(List<SubAccountImportDetail> subAccountImportDetails) {

        List<SubAccount> persistSubAccounts = new ArrayList<SubAccount>();
        SubAccount subAccount = null;

        for (SubAccountImportDetail importLine : subAccountImportDetails) {
            subAccount = new SubAccount();

            subAccount.setChartOfAccountsCode(importLine.getChartOfAccountsCode());
            subAccount.setAccountNumber(importLine.getAccountNumber());
            subAccount.setSubAccountNumber(importLine.getSubAccountNumber());
            subAccount.setSubAccountName(importLine.getSubAccountName());
            subAccount.setActive(importLine.isActive());
            subAccount.setFinancialReportChartCode(importLine.getFinancialReportChartCode());
            subAccount.setFinReportOrganizationCode(importLine.getFinReportOrganizationCode());
            subAccount.setFinancialReportingCode(importLine.getFinancialReportingCode());

            A21SubAccount a21SubAccount = new A21SubAccount();
            a21SubAccount.setChartOfAccountsCode(subAccount.getChartOfAccountsCode());
            a21SubAccount.setAccountNumber(subAccount.getAccountNumber());
            a21SubAccount.setSubAccountNumber(subAccount.getSubAccountNumber());
            a21SubAccount.setSubAccountTypeCode(importLine.getDefaultSubAccountTypeCode());
            subAccount.setA21SubAccount(a21SubAccount);

            persistSubAccounts.add(subAccount);
        }
        businessObjectService.save(persistSubAccounts);
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
