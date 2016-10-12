package edu.arizona.kfs.gl.businessobject.lite;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to retrieve the data in the table for DocumentType objects, without retrieving any reference objects.
 *
 * @author Adam Kost <kosta@email.arizona.edu>
 */
public class DocumentTypeLiteBo extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 6412897610447225159L;

    private String documentTypeId;
    private String docTypeParentId;
    private String name;
    private Integer version;
    private Boolean active;
    private Boolean currentInd;
    private String label;
    private String previousVersionId;
    private String description;
    private String unresolvedDocHandlerUrl;
    private String postProcessorName;
    private String blanketApprovePolicy;
    private String routingVersion;
    private String actualNotificationFromAddress;
    private String actualApplicationId;
    private String customEmailStylesheet;
    private String documentTypeSecurityXml;
    private String blanketApproveWorkgroupId;
    private String reportingWorkgroupId;
    private String workgroupId;
    private String unresolvedHelpDefinitionUrl;
    private String unresolvedDocSearchHelpUrl;
    private String documentId;
    private String authorizer;

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getDocTypeParentId() {
        return docTypeParentId;
    }

    public void setDocTypeParentId(String docTypeParentId) {
        this.docTypeParentId = docTypeParentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getCurrentInd() {
        return currentInd;
    }

    public void setCurrentInd(Boolean currentInd) {
        this.currentInd = currentInd;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPreviousVersionId() {
        return previousVersionId;
    }

    public void setPreviousVersionId(String previousVersionId) {
        this.previousVersionId = previousVersionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnresolvedDocHandlerUrl() {
        return unresolvedDocHandlerUrl;
    }

    public void setUnresolvedDocHandlerUrl(String unresolvedDocHandlerUrl) {
        this.unresolvedDocHandlerUrl = unresolvedDocHandlerUrl;
    }

    public String getPostProcessorName() {
        return postProcessorName;
    }

    public void setPostProcessorName(String postProcessorName) {
        this.postProcessorName = postProcessorName;
    }

    public String getBlanketApprovePolicy() {
        return blanketApprovePolicy;
    }

    public void setBlanketApprovePolicy(String blanketApprovePolicy) {
        this.blanketApprovePolicy = blanketApprovePolicy;
    }

    public String getRoutingVersion() {
        return routingVersion;
    }

    public void setRoutingVersion(String routingVersion) {
        this.routingVersion = routingVersion;
    }

    public String getActualNotificationFromAddress() {
        return actualNotificationFromAddress;
    }

    public void setActualNotificationFromAddress(String actualNotificationFromAddress) {
        this.actualNotificationFromAddress = actualNotificationFromAddress;
    }

    public String getActualApplicationId() {
        return actualApplicationId;
    }

    public void setActualApplicationId(String actualApplicationId) {
        this.actualApplicationId = actualApplicationId;
    }

    public String getCustomEmailStylesheet() {
        return customEmailStylesheet;
    }

    public void setCustomEmailStylesheet(String customEmailStylesheet) {
        this.customEmailStylesheet = customEmailStylesheet;
    }

    public String getDocumentTypeSecurityXml() {
        return documentTypeSecurityXml;
    }

    public void setDocumentTypeSecurityXml(String documentTypeSecurityXml) {
        this.documentTypeSecurityXml = documentTypeSecurityXml;
    }

    public String getBlanketApproveWorkgroupId() {
        return blanketApproveWorkgroupId;
    }

    public void setBlanketApproveWorkgroupId(String blanketApproveWorkgroupId) {
        this.blanketApproveWorkgroupId = blanketApproveWorkgroupId;
    }

    public String getReportingWorkgroupId() {
        return reportingWorkgroupId;
    }

    public void setReportingWorkgroupId(String reportingWorkgroupId) {
        this.reportingWorkgroupId = reportingWorkgroupId;
    }

    public String getWorkgroupId() {
        return workgroupId;
    }

    public void setWorkgroupId(String workgroupId) {
        this.workgroupId = workgroupId;
    }

    public String getUnresolvedHelpDefinitionUrl() {
        return unresolvedHelpDefinitionUrl;
    }

    public void setUnresolvedHelpDefinitionUrl(String unresolvedHelpDefinitionUrl) {
        this.unresolvedHelpDefinitionUrl = unresolvedHelpDefinitionUrl;
    }

    public String getUnresolvedDocSearchHelpUrl() {
        return unresolvedDocSearchHelpUrl;
    }

    public void setUnresolvedDocSearchHelpUrl(String unresolvedDocSearchHelpUrl) {
        this.unresolvedDocSearchHelpUrl = unresolvedDocSearchHelpUrl;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(String authorizer) {
        this.authorizer = authorizer;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
