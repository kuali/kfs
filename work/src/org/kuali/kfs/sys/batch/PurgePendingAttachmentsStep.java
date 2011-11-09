/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.batch;

import java.util.Calendar;
import java.util.Date;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.service.AttachmentService;


/**
 * This class is the batch step used to delete pending attachments that have not yet been associated with a document. When
 * attachments are attached to docs in an "initiated" state, the attachment is not considered to be linked with the doc in the
 * persistence layer, and so it becomes a "pending" attachment (i.e. pending persistence). When docs are saved (or submitted, etc.),
 * pending attachments become permanently persisted to the document and are no longer pending. Pending attachments may have become
 * orphaned from the document (because the doc has not been saved), and so these orphaned attachments must be deleted. This job uses
 * the file's last modified time to determine which pending attachments should be deleted. If the modified time is older than the
 * SYSTEM parameter "pendingAssignmentMaxAge", then it will be deleted.
 * 
 * @see org.kuali.rice.krad.service.impl.AttachmentServiceImpl
 * @see KFSConstants.SystemGroupParameterNames#PURGE_PENDING_ATTACHMENTS_STEP_MAX_AGE
 */
public class PurgePendingAttachmentsStep extends AbstractStep {

    private AttachmentService attachmentService;

    /**
     * Deletes all pending attachments that are older than a configured time (see class description)
     * 
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        Calendar calendar = getDateTimeService().getCurrentCalendar();
        String maxAgeInSecondsStr = getParameterService().getParameterValueAsString(PurgePendingAttachmentsStep.class, KFSConstants.SystemGroupParameterNames.PURGE_PENDING_ATTACHMENTS_STEP_MAX_AGE);
        int maxAgeInSeconds = Integer.parseInt(maxAgeInSecondsStr);
        calendar.add(Calendar.SECOND, -maxAgeInSeconds);
        getAttachmentService().deletePendingAttachmentsModifiedBefore(calendar.getTimeInMillis());
        return true;
    }

    /**
     * Gets the attachmentService attribute.
     * 
     * @return Returns the attachmentService.
     */
    public AttachmentService getAttachmentService() {
        return attachmentService;
    }

    /**
     * Sets the attachmentService attribute value.
     * 
     * @param attachmentService The attachmentService to set.
     */
    public void setAttachmentService(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }
}
