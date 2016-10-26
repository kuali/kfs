package edu.arizona.kfs.sys.dataaccess.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import edu.arizona.kfs.sys.batch.BuildingImportStep;
import edu.arizona.kfs.sys.dataaccess.BuildingAndRoomImportsDao;

public class BuildingAndRoomImportsDaoOjb implements BuildingAndRoomImportsDao {

	private ParameterService parameterService;

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	// Returns a list of routeCodes with their respective campusCode
	public Map<String, String> PopulateRoutecodeToCampusCodeMap() {
		Map<String, String> routecodeToCampuscodeMap = new HashMap<String, String>();
		String[] keyPairs = parameterService.getParameterValueAsString(BuildingImportStep.class, "ROUTE_CODE_BY_CAMPUS_CODE").split(";");

		for (String keyValues : keyPairs) {
			String[] tokens = keyValues.split("=");
			String campusCode = tokens[0];
			String values = tokens[1];
			String[] valueTokens = values.split(",");

			for (String routeCode : valueTokens) {
				routecodeToCampuscodeMap.put(routeCode, campusCode);
			}
		}
		return routecodeToCampuscodeMap;
	}

}
