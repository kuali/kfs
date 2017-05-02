package edu.arizona.kfs.coa.businessobject;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
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
    private String institutionalFringeCoaCodeExt;
    private String institutionalFringeAccountExt;

    // Unused Database fields
    private String fundsTypeCode;
    private String taxRegionCode;
    private String faCostSubCatCode;

    // Helper Objects
    private BudgetShellCode budgetShell;
    private CrossOrganizationCode crossOrganization;
    private FACostSubCategory faCostSubCategory;    // FA Subcategory Object
    private TaxRegion taxRegionObj;
    private Account institutionalFringeAccountObj;
    private Chart institutionalFringeCoaCodeObj;

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
    	return taxRegionObj;
	}

	public void setTaxRegionObj(TaxRegion taxRegionObj) {
    	this.taxRegionObj = taxRegionObj;
	}

	public String getFaCostSubCatCode() {
        return faCostSubCatCode;
    }

    public void setFaCostSubCatCode(String faCostSubCatCode) {
        this.faCostSubCatCode = faCostSubCatCode;
    }

    public FACostSubCategory getFaCostSubCategory() {
    	return faCostSubCategory;
    }
    
    public void setFaCostSubCategory(FACostSubCategory faCostSubCategory) {
    	this.faCostSubCategory = faCostSubCategory;
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
        return budgetShell;
    }

    public void setBudgetShell(BudgetShellCode budgetShell) {
        this.budgetShell = budgetShell;
    }

    public CrossOrganizationCode getCrossOrganization() {
        return crossOrganization;
    }

    public void setCrossOrganization(CrossOrganizationCode crossOrganization) {
        this.crossOrganization = crossOrganization;
    }

    protected BusinessObjectService getBusinessObjectService() {
        if (boService == null) {
            boService = SpringContext.getBean(BusinessObjectService.class);
        }
        return boService;
    }
    
    public Account getInstitutionalFringeAccountObj() {
        return institutionalFringeAccountObj;
    }

    public void setInstitutionalFringeAccountObj(Account institutionalFringeAccountObj) {
        this.institutionalFringeAccountObj = institutionalFringeAccountObj;
    }

    public Chart getInstitutionalFringeCoaCodeObj() {
        return institutionalFringeCoaCodeObj;
    }

    public void setInstitutionalFringeCoaCodeObj(Chart institutionalFringeCoaCodeObj) {
        this.institutionalFringeCoaCodeObj = institutionalFringeCoaCodeObj;
    }
    
    
}