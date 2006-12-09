<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="kul" tagdir="/WEB-INF/tags" %>
<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />
<kul:tab
    tabTitle="Pre-Encumbrance Details"
    defaultOpen="true"
    tabErrorKey="${Constants.EDIT_PRE_ENCUMBRANCE_ERRORS}"
    >
    <div class="tab-container" align=center>
    <div class="h2-container">
<h2>Pre-Encumbrance Details</h2>
</div>
        <table cellpadding=0 class="datatable"
               summary="view/edit pre-encumbrance specific fields">
            <tbody>
                
                <tr>
                    <kul:htmlAttributeHeaderCell
                        attributeEntry="${DataDictionary.KualiPreEncumbranceDocument.attributes.reversalDate}"
                        horizontal="true"
                        width="35%"
                        />
                    <td class="datacell-nowrap"><kul:htmlControlAttribute
                        attributeEntry="${DataDictionary.KualiPreEncumbranceDocument.attributes.reversalDate}"
                        datePicker="true"
                        property="document.reversalDate"
                        readOnly="${readOnly}"
                        readOnlyAlternateDisplay="${KualiForm.formattedReversalDate}"
                        /></td>
                </tr>
            </tbody>
        </table>
    </div>
</kul:tab>
