package edu.arizona.kfs.module.cam.service.impl;

import edu.arizona.kfs.module.cab.CabConstants;

public class AssetLockServiceImpl extends org.kuali.kfs.module.cam.service.impl.AssetLockServiceImpl {
	
	@Override
	protected boolean isPurApDocument(String documentTypeName) {
		return super.isPurApDocument(documentTypeName) || CabConstants.PRNC.equals(documentTypeName);
    }
}