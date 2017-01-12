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
import edu.arizona.kfs.sys.batch.service.BuildingImportService;
import edu.arizona.kfs.sys.businessobject.ArchibusBuildings;
import edu.arizona.kfs.sys.businessobject.BuildingExtension;
import edu.arizona.kfs.sys.businessobject.RouteCode;
import edu.arizona.kfs.sys.dataaccess.BuildingAndRoomImportsDao;

@Transactional
public class BuildingImportServiceImpl implements BuildingImportService {
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BuildingImportServiceImpl.class);
	private String kfsUsCountryCode;
	private String archibusUsaCountryCode;
	private String batchFileDirectoryName;
	private String reportDirectoryName;
	private BusinessObjectService businessObjectService;
	private DataDictionaryService ddService;
	private DateTimeService dateTimeService;
	private BuildingAndRoomImportsDao buildingAndRoomImportDao;

	public boolean prepareBuildingImport() {
		Building building = null;
		Building buildingCodeMatch;
		String routeCodeValue;
		String campusCode;
		String reportMsg;
		PrintWriter outReportErrorWriter = null;
		PrintWriter outReportWriter = null;
		BuildingExtension buildingExt;
		CampusBo campus;
		Map<String, String> routecodeToCampuscodeMap = buildingAndRoomImportDao.PopulateRoutecodeToCampusCodeMap();

		try {
			outReportWriter = setupReportOutputFiles(outReportWriter);
			outReportErrorWriter = setupErrorReportOutputFiles(outReportErrorWriter);
			writetoReportOutputFilesHeader(outReportWriter,outReportErrorWriter);
			Collection<ArchibusBuildings> archibusBuildings = businessObjectService.findAll(ArchibusBuildings.class);

			// This will look for all records in Archibus building then do an
			// update or insert into buildings
			for (ArchibusBuildings archBuilding : archibusBuildings) {
				LOG.info("Processing Record: " + archBuilding.toString());
				reportMsg = "";
				routeCodeValue = archBuilding.getRouteCode();

				RouteCode routecode = (RouteCode) businessObjectService.findBySinglePrimaryKey(RouteCode.class, routeCodeValue);

				// report error if routecode is not set up in KFS
				if (!KFSParameterKeyConstants.HYPHEN.equalsIgnoreCase(routeCodeValue) && ObjectUtils.isNull(routecode)) {
					LOG.debug("Routecode value of " + routeCodeValue + " is not set up in KFS.");
					writetoErrorReportOutputFile(outReportErrorWriter, reportMsg, archBuilding.getCampusCode(), archBuilding.getBuildingCode(), "Routecode value of " + routeCodeValue + " is not set up in KFS.");
					continue;
				}
				// if building is active then continue trough
				if (KFSParameterKeyConstants.STRING_A.equalsIgnoreCase(archBuilding.getActive())) {
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
							LOG.debug("Campus code is not valid, record not saved to building");
							reportMsg += "Campus Code of " + campusCode + " is invalid in kfs.";
							writetoErrorReportOutputFile(outReportErrorWriter, reportMsg);
							continue;
						}
					}
					
					// validate to update or insert
					Map<String, String> keys = new HashMap<String, String>();
					keys.put("campusCode", campusCode);
					keys.put("buildingCode", archBuilding.getBuildingCode());
					building = (Building) businessObjectService.findByPrimaryKey(Building.class, keys);

					keys.clear();
					keys.put("buildingCode", archBuilding.getBuildingCode());
					keys.put("active", "Y");
					buildingCodeMatch = (Building) businessObjectService.findByPrimaryKey(Building.class, keys);

					if (ObjectUtils.isNull(building) && ObjectUtils.isNotNull(buildingCodeMatch)) {
						writetoErrorReportOutputFile(outReportErrorWriter, reportMsg, campusCode, archBuilding.getBuildingCode(), "There is a building in KFS with building code " + archBuilding.getBuildingCode() + " But the campus is " + buildingCodeMatch.getCampusCode());
						continue;
					}
					// validate all fields
					if (!isValid(archBuilding, outReportErrorWriter, campusCode)) {
						continue;
					}
					// Insert a new record here
					if (ObjectUtils.isNull(building)) {
						buildingExt = new BuildingExtension();
						LOG.info("New Building so adding NO_RM_LC to room and saving building");
						building = new Building();
						insertBuilding(building, routeCodeValue, campusCode, reportMsg, outReportWriter, buildingExt, archBuilding);
					} 
					else {
						if (!archBuilding.equals(building)) {
							updateBuilding(building, routeCodeValue, campusCode, reportMsg, outReportWriter, archBuilding, routecode);
						}
					}
				}
			}

			// loop through all kfs buildings if they are not in archibus
			// buildings, then disable the kfs building
			Collection<Building> kfsBuildings = businessObjectService.findAll(Building.class);
			for (Building kfsBuilding : kfsBuildings) {
				disableKFSBuildingsNotInArchibus(outReportWriter, kfsBuilding);
			}
		} 
		catch (Exception e) {
			LOG.error("Problem occured in prepareBuildingImport() " + e.getMessage());
			throw new RuntimeException("Problem occured in prepareBuildingImport() " + e.getMessage(), e);
		} 
		finally {
			outReportErrorWriter.close();
			outReportWriter.close();
		}
		return true;
	}

	private void insertBuilding(Building building, String routeCodeValue,
			String campusCode, String reportMsg, PrintWriter outReportWriter,
			BuildingExtension buildingExt, ArchibusBuildings archBuilding) {
		if (archibusUsaCountryCode.equalsIgnoreCase(archBuilding.getBuildingAddressCountryCode())) {
			building.setBuildingAddressCountryCode(kfsUsCountryCode);
		} 
		else {
			building.setBuildingAddressCountryCode(archBuilding.getBuildingAddressCountryCode());
		}
		building.setBuildingName(archBuilding.getBuildingName());
		building.setCampusCode(campusCode);
		building.setBuildingCode(archBuilding.getBuildingCode());
		building.setBuildingStreetAddress(archBuilding.getBuildingStreetAddress());
		building.setBuildingAddressCityName(archBuilding.getBuildingAddressCityName());
		building.setBuildingAddressStateCode(archBuilding.getBuildingAddressStateCode());
		building.setBuildingAddressZipCode(archBuilding.getBuildingAddressZipCode());
		building.setActive(true);

		// if routeCode needs to be updated
		if (!KFSParameterKeyConstants.HYPHEN.equalsIgnoreCase(routeCodeValue)) {
			buildingExt.setRouteCode(routeCodeValue);
			buildingExt.setCampusCode(campusCode);
			buildingExt.setBuildingCode(building.getBuildingCode());
			building.setExtension(buildingExt);
		}
		LOG.info("Inserting (Name,Code,Campus,City,State,Zip,Country,Active,Address, RouteCode)(" + building.getBuildingName() + "," + building.getBuildingCode() + "," + building.getCampusCode() + "," + building.getBuildingAddressCityName() + "," + building.getBuildingAddressStateCode() + "," + building.getBuildingAddressZipCode() + "," + building.getBuildingAddressCountryCode() + "," + building.isActive() + "," + building.getBuildingStreetAddress() + buildingExt.getRouteCode() + ")");
		businessObjectService.save(building);
		writetoReportOutputFile(outReportWriter, reportMsg, campusCode, archBuilding.getBuildingCode(), "New Building added to KFS along with room NO_RM_LC");

		// check to see if there is a room of "NO_RM_LC" for this building
		Map<String, String> kes = new HashMap<String, String>();
		kes.put("campusCode", campusCode);
		kes.put("buildingCode", building.getBuildingCode());
		kes.put("buildingRoomNumber", KFSParameterKeyConstants.NO_ROOM_LOCATION);
		Room matchingroom = (Room) businessObjectService.findByPrimaryKey(Room.class, kes);
		Room room = null;

		// Insert room of NO_RM_LC for all new buildings
		if (ObjectUtils.isNull(matchingroom)) {
			room = new Room();
			room.setCampusCode(campusCode);
			room.setBuildingCode(building.getBuildingCode());
			room.setBuildingRoomNumber(KFSParameterKeyConstants.NO_ROOM_LOCATION);
			room.setActive(true);
			businessObjectService.save(room);
		} 
		else { // update room for new building
			matchingroom = new Room();
			matchingroom.setCampusCode(campusCode);
			matchingroom.setBuildingCode(building.getBuildingCode());
			matchingroom.setBuildingRoomNumber(KFSParameterKeyConstants.NO_ROOM_LOCATION);
			matchingroom.setActive(true);
			businessObjectService.save(matchingroom);
		}
	}

	private boolean isValid(ArchibusBuildings archBuilding, PrintWriter outReportErrorWriter, String campusCode) {
		boolean valid = true;
		String reportMsg = "";

		reportMsg += StringUtils.rightPad(campusCode, 15, "");
		reportMsg += StringUtils.rightPad(archBuilding.getBuildingCode(), 15, "");

		Boolean isBuildingCodeRequired = ddService.isAttributeRequired(Building.class, "buildingCode");
		Integer maxBuildingCodeLen = ddService.getAttributeMaxLength(Building.class, "buildingCode");
		Boolean isBuildingNameRequired = ddService.isAttributeRequired(Building.class, "buildingName");
		Integer maxBuildingNameLen = ddService.getAttributeMaxLength(Building.class, "buildingName");
		Boolean isBuildingStreetAddressRequired = ddService.isAttributeRequired(Building.class, "buildingStreetAddress");
		Integer maxBuildingStreetAddressLen = ddService.getAttributeMaxLength(Building.class, "buildingStreetAddress");
		Boolean isBuildingAddressCityNameRequired = ddService.isAttributeRequired(Building.class, "buildingAddressCityName");
		Integer maxBuildingAddressCityNameLen = ddService.getAttributeMaxLength(Building.class, "buildingAddressCityName");
		Boolean isBuildingAddressStateCodeRequired = ddService.isAttributeRequired(Building.class, "buildingAddressStateCode");
		Integer maxBuildingAddressStateCodeLen = ddService.getAttributeMaxLength(Building.class, "buildingAddressStateCode");
		Boolean isBuildingAddressZipCodeRequired = ddService.isAttributeRequired(Building.class, "buildingAddressZipCode");
		Integer maxBuildingAddressZipCodeLen = ddService.getAttributeMaxLength(Building.class, "buildingAddressZipCode");
		Boolean isBuildingAddressCountryCodeRequired = ddService.isAttributeRequired(Building.class, "buildingAddressCountryCode");

		if (isAttributeNotValid(isBuildingCodeRequired, archBuilding.getBuildingCode())) {
			valid = false;
			reportMsg += "BuildingCode(" + archBuilding.getBuildingCode() + ") is Not Valid, ";
		}
		if (isAttributeLenTooLong(maxBuildingCodeLen, archBuilding.getBuildingCode().length())) {
			valid = false;
			reportMsg += "BuildingName(" + archBuilding.getBuildingCode() + ") is longer than " + maxBuildingCodeLen + ", ";
		}
		if (isAttributeNotValid(isBuildingNameRequired, archBuilding.getBuildingName())) {
			valid = false; 
			reportMsg += "BuildingName(" + archBuilding.getBuildingName() + ") is Not valid, ";
		}
		if (isAttributeLenTooLong(maxBuildingNameLen, archBuilding.getBuildingName().length())) {
			valid = false;
			reportMsg += "BuildingName(" + archBuilding.getBuildingName() + ") is longer than " + maxBuildingNameLen + ", ";
		}
		if (isAttributeNotValid(isBuildingStreetAddressRequired, archBuilding.getBuildingStreetAddress())) {
			valid = false;
			reportMsg += "BuildingStreetAddres(" + archBuilding.getBuildingStreetAddress() + ") is Not Valid, ";
		}
		if (isAttributeLenTooLong(maxBuildingStreetAddressLen, archBuilding.getBuildingStreetAddress().length())) {
			valid = false; 
			reportMsg += "BuildingStreetAddress(" + archBuilding.getBuildingStreetAddress() + ") is longer than " + maxBuildingStreetAddressLen + ", ";
		}
		if (isAttributeNotValid(isBuildingAddressCityNameRequired, archBuilding.getBuildingAddressCityName())) {
			valid = false;
			reportMsg += "BuildingAddressCityName(" + archBuilding.getBuildingAddressCityName() + ") is Not Valid, ";
		}
		if (isAttributeLenTooLong(maxBuildingAddressCityNameLen, archBuilding.getBuildingAddressCityName().length())) {
			valid = false;
			reportMsg += "BuildingAddressCityName(" + archBuilding.getBuildingAddressCityName() + ") is longer than " + maxBuildingAddressCityNameLen + ", ";
		}
		if (isAttributeNotValid(isBuildingAddressStateCodeRequired, archBuilding.getBuildingAddressStateCode())) {
			valid = false; 
			reportMsg += "BuildingAddressStateCode(" + archBuilding.getBuildingAddressStateCode() + ") is Not Valid, ";
		}
		if (isAttributeLenTooLong(maxBuildingAddressStateCodeLen, archBuilding.getBuildingAddressStateCode().length())) {
			valid = false; 
			reportMsg += "BuildingAddressStateCode(" + archBuilding.getBuildingAddressStateCode() + ") is longer than " + maxBuildingAddressStateCodeLen + ", ";
		}
		if (isAttributeNotValid(isBuildingAddressZipCodeRequired, archBuilding.getBuildingAddressZipCode())) {
			valid = false; 
			reportMsg += "BuildingAddressZipCode(" + archBuilding.getBuildingAddressZipCode() + ") is Not Valid, ";
		}
		if (isAttributeLenTooLong(maxBuildingAddressZipCodeLen, archBuilding.getBuildingAddressZipCode().length())) {
			valid = false; 
			reportMsg += "BuildingAddressZipCode(" + archBuilding.getBuildingAddressZipCode() + ") is longer than " + maxBuildingAddressZipCodeLen + ", ";
		}
		if (isAttributeNotValid(isBuildingAddressCountryCodeRequired, archBuilding.getBuildingAddressCountryCode())) {
			valid = false;
			reportMsg += "BuildingAddressCountryCode(" + archBuilding.getBuildingAddressCountryCode() + ") is Not Valid, ";
		}
		if (!valid) {
			writetoErrorReportOutputFile(outReportErrorWriter, reportMsg);
		}
		return valid;
	}
	
	private void updateBuilding(Building building, String routeCodeValue, String campusCode, String reportMsg, PrintWriter outReportWriter, ArchibusBuildings archBuilding, RouteCode routecode) {
		BuildingExtension buildingExt;
		buildingExt = (BuildingExtension) building.getExtension();
		LOG.debug("Building exists so just saving building if there were changes.");
		reportMsg += StringUtils.rightPad(campusCode, 15, "");
		reportMsg += StringUtils.rightPad(archBuilding.getBuildingCode(), 15, "");
		reportMsg += "Updated KFS building and overwrote these field: ";
		String archBuild = "";
		String kfsBuild = "";

		if (!building.isActive()) {
			reportMsg += "Active(" + building.isActive() + "), ";
			building.setActive(true);
		}
		if (!archBuilding.getBuildingStreetAddress().equalsIgnoreCase(building.getBuildingStreetAddress())) {
			reportMsg += "BuildingStreetAddress(" + building.getBuildingStreetAddress() + "), ";
			building.setBuildingStreetAddress(archBuilding.getBuildingStreetAddress());
		}
		if (!building.getBuildingAddressCityName().equalsIgnoreCase(archBuilding.getBuildingAddressCityName())) {
			reportMsg += "BuildingAddressCityName(" + building.getBuildingAddressCityName() + "), ";
			building.setBuildingAddressCityName(archBuilding.getBuildingAddressCityName());
		}
		if (!building.getBuildingAddressStateCode().equalsIgnoreCase(archBuilding.getBuildingAddressStateCode())) {
			reportMsg += "BuildingAddressStateCode(" + building.getBuildingAddressStateCode() + "), ";
			building.setBuildingAddressStateCode(archBuilding.getBuildingAddressStateCode());
		}
		if (!building.getBuildingAddressZipCode().equalsIgnoreCase(archBuilding.getBuildingAddressZipCode())) {
			reportMsg += "BuildingAddressZipCode(" + building.getBuildingAddressZipCode() + "), ";
			building.setBuildingAddressZipCode(archBuilding.getBuildingAddressZipCode());
		}

		if (!archBuilding.getBuildingName().equalsIgnoreCase(building.getBuildingName())) {
			reportMsg += "BuildingName(" + building.getBuildingName() + "), ";
			building.setBuildingName(archBuilding.getBuildingName());
		}

		if (StringUtils.isNotBlank(buildingExt.getRouteCode())) {
			kfsBuild = buildingExt.getRouteCode();
		}
		else {
			kfsBuild = "";
		}
		if (!KFSParameterKeyConstants.HYPHEN.equalsIgnoreCase(routeCodeValue)) {
			archBuild = routeCodeValue;
		}
		else {
			archBuild = "";
		}

		if (!archBuild.equalsIgnoreCase(kfsBuild)) {
			if (!KFSParameterKeyConstants.HYPHEN.equalsIgnoreCase(routeCodeValue)) {
				reportMsg += "RouteCode(" + buildingExt.getRouteCode() + "), ";
				buildingExt.setRouteCode(archBuilding.getRouteCode());
				buildingExt.setRouteCodeObj(routecode);
			}
			else {
				reportMsg += "RouteCode(" + buildingExt.getRouteCode() + "), ";
				buildingExt.setRouteCode("");
			}
		}
		if (!(archibusUsaCountryCode.equalsIgnoreCase(archBuilding.getBuildingAddressCountryCode()) && kfsUsCountryCode.equalsIgnoreCase(building.getBuildingAddressCountryCode()))) {
			reportMsg += "BuildingAddressCountryCode(" + building.getBuildingAddressCountryCode() + "), ";
			if (archibusUsaCountryCode.equalsIgnoreCase(archBuilding.getBuildingAddressCountryCode())) {
				building.setBuildingAddressCountryCode(kfsUsCountryCode);
			} 
			else {
				building.setBuildingAddressCountryCode(archBuilding.getBuildingAddressCountryCode());
			}
		}

		LOG.info("Updating (Name,Code,Campus,City,State,Zip,Country,Active,Address, RouteCode)(" + building.getBuildingName() + "," + building.getBuildingCode() + "," + building.getCampusCode() + "," + building.getBuildingAddressCityName() + "," + building.getBuildingAddressStateCode() + "," + building.getBuildingAddressZipCode() + "," + building.getBuildingAddressCountryCode() + "," + building.isActive() + "," + building.getBuildingStreetAddress() + buildingExt.getRouteCode() + ")");
		buildingExt.setCampusCode(campusCode);
		buildingExt.setBuildingCode(building.getBuildingCode());
		businessObjectService.save(building);
		businessObjectService.save(buildingExt);

		// Because Archibus country code is USA and KFS US, this does not get
		// updated. So if a building's only difference is that Country code is
		// US and not USA, then this does not get added to the report. Confirmed by BA
		if (!reportMsg.matches("(?s).*Updated KFS building and overwrote these field: ")) {
			writetoReportOutputFile(outReportWriter, reportMsg);
		}
	}

	private void disableKFSBuildingsNotInArchibus(PrintWriter outReportWriter, Building kfsBuilding) {
		ArchibusBuildings archibusBuilding;
		String reportMsg;

		reportMsg = "";
		LOG.info("Processing Record: " + kfsBuilding.toString());

		// look for a valid active archibus building. There is no campusCode in
		// Archibus
		Map<String, String> keys = new HashMap<String, String>();
		keys.put("buildingCode", kfsBuilding.getBuildingCode());
		keys.put("active", KFSParameterKeyConstants.STRING_A);
		archibusBuilding = (ArchibusBuildings) businessObjectService.findByPrimaryKey(ArchibusBuildings.class, keys);

		if (ObjectUtils.isNull(archibusBuilding)) {
			if (kfsBuilding.isActive()) {
				LOG.info("inactivating building in kfs that does not exist in Archibus");
				kfsBuilding.setActive(false);
				businessObjectService.save(kfsBuilding);
				writetoReportOutputFile(outReportWriter, reportMsg, kfsBuilding.getCampusCode(), kfsBuilding.getBuildingCode(), "inactivating building, and associated rooms, in kfs. Building not in Archibus.");
			}
			// Disable all rooms for this building that is now inactive
			Map<String, String> fieldValues = new HashMap<String, String>();
			fieldValues.put("buildingCode", kfsBuilding.getBuildingCode());
			Collection<Room> rooms = (Collection<Room>) businessObjectService.findMatching(Room.class, fieldValues);
			for (Room room : rooms) {
				room.setActive(false);
				businessObjectService.save(room);
			}
		}
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

	private PrintWriter setupErrorReportOutputFiles(PrintWriter outErrorReportWriter) {
		try {
			String outputErrorFile = reportDirectoryName + File.separator + "buildingImportErrorReport_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()) + ".txt";
			outErrorReportWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputErrorFile)));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		LOG.debug("Exit setupErrorReportOutputFile() " + System.currentTimeMillis());
		return outErrorReportWriter;
	}

	private PrintWriter setupReportOutputFiles(PrintWriter outReportWriter) {
		try {
			String outputFile = reportDirectoryName + File.separator + "buildingImportSuccessReport_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()) + ".txt";
			outReportWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		LOG.debug("Exit setupReportOutputFile() " + System.currentTimeMillis());
		return outReportWriter;
	}

	private void writetoReportOutputFilesHeader(PrintWriter outReportWriter, PrintWriter outErrorReportWriter) {
		String title = "             BUILDING IMPORT REPORT";
		String header = "CampusCode     BuildingCode   Info:";
		outReportWriter.format("%s%n", title, "%s%n");
		outErrorReportWriter.format("%s%n", title, "%s%n");
		outReportWriter.format("%s%n", header);
		outErrorReportWriter.format("%s%n", header);
	}

	private void writetoErrorReportOutputFile(PrintWriter outReportErrorWriter, String reportMsg, String campusCode, String buildingCode, String msg) {
		reportMsg += StringUtils.rightPad(campusCode, 15, "");
		reportMsg += StringUtils.rightPad(buildingCode, 15, "");
		reportMsg += msg;
		outReportErrorWriter.format("%s%n", reportMsg);
	}

	private void writetoReportOutputFile(PrintWriter outReportWriter, String reportMsg, String campusCode, String buildingCode, String msg) {
		reportMsg += StringUtils.rightPad(campusCode, 15, "");
		reportMsg += StringUtils.rightPad(buildingCode, 15, "");
		reportMsg += msg;
		outReportWriter.format("%s%n", reportMsg);
	}

	private void writetoReportOutputFile(PrintWriter outReportWriter, String reportMsg) {
		outReportWriter.format("%s%n", reportMsg);
	}

	private void writetoErrorReportOutputFile(PrintWriter outReportErrorWriter, String reportMsg) {
		outReportErrorWriter.format("%s%n", reportMsg);
	}

	public String getBatchFileDirectoryName() {
		return batchFileDirectoryName;
	}

	public void setBatchFileDirectoryName(String batchFileDirectoryName) {
		this.batchFileDirectoryName = batchFileDirectoryName;
	}

	public String getReportDirectoryName() {
		return reportDirectoryName;
	}

	public void setReportDirectoryName(String reportDirectoryName) {
		this.reportDirectoryName = reportDirectoryName;
	}

	public DateTimeService getDateTimeService() {
		return dateTimeService;
	}

	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	public void setKfsUsCountryCode(String kfsUsCountryCode) {
		this.kfsUsCountryCode = kfsUsCountryCode;
	}

	public void setArchibusUsaCountryCode(String archibusUsaCountryCode) {
		this.archibusUsaCountryCode = archibusUsaCountryCode;
	}

	public void setDdService(DataDictionaryService ddService) {
		this.ddService = ddService;
	}

	public void setBuildingAndRoomImportDao(BuildingAndRoomImportsDao buildingAndRoomImportDao) {
		this.buildingAndRoomImportDao = buildingAndRoomImportDao;
	}

	public BusinessObjectService getBusinessObjectService() {
		return businessObjectService;
	}

	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

}
