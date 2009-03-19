package org.kuali.kfs.vnd.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CommodityContractManager extends PersistableBusinessObjectBase {

	private String purchasingCommodityCode;
	private String campusCode;
	private Integer contractManagerCode;

    private Campus campus;
    private CommodityCode commodityCode;
    private ContractManager contractManager;
    
	/**
	 * Default constructor.
	 */
	public CommodityContractManager() {

	}

	public String getPurchasingCommodityCode() { 
		return purchasingCommodityCode;
	}

	public void setPurchasingCommodityCode(String purchasingCommodityCode) {
		this.purchasingCommodityCode = purchasingCommodityCode;
	}

	public String getCampusCode() { 
		return campusCode;
	}

	public void setCampusCode(String campusCode) {
		this.campusCode = campusCode;
	}

	public Integer getContractManagerCode() { 
		return contractManagerCode;
	}

	public void setContractManagerCode(Integer contractManagerCode) {
		this.contractManagerCode = contractManagerCode;
	}

	public Campus getCampus() { 
        return campus = (Campus) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Campus.class).retrieveExternalizableBusinessObjectIfNecessary(this, campus, "campus");
	}

	/**
	 * Sets the campus attribute.
	 * 
	 * @param campus The campus to set.
	 * @deprecated
	 */
	public void setCampus(Campus campus) {
		this.campus = campus;
	}


    public CommodityCode getCommodityCode() {
        return commodityCode;
    }

    /**
     * Sets the commodity attribute value.
     * @param commodityCode The commodityCode to set.
     * @deprecated
     */
    public void setCommodityCode(CommodityCode commodityCode) {
        this.commodityCode = commodityCode;
    }


    public ContractManager getContractManager() {
        return contractManager;
    }

    /**
     * Sets the contractManager attribute value.
     * @param contractManager The contractManager to set.
     * @deprecated
     */
    public void setContractManager(ContractManager contractManager) {
        this.contractManager = contractManager;
    }

    /**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put(VendorPropertyConstants.PURCHASING_COMMODITY_CODE, this.purchasingCommodityCode);
        m.put(VendorPropertyConstants.CAMPUS_CODE, this.campusCode);
        if (this.contractManagerCode != null) {
            m.put(VendorPropertyConstants.CONTRACT_MANAGER_CODE, this.contractManagerCode.toString());
        }
	    return m;
    }
}
