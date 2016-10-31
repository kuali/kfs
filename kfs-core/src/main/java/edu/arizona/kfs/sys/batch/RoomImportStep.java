package edu.arizona.kfs.sys.batch;

import java.util.Date;

import org.springframework.util.StopWatch;
import org.kuali.kfs.sys.batch.AbstractStep;

import edu.arizona.kfs.sys.batch.service.RoomImportService;

public class RoomImportStep extends AbstractStep {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoomImportStep.class);
	private RoomImportService roomImportService;

	@Override
	public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
		StopWatch stopwatch = new StopWatch();
		stopwatch.start(jobName);
		try {
			return roomImportService.prepareRoomImport();
		} 
		catch (Exception e) {
			LOG.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public void setRoomImportService(RoomImportService roomImportService) {
		this.roomImportService = roomImportService;
	}

}
