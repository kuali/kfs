package edu.arizona.kfs.vnd.document.service.impl;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.businessobject.VendorRoutingComparable;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.vnd.businessobject.VendorDetailExtension;

public class VendorServiceImpl implements VendorService {

private static final Logger LOG = Logger.getLogger(VendorServiceImpl.class);
    
    private VendorService vendorService;
    private DocumentService documentService;

    public boolean equalMemberLists(List<? extends VendorRoutingComparable> list_a, List<? extends VendorRoutingComparable> list_b) {
        return vendorService.equalMemberLists(list_a, list_b);
    }

    public KualiDecimal getApoLimitFromContract(Integer contractId, String chart, String org) {
        return vendorService.getApoLimitFromContract(contractId, chart, org);
    }

    public VendorDetail getByVendorNumber(String vendorNumber) {
        return vendorService.getByVendorNumber(vendorNumber);
    }

    public VendorDetail getParentVendor(Integer vendorHeaderGeneratedIdentifier) {
        return vendorService.getParentVendor(vendorHeaderGeneratedIdentifier);
    }

    public VendorContract getVendorB2BContract(VendorDetail vendorDetail, String campus) {
        return vendorService.getVendorB2BContract(vendorDetail, campus);
    }

    public VendorDetail getVendorByDunsNumber(String vendorDunsNumber) {
        return vendorService.getVendorByDunsNumber(vendorDunsNumber);
    }

    public VendorAddress getVendorDefaultAddress(Integer vendorHeaderId, Integer vendorDetailId, String addressType, String campus) {
        return vendorService.getVendorDefaultAddress(vendorHeaderId, vendorDetailId, addressType, campus);
    }

    public VendorAddress getVendorDefaultAddress(Collection<VendorAddress> addresses, String addressType, String campus) {
        return vendorService.getVendorDefaultAddress(addresses, addressType, campus);
    }
 
    public VendorDetail getVendorDetail(String vendorNumber) {
        return vendorService.getVendorDetail(vendorNumber);
    }

    public VendorDetail getVendorDetail(Integer headerId, Integer detailId) {
        return vendorService.getVendorDetail(headerId, detailId);
    }

    public boolean isRevolvingFundCodeVendor(Integer vendorHeaderGeneratedIdentifier) {
        return vendorService.isRevolvingFundCodeVendor(vendorHeaderGeneratedIdentifier);
    }
 
    public boolean isSubjectPaymentVendor(Integer vendorHeaderGeneratedIdentifier) {
        return vendorService.isSubjectPaymentVendor(vendorHeaderGeneratedIdentifier);
    }

    public boolean isVendorForeign(Integer vendorHeaderGeneratedIdentifier) {
        return vendorService.isVendorForeign(vendorHeaderGeneratedIdentifier);
    }

    public boolean isVendorInstitutionEmployee(Integer vendorHeaderGeneratedIdentifier) {
        return vendorService.isVendorInstitutionEmployee(vendorHeaderGeneratedIdentifier);
    }

    public boolean noRouteSignificantChangeOccurred(VendorDetail newVDtl, VendorHeader newVHdr, VendorDetail oldVDtl, VendorHeader oldVHdr) {
        return vendorService.noRouteSignificantChangeOccurred(newVDtl, newVHdr, oldVDtl, oldVHdr);
    }

    public void saveVendorHeader(VendorDetail vendorDetail) {
        vendorService.saveVendorHeader(vendorDetail);
    }
    
	public List<Note> getVendorNotes(VendorDetail vendorDetail) {
		return vendorService.getVendorNotes(vendorDetail);
	}

	public boolean isVendorContractExpired(Document document, Integer vendorContractGeneratedIdentifier, VendorDetail vendorDetail) {
		return vendorService.isVendorContractExpired(document, vendorContractGeneratedIdentifier, vendorDetail);
	}
	
	public VendorAddress getVendorDefaultAddress(Integer vendorHeaderId, Integer vendorDetailId, String addressType, String campus, boolean activeCheck) {
		return vendorService.getVendorDefaultAddress(vendorHeaderId, vendorDetailId, addressType, campus, activeCheck);
	}   

    /**
     * @see org.kuali.kfs.vnd.document.service.VendorService#shouldVendorRouteForApproval(java.lang.String)
     * Returns true if super class returns true, or if there was a change in an extended attribute.
     */
    public boolean shouldVendorRouteForApproval(String documentId) {        
        if (vendorService.shouldVendorRouteForApproval(documentId)) {
             return true;
        }
                
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering customized shouldVendorRouteForApproval.");
        }
        
        boolean shouldRoute = true;
        MaintenanceDocument newDoc = null;
        try {
            newDoc = (MaintenanceDocument) documentService.getByDocumentHeaderId(documentId);
        }
        catch (WorkflowException we) {
            throw new RuntimeException("A WorkflowException was thrown which prevented the loading of " + "the comparison document (" + documentId + ")", we);
        }

        if (ObjectUtils.isNotNull(newDoc)) {

            VendorDetailExtension oldVDtlExtension =  (VendorDetailExtension) ((VendorDetail) newDoc.getOldMaintainableObject().getBusinessObject()).getExtension();
            VendorDetailExtension newVDtlExtension = (VendorDetailExtension) ((VendorDetail)newDoc.getNewMaintainableObject().getBusinessObject()).getExtension();

            if (ObjectUtils.isNotNull(oldVDtlExtension)) {
                shouldRoute = !(noRouteSignificantChangeOccurredForExtensions(oldVDtlExtension, newVDtlExtension));
            }
            else {
                shouldRoute = true;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Exiting shouldVendorRouteForApproval.");
        }
        
        return shouldRoute;
        
    }

    /**
     * This method compares the extended attributes, similar to
     *  org.kuali.kfs.vnd.document.service.VendorServiceImpl.noRouteSignificantChangeOccurred
     *            
     */
    public boolean noRouteSignificantChangeOccurredForExtensions(VendorDetailExtension oldVDtlExtension, VendorDetailExtension newVDtlExtension) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering noRouteSignificantChangeOccurredForExtensions.");
        }

        boolean unchanged = newVDtlExtension.isEqualForRouting(oldVDtlExtension);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Exiting noRouteSignificantChangeOccurredForExtensions.");
        }
        
        return unchanged;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
