/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.web.struts.form;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.NoOpStringFormatter;
import org.kuali.rice.core.web.format.TimestampAMPMFormatter;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.node.RouteNodeInstance;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.datadictionary.HeaderNavigation;
import org.kuali.rice.kns.datadictionary.KNSDocumentEntry;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.derivedvaluesetter.DerivedValuesSetter;
import org.kuali.rice.krad.UserSessionUtils;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteWorkgroup;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.springframework.util.AutoPopulatingList;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * TODO we should not be referencing kew constants from this class and wedding ourselves to that workflow application This class is
 * the base action form for all documents.
 */
public abstract class KualiDocumentFormBase extends KualiForm implements Serializable {
    private static final long serialVersionUID = 916061016201941821L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiDocumentFormBase.class);

    private Document document;
    private String annotation = "";
    private String command;

    private String docId;
    private String docTypeName;

    private List<String> additionalScriptFiles;

    private AdHocRoutePerson newAdHocRoutePerson;
    private AdHocRouteWorkgroup newAdHocRouteWorkgroup;

    private Note newNote;
    
    //TODO: is this still needed? I think it's obsolete now
    private List boNotes;
    
    protected FormFile attachmentFile = new BlankFormFile();

    protected Map editingMode;
    protected Map documentActions;
    protected boolean suppressAllButtons;
    
    protected Map adHocActionRequestCodes;
    private boolean returnToActionList;

    // for session enhancement
    private String formKey;
    private String docNum;
    
    private List<ActionRequest> actionRequests;
    private List<String> selectedActionRequests;
    private String superUserAnnotation;
    
    
    /**
     * Stores the error map from previous requests, so that we can continue to display error messages displayed during a previous request
     */
    private MessageMap errorMapFromPreviousRequest;
    
	/***
     * @see KualiForm#addRequiredNonEditableProperties()
     */
    @Override
    public void addRequiredNonEditableProperties(){
    	super.addRequiredNonEditableProperties();
    	registerRequiredNonEditableProperty(KRADConstants.DOCUMENT_TYPE_NAME);
    	registerRequiredNonEditableProperty(KRADConstants.FORM_KEY);
    	registerRequiredNonEditableProperty(KRADConstants.NEW_NOTE_NOTE_TYPE_CODE);
    }

	/**
	 * @return the docNum
	 */
	public String getDocNum() {
		return this.docNum;
	}

	/**
	 * @param docNum
	 *            the docNum to set
	 */
	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}
    
    /**
     * no args constructor that just initializes things for us
     */
    @SuppressWarnings("unchecked")
	public KualiDocumentFormBase() {
        super();
        
        instantiateDocument();
        newNote = new Note();
        this.editingMode = new HashMap();
        //this.additionalScriptFiles = new AutoPopulatingList(String.class);
        this.additionalScriptFiles = new AutoPopulatingList<String>(String.class);

        // set the initial record for persons up
        newAdHocRoutePerson = new AdHocRoutePerson();

        // set the initial record for workgroups up
        newAdHocRouteWorkgroup = new AdHocRouteWorkgroup();

        // to make sure it posts back the correct time
        setFormatterType("document.documentHeader.note.finDocNotePostedDttmStamp", TimestampAMPMFormatter.class);
        setFormatterType("document.documentHeader.note.attachment.finDocNotePostedDttmStamp", TimestampAMPMFormatter.class);
        //TODO: Chris - Notes: remove the above and change the below from boNotes when notes are finished
        //overriding note formatter to make sure they post back the full timestamp
        setFormatterType("document.documentHeader.boNote.notePostedTimestamp",TimestampAMPMFormatter.class);
        setFormatterType("document.documentBusinessObject.boNote.notePostedTimestamp",TimestampAMPMFormatter.class);

        setFormatterType("editingMode", NoOpStringFormatter.class);
        setFormatterType("editableAccounts", NoOpStringFormatter.class);

        setDocumentActions(new HashMap());
        suppressAllButtons = false;
        
        initializeHeaderNavigationTabs();
    }

    /**
     * Setup workflow doc in the document.
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        WorkflowDocument workflowDocument = null;

        if (hasDocumentId()) {
            // populate workflowDocument in documentHeader, if needed
        	// KULRICE-4444 Obtain Document Header using the Workflow Service to minimize overhead
            try {
                workflowDocument = UserSessionUtils.getWorkflowDocument(GlobalVariables.getUserSession(), getDocument().getDocumentNumber());
                if ( workflowDocument == null)
         	 	{
                    // gets the workflow document from doc service, doc service will also set the workflow document in the
                    // user's session
                    Person person = GlobalVariables.getUserSession().getPerson();
                    if (ObjectUtils.isNull(person)) {
                        person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(KRADConstants.SYSTEM_USER);
                    }
         	 		workflowDocument = KRADServiceLocatorWeb.getWorkflowDocumentService().loadWorkflowDocument(getDocument().getDocumentNumber(), person);
         	 	 	UserSessionUtils.addWorkflowDocument(GlobalVariables.getUserSession(), workflowDocument);
         	 	 	if (workflowDocument == null)
         	 	 	{
         	 	 		throw new WorkflowException("Unable to retrieve workflow document # " + getDocument().getDocumentNumber() + " from workflow document service createWorkflowDocument");
         	 	 	}
         	 	 	else
         	 	 	{
         	 	 	LOG.debug("Retrieved workflow Document ID: " + workflowDocument.getDocumentId());
         	 	 	}
         	 	}

                getDocument().getDocumentHeader().setWorkflowDocument(workflowDocument);
            } catch (WorkflowException e) {
                LOG.warn("Error while instantiating workflowDoc", e);
                throw new RuntimeException("error populating documentHeader.workflowDocument", e);
            }
        } 
        if (workflowDocument != null) {
	        //Populate Document Header attributes
	        populateHeaderFields(workflowDocument);
        }
    }
    
    protected String getPersonInquiryUrlLink(Person user, String linkBody) {
        StringBuffer urlBuffer = new StringBuffer();
        
        if(user != null && StringUtils.isNotEmpty(linkBody) ) {
        	ModuleService moduleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(Person.class);
        	Map<String, String[]> parameters = new HashMap<String, String[]>();
        	parameters.put(KimConstants.AttributeConstants.PRINCIPAL_ID, new String[] { user.getPrincipalId() });
        	String inquiryUrl = moduleService.getExternalizableBusinessObjectInquiryUrl(Person.class, parameters);
            if(!StringUtils.equals(KimConstants.EntityTypes.SYSTEM, user.getEntityTypeCode())){
	            urlBuffer.append("<a href='");
	            urlBuffer.append(inquiryUrl);
	            urlBuffer.append("' ");
	            urlBuffer.append("target='_blank'");
	            urlBuffer.append("title='Person Inquiry'>");
	            urlBuffer.append(linkBody);
	            urlBuffer.append("</a>");
            } else{
            	urlBuffer.append(linkBody);
            }
        }
        
        return urlBuffer.toString();
    }
    
    protected String getDocumentHandlerUrl(String documentId) {
        Properties parameters = new Properties();
        parameters.put(KRADConstants.PARAMETER_DOC_ID, documentId);
        parameters.put(KRADConstants.PARAMETER_COMMAND, KRADConstants.METHOD_DISPLAY_DOC_SEARCH_VIEW);
        return UrlFactory.parameterizeUrl(
                KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                        KRADConstants.WORKFLOW_URL_KEY) + "/" + KRADConstants.DOC_HANDLER_ACTION, parameters);
    }
    
    protected String buildHtmlLink(String url, String linkBody) {
        StringBuffer urlBuffer = new StringBuffer();
        
        if(StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(linkBody) ) {
            urlBuffer.append("<a href='").append(url).append("'>").append(linkBody).append("</a>");
        }
        
        return urlBuffer.toString();
    }
    
    /**
	 * This method is used to populate the list of header field objects (see {@link KualiForm#getDocInfo()}) displayed on
	 * the Kuali document form display pages.
	 * 
	 * @param workflowDocument - the workflow document of the document being displayed (null is allowed)
	 */
	public void populateHeaderFields(WorkflowDocument workflowDocument) {
		getDocInfo().clear();
		getDocInfo().addAll(getStandardHeaderFields(workflowDocument));
	}

	/**
	 * This method returns a list of {@link HeaderField} objects that are used by default on Kuali document display pages. To
	 * use this list and override an individual {@link HeaderField} object the id constants from
	 * {@link KRADConstants.DocumentFormHeaderFieldIds} can be used to identify items from the list.
	 * 
	 * @param workflowDocument - the workflow document of the document being displayed (null is allowed)
	 * @return a list of the standard fields displayed by default for all Kuali documents
	 */
    protected List<HeaderField> getStandardHeaderFields(WorkflowDocument workflowDocument) {
    	List<HeaderField> headerFields = new ArrayList<HeaderField>();
    	setNumColumns(2);
    	// check for a document template number as that will dictate column numbering
    	HeaderField docTemplateNumber = null;
        if ((ObjectUtils.isNotNull(getDocument())) && (ObjectUtils.isNotNull(getDocument().getDocumentHeader())) && (StringUtils.isNotBlank(getDocument().getDocumentHeader().getDocumentTemplateNumber()))) {
			String templateDocumentNumber = getDocument().getDocumentHeader().getDocumentTemplateNumber();
			docTemplateNumber = new HeaderField(KRADConstants.DocumentFormHeaderFieldIds.DOCUMENT_TEMPLATE_NUMBER, "DataDictionary.DocumentHeader.attributes.documentTemplateNumber",
					templateDocumentNumber,	buildHtmlLink(getDocumentHandlerUrl(templateDocumentNumber), templateDocumentNumber));
		}
        //Document Number    	
        HeaderField docNumber = new HeaderField("DataDictionary.DocumentHeader.attributes.documentNumber", workflowDocument != null? getDocument().getDocumentNumber() : null);
        docNumber.setId(KRADConstants.DocumentFormHeaderFieldIds.DOCUMENT_NUMBER);
        HeaderField docStatus = new HeaderField("DataDictionary.AttributeReferenceDummy.attributes.workflowDocumentStatus", workflowDocument != null? workflowDocument.getStatus().getLabel() : null);
        docStatus.setId(KRADConstants.DocumentFormHeaderFieldIds.DOCUMENT_WORKFLOW_STATUS);
        String initiatorNetworkId = null;
        Person user = null;
    	if (workflowDocument != null) {
       		if (getInitiator() == null) {
    			LOG.warn("User Not Found while attempting to build inquiry link for document header fields");
    		} else {
    			user = getInitiator();
    			initiatorNetworkId = getInitiator().getPrincipalName();
    		}
    	}
        String inquiryUrl = getPersonInquiryUrlLink(user, workflowDocument != null? initiatorNetworkId:null);

        HeaderField docInitiator = new HeaderField(KRADConstants.DocumentFormHeaderFieldIds.DOCUMENT_INITIATOR, "DataDictionary.AttributeReferenceDummy.attributes.initiatorNetworkId",
        workflowDocument != null? initiatorNetworkId : null, workflowDocument != null? inquiryUrl : null);
        
        String createDateStr = null;
        if(workflowDocument != null && workflowDocument.getDateCreated() != null) {
            createDateStr = CoreApiServiceLocator.getDateTimeService().toString(workflowDocument.getDateCreated().toDate(), "hh:mm a MM/dd/yyyy");
        }
        
        HeaderField docCreateDate = new HeaderField("DataDictionary.AttributeReferenceDummy.attributes.createDate", createDateStr);
        docCreateDate.setId(KRADConstants.DocumentFormHeaderFieldIds.DOCUMENT_CREATE_DATE);
        if (ObjectUtils.isNotNull(docTemplateNumber)) {
        	setNumColumns(3);
        }
        
        headerFields.add(docNumber);
        headerFields.add(docStatus);
        if (ObjectUtils.isNotNull(docTemplateNumber)) {
        	headerFields.add(docTemplateNumber);
        }
        headerFields.add(docInitiator);
        headerFields.add(docCreateDate);
        if (ObjectUtils.isNotNull(docTemplateNumber)) {
        	// adding an empty field so implementors do not have to worry about additional fields being put on the wrong row
        	headerFields.add(HeaderField.EMPTY_FIELD);
        }
    	return headerFields;
    }    

    /**
     * @see org.apache.struts.action.ActionForm#validate(ActionMapping,
     *      HttpServletRequest)
     */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        // check that annotation does not exceed 2000 characters
        setAnnotation(StringUtils.stripToNull(getAnnotation()));
        int diff = StringUtils.defaultString(getAnnotation()).length() - KRADConstants.DOCUMENT_ANNOTATION_MAX_LENGTH;
        if (diff > 0) {
            GlobalVariables.getMessageMap().putError("annotation", RiceKeyConstants.ERROR_DOCUMENT_ANNOTATION_MAX_LENGTH_EXCEEDED, new String[] { Integer.toString(KRADConstants.DOCUMENT_ANNOTATION_MAX_LENGTH), Integer.toString(diff) });
        }
        return super.validate(mapping, request);
    }

    /**
     * @return true if this document was properly initialized with a DocumentHeader and related KualiWorkflowDocument
     */
    final public boolean isFormDocumentInitialized() {
        boolean initialized = false;

        if (document != null) {
            if (document.getDocumentHeader() != null) {
                initialized = document.getDocumentHeader().hasWorkflowDocument();
            }
        }

        return initialized;
    }


    /**
     * @return Map of editingModes for this document, as set during the most recent call to
     *         populate(javax.servlet.http.HttpServletRequest)
     */
    @SuppressWarnings("unchecked")
	public Map getEditingMode() {
        return editingMode;
    }

    /**
     * Set editingMode for this document
     */
    @SuppressWarnings("unchecked")
	public void setEditingMode(Map editingMode) {
        this.editingMode = editingMode;
    }
    
    /**
	 * @return the documentActions
	 */
	@SuppressWarnings("unchecked")
	public Map getDocumentActions() {
		return this.documentActions;
	}

	/**
	 * @param documentActions the documentActions to set
	 */
	@SuppressWarnings("unchecked")
	public void setDocumentActions(Map documentActions) {
		this.documentActions = documentActions;
	}
	
	

	/**
	 * @param adHocActionRequestCodes the adHocActionRequestCodes to set
	 */
	@SuppressWarnings("unchecked")
	public void setAdHocActionRequestCodes(Map adHocActionRequestCodes) {
		this.adHocActionRequestCodes = adHocActionRequestCodes;
	}

	/**
     * @return a map of the possible action request codes that takes into account the users context on the document
     */
    @SuppressWarnings("unchecked")
	public Map getAdHocActionRequestCodes() {
        //Map adHocActionRequestCodes = new HashMap();
        //KRADServiceLocatorInternal.getDocumentHelperService()
        /*if (getWorkflowDocument() != null) {
            if (getWorkflowDocument().isFYIRequested()) {
                adHocActionRequestCodes.put(KewApiConstants.ACTION_REQUEST_FYI_REQ, KewApiConstants.ACTION_REQUEST_FYI_REQ_LABEL);
            }
            else if (getWorkflowDocument().isAcknowledgeRequested()) {
                adHocActionRequestCodes.put(KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
                adHocActionRequestCodes.put(KewApiConstants.ACTION_REQUEST_FYI_REQ, KewApiConstants.ACTION_REQUEST_FYI_REQ_LABEL);
            }
            else if (getWorkflowDocument().isApprovalRequested() || getWorkflowDocument().isCompletionRequested() || getWorkflowDocument().stateIsInitiated() || getWorkflowDocument().stateIsSaved()) {
                adHocActionRequestCodes.put(KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
                adHocActionRequestCodes.put(KewApiConstants.ACTION_REQUEST_FYI_REQ, KewApiConstants.ACTION_REQUEST_FYI_REQ_LABEL);
                adHocActionRequestCodes.put(KewApiConstants.ACTION_REQUEST_APPROVE_REQ, KewApiConstants.ACTION_REQUEST_APPROVE_REQ_LABEL);
            }
        }*/
        return adHocActionRequestCodes;
    }


    /**
     * @return the list of ad hoc routing persons
     */
    public List<AdHocRoutePerson> getAdHocRoutePersons() {
        return document.getAdHocRoutePersons();
    }


    /**
     * @return attachmentFile
     */
    public FormFile getAttachmentFile() {
        return attachmentFile;
    }

    /**
     * @param attachmentFile The attachmentFile to set.
     */
    public void setAttachmentFile(FormFile attachmentFile) {
        this.attachmentFile = attachmentFile;
    }


    /**
     * set the ad hoc routing persons list
     *
     * @param adHocRouteRecipients
     */
    public void setAdHocRoutePersons(List<AdHocRoutePerson> adHocRouteRecipients) {
        document.setAdHocRoutePersons(adHocRouteRecipients);
    }

    /**
     * get the ad hoc routing workgroup requests
     *
     * @return
     */
    public List<AdHocRouteWorkgroup> getAdHocRouteWorkgroups() {
        return document.getAdHocRouteWorkgroups();
    }

    /**
     * set the ad hoc routing workgroup requests
     *
     * @param adHocRouteWorkgroups
     */
    public void setAdHocRouteWorkgroups(List<AdHocRouteWorkgroup> adHocRouteWorkgroups) {
        document.setAdHocRouteWorkgroups(adHocRouteWorkgroups);
    }

    /**
     * Special getter based on index to work with multi rows for ad hoc routing to persons struts page
     *
     * @param index
     * @return
     */
    public AdHocRoutePerson getAdHocRoutePerson(int index) {
        while (getAdHocRoutePersons().size() <= index) {
            getAdHocRoutePersons().add(new AdHocRoutePerson());
        }
        return getAdHocRoutePersons().get(index);
    }

    /**
     * Special getter based on index to work with multi rows for ad hoc routing to workgroups struts page
     *
     * @param index
     * @return
     */
    public AdHocRouteWorkgroup getAdHocRouteWorkgroup(int index) {
        while (getAdHocRouteWorkgroups().size() <= index) {
            getAdHocRouteWorkgroups().add(new AdHocRouteWorkgroup());
        }
        return getAdHocRouteWorkgroups().get(index);
    }

    /**
     * @return the new ad hoc route person object
     */
    public AdHocRoutePerson getNewAdHocRoutePerson() {
        return newAdHocRoutePerson;
    }

    /**
     * set the new ad hoc route person object
     *
     * @param newAdHocRoutePerson
     */
    public void setNewAdHocRoutePerson(AdHocRoutePerson newAdHocRoutePerson) {
        this.newAdHocRoutePerson = newAdHocRoutePerson;
    }

    /**
     * @return the new ad hoc route workgroup object
     */
    public AdHocRouteWorkgroup getNewAdHocRouteWorkgroup() {
        return newAdHocRouteWorkgroup;
    }

    /**
     * set the new ad hoc route workgroup object
     *
     * @param newAdHocRouteWorkgroup
     */
    public void setNewAdHocRouteWorkgroup(AdHocRouteWorkgroup newAdHocRouteWorkgroup) {
        this.newAdHocRouteWorkgroup = newAdHocRouteWorkgroup;
    }

    /**
     * @return Returns the Document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @param document
     */
    public void setDocument(Document document) {
        this.document = document;
        if(document != null && StringUtils.isNotEmpty(document.getDocumentNumber())) {
            populateHeaderFields(document.getDocumentHeader().getWorkflowDocument());
        }
    }

    /**
     * @return WorkflowDocument for this form's document
     */
    public WorkflowDocument getWorkflowDocument() {
        return getDocument().getDocumentHeader().getWorkflowDocument();
    }
    
    /**
	 *  Null-safe check to see if the workflow document object exists before attempting to retrieve it.
     *  (Which, if called, will throw an exception.)
	 */
    public boolean isHasWorkflowDocument() {
    	if ( getDocument() == null || getDocument().getDocumentHeader() == null ) {
    		return false;
    	}
    	return getDocument().getDocumentHeader().hasWorkflowDocument();
    }

    /**
     * TODO rk implemented to account for caps coming from kuali user service from workflow
     */
    public boolean isUserDocumentInitiator() {
        if (getWorkflowDocument() != null) {
            return getWorkflowDocument().getInitiatorPrincipalId().equalsIgnoreCase(
            		GlobalVariables.getUserSession().getPrincipalId());
        }
        return false;
    }

    public Person getInitiator() {
    	String initiatorPrincipalId = getWorkflowDocument().getInitiatorPrincipalId();
    	return KimApiServiceLocator.getPersonService().getPerson(initiatorPrincipalId);
    }

    /**
     * @return true if the workflowDocument associated with this form is currently enroute
     */
    public boolean isDocumentEnRoute() {
        return getWorkflowDocument().isEnroute();
    }

    /**
     * @param annotation The annotation to set.
     */
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    /**
     * @return Returns the annotation.
     */
    public String getAnnotation() {
        return annotation;
    }

    /**
     * @return returns the command that was passed from workflow
     */
    public String getCommand() {
        return command;
    }

    /**
     * setter for the command that was passed from workflow on the url
     *
     * @param command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * @return returns the docId that was passed from workflow on the url
     */
    public String getDocId() {
        return docId;
    }

    /**
     * setter for the docId that was passed from workflow on the url
     *
     * @param docId
     */
    public void setDocId(String docId) {
        this.docId = docId;
    }

    /**
     * getter for the docTypeName that was passed from workflow on the url
     *
     * @return
     */
    public String getDocTypeName() {
        return docTypeName;
    }

    /**
     * setter for the docTypeName that was passed from workflow on the url
     *
     * @param docTypeName
     */
    public void setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
    }

    /**
     * getter for convenience that will return the initiators network id
     *
     * @return
     */
    public String getInitiatorNetworkId() {
        return this.getWorkflowDocument().getInitiatorPrincipalId();
    }

    /**
     * Gets the suppressAllButtons attribute.
     *
     * @return Returns the suppressAllButtons.
     */
    public final boolean isSuppressAllButtons() {
        return suppressAllButtons;
    }

    /**
     * Sets the suppressAllButtons attribute value.
     *
     * @param suppressAllButtons The suppressAllButtons to set.
     */
    public final void setSuppressAllButtons(boolean suppressAllButtons) {
        this.suppressAllButtons = suppressAllButtons;
    }

    /**
     * @return true if this form's getDocument() method returns a Document, and if that Document's getDocumentHeaderId method
     *         returns a non-null
     */
    public boolean hasDocumentId() {
        boolean hasDocId = false;

        Document d = getDocument();
        if (d != null) {
            String docHeaderId = d.getDocumentNumber();

            hasDocId = StringUtils.isNotBlank(docHeaderId);
        }

        return hasDocId;
    }

    /**
     * Sets flag indicating whether upon completion of approve, blanketApprove, cancel, or disapprove, the user should be returned
     * to the actionList instead of to the portal
     *
     * @param returnToActionList
     */
    public void setReturnToActionList(boolean returnToActionList) {
        this.returnToActionList = returnToActionList;
    }

    public boolean isReturnToActionList() {
        return returnToActionList;
    }

    public List<String> getAdditionalScriptFiles() {
        return additionalScriptFiles;
    }

    public void setAdditionalScriptFiles(List<String> additionalScriptFiles) {
        this.additionalScriptFiles = additionalScriptFiles;
    }

    public void setAdditionalScriptFile( int index, String scriptFile ) {
        additionalScriptFiles.set( index, scriptFile );
	}

    public String getAdditionalScriptFile( int index ) {
        return additionalScriptFiles.get( index );
    }

    public Note getNewNote() {
        return newNote;
    }

    public void setNewNote(Note newNote) {
        this.newNote = newNote;
    }

    /**
     * Gets the boNotes attribute. 
     * @return Returns the boNotes.
     */
    @SuppressWarnings("unchecked")
	public List getBoNotes() {
        return boNotes;
    }

    /**
     * Sets the boNotes attribute value.
     * @param boNotes The boNotes to set.
     */
    @SuppressWarnings("unchecked")
	public void setBoNotes(List boNotes) {
        this.boNotes = boNotes;
    }

    public String getFormKey() {
        return this.formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    /* Reset method
     * This is initially created for session document implementation
     * @param mapping
     * @param request
     */
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    	super.reset(mapping, request);
        this.setMethodToCall(null);
        this.setRefreshCaller(null);
        this.setAnchor(null);
        this.setCurrentTabIndex(0);
        this.setSelectedActionRequests(new ArrayList<String>());
    }

    
    /**
     * Adds the attachment file size to the list of max file sizes.
     * 
     * @see org.kuali.rice.krad.web.struts.pojo.PojoFormBase#customInitMaxUploadSizes()
     */
    @Override
    protected void customInitMaxUploadSizes() {
        super.customInitMaxUploadSizes();
        String attachmentSize = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KRADConstants.KNS_NAMESPACE, KRADConstants.DetailTypes.DOCUMENT_DETAIL_TYPE, KRADConstants.ATTACHMENT_MAX_FILE_SIZE_PARM_NM);
        if (StringUtils.isNotBlank(attachmentSize)) {
            addMaxUploadSize(attachmentSize);
        }
    }

    
    
	/**
	 * This overridden method ...
	 * IMPORTANT: any overrides of this method must ensure that nothing in the HTTP request will be used to determine whether document is in session 
	 * 
	 * @see org.kuali.rice.krad.web.struts.pojo.PojoFormBase#shouldPropertyBePopulatedInForm(String, HttpServletRequest)
	 */
	@Override
	public boolean shouldPropertyBePopulatedInForm(String requestParameterName, HttpServletRequest request) {
		for ( String prefix : KRADConstants.ALWAYS_VALID_PARAMETER_PREFIXES ) {
			if (requestParameterName.startsWith(prefix)) {
				return true;
			}
		}

		if (StringUtils.equalsIgnoreCase(getMethodToCall(), KRADConstants.DOC_HANDLER_METHOD)) {
			return true;
		}
		if (WebUtils.isDocumentSession(getDocument(), this)) {
			return isPropertyEditable(requestParameterName) || isPropertyNonEditableButRequired(requestParameterName);
		}
		return true;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see KualiForm#shouldMethodToCallParameterBeUsed(String, String, HttpServletRequest)
	 */
	@Override
	public boolean shouldMethodToCallParameterBeUsed(
			String methodToCallParameterName,
			String methodToCallParameterValue, HttpServletRequest request) {
		if (StringUtils.equals(methodToCallParameterName, KRADConstants.DISPATCH_REQUEST_PARAMETER) &&
				StringUtils.equals(methodToCallParameterValue, KRADConstants.DOC_HANDLER_METHOD)) {
			return true;
		}
		return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName,
				methodToCallParameterValue, request);
	}
	
	public MessageMap getMessageMapFromPreviousRequest() {
		return this.errorMapFromPreviousRequest;
	}
	
	public void setMessageMapFromPreviousRequest(MessageMap errorMapFromPreviousRequest) {
		this.errorMapFromPreviousRequest = errorMapFromPreviousRequest;
	}
	
	@Override
	public void setDerivedValuesOnForm(HttpServletRequest request) {
		super.setDerivedValuesOnForm(request);

		String docTypeName = getDocTypeName();
		if (StringUtils.isNotBlank(docTypeName)) {
			DataDictionary dataDictionary = KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary();

            Class<? extends DerivedValuesSetter> derivedValuesSetterClass = null;
            KNSDocumentEntry documentEntry = (KNSDocumentEntry) dataDictionary.getDocumentEntry(docTypeName);
            derivedValuesSetterClass = (documentEntry).getDerivedValuesSetterClass();

			if (derivedValuesSetterClass != null) {
				DerivedValuesSetter derivedValuesSetter = null;
				try {
					derivedValuesSetter = derivedValuesSetterClass.newInstance();
				}

				catch (Exception e) {
					LOG.error("Unable to instantiate class " + derivedValuesSetterClass.getName(), e);
					throw new RuntimeException("Unable to instantiate class " + derivedValuesSetterClass.getName(), e);
				}
				derivedValuesSetter.setDerivedValues(this, request);
			}
		}
	}
	
	protected String getDefaultDocumentTypeName() {
		return "";
	}
	
	/** will instatiate a new document setting it on the form if {@link KualiDocumentFormBase#getDefaultDocumentTypeName()} is overriden to return a valid value. */
	protected void instantiateDocument() {
		if (document == null && StringUtils.isNotBlank(getDefaultDocumentTypeName())) {
			Class<? extends Document> documentClass = getDocumentClass();
			try {
				Document document = documentClass.newInstance();
				setDocument(document);
			} catch (Exception e) {
				LOG.error("Unable to instantiate document class " + documentClass.getName() + " document type " + getDefaultDocumentTypeName());
				throw new RuntimeException(e);
			}
		}
	}
	
	/** gets the document class from the datadictionary if {@link KualiDocumentFormBase#getDefaultDocumentTypeName()} is overriden to return a valid value otherwise behavior is nondeterministic. */
	private Class<? extends Document> getDocumentClass() {
		return KRADServiceLocatorWeb.getDataDictionaryService().getDocumentClassByTypeName(getDefaultDocumentTypeName());
	}
	
	/**initializes the header tabs from what is defined in the datadictionary if {@link KualiDocumentFormBase#getDefaultDocumentTypeName()} is overriden to return a valid value. */
    protected void initializeHeaderNavigationTabs() {
    	if (StringUtils.isNotBlank(getDefaultDocumentTypeName())) {
    		final KNSDocumentEntry docEntry = (KNSDocumentEntry) KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDocumentEntry(getDocumentClass().getName());
    		final List<HeaderNavigation> navList = docEntry.getHeaderNavigationList();
    		final HeaderNavigation[] list = new HeaderNavigation[navList.size()];
    		super.setHeaderNavigationTabs(navList.toArray(list));
    	}
    }
    
    public List<ActionRequest> getActionRequests() {
		return actionRequests;
	}

	public void setActionRequests(List<ActionRequest> actionRequests) {
		this.actionRequests = actionRequests;
	}

	public List<String> getSelectedActionRequests() {
		return selectedActionRequests;
	}

	public void setSelectedActionRequests(List<String> selectedActionRequests) {
		this.selectedActionRequests = selectedActionRequests;
	}

    public List<ActionRequest> getActionRequestsRequiringApproval() {
        List<ActionRequest> actionRequests = getActionRequests();
        List<ActionRequest> actionRequestsApprove = new ArrayList<ActionRequest>();;

        for (ActionRequest actionRequest: actionRequests) {
            if  ((StringUtils.equals(actionRequest.getActionRequested().getCode(), ActionRequestType.APPROVE.getCode())) ||
                    (StringUtils.equals(actionRequest.getActionRequested().getCode(), ActionRequestType.COMPLETE.getCode()))) {
                actionRequestsApprove.add(actionRequest);
            }
        }
        return actionRequestsApprove;
    }

	public String getSuperUserAnnotation() {
		return superUserAnnotation;
	}

	public void setSuperUserAnnotation(String superUserAnnotation) {
		this.superUserAnnotation = superUserAnnotation;
	}

    public boolean isSuperUserActionAvaliable() {
        List<ActionRequest> actionRequests = getActionRequestsRequiringApproval();
        boolean hasSingleActionToTake = false;
        boolean canSuperUserApprove = false;
        boolean canSuperUserDisapprove = false;

        hasSingleActionToTake =  ( isSuperUserApproveSingleActionRequestAuthorized() &&
                isStateAllowsApproveSingleActionRequest() &&
                !actionRequests.isEmpty());
        if (!hasSingleActionToTake) {
            canSuperUserApprove = (isSuperUserApproveDocumentAuthorized() && isStateAllowsApproveOrDisapprove());
        }
        if (!canSuperUserApprove) {
            canSuperUserDisapprove = (isSuperUserDisapproveDocumentAuthorized() && isStateAllowsApproveOrDisapprove());
        }

        return (hasSingleActionToTake || canSuperUserApprove || canSuperUserDisapprove) ;
    }

    public boolean isSuperUserApproveSingleActionRequestAuthorized() {
        String principalId =  GlobalVariables.getUserSession().getPrincipalId();
        String docId = this.getDocId();
        DocumentType documentType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(docTypeName);
        String docTypeId = null;
        if (documentType != null) {
            docTypeId = documentType.getId();
        }
        if ( KewApiServiceLocator.getDocumentTypeService().isSuperUserForDocumentTypeId(principalId, docTypeId) ) {
            return true;
        }
        List<RouteNodeInstance> routeNodeInstances= KewApiServiceLocator.getWorkflowDocumentService().getRouteNodeInstances(docId);
        String documentStatus =  KewApiServiceLocator.getWorkflowDocumentService().getDocumentStatus(docId).getCode();
        return KewApiServiceLocator.getDocumentTypeService().canSuperUserApproveSingleActionRequest(
                principalId, getDocTypeName(), routeNodeInstances, documentStatus);
    }
	
	public boolean isSuperUserApproveDocumentAuthorized() {
        String principalId =  GlobalVariables.getUserSession().getPrincipalId();
        String docId = this.getDocId();
        DocumentType documentType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(docTypeName);
        String docTypeId = null;
        if (documentType != null) {
            docTypeId = documentType.getId();
        }
        if ( KewApiServiceLocator.getDocumentTypeService().isSuperUserForDocumentTypeId(principalId, docTypeId) ) {
            return true;
        }
	    List<RouteNodeInstance> routeNodeInstances= KewApiServiceLocator.getWorkflowDocumentService().getRouteNodeInstances(docId);
        String documentStatus =  KewApiServiceLocator.getWorkflowDocumentService().getDocumentStatus(docId).getCode();
        return KewApiServiceLocator.getDocumentTypeService().canSuperUserApproveDocument(
                    principalId, this.getDocTypeName(), routeNodeInstances, documentStatus);
	}
	
	public boolean isSuperUserDisapproveDocumentAuthorized() {
        String principalId =  GlobalVariables.getUserSession().getPrincipalId();
        String docId = this.getDocId();
        DocumentType documentType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(docTypeName);
        String docTypeId = null;
        if (documentType != null) {
            docTypeId = documentType.getId();
        }
        if ( KewApiServiceLocator.getDocumentTypeService().isSuperUserForDocumentTypeId(principalId, docTypeId) ) {
            return true;
        }
	    List<RouteNodeInstance> routeNodeInstances= KewApiServiceLocator.getWorkflowDocumentService().getRouteNodeInstances(docId);
        String documentStatus =  KewApiServiceLocator.getWorkflowDocumentService().getDocumentStatus(docId).getCode();
        return KewApiServiceLocator.getDocumentTypeService().canSuperUserDisapproveDocument(
            principalId, this.getDocTypeName(), routeNodeInstances, documentStatus);
   	}

    public boolean isSuperUserAuthorized() {
        String docId = this.getDocId();
        if (StringUtils.isBlank(docId) || ObjectUtils.isNull(docTypeName)) {
            return false;
        }

        DocumentType documentType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(docTypeName);
        String docTypeId = null;
        if (documentType != null) {
            docTypeId = documentType.getId();
        }
        String principalId =  GlobalVariables.getUserSession().getPrincipalId();
        if ( KewApiServiceLocator.getDocumentTypeService().isSuperUserForDocumentTypeId(principalId, docTypeId) ) {
            return true;
        }
        List<RouteNodeInstance> routeNodeInstances= KewApiServiceLocator.getWorkflowDocumentService().getRouteNodeInstances(
                docId);
        String documentStatus =  KewApiServiceLocator.getWorkflowDocumentService().getDocumentStatus(docId).getCode();
        return ((KewApiServiceLocator.getDocumentTypeService().canSuperUserApproveSingleActionRequest(
                    principalId, this.getDocTypeName(), routeNodeInstances, documentStatus)) ||
                (KewApiServiceLocator.getDocumentTypeService().canSuperUserApproveDocument(
                    principalId, this.getDocTypeName(), routeNodeInstances, documentStatus)) ||
                (KewApiServiceLocator.getDocumentTypeService().canSuperUserDisapproveDocument (
                    principalId, this.getDocTypeName(), routeNodeInstances, documentStatus))) ;
    }
	
    public boolean isStateAllowsApproveOrDisapprove() {
        if(this.getDocument().getDocumentHeader().hasWorkflowDocument()) {
            DocumentStatus status = null;
            WorkflowDocument document = WorkflowDocumentFactory.loadDocument(GlobalVariables.getUserSession().getPrincipalId(),
                this.getDocument().getDocumentHeader().getWorkflowDocument().getDocumentId());
            if (ObjectUtils.isNotNull(document)) {
                status = document.getStatus();
            } else {
                status = this.getDocument().getDocumentHeader().getWorkflowDocument().getStatus();
            }
            return !(isStateProcessedOrDisapproved(status) ||
                     isStateInitiatedFinalCancelled(status) ||
                     StringUtils.equals(status.getCode(), DocumentStatus.SAVED.getCode()));
        } else {
            return false;
        }
    }

    public boolean isStateAllowsApproveSingleActionRequest() {
        if(this.getDocument().getDocumentHeader().hasWorkflowDocument()) {
            DocumentStatus status = null;
            WorkflowDocument document = WorkflowDocumentFactory.loadDocument(GlobalVariables.getUserSession().getPrincipalId(),
                    this.getDocument().getDocumentHeader().getWorkflowDocument().getDocumentId());
            if (ObjectUtils.isNotNull(document)) {
                status = document.getStatus();
            } else {
                status = this.getDocument().getDocumentHeader().getWorkflowDocument().getStatus();
            }
            return !(isStateInitiatedFinalCancelled(status));
        } else {
            return false;
        }
    }

    public boolean isStateProcessedOrDisapproved(DocumentStatus status) {
        return (StringUtils.equals(status.getCode(), DocumentStatus.PROCESSED.getCode()) ||
                StringUtils.equals(status.getCode(), DocumentStatus.DISAPPROVED.getCode()));
    }

    public boolean isStateInitiatedFinalCancelled(DocumentStatus status) {
        return (StringUtils.equals(status.getCode(), DocumentStatus.INITIATED.getCode()) ||
                StringUtils.equals(status.getCode(), DocumentStatus.FINAL.getCode()) ||
                StringUtils.equals(status.getCode(), DocumentStatus.CANCELED.getCode()));
    }
}
