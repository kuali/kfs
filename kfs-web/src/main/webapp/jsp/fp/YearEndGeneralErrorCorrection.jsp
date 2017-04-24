<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:set var="EntryAttributes" value="${DataDictionary.Entry.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="glEntryImporting" value="${!readOnly && KualiForm.editingMode['glEntryImporting']}"/>

<kul:documentPage showDocumentInfo="true"
	documentTypeName="YearEndGeneralErrorCorrectionDocument"
	htmlFormAction="financialYearEndGeneralErrorCorrection"
	renderMultipart="true" showTabButtons="true">
	
	<sys:documentOverview editingMode="${KualiForm.editingMode}" />

    <c:if test="${glEntryImporting}">
    <kul:tab tabTitle="YE GL Entry Importing" defaultOpen="true" tabErrorKey="universityFiscalYear,glDocId">
        <div class="tab-container" align=center>
        <h3>GL Entry Importing</h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="GL Entry Importing">
            <tr>
                <kul:htmlAttributeHeaderCell attributeEntry="${EntryAttributes.universityFiscalYear}" horizontal="true" width="35%"  labelFor="universityFiscalYear" forceRequired="true" useShortLabel="false" />
                <td class="datacell-nowrap"><kul:htmlControlAttribute attributeEntry="${EntryAttributes.universityFiscalYear}" property="universityFiscalYear" forceRequired="true" readOnly="${readOnly}" /></td>
            </tr>
            <tr>
                <kul:htmlAttributeHeaderCell attributeEntry="${EntryAttributes.documentNumber}" horizontal="true" width="35%"  labelFor="glDocId" forceRequired="true" useShortLabel="false" />
                <td class="datacell-nowrap"><kul:htmlControlAttribute attributeEntry="${EntryAttributes.documentNumber}" property="glDocId" forceRequired="true" readOnly="${readOnly}" /></td>
            </tr>

             <tr>
                <td height="30" class="infoline">&nbsp;</td>
                <td height="30" class="infoline">
                    <c:if test="${!readOnly}">
                        <input type="hidden" name="universityFiscalPeriodCodeLookupOverride" value="${KualiForm.universityFiscalPeriodCodeLookupOverride}" />
                        <gl:gecEntryLookup
                            boClassName="org.kuali.kfs.gl.businessobject.Entry"
                            actionPath="yegecEntryLookup.do"
                            lookupParameters="universityFiscalYear:universityFiscalYear,glDocId:documentNumber,universityFiscalPeriodCodeLookupOverride:universityFiscalPeriodCode"
                            tabindexOverride="KualiForm.currentTabIndex"
                            hideReturnLink="false"
                            image="buttonsmall_search.gif"/>
                    </c:if>
                </td>
            </tr>
        </table>
        </div>
    </kul:tab>
    </c:if>

	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
		<sys-java:accountingLines>
			<sys-java:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
			<sys-java:accountingLineGroup newLinePropertyName="newTargetLine" collectionPropertyName="document.targetAccountingLines" collectionItemPropertyName="document.targetAccountingLine" attributeGroupName="target"/>
		</sys-java:accountingLines>
	</kul:tab>

	<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
  	<fp:capitalAccountingLines readOnly="${readOnly}"/>
  	
	<c:if test="${KualiForm.capitalAccountingLine.canCreateAsset}">
		<fp:capitalAssetCreateTab readOnly="${readOnly}"/>
	</c:if>
  	
	<fp:capitalAssetModifyTab readOnly="${readOnly}"/>  
	
	<fp:errorCertification documentAttributes="${DataDictionary.ErrorCertification.attributes}" />
	
	<gl:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="${documentEntry.transactionalDocument}" extraButtons="${KualiForm.extraButtons}" />

</kul:documentPage>
