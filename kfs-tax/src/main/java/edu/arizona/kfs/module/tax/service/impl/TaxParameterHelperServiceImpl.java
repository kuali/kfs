package edu.arizona.kfs.module.tax.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.kuali.kfs.vnd.businessobject.OwnershipCategory;
import org.kuali.kfs.vnd.businessobject.OwnershipType;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.module.tax.TaxConstants;
import edu.arizona.kfs.module.tax.TaxParameterConstants;
import edu.arizona.kfs.module.tax.TaxPropertyConstants;
import edu.arizona.kfs.module.tax.businessobject.Payer;
import edu.arizona.kfs.module.tax.service.TaxParameterHelperService;
import edu.arizona.kfs.sys.KFSConstants;

public class TaxParameterHelperServiceImpl implements TaxParameterHelperService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaxParameterHelperServiceImpl.class);

    private ParameterService parameterService;
    private ParameterEvaluatorService parameterEvaluatorService;
    private BusinessObjectService businessObjectService;

    // Spring Injectors

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
        this.parameterEvaluatorService = parameterEvaluatorService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    // Public Service Methods

    @Override
    public Map<String, String> getOverridePaymentTypeCodeMap() {
        Map<String, String> retval = new HashMap<String, String>();
        String codeList = parameterService.getParameterValueAsString(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxParameterConstants.PARAMETER_1099_EXTRACT_OVERRIDE_PMT_TYPE_CODE);

        if (StringUtils.isNotBlank(codeList)) {
            StringTokenizer parser = new StringTokenizer(codeList, KFSConstants.MULTI_VALUE_SEPERATION_CHARACTER, false);

            while (parser.hasMoreTokens()) {
                String code = parser.nextToken();
                int equalPos = code.indexOf(KFSConstants.EQUALS);
                int pipePos = code.indexOf(KFSConstants.PIPE);

                if ((equalPos == -1) || (pipePos == -1) || (equalPos < pipePos)) {
                    throw new IllegalArgumentException("The " + TaxParameterConstants.PARAMETER_1099_EXTRACT_OVERRIDE_PMT_TYPE_CODE + " parameter, " + codeList + ", is not a valid format XX|YY=Z.");
                }

                retval.put(code.substring(0, pipePos), code.substring(equalPos + 1, code.length()));
            }
        }
        return retval;
    }

    @Override
    public Set<String> getVendorOwnershipCodes() {
        Set<String> retval = new HashSet<String>();
        String param = parameterService.getParameterValueAsString(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxParameterConstants.PARAMETER_1099_VENDOR_OWNER_CODES);
        boolean allow = isVendorOwnershipCodesAllow();

        if (StringUtils.isBlank(param)) {
            throw new IllegalArgumentException("The " + TaxParameterConstants.PARAMETER_1099_VENDOR_OWNER_CODES + " parameter was not specified or did not specify any vendor ownership codes..");
        }

        // parse the list of comma separated vendor ownership codes
        StringTokenizer st = new StringTokenizer(param.trim(), ";");

        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();

            if (StringUtils.isBlank(token)) {
                throw new IllegalArgumentException("The " + TaxParameterConstants.PARAMETER_1099_VENDOR_OWNER_CODES + " parameter, " + token + ", contained an empty vendor ownership code.");
            }

            if (!token.matches("\\w\\w") && !token.matches("\\w\\w=\\w\\w")) {
                throw new IllegalArgumentException("The " + TaxParameterConstants.PARAMETER_1099_VENDOR_OWNER_CODES + " parameter must be specified as XX or XX=YY.");
            }

            retval.add(token);
        }

        if (retval.isEmpty()) {
            throw new IllegalArgumentException("The " + TaxParameterConstants.PARAMETER_1099_VENDOR_OWNER_CODES + " parameter, " + param + ", could not parse any vendor ownership codes.");
        }

        if (!allow) {
            // Allow all except those in vendorOwnershipCodes
            Set<String> allowedOwnershipCodes = new HashSet<String>();

            Collection<OwnershipType> ownershipTypes = businessObjectService.findAll(OwnershipType.class);
            Collection<OwnershipCategory> ownershipCats = businessObjectService.findAll(OwnershipCategory.class);

            String tempCombo = null;

            for (OwnershipType type : ownershipTypes) {
                if (type.isActive()) {
                    if (!retval.contains(type.getVendorOwnershipCode())) {
                        // Add vendor ownership code
                        allowedOwnershipCodes.add(type.getVendorOwnershipCode());

                        for (OwnershipCategory cat : ownershipCats) {
                            tempCombo = type.getVendorOwnershipCode() + KFSConstants.EQUALS + cat.getVendorOwnershipCategoryCode();

                            // Add combo
                            if (cat.isActive() && !retval.contains(tempCombo)) {
                                allowedOwnershipCodes.add(tempCombo);
                            }
                        }
                    }
                }
            }

            retval = allowedOwnershipCodes;
        }

        return retval;
    }

    @Override
    public boolean isVendorOwnershipCodesAllow() {
        boolean retval = parameterEvaluatorService.getParameterEvaluator(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxParameterConstants.PARAMETER_1099_VENDOR_OWNER_CODES).constraintIsAllow();
        return retval;
    }

    @Override
    public Set<String> getAddressTypeCodes() {
        Set<String> retval = new HashSet<String>();
        String param = parameterService.getParameterValueAsString(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxParameterConstants.PARAMETER_1099_PAYEE_ADDR_TYPE_CODES);

        if (StringUtils.isBlank(param)) {
            throw new RuntimeException("The " + TaxParameterConstants.PARAMETER_1099_PAYEE_ADDR_TYPE_CODES + " parameter was not specified or did not specify any vendor ownership codes.");
        }

        StringTokenizer st = new StringTokenizer(param.trim(), KFSConstants.MULTI_VALUE_SEPERATION_CHARACTER);

        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();

            if (token.length() != 2) {
                throw new RuntimeException("The " + TaxParameterConstants.PARAMETER_1099_PAYEE_ADDR_TYPE_CODES + " parameter, " + param + ", contained an illegal address type code, " + token + ".");
            }

            retval.add(null);
        }

        if (retval.isEmpty()) {
            throw new RuntimeException("The " + TaxParameterConstants.PARAMETER_1099_PAYEE_ADDR_TYPE_CODES + " parameter, " + param + ", could not parse any vendor ownership codes.");
        }

        return retval;
    }

    @Override
    public int getTaxYear() {
        int retval = -1;
        String param = parameterService.getParameterValueAsString(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxParameterConstants.PARAMETER_1099_REPORTING_PERIOD);

        if (StringUtils.isBlank(param)) {
            throw new RuntimeException("The " + TaxParameterConstants.PARAMETER_1099_REPORTING_PERIOD + " parameter was not specified.");
        }

        param = param.trim();

        if (param.length() != 4 || !StringUtils.isNumeric(param)) {
            throw new RuntimeException("The " + TaxParameterConstants.PARAMETER_1099_REPORTING_PERIOD + " parameter must be specified as a four digit year.");
        }

        try {
            retval = Integer.parseInt(param);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("The " + TaxParameterConstants.PARAMETER_1099_REPORTING_PERIOD + " parameter, " + param + ", could not be parsed as a four digit year.");
        }

        return retval;
    }

    @Override
    public double getIncomeThreshold() {
        double retval = -1;

        String param = parameterService.getParameterValueAsString(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxParameterConstants.PARAMETER_1099_TOTAL_TAX_AMOUNT);

        if (StringUtils.isBlank(param)) {
            throw new RuntimeException("The " + TaxParameterConstants.PARAMETER_1099_TOTAL_TAX_AMOUNT + " parameter was not specified.");
        }

        try {
            retval = Double.parseDouble(param.trim());
        } catch (NumberFormatException ex) {
            throw new RuntimeException("The " + TaxParameterConstants.PARAMETER_1099_TOTAL_TAX_AMOUNT + " parameter, " + param + ", could not be parsed as a numerical amount.");
        }

        return retval;
    }

    @Override
    public Timestamp getPaymentStartDate() {
        Timestamp retval = getPaymentDate(TaxParameterConstants.PARAMETER_1099_PAYMENT_PERIOD_START);
        return retval;
    }

    @Override
    public Timestamp getPaymentEndDate() {
        Timestamp retval = getPaymentDate(TaxParameterConstants.PARAMETER_1099_PAYMENT_PERIOD_END);
        return retval;
    }

    private Timestamp getPaymentDate(String parameter) {
        Date date = null;
        String parameterValue = parameterService.getParameterValueAsString(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, parameter);

        if (StringUtils.isBlank(parameterValue)) {
            throw new IllegalArgumentException("The " + parameter + " parameter was not specified.");
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(parameterValue.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException("The " + parameter + " parameter, " + parameterValue + ", could not be parsed as a date (yyyy-MM-dd).");
        }

        return new Timestamp(date.getTime());
    }

    @Override
    public double getTaxThreshholdAmount() {
        String amt = parameterService.getParameterValueAsString(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxParameterConstants.PARAMETER_1099_TOTAL_TAX_AMOUNT);

        if (StringUtils.isBlank(amt) || !NumberUtils.isNumber(amt)) {
            throw new IllegalArgumentException("The " + TaxParameterConstants.PARAMETER_1099_TOTAL_TAX_AMOUNT + " parameter was not specified or is invalid.");
        }

        return Double.parseDouble(amt);
    }

    @Override
    public Map<String, KualiDecimal> getTaxAmountByPaymentType() {
        Map<String, KualiDecimal> taxAmountByPaymentType = new HashMap<String, KualiDecimal>();
        String[] keyValues = parameterService.getParameterValueAsString(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxParameterConstants.PARAMETER_1099_TAX_AMOUNT_BY_PAYMENT_TYPE).split(";");
        try {
            for (String entry : keyValues) {
                String[] token = entry.split(KFSConstants.EQUALS);
                String paymentType = token[0];
                String taxAmount = token[1];
                taxAmountByPaymentType.put(paymentType, new KualiDecimal(taxAmount));
            }
        }

        catch (Exception e) {
            LOG.error("There was an error parsing the " + TaxParameterConstants.PARAMETER_1099_TAX_AMOUNT_BY_PAYMENT_TYPE + " parameter. The value returned will be an empty Map.");
        }

        return taxAmountByPaymentType;
    }

    @Override
    public boolean getReplaceData() {
        boolean retval = parameterService.getParameterValueAsBoolean(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxParameterConstants.PARAMETER_1099_REP_DATA_LOAD_IND);
        return retval;
    }

    @Override
    public Payer getDefaultPayer() {
        Payer retval = null;

        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(TaxPropertyConstants.PayerFields.TRANS_CD, parameterService.getParameterValueAsString(TaxConstants.TAX_NAMESPACE_CODE, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxParameterConstants.PARAMETER_1099_PAYER_TRANSCD));

        List<Payer> payers = (List<Payer>) businessObjectService.findMatching(Payer.class, fieldValues);

        if ((payers != null) && !payers.isEmpty()) {
            retval = payers.get(0);
        }

        return retval;
    }

}
