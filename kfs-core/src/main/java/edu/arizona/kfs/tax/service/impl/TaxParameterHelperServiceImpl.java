package edu.arizona.kfs.tax.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.OwnershipCategory;
import org.kuali.kfs.vnd.businessobject.OwnershipType;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.tax.TaxConstants;
import edu.arizona.kfs.tax.dataaccess.TaxParameterHelperDao;
import edu.arizona.kfs.tax.service.TaxParameterHelperService;

public class TaxParameterHelperServiceImpl implements TaxParameterHelperService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaxParameterHelperServiceImpl.class);

    private ParameterService parameterService;
    private ParameterEvaluatorService parameterEvaluatorService;
    private BusinessObjectService businessObjectService;
    private TaxParameterHelperDao taxParameterHelperDao;

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
        this.parameterEvaluatorService = parameterEvaluatorService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setTaxParameterHelperDao(TaxParameterHelperDao taxParameterHelperDao) {
        this.taxParameterHelperDao = taxParameterHelperDao;
    }

    protected ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

    protected ParameterEvaluatorService getParameterEvaluatorService() {
        if (parameterEvaluatorService == null) {
            parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        }
        return parameterEvaluatorService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    protected TaxParameterHelperDao getTaxParameterHelperDao() {
        if (taxParameterHelperDao == null) {
            taxParameterHelperDao = SpringContext.getBean(TaxParameterHelperDao.class);
        }
        return taxParameterHelperDao;
    }

    @Override
    public Map<String, String> getOverridePaymentTypeCodeMap() {
        Map<String, String> retval = new HashMap<String, String>();
        String codeList = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PAYMENT_TYPE_OVERRIDE_CODES);

        if (StringUtils.isNotBlank(codeList)) {
            StringTokenizer parser = new StringTokenizer(codeList, ";", false);

            while (parser.hasMoreTokens()) {
                String code = parser.nextToken();
                int equalPos = code.indexOf('=');
                int pipePos = code.indexOf('|');

                if ((equalPos == -1) || (pipePos == -1) || (equalPos < pipePos)) {
                    throw new IllegalArgumentException("The " + TaxConstants.Form1099.PAYMENT_TYPE_OVERRIDE_CODES + " property, " + codeList + ", is not a valid format XX|YY=Z.");
                }

                retval.put(code.substring(0, pipePos), code.substring(equalPos + 1, code.length()));
            }
        }
        return retval;
    }

    @Override
    public Map<String, String> getObjectCodeMap() {
        Map<String, String> retval = new HashMap<String, String>();
        String codeList = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_OBJECT_CODES);

        if (StringUtils.isNotBlank(codeList)) {
            String temp = null;
            StringTokenizer parser = new StringTokenizer(codeList, ";", false);

            while (parser.hasMoreTokens()) {
                String code = parser.nextToken();
                int equalPos = code.indexOf('=');
                int dashPos = code.indexOf('-');

                if (dashPos != -1) {
                    if (equalPos != -1) {
                        int start = Integer.parseInt(code.substring(0, dashPos));
                        int end = Integer.parseInt(code.substring(dashPos + 1, equalPos));

                        temp = code.substring(equalPos + 1, code.length());

                        if (start < end) {
                            for (int i = start; i <= end; i++) {
                                if (TaxConstants.VALID_AMOUNT_CODES.indexOf(temp) != -1) {
                                    retval.put(i + "", temp);
                                } else {
                                    throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_OBJECT_CODES + " property, " + codeList + ", contains an invalid object code or type.");
                                }
                            }
                        } else {
                            throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_OBJECT_CODES + " property, " + codeList + ", contains an invalid object code or type.");
                        }
                    }
                } else if (equalPos != -1) {
                    temp = code.substring(equalPos + 1, code.length());

                    if (TaxConstants.VALID_AMOUNT_CODES.indexOf(temp) != -1) {
                        retval.put(code.substring(0, equalPos), temp);
                    } else {
                        throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_OBJECT_CODES + " property, " + codeList + ", contains an invlaid object code.");
                    }
                }
            }
        }

        return retval;
    }

    @Override
    public Set<String> getOverridingObjectCodes() {
        Set<String> retval = new HashSet<String>();
        String codeList = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_OBJECT_CODES_OVERRIDING_RESTRICTIONS);

        if (StringUtils.isNotBlank(codeList)) {

            StringTokenizer parser = new StringTokenizer(codeList, ";", false);
            while (parser.hasMoreTokens()) {
                String temp = parser.nextToken();
                int dash = temp.indexOf('-');

                if (dash != -1) {
                    int start = Integer.parseInt(temp.substring(0, dash));
                    int end = Integer.parseInt(temp.substring(dash + 1, temp.length()));

                    if (start < end) {
                        for (int i = start; i <= end; i++) {
                            retval.add(i + "");
                        }
                    } else {
                        throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_OBJECT_CODES_OVERRIDING_RESTRICTIONS + " property, " + codeList + ", contains an invalid object code range.");
                    }
                } else {
                    retval.add(temp);
                }
            }
        }

        return retval;
    }

    @Override
    public String getExtractType() {
        String type = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_EXTRACT_TYPE);
        type = type.trim();

        if (StringUtils.isBlank(type)) {
            throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_EXTRACT_TYPE + " property was not specified.");
        } else if (!(type.equals(TaxConstants.Form1099.LEVEL) || type.equals(TaxConstants.Form1099.CONS))) {
            throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_EXTRACT_TYPE + " property, " + type + ", must be LEVEL or CONS.");
        }

        return type;
    }

    @Override
    public Set<String> getExtractCodes(String extractType, Set<String> overridingObjCodes) {
        Set<String> retval = new HashSet<String>();

        if (TaxConstants.Form1099.OBJECT.equals(extractType)) {
            String codeList = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_EXTRACT_OBJECT_CODES);

            if (StringUtils.isBlank(codeList)) {
                throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_EXTRACT_OBJECT_CODES + " property was not specified.");
            } else {
                StringTokenizer parser = new StringTokenizer(codeList, ";", false);
                while (parser.hasMoreTokens()) {
                    String temp = parser.nextToken();
                    int dash = temp.indexOf('-');

                    if (dash != -1) {
                        int start = Integer.parseInt(temp.substring(0, dash));
                        int end = Integer.parseInt(temp.substring(dash + 1, temp.length()));

                        if (start < end) {
                            for (int i = start; i <= end; i++) {
                                retval.add(i + "");
                            }
                        } else {
                            throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_OBJECT_CODES + " property, " + codeList + ", contains an invalid object code range.");
                        }
                    } else {
                        retval.add(temp);
                    }
                }
            }
        } else if (TaxConstants.Form1099.CONS.equals(extractType)) {
            String consList = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_EXTRACT_CONS_CODES);

            if (StringUtils.isBlank(consList)) {
                throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_EXTRACT_CONS_CODES + " property, " + consList + ", did not specify any consolidation codes.");
            } else {
                StringTokenizer parser = new StringTokenizer(consList, ";", false);
                List<String> cons = new ArrayList<String>();

                while (parser.hasMoreTokens()) {
                    cons.add(parser.nextToken());
                }

                // Get object codes for cons codes
                List<String> objectCodes = getTaxParameterHelperDao().getObjectCodes(TaxConstants.Form1099.CONS, cons);
                retval.addAll(objectCodes);
            }
        } else {
            String levelList = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_EXTRACT_OBJECT_LEVELS);

            if (StringUtils.isBlank(levelList)) {
                throw new IllegalArgumentException("The " + TaxConstants.Form1099.PROPERTY_EXTRACT_OBJECT_LEVELS + " property was not specified.");
            } else {
                StringTokenizer parser = new StringTokenizer(levelList, ";", false);
                List<String> levels = new ArrayList<String>();

                while (parser.hasMoreTokens()) {
                    levels.add(parser.nextToken());
                }

                // Get object codes from levels
                retval.addAll(getTaxParameterHelperDao().getObjectCodes(TaxConstants.Form1099.LEVEL, levels));
            }
        }

        // Remove overriding object codes
        retval.removeAll(overridingObjCodes);

        return retval;
    }

    @Override
    public String getOverridePaymentType(VendorHeader vendor, Map<String, String> pmtTypeCodes) {
        String retval = pmtTypeCodes.get(vendor.getVendorOwnershipCode() + "|" + vendor.getVendorOwnershipCategoryCode());
        return retval;
    }

    @Override
    public Set<String> getVendorOwnershipCodes() throws Exception {
        Set<String> retval = new HashSet<String>();
        String param = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_VENDOR_OWNERSHIP_CODES);
        boolean allow = isVendorOwnershipCodesAllow();

        if (StringUtils.isBlank(param)) {
            throw new IllegalArgumentException("The " + TaxConstants.Form1099.PROPERTY_VENDOR_OWNERSHIP_CODES + " property was not specified or did not specify any vendor ownership codes..");
        }

        // parse the list of comma separated vendor ownership codes
        StringTokenizer st = new StringTokenizer(param.trim(), ";");

        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();

            if (StringUtils.isBlank(token)) {
                throw new IllegalArgumentException("The " + TaxConstants.Form1099.PROPERTY_VENDOR_OWNERSHIP_CODES + " property, " + token + ", contained an empty vendor ownership code.");
            }

            if (!token.matches("\\w\\w") && !token.matches("\\w\\w=\\w\\w")) {
                throw new IllegalArgumentException("The " + TaxConstants.Form1099.PROPERTY_VENDOR_OWNERSHIP_CODES + " property must be specified as XX or XX=YY.");
            }

            retval.add(token);
        }

        if (retval.isEmpty()) {
            throw new IllegalArgumentException("The " + TaxConstants.Form1099.PROPERTY_VENDOR_OWNERSHIP_CODES + " property, " + param + ", could not parse any vendor ownership codes.");
        }

        if (!allow) {
            // Allow all except those in vendorOwnershipCodes
            Set<String> allowedOwnershipCodes = new HashSet<String>();

            Collection<OwnershipType> ownershipTypes = getBusinessObjectService().findAll(OwnershipType.class);
            Collection<OwnershipCategory> ownershipCats = getBusinessObjectService().findAll(OwnershipCategory.class);

            String tempCombo = null;

            for (OwnershipType type : ownershipTypes) {
                if (type.isActive()) {
                    if (!retval.contains(type.getVendorOwnershipCode())) {
                        // Add vendor ownership code
                        allowedOwnershipCodes.add(type.getVendorOwnershipCode());

                        for (OwnershipCategory cat : ownershipCats) {
                            tempCombo = type.getVendorOwnershipCode() + "=" + cat.getVendorOwnershipCategoryCode();

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
    public boolean isVendorOwnershipCodesAllow() throws Exception {
        boolean retval = getParameterEvaluatorService().getParameterEvaluator(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_VENDOR_OWNERSHIP_CODES).constraintIsAllow();
        return retval;
    }

    @Override
    public Set<String> getAddressTypeCodes() {
        Set<String> retval = new HashSet<String>();
        String param = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_ADDR_TYPE_CD);

        if (StringUtils.isBlank(param)) {
            throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_ADDR_TYPE_CD + " property was not specified or did not specify any vendor ownership codes.");
        }

        StringTokenizer st = new StringTokenizer(param.trim(), ";");

        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();

            if (token.length() != 2) {
                throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_ADDR_TYPE_CD + " property, " + param + ", contained an illegal address type code, " + token + ".");
            }

            retval.add(null);
        }

        if (retval.isEmpty()) {
            throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_ADDR_TYPE_CD + " property, " + param + ", could not parse any vendor ownership codes.");
        }

        return retval;
    }

    @Override
    public int getTaxYear() {
        int retval = -1;
        String param = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_TAX_YEAR);

        if (StringUtils.isBlank(param)) {
            throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_TAX_YEAR + " property was not specified.");
        }

        param = param.trim();

        if (param.length() != 4 || !StringUtils.isNumeric(param)) {
            throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_TAX_YEAR + " property must be specified as a four digit year.");
        }

        try {
            retval = Integer.parseInt(param);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_TAX_YEAR + " property, " + param + ", could not be parsed as a four digit year.");
        }

        return retval;
    }

    @Override
    public double getIncomeThreshold() {
        double retval = -1;

        String param = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_INCOME_THRESHOLD);

        if (StringUtils.isBlank(param)) {
            throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_INCOME_THRESHOLD + " property was not specified.");
        }

        try {
            retval = Double.parseDouble(param.trim());
        } catch (NumberFormatException ex) {
            throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_INCOME_THRESHOLD + " property, " + param + ", could not be parsed as a numerical amount.");
        }

        return retval;
    }

    @Override
    public Timestamp getPaymentStartDate() throws Exception {
        Timestamp retval = getPaymentDate(TaxConstants.Form1099.PROPERTY_PAYMENT_PERIOD_START);
        return retval;
    }

    @Override
    public Timestamp getPaymentEndDate() throws Exception {
        Timestamp retval = getPaymentDate(TaxConstants.Form1099.PROPERTY_PAYMENT_PERIOD_END);
        return retval;
    }

    private Timestamp getPaymentDate(String parameter) throws Exception {
        Date date = null;
        String parameterValue = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, parameter);

        if (StringUtils.isBlank(parameterValue)) {
            throw new IllegalArgumentException("The " + parameter + " property was not specified.");
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(parameterValue.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException("The " + parameter + " property, " + parameterValue + ", could not be parsed as a date (yyyy-MM-dd).");
        }

        return new Timestamp(date.getTime());
    }

    @Override
    public double getTaxThreshholdAmount() {
        String amt = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_INCOME_THRESHOLD);

        if (StringUtils.isBlank(amt) || !NumberUtils.isNumber(amt)) {
            throw new IllegalArgumentException("The " + TaxConstants.Form1099.PROPERTY_INCOME_THRESHOLD + " property was not specified or is invalid.");
        }

        return Double.parseDouble(amt);
    }

    @Override
    public Map<String, KualiDecimal> getTaxAmountByPaymentType() {
        Map<String, KualiDecimal> taxAmountByPaymentType = new HashMap<String, KualiDecimal>();
        String[] keyValues = getParameterService().getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_TAX_AMOUNT_BY_PAYMENT_TYPE).split(";");
        try {
            for (String entry : keyValues) {
                String[] token = entry.split("=");
                String paymentType = token[0];
                String taxAmount = token[1];
                taxAmountByPaymentType.put(paymentType, new KualiDecimal(taxAmount));
            }
        }

        catch (Exception e) {
            LOG.error("There was an error parsing the " + TaxConstants.Form1099.PROPERTY_TAX_AMOUNT_BY_PAYMENT_TYPE + " parameter. The value returned will be an empty Map.");
        }

        return taxAmountByPaymentType;
    }

}
