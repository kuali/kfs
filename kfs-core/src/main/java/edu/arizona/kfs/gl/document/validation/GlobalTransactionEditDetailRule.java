/**
 * This class provides validation for Global Transaction Edit Detail documents.
 */
package edu.arizona.kfs.gl.document.validation;

import edu.arizona.kfs.gl.businessobject.GlobalTransactionEdit;
import edu.arizona.kfs.gl.businessobject.GlobalTransactionEditDetail;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.ObjectSubType;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.HashMap;
import java.util.Map;

public class GlobalTransactionEditDetailRule extends MaintenanceDocumentRuleBase {

    private static final String PROPERTY_ERROR_INVALID_KEY = "error.global.transaction.edit.detail.invalid.key";
    private static final String PROPERTY_ERROR_INVALID_OBJECT_KEY = "error.document.invalid.object.key";
    private static final String PRIMARY_KEY = "editObjectCodeRules";
    private static final String OBJECT_SUB_TYPE_CODE = "objectSubTypeCode";
    private static final String WILDCARD = "@";

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        GlobalTransactionEditDetail gted = (GlobalTransactionEditDetail) document.getNewMaintainableObject().getBusinessObject();
        boolean result = isPrimaryKeyValid(gted);
        result &= isOriginCodeValid(gted);
        result &= isFundGroupCodeValid(gted);
        result &= isSubFundGroupCodeValid(gted);
        result &= isDocumentTypeValid(gted);
        result &= isObjectTypeValid(gted);
        result &= isObjectSubTypeValid(gted);
        return result;
    }

    protected boolean isPrimaryKeyValid(GlobalTransactionEditDetail detail) {
        if (StringUtils.isBlank(detail.getOriginCode()) || StringUtils.isBlank(detail.getFundGroupCode()) || StringUtils.isBlank(detail.getSubFundGroupCode())) {
            return true;
        }
        GlobalTransactionEdit gte = loadGTEByPrimaryKey(detail.getOriginCode(), detail.getFundGroupCode(), detail.getSubFundGroupCode());
        if (gte == null) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(PRIMARY_KEY, PROPERTY_ERROR_INVALID_KEY);
            return false;
        }
        return true;
    }

    protected GlobalTransactionEdit loadGTEByPrimaryKey(String originCode, String fundGroupCode, String subFundGroupCode) {
        Map<String, Object> pkeys = new HashMap<String, Object>();
        pkeys.put(KFSPropertyConstants.ORIGIN_CODE, originCode);
        pkeys.put(KFSPropertyConstants.FUND_GROUP_CODE, fundGroupCode);
        pkeys.put(KFSPropertyConstants.SUB_FUND_GROUP_CODE, subFundGroupCode);
        return boService.findByPrimaryKey(GlobalTransactionEdit.class, pkeys);
    }

    protected boolean isOriginCodeValid(GlobalTransactionEditDetail detail) {
        String originCode = detail.getOriginCode();
        if (StringUtils.isBlank(originCode)) {
            return true;
        }
        if (WILDCARD.equals(originCode)) {
            return true;
        }
        OriginationCode oc = boService.findBySinglePrimaryKey(OriginationCode.class, originCode);
        if (oc == null) {
            putFieldError(KFSPropertyConstants.ORIGIN_CODE, PROPERTY_ERROR_INVALID_OBJECT_KEY, "Origination Code");
            return false;
        }
        return true;
    }

    protected boolean isFundGroupCodeValid(GlobalTransactionEditDetail detail) {
        String fundGroupCode = detail.getFundGroupCode();
        if (StringUtils.isBlank(fundGroupCode)) {
            return true;
        }
        if (WILDCARD.equals(fundGroupCode)) {
            return true;
        }
        FundGroup fg = boService.findBySinglePrimaryKey(FundGroup.class, fundGroupCode);
        if (fg == null) {
            putFieldError(KFSPropertyConstants.FUND_GROUP_CODE, PROPERTY_ERROR_INVALID_OBJECT_KEY, "Fund Group Code");
            return false;
        }
        return true;
    }

    protected boolean isSubFundGroupCodeValid(GlobalTransactionEditDetail detail) {
        String subFundGroupCode = detail.getSubFundGroupCode();
        if (StringUtils.isBlank(subFundGroupCode)) {
            return true;
        }
        if (WILDCARD.equals(subFundGroupCode)) {
            return true;
        }
        SubFundGroup sfg = boService.findBySinglePrimaryKey(SubFundGroup.class, subFundGroupCode);
        if (sfg == null) {
            putFieldError(KFSPropertyConstants.SUB_FUND_GROUP_CODE, PROPERTY_ERROR_INVALID_OBJECT_KEY, "Sub Fund Group Code");
            return false;
        }
        return true;
    }

    protected boolean isDocumentTypeValid(GlobalTransactionEditDetail detail) {
        String documentTypeCode = detail.getDocumentTypeCode();
        if (StringUtils.isBlank(documentTypeCode)) {
            return true;
        }
        if (WILDCARD.equals(documentTypeCode)) {
            return true;
        }
        DocumentType dt = KEWServiceLocator.getDocumentTypeService().findByNameCaseInsensitive(documentTypeCode);
        if (dt == null) {
            putFieldError(KFSPropertyConstants.DOCUMENT_TYPE_CODE, PROPERTY_ERROR_INVALID_OBJECT_KEY, "Document Type Code");
            return false;
        }
        return true;
    }

    protected boolean isObjectTypeValid(GlobalTransactionEditDetail detail) {
        String objectType = detail.getObjectTypeCode();
        if (StringUtils.isBlank(objectType)) {
            return true;
        }
        if (WILDCARD.equals(objectType)) {
            return true;
        }
        ObjectType ot = boService.findBySinglePrimaryKey(ObjectType.class, objectType);
        if (ot == null) {
            putFieldError(KFSPropertyConstants.OBJECT_TYPE_CODE, PROPERTY_ERROR_INVALID_OBJECT_KEY, "Object Type Code");
            return false;
        }
        return true;
    }

    protected boolean isObjectSubTypeValid(GlobalTransactionEditDetail detail) {
        String objectSubType = detail.getObjectSubTypeCode();
        if (StringUtils.isBlank(objectSubType)) {
            return true;
        }
        if (WILDCARD.equals(objectSubType)) {
            return true;
        }
        ObjectSubType ost = boService.findBySinglePrimaryKey(ObjectSubType.class, objectSubType);
        if (ost == null) {
            putFieldError(OBJECT_SUB_TYPE_CODE, PROPERTY_ERROR_INVALID_OBJECT_KEY, "Object Sub-Type Code");
            return false;
        }
        return true;
    }

}
