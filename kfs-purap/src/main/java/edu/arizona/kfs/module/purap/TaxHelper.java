package edu.arizona.kfs.module.purap;

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
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.OwnershipCategory;
import org.kuali.kfs.vnd.businessobject.OwnershipType;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;
import edu.arizona.kfs.module.purap.service.TaxReporting1099Service;
import edu.arizona.kfs.tax.TaxConstants;
import edu.arizona.kfs.tax.TaxPropertyConstants;

public class TaxHelper {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaxHelper.class);

	public static Map<String, String> getOverridePaymentTypeCodeMap(ParameterService parameterService) {
		Map<String, String> retval = new HashMap<String, String>();
		String codeList = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PAYMENT_TYPE_OVERRIDE_CODES);

		if (StringUtils.isNotBlank(codeList)) {
			StringTokenizer parser = new StringTokenizer(codeList, ";", false);

			while (parser.hasMoreTokens()) {
				String code = parser.nextToken();
				int equalPos = code.indexOf('=');
				int pipePos = code.indexOf('|');

				if ((equalPos == -1) || (pipePos == -1) || (equalPos < pipePos)) {
					throw new IllegalArgumentException("The " + TaxConstants.Form1099.PAYMENT_TYPE_OVERRIDE_CODES + " property, " + codeList + ", is not a valid format XX|YY=Z.");
				}

				retval.put(code.substring(0, pipePos),
						code.substring(equalPos + 1, code.length()));
			}
		}
		return retval;
	}

	public static Map<String, String> getObjectCodeMap(ParameterService parameterService) {
		Map<String, String> retval = new HashMap<String, String>();
		String codeList = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_OBJECT_CODES);

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
								} 
								else {
									throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_OBJECT_CODES + " property, " + codeList + ", contains an invalid object code or type.");
								}
							}
						} 
						else {
							throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_OBJECT_CODES + " property, " + codeList + ", contains an invalid object code or type.");
						}
					}
				} 
				else if (equalPos != -1) {
					temp = code.substring(equalPos + 1, code.length());

					if (TaxConstants.VALID_AMOUNT_CODES.indexOf(temp) != -1) {
						retval.put(code.substring(0, equalPos), temp);
					} 
					else {
						throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_OBJECT_CODES + " property, " + codeList + ", contains an invlaid object code.");
					}
				}
			}
		}

		return retval;
	}

	public static Set<String> getOverridingObjectCodes(ParameterService parameterService) {
		Set<String> retval = new HashSet<String>();
		String codeList = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_OBJECT_CODES_OVERRIDING_RESTRICTIONS);

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
					} 
					else {
						throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_OBJECT_CODES_OVERRIDING_RESTRICTIONS + " property, " + codeList + ", contains an invalid object code range.");
					}
				} 
				else {
					retval.add(temp);
				}
			}
		}

		return retval;
	}

	public static String getExtractType(ParameterService parameterService) {
		String type = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_EXTRACT_TYPE);
		type = type.trim();

		if (StringUtils.isBlank(type)) {
			throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_EXTRACT_TYPE + " property was not specified.");
		} 
		else if (!(type.equals(TaxConstants.Form1099.LEVEL) || type.equals(TaxConstants.Form1099.CONS))) {
			throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_EXTRACT_TYPE + " property, " + type + ", must be LEVEL or CONS.");
		}

		return type;
	}

	public static Set<String> getExtractCodes(ParameterService parameterService, TaxReporting1099Service taxReporting1099Service, String extractType, Set<String> overridingObjCodes) {
		Set<String> retval = new HashSet<String>();

		if (TaxConstants.Form1099.OBJECT.equals(extractType)) {
			String codeList = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_EXTRACT_OBJECT_CODES);

			if (StringUtils.isBlank(codeList)) {
				throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_EXTRACT_OBJECT_CODES + " property was not specified.");
			} 
			else {
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
						} 
						else {
							throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_OBJECT_CODES + " property, " + codeList + ", contains an invalid object code range.");
						}
					} 
					else {
						retval.add(temp);
					}
				}
			}
		} 
		else if (TaxConstants.Form1099.CONS.equals(extractType)) {
			String consList = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_EXTRACT_CONS_CODES);

			if (StringUtils.isBlank(consList)) {
				throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_EXTRACT_CONS_CODES + " property, " + consList + ", did not specify any consolidation codes.");
			} 
			else {
				StringTokenizer parser = new StringTokenizer(consList, ";", false);
				List<String> cons = new ArrayList<String>();

				while (parser.hasMoreTokens()) {
					cons.add(parser.nextToken());
				}

				// Get object codes for cons codes
				retval.addAll(taxReporting1099Service.getObjectCodes(cons, TaxConstants.Form1099.CONS));
			}
		} 
		else {
			String levelList = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_EXTRACT_OBJECT_LEVELS);

			if (StringUtils.isBlank(levelList)) {
				throw new IllegalArgumentException("The " + TaxConstants.Form1099.PROPERTY_EXTRACT_OBJECT_LEVELS + " property was not specified.");
			} 
			else {
				StringTokenizer parser = new StringTokenizer(levelList, ";", false);
				List<String> levels = new ArrayList<String>();

				while (parser.hasMoreTokens()) {
					levels.add(parser.nextToken());
				}

				// Get object codes from levels
				retval.addAll(taxReporting1099Service.getObjectCodes(levels, TaxConstants.Form1099.LEVEL));
			}
		}

		// Remove overriding object codes
		retval.removeAll(overridingObjCodes);

		return retval;
	}

	public static String getOverridePaymentType(VendorHeader vendor, Map<String, String> pmtTypeCodes) {
		return pmtTypeCodes.get(vendor.getVendorOwnershipCode() + "|" + vendor.getVendorOwnershipCategoryCode());
	}

	public static Set<String> getVendorOwnershipCodes(ParameterService parameterService, BusinessObjectService businessObjectService) throws Exception {
		Set<String> retval = new HashSet<String>();
		String param = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_VENDOR_OWNERSHIP_CODES);
		boolean allow = SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_VENDOR_OWNERSHIP_CODES).constraintIsAllow();

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

			Collection<OwnershipType> ownershipTypes = businessObjectService.findAll(OwnershipType.class);
			Collection<OwnershipCategory> ownershipCats = businessObjectService.findAll(OwnershipCategory.class);

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

	public static boolean isVendorOwnershipCodesAllow(ParameterService parameterService) throws Exception {
		return SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_VENDOR_OWNERSHIP_CODES).constraintIsAllow();
	}

	public static Set<String> getAddressTypeCodes(ParameterService parameterService) {
		Set<String> retval = new HashSet<String>();
		String param = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_ADDR_TYPE_CD);

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

	public static int getTaxYear(ParameterService parameterService) {
		int retval = -1;
		String param = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_TAX_YEAR);

		if (StringUtils.isBlank(param)) {
			throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_TAX_YEAR + " property was not specified.");
		}

		param = param.trim();

		if (param.length() != 4 || !StringUtils.isNumeric(param)) {
			throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_TAX_YEAR + " property must be specified as a four digit year.");
		}

		try {
			retval = Integer.parseInt(param);
		} 
		catch (NumberFormatException ex) {
			throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_TAX_YEAR + " property, " + param + ", could not be parsed as a four digit year.");
		}

		return retval;
	}

	public static double getIncomeThreshold(ParameterService parameterService) {
		double retval = -1;

		String param = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_INCOME_THRESHOLD);

		if (StringUtils.isBlank(param)) {
			throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_INCOME_THRESHOLD + " property was not specified.");
		}

		try {
			retval = Double.parseDouble(param.trim());
		} 
		catch (NumberFormatException ex) {
			throw new RuntimeException("The " + TaxConstants.Form1099.PROPERTY_INCOME_THRESHOLD + " property, " + param + ", could not be parsed as a numerical amount.");
		}

		return retval;
	}

	public static Timestamp getPaymentDate(ParameterService parameterService, String key) throws Exception {
		Date date = null;
		String param = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, key);

		if (StringUtils.isBlank(param)) {
			throw new IllegalArgumentException("The " + key + " property was not specified.");
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.parse(param.trim());
		} 
		catch (Exception ex) {
			throw new IllegalArgumentException("The " + key + " property, " + param + ", could not be parsed as a date (yyyy-MM-dd).");
		}

		return new Timestamp(date.getTime());
	}

	public static double getTaxThreshholdAmount(ParameterService parameterService) {
		String amt = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_INCOME_THRESHOLD);

		if (StringUtils.isBlank(amt) || !NumberUtils.isNumber(amt)) {
			throw new IllegalArgumentException("The " + TaxConstants.Form1099.PROPERTY_INCOME_THRESHOLD + " property was not specified or is invalid.");
		}

		return Double.parseDouble(amt);
	}

	public static Map<String, KualiDecimal> getTaxAmountByPaymentType(ParameterService parameterService) {
		Map<String, KualiDecimal> taxAmountByPaymentType = new HashMap<String, KualiDecimal>();
		String[] keyValues = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_TAX_AMOUNT_BY_PAYMENT_TYPE).split(";");
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

	public static boolean isPreq(String documentType) {
		return (PurapConstants.PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(documentType) || PaymentRequestDocument.DOCUMENT_TYPE_NON_CHECK.equals(documentType));
	}

	public static boolean isCm(String documentType) {
		return PurapConstants.PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(documentType);
	}

	public static boolean isDv(String documentType) {
		return (DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH.equals(documentType) || TaxPropertyConstants.DV_DOCUMENT_TYPE_CODE.equals(documentType) || DisbursementVoucherDocument.DOCUMENT_TYPE_DV_NON_CHECK.equals(documentType));
	}

	public static boolean isPreqClass(Class clazz) {
		return PaymentRequestDocument.class.equals(clazz);
	}

	public static boolean isCmClass(Class clazz) {
		return VendorCreditMemoDocument.class.equals(clazz);
	}

	public static boolean isDvClass(Class clazz) {
		return DisbursementVoucherDocument.class.equals(clazz);
	}

	public static Class getClassForDocumentType(String documentType) {
		Class retval = null;

		if (StringUtils.isNotBlank(documentType)) {
			if (isPreq(documentType)) {
				retval = PaymentRequestDocument.class;
			} 
			else if (isCm(documentType)) {
				retval = VendorCreditMemoDocument.class;
			} 
			else if (isDv(documentType)) {
				retval = DisbursementVoucherDocument.class;
			}
		}

		return retval;
	}

	public static boolean isValidIncomeTypeDocumentClass(Class clazz) {
		return ((clazz != null) && (isCmClass(clazz) || isPreqClass(clazz) || isDvClass(clazz)));
	}

	public static boolean isValidPurapDocumentClass(Class clazz) {
		return ((clazz != null) && (isCmClass(clazz) || isPreqClass(clazz)));
	}

}
