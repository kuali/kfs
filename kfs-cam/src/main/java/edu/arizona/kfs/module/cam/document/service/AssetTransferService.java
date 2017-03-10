package edu.arizona.kfs.module.cam.document.service;

import edu.arizona.kfs.module.cam.document.AssetTransferDocument;

public interface AssetTransferService extends org.kuali.kfs.module.cam.document.service.AssetTransferService {

	void saveApprovedChanges(AssetTransferDocument document);

}
