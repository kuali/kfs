package edu.arizona.kfs.module.cam.document;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetDepreciationConvention;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.document.service.AssetDateService;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.module.cam.CamsConstants;
import edu.arizona.kfs.module.cam.businessobject.AssetGlobalDetailExtension;

public class AssetGlobalMaintainableImpl extends org.kuali.kfs.module.cam.document.AssetGlobalMaintainableImpl {

	private static final long serialVersionUID = 1L;
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetGlobalMaintainableImpl.class);

	@Override
	public void prepareForSave() {
		super.prepareForSave();
		AssetGlobal assetGlobal = (AssetGlobal) this.getBusinessObject();

		// we need to set the posting period and posting year from the value of the drop-down box...
		if (StringUtils.isNotBlank(assetGlobal.getUniversityFiscalPeriodName())) {
			assetGlobal.setFinancialDocumentPostingPeriodCode(StringUtils.left(assetGlobal.getUniversityFiscalPeriodName(), 2));
			assetGlobal.setFinancialDocumentPostingYear(new Integer(StringUtils.right(assetGlobal.getUniversityFiscalPeriodName(), 4)));
		}

		List<AssetGlobalDetail> assetSharedDetails = assetGlobal.getAssetSharedDetails();
		List<AssetGlobalDetail> newDetails = new ArrayList<AssetGlobalDetail>();
		
		if (!assetSharedDetails.isEmpty() && !assetSharedDetails.get(0).getAssetGlobalUniqueDetails().isEmpty()) {

			for (AssetGlobalDetail locationDetail : assetSharedDetails) {
				List<AssetGlobalDetail> assetGlobalUniqueDetails = locationDetail.getAssetGlobalUniqueDetails();

				for (AssetGlobalDetail detail : assetGlobalUniqueDetails) {

					detail.setDocumentNumber(assetGlobal.getDocumentNumber());
					AssetGlobalDetailExtension assetGlobalDetailExtension = (AssetGlobalDetailExtension) detail.getExtension();
					assetGlobalDetailExtension.setDocumentNumber(assetGlobal.getDocumentNumber());
					assetGlobalDetailExtension.setCapitalAssetNumber(detail.getCapitalAssetNumber());

					// read from location and set it to detail
					if (ObjectUtils.isNotNull(locationDetail.getCampusCode())) {
						detail.setCampusCode(locationDetail.getCampusCode().toUpperCase());
					}
					else {
						detail.setCampusCode(locationDetail.getCampusCode());
					}
					if (ObjectUtils.isNotNull(locationDetail.getBuildingCode())) {
						detail.setBuildingCode(locationDetail.getBuildingCode().toUpperCase());
					}
					else {
						detail.setBuildingCode(locationDetail.getBuildingCode());
					}
					detail.setBuildingRoomNumber(locationDetail.getBuildingRoomNumber());
					detail.setBuildingSubRoomNumber(locationDetail.getBuildingSubRoomNumber());
					detail.setOffCampusName(locationDetail.getOffCampusName());
					detail.setOffCampusAddress(locationDetail.getOffCampusAddress());
					detail.setOffCampusCityName(locationDetail.getOffCampusCityName());
					detail.setOffCampusStateCode(locationDetail.getOffCampusStateCode());
					detail.setOffCampusCountryCode(locationDetail.getOffCampusCountryCode());
					detail.setOffCampusZipCode(locationDetail.getOffCampusZipCode());
					newDetails.add(detail);
				}
			}
		}

		if (assetGlobal.getCapitalAssetTypeCode() != null) {
			assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE);
			AssetType capitalAssetType = assetGlobal.getCapitalAssetType();
			if (ObjectUtils.isNotNull(capitalAssetType)) {
				if (capitalAssetType.getDepreciableLifeLimit() != null && capitalAssetType.getDepreciableLifeLimit().intValue() != 0) {
					assetGlobal.setCapitalAssetInServiceDate(assetGlobal.getCreateDate() == null ? getDateTimeService().getCurrentSqlDate() : assetGlobal.getCreateDate());
				}
				else {
					assetGlobal.setCapitalAssetInServiceDate(null);
				}
				computeDepreciationDate(assetGlobal);
				doPeriod13Changes(assetGlobal);
				
			}
		}
		assetGlobal.getAssetGlobalDetails().clear();
		assetGlobal.getAssetGlobalDetails().addAll(newDetails);
	}

	private DateTimeService getDateTimeService() {
		return SpringContext.getBean(DateTimeService.class);
	}

	private void computeDepreciationDate(AssetGlobal assetGlobal) {
		List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();
		if (assetPaymentDetails != null && !assetPaymentDetails.isEmpty()) {

			LOG.debug("Compute depreciation date based on asset type, depreciation convention and in-service date");
			AssetPaymentDetail firstAssetPaymentDetail = assetPaymentDetails.get(0);
			ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(firstAssetPaymentDetail.getPostingYear(), firstAssetPaymentDetail.getChartOfAccountsCode(), firstAssetPaymentDetail.getFinancialObjectCode());
			if (ObjectUtils.isNotNull(objectCode)) {
				Map<String, String> primaryKeys = new HashMap<String, String>();
				primaryKeys.put(CamsPropertyConstants.AssetDepreciationConvention.FINANCIAL_OBJECT_SUB_TYPE_CODE, objectCode.getFinancialObjectSubTypeCode());
				AssetDepreciationConvention depreciationConvention = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(AssetDepreciationConvention.class, primaryKeys);
				Date depreciationDate = SpringContext.getBean(AssetDateService.class).computeDepreciationDate(assetGlobal.getCapitalAssetType(),depreciationConvention, assetGlobal.getCapitalAssetInServiceDate());
				assetGlobal.setCapitalAssetDepreciationDate(depreciationDate);
			}
		}
	}

	private void doPeriod13Changes(AssetGlobal assetGlobal) {
		if (isPeriod13(assetGlobal)) {
			Integer closingYear = new Integer(SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));
			String closingDate = getClosingDate(closingYear);
			try {
				updateAssetGlobalForPeriod13(assetGlobal, closingYear, closingDate);
			}
			catch (Exception e) {
				LOG.error(e);
			}
		}
	}

	private boolean isPeriod13(AssetGlobal assetGlobal) {
		if (ObjectUtils.isNull(assetGlobal.getAccountingPeriod())) {
			return false;
		}
		return CamsConstants.AssetGlobal.PERIOD_THIRTEEN.equals(assetGlobal.getAccountingPeriod().getUniversityFiscalPeriodCode());
	}

	private String getClosingDate(Integer closingYear) {
		return getAssetGlobalService().getFiscalYearEndDayAndMonth() + closingYear.toString();
	}

	private void updateAssetGlobalForPeriod13(AssetGlobal assetGlobal, Integer closingYear, String closingDate) throws ParseException {
		assetGlobal.setCreateDate(getDateTimeService().getCurrentSqlDate());
		assetGlobal.setCapitalAssetInServiceDate(getDateTimeService().convertToSqlDate(closingDate));
		assetGlobal.setCreateDate(getDateTimeService().convertToSqlDate(closingDate));
		assetGlobal.setCapitalAssetDepreciationDate(getDateTimeService().convertToSqlDate(getClosingCalendarDate(closingYear)));
		assetGlobal.setLastInventoryDate(getDateTimeService().getCurrentSqlDate());
	}

	private AssetGlobalService getAssetGlobalService() {
		return SpringContext.getBean(AssetGlobalService.class);
	}

	private String getClosingCalendarDate(Integer closingYear) {
		return CamsConstants.AssetGlobal.JANUARY_FIRST + closingYear.toString();
	}

}
