package edu.arizona.kfs.vnd.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion;
import org.kuali.rice.kew.api.KEWPropertyConstants;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.impl.KewImplConstants;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.sys.businessobject.BatchFileUploads;
import edu.arizona.kfs.vnd.VendorConstants;
import edu.arizona.kfs.vnd.VendorPropertyConstants;
import edu.arizona.kfs.vnd.businessobject.VendorDetailExtension;
import edu.arizona.kfs.vnd.service.EcustomsFileService;
import edu.arizona.kfs.vnd.service.EcustomsService;

/**
 * UAF-66 MOD-PA7000-02 ECustoms - US Export Compliance
 *
 * New service is to perform the batch job steps for validating vendor compliance with US Export Controls.
 *
 * @author Adam Kost kosta@email.arizona.edu
 */
@Transactional
public class EcustomsServiceImpl implements EcustomsService {

    private static final Logger LOG = Logger.getLogger(EcustomsServiceImpl.class);
    private static final Pattern INVALID_CHARACTERS = Pattern.compile("[^A-Za-z0-9.\\-\\ ]");
    private static final Pattern DUPLICATE_WHITESPACE = Pattern.compile("\\s{2,}");

    private static transient volatile VendorService vendorService;
    private static transient volatile DocumentService documentService;
    private static transient volatile BusinessObjectService businessObjectService;
    private static transient volatile LookupService lookupService;
    private static transient volatile EcustomsFileService ecustomsFileService;

    private File dataFile;
    private File vendorCountFile;

    private String defaultLastRunDate;
    private String batchFileName;
    private String batchName;

    public String getDefaultLastRunDate() {
        return defaultLastRunDate;
    }

    public void setDefaultLastRunDate(String defaultLastRunDate) {
        this.defaultLastRunDate = defaultLastRunDate;
    }

    public String getBatchFileName() {
        return batchFileName;
    }

    public void setBatchFileName(String batchFileName) {
        this.batchFileName = batchFileName;
    }

    private static VendorService getVendorService() {
        if (vendorService == null) {
            vendorService = SpringContext.getBean(VendorService.class);
        }
        return vendorService;
    }

    private static DocumentService getDocumentService() {
        if (documentService == null) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }

    private static BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    private static LookupService getLookupService() {
        if (lookupService == null) {
            lookupService = SpringContext.getBean(LookupService.class);
        }
        return lookupService;
    }

    private static EcustomsFileService getEcustomsFileService() {
        if (ecustomsFileService == null) {
            ecustomsFileService = SpringContext.getBean(EcustomsFileService.class);
        }
        return ecustomsFileService;
    }

    @Override
    public boolean createEcustomsDailyFile(String jobName, Date jobRunDate) throws Exception {
        LOG.debug("createEcustomsDailyFile(): jobRunDate=" + jobRunDate + ", start-time=" + new Date());
        boolean processVendorDetails = false;
        boolean deleteOldDailyDone = getEcustomsFileService().deleteOldDailyDoneFiles();
        LOG.debug("getEcustomsFileService().deleteOldDailyDoneFiles() returned " + deleteOldDailyDone);
        dataFile = getEcustomsFileService().getDailyBatchDataFile(jobRunDate);
        vendorCountFile = getEcustomsFileService().getDailyBatchVendorCountFile(jobRunDate);
        batchName = VendorConstants.ECUSTOMS_BATCH_DAILY;
        List<VendorDetail> vendorDetails = getDailyVendorDetailList(jobRunDate);
        processVendorDetails = processVendorDetails(jobRunDate, vendorDetails);
        getEcustomsFileService().createDailyBatchDoneFile(jobRunDate);
        return processVendorDetails;
    }

    @Override
    public boolean createEcustomsAnnualFile(String jobName, Date jobRunDate) throws Exception {
        LOG.debug("createEcustomsDailyFile(): jobRunDate=" + jobRunDate + ", start-time=" + new Date());
        boolean processVendorDetails = false;
        boolean deleteOldAnnualBatch = getEcustomsFileService().deleteOldAnnualDoneFiles();
        LOG.debug("getEcustomsFileService().deleteOldAnnualDoneFiles() returned " + deleteOldAnnualBatch);
        dataFile = getEcustomsFileService().getAnnualBatchDataFile(jobRunDate);
        vendorCountFile = getEcustomsFileService().getAnnualBatchVendorCountFile(jobRunDate);
        batchName = VendorConstants.ECUSTOMS_BATCH_ANNUAL;

        List<VendorDetail> vendorDetails = getAllActiveVendors();
        processVendorDetails = processVendorDetails(jobRunDate, vendorDetails);
        getEcustomsFileService().createAnnualBatchDoneFile(jobRunDate);
        return processVendorDetails;
    }

    /**
     * Processes the list of vendors for output.
     *
     * @param jobRunDate
     *
     * @param vendorDetails
     * @return
     * @throws IOException
     */
    private boolean processVendorDetails(Date jobRunDate, List<VendorDetail> vendorDetails) throws IOException {
        FileOutputStream dataFileStream = new FileOutputStream(dataFile);
        List<String> outputVendorLineList = new ArrayList<String>();

        for (VendorDetail vendorDetail : vendorDetails) {
            String vendorLine = generateVendorLine(vendorDetail);
            if (!outputVendorLineList.contains(vendorLine)) {
                outputVendorLineList.add(vendorLine);
                dataFileStream.write(vendorLine.getBytes());
                updateVendor(vendorDetail);
            }
        }

        dataFileStream.flush();
        dataFileStream.close();
        writeVendorCountFile(outputVendorLineList.size());
        KualiInteger listSize = new KualiInteger(outputVendorLineList.size());
        saveLastRunDate(jobRunDate, listSize);
        return true;
    }

    /**
     * write the file containing number of vendors processed for ecustoms
     *
     * @param vendorDetails
     */
    private void writeVendorCountFile(int size) throws IOException {
        FileOutputStream countFileStream = new FileOutputStream(vendorCountFile);
        String outputMessage = "Vendors added to ecustoms file = " + size;
        LOG.info(outputMessage);
        try {
            countFileStream.write(outputMessage.getBytes());
            countFileStream.flush();
            countFileStream.close();
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            IOUtils.closeQuietly(countFileStream);
        }
    }

    /**
     * Get the list of vendors that need to be processed since the last daily run.
     *
     * @param jobRunDate
     * @return list of vendors to process for security compliance
     * @throws WorkflowException
     */
    private List<VendorDetail> getDailyVendorDetailList(Date jobRunDate) {
        List<VendorDetail> retval = new ArrayList<VendorDetail>();

        List<VendorDetail> vendorDetails = new ArrayList<VendorDetail>();

        List<VendorDetail> recentVendorList = getRecentVendorList();
        List<VendorDetail> exportList = getVendorsWithExportFlagSetToNo();

        vendorDetails.addAll(recentVendorList);
        vendorDetails.addAll(exportList);

        if (vendorDetails.isEmpty()) {
            LOG.info("No vendor candidates found.");
            return retval;
        } else {
            LOG.info("Found " + vendorDetails.size() + " total vendor candidates for ecustoms.");
        }

        for (VendorDetail vendorDetail : vendorDetails) {
            boolean wasProcessedToday = hasProcessedToday(vendorDetail);
            LOG.trace("wasProcessedToday=" + wasProcessedToday);
            if (!wasProcessedToday) {
                retval.add(vendorDetail);
            }
        }
        // retval.addAll(vendorDetails);
        LOG.info("Found " + retval.size() + " vendors to be processed by batch for ecustoms.");
        return retval;

    }

    /**
     * Get a list of all active vendors.
     *
     * @param jobRunDate
     * @return list of vendors to process for security compliance
     * @throws WorkflowException
     */
    private List<VendorDetail> getAllActiveVendors() {
        List<VendorDetail> retval = new ArrayList<VendorDetail>();
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KRADPropertyConstants.ACTIVE_INDICATOR, OjbCharBooleanConversion.DATABASE_BOOLEAN_TRUE_STRING_REPRESENTATION);
        retval.addAll(getBusinessObjectService().findMatching(VendorDetail.class, fieldValues));
        LOG.debug("found " + retval.size() + " active vendors.");
        return retval;
    }

    /**
     * Get a list of new vendors that are ready to be processed.
     *
     * @param newVendorNotes
     * @return List of VendorDetail objects
     */
    private List<VendorDetail> getRecentVendorList() {
        List<VendorDetail> retval = new ArrayList<VendorDetail>();

        List<DocumentRouteHeaderValue> docHeaderList = getRecentFinalizedVendorsDocHeaders();

        for (DocumentRouteHeaderValue routeHeader : docHeaderList) {
            String documentId = routeHeader.getDocumentId();
            MaintenanceDocument doc = null;
            LOG.info("+================================================================================");
            LOG.info("Retrieving maintenance document: " + documentId);
            try {
                doc = (MaintenanceDocument) getDocumentService().getByDocumentHeaderId(documentId);
            } catch (Exception e) {
                LOG.error("Document " + documentId + " experienced an error: " + e.getMessage());
                LOG.trace("Assuming this document contains a vendor that is new or has a changed name.");
                LOG.trace("Unable to find vendor by the maintenance document, attempting to acquire the vendor by retrieving associated notes and using the remoteObjectId.");
                List<Note> notes = getNotesByDocumentId(documentId);
                List<VendorDetail> vendorList = new ArrayList<VendorDetail>();
                for (Note note : notes) {
                    LOG.debug("Object ID: " + note.getRemoteObjectIdentifier());
                    List<VendorDetail> vendorListByObjectId = getVendorByObjectId(note.getRemoteObjectIdentifier());
                    vendorList.addAll(vendorListByObjectId);
                }
                LOG.trace("Found " + vendorList.size() + " vendors related to documentId " + documentId);
                retval.addAll(vendorList);
            }

            if (doc != null) {
                LOG.info("Document " + documentId + " successfully retrieved.");
                boolean isVendorNewOrChanged = isVendorNewOrChanged(doc);
                if (isVendorNewOrChanged) {
                    List<VendorDetail> vendorList = getVendorByDocumentId(doc);
                    retval.addAll(vendorList);
                }
            }
            LOG.info("Document " + documentId + " processed.");
        }
        return retval;
    }

    /**
     * Searches for and retrieves vendor maintenance docs for vendors that were finalized since the last run date.
     *
     * @return List of Note objects
     */
    private List<DocumentRouteHeaderValue> getRecentFinalizedVendorsDocHeaders() {
        Date lastRunDate = getLastRunDate();
        SimpleDateFormat timestampFormat = new SimpleDateFormat("MM/dd/yyyy");
        String lastDateString = timestampFormat.format(lastRunDate);

        List<DocumentRouteHeaderValue> retval = new ArrayList<DocumentRouteHeaderValue>();

        List<String> docTypeIdList = getPVENDocumentTypeIds();
        for (String docTypeId : docTypeIdList) {
            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(KEWPropertyConstants.DOCUMENT_TYPE_ID, docTypeId);
            fieldValues.put(KFSPropertyConstants.DOC_ROUTE_STATUS, KewApiConstants.ROUTE_HEADER_FINAL_CD);
            fieldValues.put(KFSPropertyConstants.FINALIZED_DATE, SearchOperator.GREATER_THAN_EQUAL.op() + lastDateString);
            LOG.trace("Getting applicable documents for docTypeId " + docTypeId);
            Collection<DocumentRouteHeaderValue> docHeaderList = getLookupService().findCollectionBySearchUnbounded(DocumentRouteHeaderValue.class, fieldValues);
            if (docHeaderList != null) {
                LOG.trace("Found " + docHeaderList.size() + " docHeaderList.");
                retval.addAll(docHeaderList);
            }
        }

        LOG.info("Found " + retval.size() + " recent vendor documents.");
        return retval;
    }

    private List<String> getPVENDocumentTypeIds() {
        List<String> retval = new ArrayList<String>();
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KewImplConstants.PropertyConstants.NAME, VendorConstants.DOCUMENT_TYPE_NAME_PVEN);
        Collection<DocumentType> docTypeCollection = getBusinessObjectService().findMatching(DocumentType.class, fieldValues);

        if (docTypeCollection == null) {
            LOG.error("Unable to find any valid DocumentType IDs for documents of type " + VendorConstants.DOCUMENT_TYPE_NAME_PVEN);
        }
        for (DocumentType doc : docTypeCollection) {
            retval.add(doc.getDocumentTypeId().toString());
        }

        LOG.debug("Found " + retval.size() + " documentTypeId values for PVEN.");
        return retval;
    }

    /**
     * Determines if this document has a new vendor or updates to the vendor name.
     *
     * @return true if vendor name has been updated
     */
    private boolean isVendorNewOrChanged(MaintenanceDocument doc) {
        VendorDetail olddet = (VendorDetail) getOldPersistableObject(doc);
        VendorDetail newdet = (VendorDetail) getNewPersistableObject(doc);
        if (olddet == null && newdet == null) {
            LOG.debug("Old and New are null. Assumed to not contain a Vendor.");
            return false;
        }
        if ((olddet != null && newdet == null) || (olddet == null && newdet != null)) {
            LOG.debug("One of the objects is null and the other is not. Vendor assumed to be new.");
            return true;
        }

        boolean changedFirstName = !StringUtils.equalsIgnoreCase(olddet.getVendorFirstName(), newdet.getVendorFirstName());
        boolean changedLastName = !StringUtils.equalsIgnoreCase(olddet.getVendorLastName(), newdet.getVendorLastName());
        boolean changedVendorName = !StringUtils.equalsIgnoreCase(olddet.getVendorName(), newdet.getVendorName());
        boolean retval = changedFirstName || changedLastName || changedVendorName;
        LOG.debug("first name  : old=" + olddet.getVendorFirstName() + ", new=" + newdet.getVendorFirstName());
        LOG.debug("last name   : old=" + olddet.getVendorLastName() + ", new=" + newdet.getVendorLastName());
        LOG.debug("vendor name : old=" + olddet.getVendorName() + ", new=" + newdet.getVendorName());
        LOG.debug("isVendorNameChanged retval=" + retval);
        return retval;

    }

    private PersistableBusinessObject getOldPersistableObject(MaintenanceDocument doc) {
        PersistableBusinessObject object = null;
        if (doc.getOldMaintainableObject() != null) {
            object = doc.getOldMaintainableObject().getPersistableBusinessObject();
        }
        return object;
    }

    private PersistableBusinessObject getNewPersistableObject(MaintenanceDocument doc) {
        PersistableBusinessObject object = null;
        if (doc.getNewMaintainableObject() != null) {
            object = doc.getNewMaintainableObject().getPersistableBusinessObject();
        }
        return object;
    }

    /**
     * Retrieve the vendor(s) associated with the documentId.
     */
    private List<VendorDetail> getVendorByDocumentId(MaintenanceDocument doc) {
        List<VendorDetail> retval = new ArrayList<VendorDetail>();
        VendorDetail vendor = (VendorDetail) doc.getNewMaintainableObject().getPersistableBusinessObject();
        if (vendor != null) {
            retval.add(vendor);
        }
        return retval;
    }

    /**
     * Searches for and retrieves vendor notes that mention the documentId.
     */
    private List<Note> getNotesByDocumentId(String documentId) {
        List<Note> retval = new ArrayList<Note>();

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KRADConstants.NOTE_TEXT_PROPERTY_NAME, SearchOperator.LIKE_MANY + documentId + SearchOperator.LIKE_MANY);
        Collection<Note> noteCollection = getBusinessObjectService().findMatching(Note.class, fieldValues);

        if (noteCollection != null) {
            retval.addAll(noteCollection);
        }

        LOG.debug("Found " + retval.size() + " notes related to documentId " + documentId);
        return retval;
    }

    /**
     * Get the vendor(s) with the associated objectId.
     */
    private List<VendorDetail> getVendorByObjectId(String objectId) {
        List<VendorDetail> retval = new ArrayList<VendorDetail>();
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KRADPropertyConstants.OBJECT_ID, objectId);
        fieldValues.put(KRADPropertyConstants.ACTIVE_INDICATOR, OjbCharBooleanConversion.DATABASE_BOOLEAN_TRUE_STRING_REPRESENTATION);
        Collection<VendorDetail> vendorDetailCollection = getBusinessObjectService().findMatching(VendorDetail.class, fieldValues);

        if (vendorDetailCollection != null) {
            retval.addAll(vendorDetailCollection);
        }

        LOG.debug("Found " + retval.size() + " vendors related to objectId " + objectId + ".");
        return retval;
    }

    /**
     * Get the list of active vendor(s) that have the Export Controls Flag set to No.
     */
    private List<VendorDetail> getVendorsWithExportFlagSetToNo() {
        List<VendorDetail> retval = new ArrayList<VendorDetail>();

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KRADPropertyConstants.ACTIVE_INDICATOR, OjbCharBooleanConversion.DATABASE_BOOLEAN_TRUE_STRING_REPRESENTATION);
        fieldValues.put(VendorPropertyConstants.VENDOR_DETAIL_EXTENSION_EXPORT_CONTROLS_FLAG, OjbCharBooleanConversion.DATABASE_BOOLEAN_FALSE_STRING_REPRESENTATION);
        Collection<VendorDetail> vendorDetailCollection = getLookupService().findCollectionBySearchUnbounded(VendorDetail.class, fieldValues);

        if (vendorDetailCollection != null) {
            retval.addAll(vendorDetailCollection);
        }

        LOG.info("Found " + retval.size() + " vendors with Export Controls Flag set to No.");
        return retval;
    }

    /**
     * Check notes to see if it has been processed today.
     *
     * @param vendorDetail
     * @param jobRunDate
     * @return
     */
    private boolean hasProcessedToday(VendorDetail vendorDetail) {
        boolean retval = false;
        List<Note> notes = vendorDetail.getBoNotes();
        if (notes == null) {
            LOG.debug("Vendor " + vendorDetail.getVendorNumber() + " has no notes attached. Vendor has not been processed today.");
            return false;
        }

        // check the notes to see if we have one that indicates this vendor was already processed today
        for (Note note : notes) {
            LOG.trace("+================================================================================");
            LOG.trace("note: " + note.getNoteText());
            LOG.trace("date: " + note.getNotePostedTimestamp().toString());
            // if this note was created today & note text equals SENT_TO_ECUSTOMS_NOTE_TEXT
            boolean isSameDay = DateUtils.isSameDay(note.getNotePostedTimestamp(), new Date());
            boolean isSentToEcustoms = VendorConstants.ECUSTOMS_SENT_TO_ECUSTOMS_NOTE_TEXT.equalsIgnoreCase(note.getNoteText());
            if (isSameDay && isSentToEcustoms) {
                LOG.debug("Vendor " + vendorDetail.getVendorNumber() + " was already procesed by batch for ecustoms today.");
                retval |= true;
            }
        }
        return retval;
    }

    /**
     * Removes characters which are invalid in the vendor security compliance file. Also upper-cases the data per examples from the eCustoms spec.
     */
    private String getCleanedData(String inputString) {
        String retval = inputString;
        if (StringUtils.isBlank(retval)) {
            return CoreConstants.EMPTY_STRING;
        }
        retval = INVALID_CHARACTERS.matcher(retval).replaceAll(" ");
        retval = DUPLICATE_WHITESPACE.matcher(retval).replaceAll(" ");
        retval = retval.toUpperCase();
        return retval;
    }

    /**
     * Generates the line of output that goes in the data file.
     *
     * @param vendorDetail
     * @return security compliance file output line for input vendor detail record
     */
    private String generateVendorLine(VendorDetail vendorDetail) {
        StringBuilder retval = new StringBuilder();

        String vendorId = getAssembledVendorId(vendorDetail);
        String vendorPersonName = getAssembledVendorPersonName(vendorDetail);
        String vendorCompanyName = getVendorCompanyName(vendorDetail);
        String vendorAddress = CoreConstants.EMPTY_STRING;
        String vendorCountry = CoreConstants.EMPTY_STRING;

        List<VendorAddress> vendorAddresses = vendorDetail.getVendorAddresses();
        String addressType = vendorDetail.getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode();
        VendorAddress va = getVendorService().getVendorDefaultAddress(vendorAddresses, addressType, null);
        if (va != null) {
            vendorAddress = getAssembledVendorAddress(va);
            vendorCountry = getVendorCountry(va);
        }

        retval.append(vendorId);
        retval.append(VendorConstants.ECUSTOMS_OUTPUT_FILE_DELIMITER);
        retval.append(vendorPersonName);
        retval.append(VendorConstants.ECUSTOMS_OUTPUT_FILE_DELIMITER);
        retval.append(vendorCompanyName);
        retval.append(VendorConstants.ECUSTOMS_OUTPUT_FILE_DELIMITER);
        retval.append(vendorAddress);
        retval.append(VendorConstants.ECUSTOMS_OUTPUT_FILE_DELIMITER);
        retval.append(vendorCountry);
        retval.append(VendorConstants.ECUSTOMS_OUTPUT_FILE_LINE_TERMINATOR);
        LOG.info("New Ecustoms record: " + retval.toString());
        return retval.toString();
    }

    /**
     * @param vendorDetail
     * @return vendor number formatted for security compliance output file
     */
    private String getAssembledVendorId(VendorDetail vendorDetail) {
        String vendorNumber = vendorDetail.getVendorNumber();
        String cleanedVendorNumber = getCleanedData(vendorNumber);
        return cleanedVendorNumber;
    }

    /**
     * @param vendorDetail
     * @return vendor name formatted for security compliance output file
     */
    private String getAssembledVendorPersonName(VendorDetail vendorDetail) {
        StringBuilder retval = new StringBuilder();
        String firstName = vendorDetail.getVendorFirstName();
        String lastName = vendorDetail.getVendorLastName();

        firstName = getCleanedData(firstName);
        lastName = getCleanedData(lastName);

        if (StringUtils.isBlank(firstName) && StringUtils.isBlank(lastName)) {
            String vendorName = vendorDetail.getVendorName();
            if (vendorName == null) {
                vendorName = CoreConstants.EMPTY_STRING;
            }
            int nameDelimiterPosition = vendorName.indexOf(VendorConstants.NAME_DELIM);
            if (nameDelimiterPosition > 0) {
                lastName = vendorName.substring(0, nameDelimiterPosition);
                firstName = vendorName.substring(nameDelimiterPosition + VendorConstants.NAME_DELIM.length(), vendorName.length());
            }
        }

        retval.append(firstName.trim());
        retval.append(" ");
        retval.append(lastName.trim());
        if (StringUtils.isBlank(retval.toString())) {
            return CoreConstants.EMPTY_STRING;
        }
        return retval.toString();
    }

    /**
     * @param vendorDetail
     * @return vendor company name formatted for security compliance output file
     */
    private String getVendorCompanyName(VendorDetail vendorDetail) {
        StringBuilder retval = new StringBuilder();
        String vendorName = vendorDetail.getVendorName();
        if (vendorName == null) {
            vendorName = CoreConstants.EMPTY_STRING;
        }
        if (vendorName.contains(VendorConstants.NAME_DELIM)) {
            vendorName = CoreConstants.EMPTY_STRING;
        }
        vendorName = getCleanedData(vendorName);

        retval.append(vendorName);
        return retval.toString();
    }

    /**
     * @param va
     * @return vendor address formatted for security compliance output file
     */
    private String getAssembledVendorAddress(VendorAddress va) {
        StringBuilder retval = new StringBuilder();

        String vendorCity = va.getVendorCityName();
        vendorCity = getCleanedData(vendorCity);

        String vendorState = va.getVendorStateCode();
        vendorState = getCleanedData(vendorState);

        retval.append(VendorConstants.ECUSTOMS_TOKEN_CITY);
        retval.append(vendorCity);
        retval.append(VendorConstants.ECUSTOMS_TOKEN_CITY);
        retval.append(VendorConstants.ECUSTOMS_TOKEN_STATE_OPEN);
        retval.append(vendorState);
        retval.append(VendorConstants.ECUSTOMS_TOKEN_STATE_CLOSE);
        return retval.toString();
    }

    /**
     * @param va
     * @return vendor country formatted for security compliance output file
     */
    private String getVendorCountry(VendorAddress va) {
        StringBuilder retval = new StringBuilder();

        String vendorCountry = va.getVendorCountryCode();
        vendorCountry = getCleanedData(vendorCountry);

        retval.append(vendorCountry);
        return retval.toString();
    }

    /**
     * This method pulls the last ecustoms run timestamp from the database. If not found it will default to ecustoms.last.run.date.default specified in the institutional-config file.
     *
     * @return
     */
    private Timestamp getLastRunDate() {
        java.sql.Timestamp retval = java.sql.Timestamp.valueOf(defaultLastRunDate);
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.BATCH_FILE_UPLOADS_BATCH_FILE_NAME, batchFileName);
        List<BatchFileUploads> uploads = (List<BatchFileUploads>) getBusinessObjectService().findMatchingOrderBy(BatchFileUploads.class, fieldValues, KFSPropertyConstants.BATCH_FILE_UPLOADS_FILE_PROCESS_TIMESTAMP, false);
        if ((uploads != null) && !uploads.isEmpty()) {
            LOG.debug("found " + uploads.size() + " ecustoms upload records");
            retval = uploads.get(0).getFileProcessTimestamp();
        }
        LOG.info("Last Ecustoms Job Run Date: " + retval);
        return retval;
    }

    /**
     * Updates the vendor detail extension export controls flag to true if currently false and add a note to indicate that this vendor was added to the ecustoms output file @param vendorDetail
     */
    private void updateVendor(VendorDetail vendorDetail) {
        VendorDetailExtension vde = getVendorDetailExtension(vendorDetail.getVendorHeaderGeneratedIdentifier().toString(), vendorDetail.getVendorDetailAssignedIdentifier().toString());
        if (vde == null) {
            vde = new VendorDetailExtension();
        }
        vde.setVendorHeaderGeneratedIdentifier(vendorDetail.getVendorHeaderGeneratedIdentifier());
        vde.setVendorDetailAssignedIdentifier(vendorDetail.getVendorDetailAssignedIdentifier());
        vde.setExportControlsFlag(true);
        LOG.trace("Extension for vendor " + vendorDetail.getVendorNumber() + " ready for saving.");
        getBusinessObjectService().save(vde);
        LOG.trace("Extension for vendor " + vendorDetail.getVendorNumber() + " saved.");
        Note note = createNote(vendorDetail);
        getBusinessObjectService().save(note);
    }

    private VendorDetailExtension getVendorDetailExtension(String generatedId, String assignedId) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, generatedId);
        fieldValues.put(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, assignedId);
        VendorDetailExtension retval = getBusinessObjectService().findByPrimaryKey(VendorDetailExtension.class, fieldValues);
        return retval;
    }

    /**
     * add a note to indicate that this vendor was added to the eCustoms file today
     * */
    private Note createNote(VendorDetail vendorDetail) {
        String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        Note note = new Note();
        note.setNoteText(VendorConstants.ECUSTOMS_SENT_TO_ECUSTOMS_NOTE_TEXT);
        note.setAuthorUniversalIdentifier(principalId);
        note.setRemoteObjectIdentifier(vendorDetail.getObjectId());
        note.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
        note.setNotePostedTimestampToCurrent();
        LOG.debug("Note (" + VendorConstants.ECUSTOMS_SENT_TO_ECUSTOMS_NOTE_TEXT + ") created for vendor " + vendorDetail.getVendorNumber() + ".");
        return note;
    }

    /**
     * This method saves the job run timestamp and transaction count to the database.
     */
    private void saveLastRunDate(Date jobRunDate, KualiInteger vendorCount) {
        BatchFileUploads batchFileUploads = new BatchFileUploads();
        batchFileUploads.setFileProcessTimestamp(new Timestamp(jobRunDate.getTime()));
        batchFileUploads.setBatchFileName(batchFileName);
        batchFileUploads.setBatchDate(batchFileUploads.getFileProcessTimestamp());
        batchFileUploads.setTransactionCount(vendorCount);
        batchFileUploads.setBatchTotalAmount(new KualiDecimal(0));
        batchFileUploads.setSubmiterUserId(GlobalVariables.getUserSession().getPerson().getPrincipalId());
        batchFileUploads.setBatchName(batchName);
        batchFileUploads.setVersionNumber(1L);
        LOG.info("Saving new lastRunDate: " + jobRunDate.toString());
        businessObjectService.save(batchFileUploads);
    }

}