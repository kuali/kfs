package edu.arizona.kfs.sys.batch.service;

import org.kuali.rice.krad.service.BusinessObjectService;

public interface BuildingImportService {

	public boolean prepareBuildingImport();

	public String getReportDirectoryName();

	public void setReportDirectoryName(String reportDirectoryName);

	public BusinessObjectService getBusinessObjectService();

	public void setBusinessObjectService(BusinessObjectService businessObjectService);

	public String getBatchFileDirectoryName();

	public void setBatchFileDirectoryName(String batchFileDirectoryName);

}
