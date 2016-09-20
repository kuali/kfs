package edu.arizona.kfs.gl.businessobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.ObjectSubType;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;


public class GlobalTransactionEditDetail extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String originCode;
    private String fundGroupCode;
    private String subFundGroupCode;
    private String documentTypeCode;
    private String objectTypeCode;
    private String objectSubTypeCode;
    private String objectCodeRulePurpose;
    private boolean active;
    private DocumentType documentType;
    private ObjectType objectType;
    private ObjectSubType objectSubType;
    private GlobalTransactionEdit globalTransactionEdit;
    private BusinessObjectService boService;


    public GlobalTransactionEditDetail() {
        super();
    }


    public String getOriginCodeFullText() {
        OriginationCode origin = getBoService().findBySinglePrimaryKey(OriginationCode.class, originCode);
        return origin.getFinancialSystemOriginationCode() + "-" + origin.getFinancialSystemServerName();
    }

    public String getOriginCode() {
        return originCode;
    }

    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }

    public String getFundGroupCodeFullText() {
        FundGroup fundGroup = getBoService().findBySinglePrimaryKey(FundGroup.class, fundGroupCode);
        return fundGroup.getCode() + "-" + fundGroup.getName();
    }

    public String getFundGroupCode() {
        return fundGroupCode;
    }

    public void setFundGroupCode(String fundGroupCode) {
        this.fundGroupCode = fundGroupCode;
    }

    public String getSubFundGroupCodeFullText() {
        SubFundGroup subFundGroup = getBoService().findBySinglePrimaryKey(SubFundGroup.class, subFundGroupCode);
        return subFundGroup.getSubFundGroupCode() + "-" + subFundGroup.getSubFundGroupDescription();
    }

    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }

    public String getDocumentTypeCodeFullText() {
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("name", documentTypeCode);
        fieldMap.put("active", true);
        fieldMap.put("currentInd", true);
        DocumentType docType = new ArrayList<DocumentType>(getBoService().findMatching(DocumentType.class, fieldMap)).get(0);
        return docType.getName() + "-" + docType.getLabel();
    }

    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    public void setDocumentTypeCode(String documentType) {
        this.documentTypeCode = documentType;
    }

    public String getObjectTypeCodeFullText() {
        ObjectType objectType = getBoService().findBySinglePrimaryKey(ObjectType.class, objectTypeCode);
        return objectType.getName() + "-" + objectType.getName();
    }

    public String getObjectTypeCode() {
        return objectTypeCode;
    }

    public void setObjectTypeCode(String objectType) {
        this.objectTypeCode = objectType;
    }

    public String getObjectSubTypeCodeFullText() {
        ObjectSubType objectSubType = getBoService().findBySinglePrimaryKey(ObjectSubType.class, objectSubTypeCode);
        return objectSubType.getName() + "-" + objectSubType.getName();
    }

    public String getObjectSubTypeCode() {
        return objectSubTypeCode;
    }

    public void setObjectSubTypeCode(String objectSubType) {
        this.objectSubTypeCode = objectSubType;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public ObjectSubType getObjectSubType() {
        return objectSubType;
    }

    public void setObjectSubType(ObjectSubType objectSubType) {
        this.objectSubType = objectSubType;
    }

    public GlobalTransactionEdit getGlobalTransactionEdit() {
        return globalTransactionEdit;
    }

    public void setGlobalTransactionEdit(GlobalTransactionEdit globalTransactionEdit) {
        this.globalTransactionEdit = globalTransactionEdit;
    }

    public String getObjectCodeRulePurpose() {
        return objectCodeRulePurpose;
    }

    public void setObjectCodeRulePurpose(String objectCodeRulePurpose) {
        this.objectCodeRulePurpose = objectCodeRulePurpose;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    private BusinessObjectService getBoService() {
        if (boService == null) {
            boService = SpringContext.getBean(BusinessObjectService.class);
        }
        return boService;
    }

}
