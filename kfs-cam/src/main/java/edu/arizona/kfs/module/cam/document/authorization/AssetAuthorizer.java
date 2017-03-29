package edu.arizona.kfs.module.cam.document.authorization;

import java.util.Set;

import edu.arizona.kfs.module.cam.CamsPropertyConstants;

/**
 * @see org.kuali.kfs.module.cam.businessobject.authorization.AssetInquiryAuthorizer
 */
public class AssetAuthorizer extends org.kuali.kfs.module.cam.document.authorization.AssetAuthorizer {
	 /**
	  * Override to validate user permission to access account responsibility tab 
	  */
    @Override
    public Set<String> getSecurePotentiallyReadOnlySectionIds() {
    	Set<String> secureSectionIds = super.getSecurePotentiallyReadOnlySectionIds();
    	secureSectionIds.add(CamsPropertyConstants.AssetAccountResponsibility.MAINT_SECTION_ID);
    	return secureSectionIds;
    }
}
