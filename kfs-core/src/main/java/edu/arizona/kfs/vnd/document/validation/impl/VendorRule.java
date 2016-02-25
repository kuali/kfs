package edu.arizona.kfs.vnd.document.validation.impl;

import java.util.List;
import org.kuali.kfs.sys.context.SpringContext;
import edu.arizona.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorSupplierDiversity;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;


public class VendorRule extends org.kuali.kfs.vnd.document.validation.impl.VendorRule {

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
    	boolean valid = super.processCustomApproveDocumentBusinessRules(document);
    	return valid & processSupplierDiversityValidation(document);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
    	boolean valid = super.processCustomRouteDocumentBusinessRules(document);
    	return valid & processSupplierDiversityValidation(document);
    }
    
    private boolean processSupplierDiversityValidation(MaintenanceDocument document) {
    	boolean valid = true;
    	List<VendorSupplierDiversity> vendorSupplierDiversities = ((VendorDetail) document.getNewMaintainableObject().getBusinessObject()).getVendorHeader().getVendorSupplierDiversities();
    	DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
    	
    	if(vendorSupplierDiversities == null || vendorSupplierDiversities.isEmpty()) {
    		valid = false;
    		String fieldName = VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITY_CODE_FIELD_NAME;
    		String attributeLabel = dataDictionaryService.getAttributeLabel(VendorSupplierDiversity.class, fieldName);
    		String shortLabel = dataDictionaryService.getAttributeShortLabel(VendorSupplierDiversity.class, fieldName);
    		
    		putFieldError(VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITY_CODE, RiceKeyConstants.ERROR_REQUIRED, attributeLabel + " (" + shortLabel + ")");
    	}
    	
    	else {
    		int count = 0;
    		for(VendorSupplierDiversity vsd : vendorSupplierDiversities) {
    			if(vsd.isActive()) {
    				count++;
    			}
    		}
    		
    		if(count == 0) {
    			valid = false;
    			String fieldName = VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITY_ACTIVE_FIELD_NAME;
    			String attributeLabel = dataDictionaryService.getAttributeLabel(VendorSupplierDiversity.class, fieldName);
        		String shortLabel = dataDictionaryService.getAttributeShortLabel(VendorSupplierDiversity.class, fieldName);
        		
        		putFieldError(VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITY_ACTIVE, RiceKeyConstants.ERROR_REQUIRED, attributeLabel + " (" + shortLabel + ")");
    		}
    	}
    	
		return valid;
	}
}
