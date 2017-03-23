package edu.arizona.kfs.module.cam.businessobject.lookup;

import java.util.Properties;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.UrlFactory;

import edu.arizona.kfs.module.cam.businessobject.AssetRetirementGlobal;

/**
 * This class overrides the base getActionUrls method
 */
public class AssetLookupableHelperServiceImpl extends org.kuali.kfs.module.cam.businessobject.lookup.AssetLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLookupableHelperServiceImpl.class);

    protected HtmlData getMergeUrl(Asset asset) {
        FinancialSystemMaintenanceDocumentAuthorizerBase documentAuthorizer = (FinancialSystemMaintenanceDocumentAuthorizerBase) SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer(CamsConstants.DocumentTypeName.ASSET_RETIREMENT_GLOBAL);
        boolean isAuthorized = documentAuthorizer.isAuthorized(asset, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.MERGE, GlobalVariables.getUserSession().getPerson().getPrincipalId());

        if (isAuthorized) {
            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
            // Changed to use edu AssetRetirementGlobal class name for matching ARG DD definiation
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AssetRetirementGlobal.class.getName());
            parameters.put(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber().toString());
            parameters.put(KFSConstants.OVERRIDE_KEYS, CamsPropertyConstants.AssetRetirementGlobal.RETIREMENT_REASON_CODE + KFSConstants.FIELD_CONVERSIONS_SEPERATOR + CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER);
            parameters.put(CamsPropertyConstants.AssetRetirementGlobal.RETIREMENT_REASON_CODE, CamsConstants.AssetRetirementReasonCode.MERGED);
            parameters.put(KFSConstants.REFRESH_CALLER, CamsPropertyConstants.AssetRetirementGlobal.RETIREMENT_REASON_CODE + "::" + CamsConstants.AssetRetirementReasonCode.MERGED);


            String href = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, parameters);

            return new AnchorHtmlData(href, CamsConstants.AssetActions.MERGE, CamsConstants.AssetActions.MERGE);
        }
        else {
            return new AnchorHtmlData("", "", "");
        }
    }
}
