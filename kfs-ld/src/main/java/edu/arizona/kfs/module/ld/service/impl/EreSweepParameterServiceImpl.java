package edu.arizona.kfs.module.ld.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

import edu.arizona.kfs.module.ld.LaborConstants;
import edu.arizona.kfs.module.ld.LaborParameterKeyConstants;
import edu.arizona.kfs.module.ld.batch.EreSweepStep;
import edu.arizona.kfs.module.ld.service.EreSweepParameterService;

public class EreSweepParameterServiceImpl implements EreSweepParameterService {
	
	private ParameterService parameterService;

	@Override
	public String[] getSubFundGroupParameters() {
		return parameterService.getParameterValueAsString(EreSweepStep.class, LaborParameterKeyConstants.FRINGE_SUB_FUND_GROUPS).split(KFSConstants.COMMA);
	}

	@Override
	public String[] getObjectSubTypesParameters() {
		return parameterService.getParameterValueAsString(EreSweepStep.class, LaborParameterKeyConstants.FRINGE_OBJECT_SUB_TYPES).split(KFSConstants.COMMA);
	}

	@Override
	public String getFringeBudgetSweepIndParameters() {
		return parameterService.getParameterValueAsString(EreSweepStep.class, LaborParameterKeyConstants.FRINGE_BUDGET_SWEEP_IND);
	}

	@Override
	public List<String> getFiscalPeriodsToExclude() {
		return (List<String>) parameterService.getParameterValuesAsString(EreSweepStep.class, LaborParameterKeyConstants.FISCAL_PERIODS_TO_EXCLUDE);
	}

	@Override
	public List<String> getBalanceTypesParameters() {
		List<String> balanceTypes = Arrays.asList(LaborConstants.BALANCE_TYPES);
		return balanceTypes;
	}
	

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

}
