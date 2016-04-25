package edu.arizona.kfs.coa.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtensionBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * UAF-120 / MOD-FP0072-01 : Budget Shell and Cross Organization Attributes for
 * Account
 * 
 * @author Jonathan Keller <keller.jonathan@gmail.com>
 * @author Adam Kost <kosta@email.arizona.edu>
 */
public class AccountExtension extends PersistableBusinessObjectExtensionBase {

    private static final long serialVersionUID = 1L;
    private static transient volatile BusinessObjectService boService;

    private String chartOfAccountsCode;
    private String accountNumber;
    private String budgetShellCode;
    private String crossOrganizationCode;

    // Unused Database fields
    private String fundsTypeCode;
    private String taxRegionCode;
    private String faCostSubCatCode;
    private String institutionalFringeCoaCodeExt;
    private String institutionalFringeAccountExt;

    // Helper Objects
    private transient volatile BudgetShellCode budgetShell;
    private transient volatile CrossOrganizationCode crossOrganization;
    private transient volatile FACostSubCategory faCostSubCategory;    // FA Subcategory Object
    private transient volatile TaxRegion taxRegionObj;

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBudgetShellCode() {
        return budgetShellCode;
    }

    public void setBudgetShellCode(String budgetShellCode) {
        this.budgetShellCode = budgetShellCode;
    }

    public String getCrossOrganizationCode() {
        return crossOrganizationCode;
    }

    public void setCrossOrganizationCode(String crossOrganizationCode) {
        this.crossOrganizationCode = crossOrganizationCode;
    }

    public String getFundsTypeCode() {
        return fundsTypeCode;
    }

    public void setFundsTypeCode(String fundsTypeCode) {
        this.fundsTypeCode = fundsTypeCode;
    }

    public String getTaxRegionCode() {
        return taxRegionCode;
    }

    public void setTaxRegionCode(String taxRegionCode) {
        this.taxRegionCode = taxRegionCode;
    }

    public TaxRegion getTaxRegionObj() {
    	if (taxRegionObj == null || !StringUtils.equals(taxRegionObj.getTaxRegionCode(), taxRegionCode)) {
    		taxRegionObj = getBusinessObjectService().findBySinglePrimaryKey(TaxRegion.class, taxRegionCode);
        }
    	return taxRegionObj;
	}

	public void setTaxRegionObj(TaxRegion taxRegionObj) {
    	this.taxRegionObj = taxRegionObj;
    	setTaxRegionCode(taxRegionObj.getTaxRegionCode());
	}

	public String getFaCostSubCatCode() {
        return faCostSubCatCode;
    }

    public void setFaCostSubCatCode(String faCostSubCatCode) {
        this.faCostSubCatCode = faCostSubCatCode;
    }

    public FACostSubCategory getFaCostSubCategory() {
    	if (faCostSubCategory == null || !StringUtils.equals(faCostSubCategory.getFaCostSubCatCode(), faCostSubCatCode)) {
    		faCostSubCategory = getBusinessObjectService().findBySinglePrimaryKey(FACostSubCategory.class, faCostSubCatCode);
        }
    	return faCostSubCategory;
    }
    
    public void setFaCostSubCategory(FACostSubCategory faCostSubCategory) {
    	this.faCostSubCategory = faCostSubCategory;
    	setFaCostSubCatCode(faCostSubCategory.getFaCostSubCatCode());
    }

    public String getInstitutionalFringeCoaCodeExt() {
        return institutionalFringeCoaCodeExt;
    }

    public void setInstitutionalFringeCoaCodeExt(String institutionalFringeCoaCodeExt) {
        this.institutionalFringeCoaCodeExt = institutionalFringeCoaCodeExt;
    }

    public String getInstitutionalFringeAccountExt() {
        return institutionalFringeAccountExt;
    }

    public void setInstitutionalFringeAccountExt(String institutionalFringeAccountExt) {
        this.institutionalFringeAccountExt = institutionalFringeAccountExt;
    }

    public BudgetShellCode getBudgetShell() {
        if (budgetShell == null || !StringUtils.equals(budgetShell.getBudgetShellCode(), budgetShellCode)) {
            budgetShell = getBusinessObjectService().findBySinglePrimaryKey(BudgetShellCode.class, budgetShellCode);
        }
        return budgetShell;
    }

    public void setBudgetShell(BudgetShellCode budgetShell) {
        this.budgetShell = budgetShell;
        setBudgetShellCode(budgetShell.getBudgetShellCode());
    }

    public CrossOrganizationCode getCrossOrganization() {
        if (crossOrganization == null || !StringUtils.equals(crossOrganization.getCrossOrganizationCode(), crossOrganizationCode)) {
            crossOrganization = getBusinessObjectService().findBySinglePrimaryKey(CrossOrganizationCode.class, crossOrganizationCode);
        }
        return crossOrganization;
    }

    public void setCrossOrganization(CrossOrganizationCode crossOrganization) {
        this.crossOrganization = crossOrganization;
        setCrossOrganizationCode(crossOrganization.getCrossOrganizationCode());
    }

    protected BusinessObjectService getBusinessObjectService() {
        if (boService == null) {
            boService = SpringContext.getBean(BusinessObjectService.class);
        }
        return boService;
    }
}