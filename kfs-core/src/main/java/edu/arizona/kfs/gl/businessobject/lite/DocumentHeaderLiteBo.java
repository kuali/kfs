package edu.arizona.kfs.gl.businessobject.lite;

import java.sql.Timestamp;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to retrieve the data in the table for DocumentRouteHeaderValue objects, without retrieving any reference objects.
 *
 * @author Adam Kost <kosta@email.arizona.edu>
 */

public class DocumentHeaderLiteBo extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 7982878716717724903L;

    private String documentId;
    private String documentTypeId;
    private String docRouteStatus;
    private Integer docRouteLevel;
    private String appDocStatus;
    private Timestamp dateModified;
    private Timestamp createDate;
    private Timestamp approvedDate;
    private Timestamp finalizedDate;
    private Timestamp routeStatusDate;
    private Timestamp appDocStatusDate;
    private String docTitle;
    private String appDocId;
    private Integer docVersion;
    private String initiatorWorkflowId;
    private String routedByUserWorkflowId;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getDocRouteStatus() {
        return docRouteStatus;
    }

    public void setDocRouteStatus(String docRouteStatus) {
        this.docRouteStatus = docRouteStatus;
    }

    public Integer getDocRouteLevel() {
        return docRouteLevel;
    }

    public void setDocRouteLevel(Integer docRouteLevel) {
        this.docRouteLevel = docRouteLevel;
    }

    public String getAppDocStatus() {
        return appDocStatus;
    }

    public void setAppDocStatus(String appDocStatus) {
        this.appDocStatus = appDocStatus;
    }

    public Timestamp getDateModified() {
        return dateModified;
    }

    public void setDateModified(Timestamp dateModified) {
        this.dateModified = dateModified;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Timestamp approvedDate) {
        this.approvedDate = approvedDate;
    }

    public Timestamp getFinalizedDate() {
        return finalizedDate;
    }

    public void setFinalizedDate(Timestamp finalizedDate) {
        this.finalizedDate = finalizedDate;
    }

    public Timestamp getRouteStatusDate() {
        return routeStatusDate;
    }

    public void setRouteStatusDate(Timestamp routeStatusDate) {
        this.routeStatusDate = routeStatusDate;
    }

    public Timestamp getAppDocStatusDate() {
        return appDocStatusDate;
    }

    public void setAppDocStatusDate(Timestamp appDocStatusDate) {
        this.appDocStatusDate = appDocStatusDate;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getAppDocId() {
        return appDocId;
    }

    public void setAppDocId(String appDocId) {
        this.appDocId = appDocId;
    }

    public Integer getDocVersion() {
        return docVersion;
    }

    public void setDocVersion(Integer docVersion) {
        this.docVersion = docVersion;
    }

    public String getInitiatorWorkflowId() {
        return initiatorWorkflowId;
    }

    public void setInitiatorWorkflowId(String initiatorWorkflowId) {
        this.initiatorWorkflowId = initiatorWorkflowId;
    }

    public String getRoutedByUserWorkflowId() {
        return routedByUserWorkflowId;
    }

    public void setRoutedByUserWorkflowId(String routedByUserWorkflowId) {
        this.routedByUserWorkflowId = routedByUserWorkflowId;
    }

}
