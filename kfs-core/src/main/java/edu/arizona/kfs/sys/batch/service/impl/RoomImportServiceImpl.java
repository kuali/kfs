package edu.arizona.kfs.sys.batch.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.impl.campus.CampusBo;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.sys.KFSParameterKeyConstants;
import edu.arizona.kfs.sys.batch.service.RoomImportService;
import edu.arizona.kfs.sys.businessobject.ArchibusRooms;
import edu.arizona.kfs.sys.dataaccess.BuildingAndRoomImportsDao;
import edu.arizona.kfs.sys.businessobject.ArchibusBuildings;

@Transactional
public class RoomImportServiceImpl implements RoomImportService {

	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoomImportServiceImpl.class);
	private BusinessObjectService businessObjectService;
	private String reportDirectoryName;
	private String batchFileDirectoryName;
	private DateTimeService dateTimeService;
	private DataDictionaryService ddService;
	private BuildingAndRoomImportsDao buildingAndRoomImportDao;

	public boolean prepareRoomImport() {
		LOG.info("Inside of prepareRoomImport()");
		String buildingCode = "";
		String campusCode = "";
		Room matchingRoom;
		Building matchingBuilding;
		String reportMsg;
		PrintWriter outReportErrorWriter = null;
		PrintWriter outReportWriter = null;
		String routeCodeValue = "";
		Map<String, String> routecodeToCampuscodeMap;
		CampusBo campus;

		try {
			outReportWriter = setupReportOutputFiles(outReportWriter);
			outReportErrorWriter = setupErrorReportOutputFiles(outReportErrorWriter);
			writetoReportOutputFilesHeader(outReportWriter, outReportErrorWriter);

			Collection<ArchibusRooms> archibusRooms = businessObjectService.findAll(ArchibusRooms.class);

			for (ArchibusRooms archibusRoom : archibusRooms) {
				LOG.info("Processing Record: " + archibusRoom.toString());
				reportMsg = "";
				matchingRoom = new Room();
				matchingBuilding = new Building();

				// look up building Alpha (buildingCode) by building
				// ID(BuildingId)
				Map<String, String> fieldValues = new HashMap<String, String>();
				fieldValues.put("buildingId", archibusRoom.getBuildingCode());
				Collection<ArchibusBuildings> archibusBuildings = (Collection<ArchibusBuildings>) businessObjectService.findMatching(ArchibusBuildings.class, fieldValues);

				if (archibusBuildings.size() < 1) {
					LOG.debug("There is no building for BL_ID of " + archibusRoom.getBuildingCode());
					reportMsg += "There is no building for BL_ID of " + archibusRoom.getBuildingCode() + ".  Room not added: " + archibusRoom.getBuildingRoomNumber();
					writetoErrorReportOutputFile(outReportErrorWriter, 	reportMsg);
					continue;
				}

				for (ArchibusBuildings archbuilding : archibusBuildings) {
					buildingCode = archbuilding.getBuildingCode();
					routeCodeValue = archbuilding.getRouteCode();
					break;
				}

				// Look up campusCode by using routecode value
				routecodeToCampuscodeMap = buildingAndRoomImportDao.PopulateRoutecodeToCampusCodeMap();

				if (StringUtils.isBlank(routecodeToCampuscodeMap.get(routeCodeValue))) {
					campusCode = KFSParameterKeyConstants.MAIN_CAMPUSCODE;
				} 
				else {
					campusCode = routecodeToCampuscodeMap.get(routeCodeValue);

					// Look for a valid Campus Code in KFS
					Map<String, String> pkeys = new HashMap<String, String>();
					pkeys.put("code", campusCode);
					pkeys.put("active", "Y");
					campus = (CampusBo) businessObjectService.findByPrimaryKey(CampusBo.class, pkeys);

					if (ObjectUtils.isNull(campus)) {
						LOG.debug("Campus Code is not valid, record not saved to building");
						reportMsg += "Campus Code of " + campusCode + " is invalid in KFS.";
						writetoErrorReportOutputFile(outReportErrorWriter, reportMsg);
						continue;
					}
				}

				// validate Building exists in KFS. No campus codes in Archibus
				Map<String, String> bpkeys = new HashMap<String, String>();
				bpkeys.put("campusCode", campusCode);
				bpkeys.put("buildingCode", buildingCode);
				bpkeys.put("active", "Y");
				matchingBuilding = (Building) businessObjectService.findByPrimaryKey(Building.class, bpkeys);

				// if building is not in kfs write error do not save!
				if (ObjectUtils.isNull(matchingBuilding)) {
					LOG.info("Building " + archibusRoom.getBuildingCode() + "does not exist in KFS, room not saved or inactivated");
					writetoErrorReportOutputFile(outReportErrorWriter, reportMsg, "", buildingCode, archibusRoom.getBuildingRoomNumber(), "Building(" + buildingCode + ") does not exist in KFS or inactivated");
					continue;
				}

				// validate all fields
				if (!isValid(archibusRoom, outReportErrorWriter, campusCode, buildingCode)) {
					continue;
				}

				// validate to update or insert
				Map<String, String> keys = new HashMap<String, String>();
				keys.put("campusCode", campusCode);
				keys.put("buildingCode", buildingCode);
				keys.put("buildingRoomNumber", archibusRoom.getBuildingRoomNumber());
				matchingRoom = (Room) businessObjectService.findByPrimaryKey(Room.class, keys);

				// Do an insert to room
				if (ObjectUtils.isNull(matchingRoom)) {
					LOG.debug("New Room added to KFS");
					matchingRoom = new Room();
					insertRoom(buildingCode, campusCode, matchingRoom, reportMsg, outReportWriter, archibusRoom);
				} else {
					// if fields have changed then do an update
					if (!archibusRoom.equals(matchingRoom)) {
						updateRoom(buildingCode, campusCode, matchingRoom, reportMsg, outReportWriter, archibusRoom);
					}
				}// update or insert
			}

			// loop through all KFS rooms If they are not in archibus rooms then
			// deactivate the kfs room
			Collection<Room> kfsRooms = businessObjectService.findAll(Room.class);
			for (Room kfsRoom : kfsRooms) {
				disableKFSRoomsNotInArchibus(outReportWriter, kfsRoom);
			}
		}
		catch (Exception e) {
			LOG.error("Problem occured in prepareRoomImport() " + e.getMessage());
			throw new RuntimeException("Problem occured in prepareRoomImport() " + e.getMessage(), e);
		} 
		finally {
			outReportErrorWriter.close();
			outReportWriter.close();
		}
		return true;
	}

	private void updateRoom(String buildingCode, String campusCode, Room matchingRoom, String reportMsg, PrintWriter outReportWriter, ArchibusRooms archibusRoom) {
		reportMsg += StringUtils.rightPad(campusCode, 15, "");
		reportMsg += StringUtils.rightPad(buildingCode, 15, "");
		reportMsg += StringUtils.rightPad(archibusRoom.getBuildingRoomNumber(), 15, "");
		LOG.debug("Room exists, so updating Room");
		
		// See which fields were updated and report them
		reportMsg += "Updated KFS room and overwrote these field: ";
		String kfsRoomValue = null;
		String archRoomValue = null;
		
		if (archibusRoom.getBuildingRoomDepartment().equalsIgnoreCase(KFSParameterKeyConstants.HYPHEN) || StringUtils.isBlank(archibusRoom.getBuildingRoomDepartment())) {
			archRoomValue = "";
		}
		else {
			archRoomValue = archibusRoom.getBuildingRoomDepartment();
		}
		if (StringUtils.isBlank(matchingRoom.getBuildingRoomDepartment())) {
			kfsRoomValue = "";
		}
		else {
			kfsRoomValue = matchingRoom.getBuildingRoomDepartment();
		}
		if (!archRoomValue.equalsIgnoreCase(kfsRoomValue)) {
			reportMsg += "BuildingRoomDepartment(" + matchingRoom.getBuildingRoomDepartment() + "), ";
			matchingRoom.setBuildingRoomDepartment(archibusRoom.getBuildingRoomDepartment());
		}
		if (archibusRoom.getBuildingRoomType().equalsIgnoreCase(KFSParameterKeyConstants.HYPHEN) || StringUtils.isBlank(archibusRoom.getBuildingRoomType())) {
			archRoomValue = "";
		}
		else {
			archRoomValue = archibusRoom.getBuildingRoomType();
		}
		if (StringUtils.isBlank(matchingRoom.getBuildingRoomType())) {
			kfsRoomValue = "";
		}
		else {
			kfsRoomValue = matchingRoom.getBuildingRoomType();
		}
		if (!archRoomValue.equalsIgnoreCase(kfsRoomValue)) {
			reportMsg += "BuildingRoomType(" + matchingRoom.getBuildingRoomType() + "), ";
			matchingRoom.setBuildingRoomType(archibusRoom.getBuildingRoomType());
		}
		if (archibusRoom.getBuildingRoomDescription().equalsIgnoreCase(KFSParameterKeyConstants.HYPHEN) || StringUtils.isBlank(archibusRoom.getBuildingRoomDescription())) {
			archRoomValue = "";
		}
		else {
			archRoomValue = archibusRoom.getBuildingRoomDescription();
		}
		if (StringUtils.isBlank(matchingRoom.getBuildingRoomDescription())) {
			kfsRoomValue = "";
		}
		else {
			kfsRoomValue = matchingRoom.getBuildingRoomDescription();
		}
		if (!archRoomValue.equalsIgnoreCase(kfsRoomValue)) {
			reportMsg += "BuildingRoomDescription(" + matchingRoom.getBuildingRoomDescription() + "), ";
			matchingRoom.setBuildingRoomDescription(archibusRoom.getBuildingRoomDescription());
		}
		if(!matchingRoom.isActive()){
			reportMsg += "Active(false)";
			matchingRoom.setActive(true);
		}
		businessObjectService.save(matchingRoom);
		writetoReportOutputFile(outReportWriter, reportMsg);
	}

	private void insertRoom(String buildingCode, String campusCode, Room matchingRoom, String reportMsg, PrintWriter outReportWriter, ArchibusRooms archibusRoom) {
		matchingRoom.setCampusCode(campusCode);
		matchingRoom.setBuildingCode(buildingCode);
		matchingRoom.setBuildingRoomNumber(archibusRoom.getBuildingRoomNumber());

		if (!StringUtils.isBlank(archibusRoom.getBuildingRoomType()) || !archibusRoom.getBuildingRoomType().equalsIgnoreCase(KFSParameterKeyConstants.HYPHEN)) {
			matchingRoom.setBuildingRoomType(archibusRoom.getBuildingRoomType());
		}

		if (!StringUtils.isBlank(archibusRoom.getBuildingRoomDepartment()) || !archibusRoom.getBuildingRoomDepartment().equalsIgnoreCase(KFSParameterKeyConstants.HYPHEN)) {
			matchingRoom.setBuildingRoomDepartment(archibusRoom.getBuildingRoomDepartment());
		}

		if (!StringUtils.isBlank(archibusRoom.getBuildingRoomDescription()) || !archibusRoom.getBuildingRoomDescription().equalsIgnoreCase(KFSParameterKeyConstants.HYPHEN)) {
			matchingRoom.setBuildingRoomDescription(archibusRoom.getBuildingRoomDescription());
		}
		matchingRoom.setActive(true);
		 businessObjectService.save(matchingRoom);
		writetoReportOutputFile(outReportWriter, reportMsg, campusCode, buildingCode, archibusRoom.getBuildingRoomNumber(), "New Room added to KFS.");
	}

	private boolean isValid(ArchibusRooms archibusRoom, PrintWriter outReportErrorWriter, String campusCode, String buildingCode) {
		boolean valid = true;
		String reportMsg = "";

		reportMsg += StringUtils.rightPad(campusCode, 15, "");
		reportMsg += StringUtils.rightPad(buildingCode, 15, "");
		reportMsg += StringUtils.rightPad(archibusRoom.getBuildingRoomNumber(), 15, "");

		Boolean isBuildingCodeRequired = ddService.isAttributeRequired(Room.class, "buildingCode");
		Integer maxBuildingCodeLen = ddService.getAttributeMaxLength(Room.class, "buildingCode");
		Boolean isBuildingRoomNumberRequired = ddService.isAttributeRequired(Room.class, "buildingRoomNumber");
		Integer maxBuildingRoomNumberLen = ddService.getAttributeMaxLength(Room.class, "buildingRoomNumber");
		Boolean isBuildingRoomTypeRequired = ddService.isAttributeRequired(Room.class, "buildingRoomType");
		Integer maxBuildingRoomTypeLen = ddService.getAttributeMaxLength(Room.class, "buildingRoomType");		
		Boolean isBuildingRoomDepartmentRequired = ddService.isAttributeRequired(Room.class, "buildingRoomDepartment");
		Integer maxBuildingRoomDepartmentLen = ddService.getAttributeMaxLength(Room.class, "buildingRoomDepartment");
		Boolean isBuildingRoomDescriptionRequired = ddService.isAttributeRequired(Room.class, "buildingRoomDescription");
		Integer maxBuildingRoomDescriptionLen = ddService.getAttributeMaxLength(Room.class, "buildingRoomDescription");
		
		if (isAttributeNotValid(isBuildingCodeRequired, archibusRoom.getBuildingCode())) {
			valid = false; 
			reportMsg += "BuildingCode(" + archibusRoom.getBuildingCode() + ") is Not Valid, ";
		}
		if (isAttributeLenTooLong(maxBuildingCodeLen, archibusRoom.getBuildingCode().length())) {
			valid = false; 
			reportMsg += "BuildingCode(" + archibusRoom.getBuildingCode() + ") is longer than " + maxBuildingCodeLen + ", ";
		}
		if (isAttributeNotValid(isBuildingRoomNumberRequired, archibusRoom.getBuildingRoomNumber())) {
			valid = false; 
			reportMsg += "BuidlingRoomNumber(" + archibusRoom.getBuildingRoomNumber() + ") is Not Valid, ";
		}
		if (isAttributeLenTooLong(maxBuildingRoomNumberLen, archibusRoom.getBuildingRoomNumber().length())) {
			valid = false; 
			reportMsg += "BuildingRoomNumber("	+ archibusRoom.getBuildingRoomNumber() + ") is longer than " + maxBuildingRoomNumberLen + ", ";
		}
		if (isAttributeNotValid(isBuildingRoomTypeRequired, archibusRoom.getBuildingRoomType())) {
			valid = false; 
			reportMsg += "BuildingRoomType(" + archibusRoom.getBuildingRoomType() + ") is Not Valid, ";
		}
		if (isAttributeLenTooLong(maxBuildingRoomTypeLen, archibusRoom.getBuildingRoomType().length())) {
			valid = false;
			reportMsg += "BuidlingRoomType(" + archibusRoom.getBuildingRoomType() + ") is longer than " + maxBuildingRoomTypeLen + ", ";
		}
		if (isAttributeNotValid(isBuildingRoomDepartmentRequired, archibusRoom.getBuildingRoomDepartment())) {
			valid = false; 
			reportMsg += "BuildingRoomDepartment(" + archibusRoom.getBuildingRoomDepartment() + ") is Not Valid, ";
		}
		if (isAttributeLenTooLong(maxBuildingRoomDepartmentLen, archibusRoom.getBuildingRoomDepartment().length())) {
			valid = false;
			reportMsg += "BuildingRoomDepartment(" + archibusRoom.getBuildingRoomDepartment() + ") is longer than " + maxBuildingRoomDepartmentLen + ", ";
		}
		if (isAttributeNotValid(isBuildingRoomDescriptionRequired, archibusRoom.getBuildingRoomDescription())) {
			valid = false; 
			reportMsg += "BuildingRoomDescription(" + archibusRoom.getBuildingRoomDescription() + ") is Not Valid, ";
		}
		if (isAttributeLenTooLong(maxBuildingRoomDescriptionLen, archibusRoom.getBuildingRoomDescription().length())) {
			valid = false;
			reportMsg += "BuildingRoomDescription(" + archibusRoom.getBuildingRoomDescription() + ") is longer than " + maxBuildingRoomDescriptionLen + ", ";
		}
		
		if (!valid) {
			writetoErrorReportOutputFile(outReportErrorWriter, reportMsg);
		}
		return valid;
	}

	private void disableKFSRoomsNotInArchibus(PrintWriter outReportWriter, Room kfsRoom) {
		String buildingCode;
		String reportMsg;

		ArchibusRooms archRoom;

		reportMsg = "";

		LOG.info("Processing Record: " + kfsRoom.toString());
		// We need to get the buildingId to query the ArchibusRooms
		buildingCode = null;

		Map<String, String> fieldValues = new HashMap<String, String>();
		fieldValues.put("buildingCode", kfsRoom.getBuildingCode());
		Collection<ArchibusBuildings> archibusBuildings = (Collection<ArchibusBuildings>) businessObjectService.findMatching(ArchibusBuildings.class, fieldValues);

		for (ArchibusBuildings archBuilding : archibusBuildings) {
			buildingCode = archBuilding.getBuildingId();
			break;
		}
		Map<String, String> keys = new HashMap<String, String>();
		keys.put("buildingRoomNumber", kfsRoom.getBuildingRoomNumber());
		keys.put("buildingCode", buildingCode);
		archRoom = (ArchibusRooms) businessObjectService.findByPrimaryKey(ArchibusRooms.class, keys);

		if (ObjectUtils.isNull(archRoom) && !KFSParameterKeyConstants.NO_ROOM_LOCATION.equals(kfsRoom.getBuildingRoomNumber())) {
			if (kfsRoom.isActive()) {
				LOG.info("inactivating building in kfs that does not exist in Archibus");
				kfsRoom.setActive(false);
				 businessObjectService.save(kfsRoom);
				writetoReportOutputFile(outReportWriter, reportMsg, kfsRoom.getCampusCode(), kfsRoom.getBuildingCode(), kfsRoom.getBuildingRoomNumber(), "inactivating room in kfs that does not exist in Archibus");
			}
		}
	}

	private PrintWriter setupErrorReportOutputFiles(PrintWriter outErrorReportWriter) {
		try {
			String outputErrorFile = reportDirectoryName + File.separator + "roomImportErrorReport_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()) + ".txt";
			outErrorReportWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputErrorFile)));
		} 
		catch (FileNotFoundException e) {
			LOG.error("Error in RoomImportServiceImpl in method setUp prepareRoomImport. File won't process");
			throw new RuntimeException(e);
		} 
		catch (IOException e) {
			LOG.error("Error in RoomImportServiceImpl in method setUp prepareRoomImport. IOException");
			throw new RuntimeException(e);
		} 
		catch (Exception e) {
			LOG.error("Error in RoomImportServiceImpl in method setUp prepareRoomImport. Exception");
			throw new RuntimeException(e);
		}
		LOG.debug("Exit setupErrorReportOutputFiles() " + System.currentTimeMillis());
		return outErrorReportWriter;
	}

	private PrintWriter setupReportOutputFiles(PrintWriter outReportWriter) {
		try {
			String outputFile = reportDirectoryName + File.separator + "roomImportSuccessReport_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()) + ".txt";
			outReportWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
		} 
		catch (FileNotFoundException e) {
			LOG.error("Error in RoomImportServiceImpl in method setUp prepareRoomImport. File won't process");
			throw new RuntimeException(e);
		} 
		catch (IOException e) {
			LOG.error("Error in RoomImportServiceImpl in method setUp prepareRoomImport. IOException");
			throw new RuntimeException(e);
		} 
		catch (Exception e) {
			LOG.error("Error in RoomImportServiceImpl in method setUp prepareRoomImport. Exception");
			throw new RuntimeException(e);
		}
		LOG.debug("Exit setupReportOutputFiles() " + System.currentTimeMillis());
		return outReportWriter;
	}

	private void writetoReportOutputFilesHeader(PrintWriter outReportWriter, PrintWriter outErrorReportWriter) {
		String title = "             ROOM IMPORT REPORT";
		String header = "CampusCode     BuildingCode   RoomNumber     Info:";
		outReportWriter.format("%s%n", title);
		outErrorReportWriter.format("%s%n", title);
		outReportWriter.format("%s%n", header);
		outErrorReportWriter.format("%s%n", header);
	}

	private void writetoErrorReportOutputFile(PrintWriter outReportErrorWriter, String reportMsg, String campusCode, String buildingCode, String buildingRoomNumber, String msg) {
		reportMsg += StringUtils.rightPad(campusCode, 15, "");
		reportMsg += StringUtils.rightPad(buildingCode, 15, "");
		reportMsg += StringUtils.rightPad(buildingRoomNumber, 15, "");
		reportMsg += msg;
		outReportErrorWriter.format("%s%n", reportMsg);
	}

	private void writetoErrorReportOutputFile(PrintWriter outReportErrorWriter, String reportMsg) {
		outReportErrorWriter.format("%s%n", reportMsg);
	}

	private void writetoReportOutputFile(PrintWriter outReportWriter, String reportMsg, String campusCode, String buildingCode, String buildingRoomNumber, String msg) {
		reportMsg += StringUtils.rightPad(campusCode, 15, "");
		reportMsg += StringUtils.rightPad(buildingCode, 15, "");
		reportMsg += StringUtils.rightPad(buildingRoomNumber, 15, "");
		reportMsg += msg;
		outReportWriter.format("%s%n", reportMsg);
	}

	private void writetoReportOutputFile(PrintWriter outReportWriter, String reportMsg) {
		outReportWriter.format("%s%n", reportMsg);
	}

	private Boolean isAttributeLenTooLong(Integer maxLen, Integer attributeLen) {
		if (attributeLen > maxLen) {
			return true;
		}
		return false;
	}
	
	private Boolean isAttributeNotValid(Boolean isAttributeRequired, String attributeValue) {
		if ((attributeValue.equalsIgnoreCase(KFSParameterKeyConstants.HYPHEN) || StringUtils.isBlank(attributeValue)) && isAttributeRequired) {
			return true;
		}
		return false;
	}
	
	public String getReportDirectoryName() {
		return reportDirectoryName;
	}

	public void setReportDirectoryName(String reportDirectoryName) {
		this.reportDirectoryName = reportDirectoryName;
	}

	public String getBatchFileDirectoryName() {
		return batchFileDirectoryName;
	}

	public void setBatchFileDirectoryName(String batchFileDirectoryName) {
		this.batchFileDirectoryName = batchFileDirectoryName;
	}

	public DateTimeService getDateTimeService() {
		return dateTimeService;
	}

	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	public void setDdService(DataDictionaryService ddService) {
		this.ddService = ddService;
	}

	public void setBuildingAndRoomImportDao(
			BuildingAndRoomImportsDao buildingAndRoomImportDao) {
		this.buildingAndRoomImportDao = buildingAndRoomImportDao;
	}

	public BusinessObjectService getBusinessObjectService() {
		return businessObjectService;
	}

	public void setBusinessObjectService(
			BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

}
