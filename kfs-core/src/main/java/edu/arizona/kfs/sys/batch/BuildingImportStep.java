package edu.arizona.kfs.sys.batch;

import java.util.Date;

import org.kuali.kfs.sys.batch.AbstractStep;
import org.springframework.util.StopWatch;

import edu.arizona.kfs.sys.batch.service.BuildingImportService;

public class BuildingImportStep extends AbstractStep {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BuildingImportStep.class);
	private BuildingImportService buildingImportService;

	public void setBuildingImportService(BuildingImportService buildingImportService) {
		this.buildingImportService = buildingImportService;
	}
	
	@Override
	public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start(jobName);
		try {
			return buildingImportService.prepareBuildingImport();
		} 
		catch (Exception e) {
			LOG.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

}
