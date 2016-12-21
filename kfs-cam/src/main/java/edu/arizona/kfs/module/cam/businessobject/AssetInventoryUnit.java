package edu.arizona.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import edu.arizona.kfs.sys.KFSPropertyConstants;

public class AssetInventoryUnit extends PersistableBusinessObjectBase implements MutableInactivatable {
	private static final long serialVersionUID = 1L;
	private String inventoryUnitCode;
	private String inventoryUnitName;
	private String chartOfAccountsCode;
	private String organizationCode;
	private boolean active;

	private Chart coaCodeObj;
	private Organization orgObj;

	public String getInventoryUnitCode() {
		return inventoryUnitCode;
	}

	public void setInventoryUnitCode(String inventoryUnitCode) {
		this.inventoryUnitCode = inventoryUnitCode;
	}

	public String getInventoryUnitName() {
		return inventoryUnitName;
	}

	public void setInventoryUnitName(String inventoryUnitName) {
		this.inventoryUnitName = inventoryUnitName;
	}

	public String getChartOfAccountsCode() {
		return chartOfAccountsCode;
	}

	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Chart getCoaCodeObj() {
		return coaCodeObj;
	}

	public void setCoaCodeObj(Chart coaCodeObj) {
		this.coaCodeObj = coaCodeObj;
	}

	public Organization getOrgObj() {
		return orgObj;
	}

	public void setOrgObj(Organization orgObj) {
		this.orgObj = orgObj;
	}

	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();

		if (this.inventoryUnitCode != null && this.chartOfAccountsCode != null && this.organizationCode != null) {
			m.put(KFSPropertyConstants.INVENTORY_UNIT_CODE, this.inventoryUnitCode.toString());
			m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.chartOfAccountsCode.toString());
			m.put(KFSPropertyConstants.INVENTORY_UNIT_ORGANIZATION_CODE, this.organizationCode.toString());
		}

		return m;
	}

}
