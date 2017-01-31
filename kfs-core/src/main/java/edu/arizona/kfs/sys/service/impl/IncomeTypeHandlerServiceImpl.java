package edu.arizona.kfs.sys.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.businessobject.IncomeType;
import edu.arizona.kfs.sys.dataaccess.IncomeTypeHandlerDao;
import edu.arizona.kfs.sys.service.IncomeTypeHandlerService;

public class IncomeTypeHandlerServiceImpl implements IncomeTypeHandlerService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IncomeTypeHandlerService.class);

    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private IncomeTypeHandlerDao incomeTypeHandlerDao;

    // Spring Injectors

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setIncomeTypeHandlerDao(IncomeTypeHandlerDao incomeTypeHandlerDao) {
        this.incomeTypeHandlerDao = incomeTypeHandlerDao;
    }

    // Public Service Methods
    @Override
    public List<String> getExtractCodes() {
        List<String> overridingObjCodes = getOverridingObjectCodes();
        List<String> objectCodes = getObjectCodes();
        objectCodes.removeAll(overridingObjCodes);
        return objectCodes;
    }

    @Override
    public String getOverridePaymentType(VendorHeader vendor) {
        Map<String, String> pmtTypeCodes = getOverridePaymentTypeCodes();
        String retval = pmtTypeCodes.get(vendor.getVendorOwnershipCode() + KFSConstants.PIPE + vendor.getVendorOwnershipCategoryCode());
        return retval;
    }

    @Override
    public Map<String, String> getObjectCodeMap() {
        Map<String, String> retval = new HashMap<String, String>();
        String objectCodes = parameterService.getParameterValueAsString(KFSConstants.IncomeTypeConstants.TAX_NAMESPACE_CODE, KFSConstants.IncomeTypeConstants.PAYEE_MASTER_EXTRACT_STEP, KFSConstants.IncomeTypeConstants.PARAMETER_1099_OBJECT_CODES);
        if (StringUtils.isBlank(objectCodes)) {
            throw new RuntimeException("The " + KFSConstants.IncomeTypeConstants.PARAMETER_1099_OBJECT_CODES + " parameter has no value.");
        }
        List<String> objectCodeList = convertParamaterStringToList(objectCodes);

        for (String objectCode : objectCodeList) {
            int equalPosition = objectCode.indexOf('=');
            if (equalPosition == -1) {
                throw new RuntimeException("The " + KFSConstants.IncomeTypeConstants.PARAMETER_1099_OBJECT_CODES + " parameter contains an invalid object code or type: " + objectCode);
            }
            String object = objectCode.substring(0, equalPosition);
            String amountCode = objectCode.substring(equalPosition + 1, objectCode.length());
            if (KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.VALID_AMOUNT_CODES.indexOf(amountCode) == -1) {
                throw new RuntimeException("The " + KFSConstants.IncomeTypeConstants.PARAMETER_1099_OBJECT_CODES + " parameter contains an invalid amount code: " + objectCode);
            }
            retval.put(object, amountCode);
        }
        return retval;
    }

    @Override
    public Map<String, IncomeType> getIncomeTypeMap() {
        Map<String, IncomeType> retval = new HashMap<String, IncomeType>();
        List<IncomeType> incomeTypes = (List<IncomeType>) businessObjectService.findAll(IncomeType.class);
        if (incomeTypes != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("found " + incomeTypes.size() + " income types");
            }
            // create IncomeType map by 1099 box
            for (IncomeType incomeType : incomeTypes) {
                if (StringUtils.isNotBlank(incomeType.getIncomeTypeBox())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("IncomeTypeCode: " + incomeType.getIncomeTypeCode() + ", box: " + incomeType.getIncomeTypeBox());
                    }
                    retval.put(incomeType.getIncomeTypeBox(), incomeType);
                }
            }
        }
        return retval;
    }

    private List<String> getOverridingObjectCodes() {
        String codeList = parameterService.getParameterValueAsString(KFSConstants.IncomeTypeConstants.TAX_NAMESPACE_CODE, KFSConstants.IncomeTypeConstants.PAYEE_MASTER_EXTRACT_STEP, KFSConstants.IncomeTypeConstants.PARAMETER_1099_OBJECT_CODES_OVERRIDING_RESTRICTIONS);
        List<String> retval = convertParamaterStringToList(codeList);
        return retval;
    }

    private List<String> convertParamaterStringToList(String stringList) {
        List<String> retval = new ArrayList<String>();
        if (StringUtils.isNotBlank(stringList)) {
            StringTokenizer parser = new StringTokenizer(stringList, KFSConstants.MULTI_VALUE_SEPERATION_CHARACTER, false);
            while (parser.hasMoreTokens()) {
                String temp = parser.nextToken();
                retval.add(temp);
            }
        }
        if (retval.isEmpty()) {
            LOG.warn("There are no strings in the list.");
        }
        return retval;
    }

    private List<String> getObjectCodes() {
        String levels = parameterService.getParameterValueAsString(KFSConstants.IncomeTypeConstants.TAX_NAMESPACE_CODE, KFSConstants.IncomeTypeConstants.PAYEE_MASTER_EXTRACT_STEP, KFSConstants.IncomeTypeConstants.PARAMETER_1099_EXTRACT_OBJECT_LEVELS);
        if (StringUtils.isBlank(levels)) {
            throw new IllegalArgumentException("The " + KFSConstants.IncomeTypeConstants.PARAMETER_1099_EXTRACT_OBJECT_LEVELS + " parameter has no value.");
        }
        List<String> levelList = convertParamaterStringToList(levels);
        List<String> objectCodes = incomeTypeHandlerDao.getObjectCodesByObjectLevelCodes(levelList);
        return objectCodes;
    }

    private Map<String, String> getOverridePaymentTypeCodes() {
        Map<String, String> retval = new HashMap<String, String>();
        String codeList = parameterService.getParameterValueAsString(KFSConstants.IncomeTypeConstants.TAX_NAMESPACE_CODE, KFSConstants.IncomeTypeConstants.PAYEE_MASTER_EXTRACT_STEP, KFSConstants.IncomeTypeConstants.PARAMETER_1099_EXTRACT_OVERRIDE_PAYMENT_TYPE_CODE);
        List<String> codes = convertParamaterStringToList(codeList);
        for (String code : codes) {
            int equalPos = code.indexOf(KFSConstants.EQUALS);
            int pipePos = code.indexOf(KFSConstants.PIPE);
            if ((equalPos == -1) || (pipePos == -1) || (equalPos < pipePos)) {
                throw new IllegalArgumentException("The " + KFSConstants.IncomeTypeConstants.PARAMETER_1099_EXTRACT_OVERRIDE_PAYMENT_TYPE_CODE + " parameter value " + code + ", is not a valid format XX|YY=Z.");
            }
            retval.put(code.substring(0, pipePos), code.substring(equalPos + 1, code.length()));
        }
        return retval;
    }
}
