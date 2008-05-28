/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.rule.event;

import org.kuali.core.bo.Note;
import org.kuali.core.document.Document;

/**
 * An attributed version of the Add Note event.
 */
public class AttributedAddNoteEvent extends AttributedDocumentEventBase {
    private Note note;

    /**
     * Constructs an AddNoteEvent with the specified errorPathPrefix and document
     * 
     * @param document
     * @param errorPathPrefix
     */
    public AttributedAddNoteEvent(String errorPathPrefix, Document document, Note note) {
        super("creating add note event for document " + getDocumentId(document), errorPathPrefix, document);
        this.note = note;
    }

    /**
     * Constructs an AddNoteEvent with the given document
     * 
     * @param document
     */
    public AttributedAddNoteEvent(Document document, Note note) {
        this("", document, note);
    }

    /**
     * This method retrieves the note associated with this event.
     * 
     * @return
     */
    public Note getNote() {
        return note;
    }
}
