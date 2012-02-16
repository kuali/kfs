<%--
 Copyright 2006 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>


<kul:documentPage showDocumentInfo="true"
	documentTypeName="${KualiForm.docTitle}"
	htmlFormAction="${KualiForm.htmlFormAction}" renderMultipart="true"
	showTabButtons="true">
	<c:set var="readOnly"
		value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	<c:if test="${debug == true}">
		<kul:tab tabTitle="Debug" defaultOpen="true" tabErrorKey="debug">
			<div class="tab-container" align="center">
				<table cellpadding="0" class="datatable" summary="">
					<tr>
						<td align="left" valign="middle" class="subhead"><span
							class="subhead-left">Debug</span></td>
					</tr>
				</table>
				<table cellpadding="0" class="datatable">
					<tr>
						<td width="10%">editableFlag</td>
						<td>${KualiForm.editableFlag}</td>
					</tr>
					<tr>
						<td>manualEditFlag</td>
						<td>${KualiForm.manualEditFlag}</td>
					</tr>
					<tr>
						<td>processInBatch</td>
						<td>${KualiForm.processInBatch}</td>
					</tr>
					<tr>
						<td>chooseSystem</td>
						<td>${KualiForm.chooseSystem}</td>
					</tr>
					<tr>
						<td>editMethod</td>
						<td>${KualiForm.editMethod}</td>
					</tr>
					<tr>
						<td>inputGroupId</td>
						<td>${KualiForm.document.correctionInputFileName}</td>
					</tr>
					<tr>
						<td>outputGroupId</td>
						<td>${KualiForm.document.correctionOutputFileName}</td>
					</tr>
					<tr>
						<td>inputFileName</td>
						<td>${KualiForm.inputFileName}</td>
					</tr>
					<tr>
						<td>dataLoadedFlag</td>
						<td>${KualiForm.dataLoadedFlag}</td>
					</tr>
					<tr>
						<td>matchCriteriaOnly</td>
						<td>${KualiForm.matchCriteriaOnly}</td>
					</tr>
					<tr>
						<td>editableFlag</td>
						<td>${KualiForm.editableFlag}</td>
					</tr>
					<tr>
						<td>deleteFileFlag</td>
						<td>${KualiForm.deleteFileFlag}</td>
					</tr>
					<tr>
						<td>allEntries.size</td>
						<td>${KualiForm.allEntriesSize}</td>
					</tr>
					<tr>
						<td>readOnly</td>
						<td>${readOnly}</td>
					</tr>
				</table>
			</div>
		</kul:tab>
	</c:if>
	<kul:tab tabTitle="Summary" defaultOpen="true" tabErrorKey="summary">
		<c:if
			test="${KualiForm.document.correctionTypeCode ne 'R' and (not (KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.inputGroupId)) && ((KualiForm.dataLoadedFlag and !KualiForm.restrictedFunctionalityMode) or KualiForm.document.correctionOutputFileName != null or !KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT])}">

			<div class="tab-container" align="center">
				<table cellpadding="0" class="datatable" summary="">
					<tr>
						<c:if
							test="${KualiForm.showOutputFlag == true or KualiForm.showSummaryOutputFlag == true}">
							<td align="left" valign="middle" class="subhead"><span
								class="subhead-left">Summary of Output Group</span></td>
						</c:if>
						<c:if test="${KualiForm.showOutputFlag == false}">
							<td align="left" valign="middle" class="subhead"><span
								class="subhead-left">Summary of Input Group</span></td>
						</c:if>
					</tr>
				</table>
				<table cellpadding="0" class="datatable">
					<tr>
						<td width="20%" align="left" valign="middle">Trip Totals:</td>
						<td align="right" valign="middle"><fmt:formatNumber
								value="${KualiForm.document.correctionTripTotalAmount}"
								groupingUsed="true" minFractionDigits="2" /></td>
					</tr>
					<tr>
						<td width="20%" align="left" valign="middle">Rows output:</td>
						<td align="right" valign="middle"><fmt:formatNumber
								value="${KualiForm.document.correctionRowCount}"
								groupingUsed="true" /></td>
					</tr>
				</table>
			</div>
		</c:if>
		<c:if
			test="${KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.document.correctionInputFileName}">
			<div class="tab-container" align="center">
				<table cellpadding="0" class="datatable" summary="">
					<tr>
						<c:if
							test="${KualiForm.showOutputFlag == true or KualiForm.showSummaryOutputFlag == true}">
							<td align="left" valign="middle" class="subhead"><span
								class="subhead-left">Summary of Output Group</span></td>
						</c:if>
						<c:if
							test="${KualiForm.showOutputFlag == false or KualiForm.showSummaryOutputFlag == true}">
							<td align="left" valign="middle" class="subhead"><span
								class="subhead-left">Summary of Input Group</span></td>
						</c:if>
					</tr>
				</table>
				<table cellpadding="0" class="datatable">
					<tr>
						<td>The summary is unavailable because the agency entries are
							unavailable.</td>
					</tr>
				</table>
			</div>
		</c:if>
		<c:if
			test="${KualiForm.restrictedFunctionalityMode && KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}">
			<div class="tab-container" align="center">
				<table cellpadding="0" class="datatable" summary="">
					<tr>
						<c:if
							test="${KualiForm.showOutputFlag == true or KualiForm.showSummaryOutputFlag == true}">
							<td align="left" valign="middle" class="subhead"><span
								class="subhead-left">Summary of Output Group</span></td>
						</c:if>
						<c:if
							test="${KualiForm.showOutputFlag == false or KualiForm.showSummaryOutputFlag == true}">
							<td align="left" valign="middle" class="subhead"><span
								class="subhead-left">Summary of Input Group</span></td>
						</c:if>
					</tr>
				</table>
				<table cellpadding="0" class="datatable">
					<tr>
						<td>The summary is unavailable because the selected agency
							entry group is too large.</td>
					</tr>
				</table>
			</div>
		</c:if>
	</kul:tab>
	<%-- ------------------------------------------------------------ This is read/write mode --------------------------------------------------- --%>
	<c:if test="${readOnly == false}">
		<kul:tab tabTitle="Correction Process" defaultOpen="true"
			tabErrorKey="systemAndEditMethod">
			<div class="tab-container" align="center">
				<table cellpadding=0 class="datatable" summary="">
					<tr>
						<td align="left" valign="middle" class="subhead"><span
							class="subhead-left"></span><label for="chooseSystem">Select
								System</label> and <labelfor"editMethod">Edit Method</label>
						</td>
					</tr>
					<tr>
						<td>
							<center>
								<strong>System:</strong> Database

								<html:select property="editMethod" styleId="editMethod"
									title="Edit Method">
									<html:optionsCollection
										property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|tem|businessobject|options|CorrectionEditMethodValuesFinder"
										label="label" value="key" />
								</html:select>
								<html:image
									property="methodToCall.selectSystemEditMethod.anchor${currentTabIndex}"
									src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif"
									styleClass="tinybutton" alt="Select System and Edit Method"
									title="Select System and Edit Method" />
							</center>
						</td>
					</tr>
				</table>
			</div>
		</kul:tab>
	</c:if>
	<kul:tab tabTitle="Documents in System" defaultOpen="true"
		tabErrorKey="documentsInSystem">
		<c:if test="${KualiForm.chooseSystem == 'D'}">
			<div class="tab-container" align="center">
				<table cellpadding=0 class="datatable" summary="">
					<tr>
						<td align="left" valign="middle" class="subhead"><span
							class="subhead-left"></span>Documents in System</td>
					</tr>
					<tr>
						<td colspan="2" class="bord-l-b"
							style="padding: 4px; vertical-align: top;">
							<center>
								<label for="inputGroupId"><strong>Agency Entry
										Group</strong> </label><br /> <br />
								<html:select property="document.correctionInputFileName"
									size="10" styleId="inputGroupId" title="Agency Entry Group">
									<c:if
										test="${KualiForm.inputGroupIdFromLastDocumentLoadIsMissing and KualiForm.inputGroupId eq KualiForm.inputGroupIdFromLastDocumentLoad}">
										<option
											value="<c:out value="${KualiForm.inputGroupIdFromLastDocumentLoad}"/>"
											selected="selected">
											<c:out value="${KualiForm.inputGroupIdFromLastDocumentLoad}" />
											Document was last saved with this agency entry group
											selected. Group is no longer in system.
										</option>
									</c:if>

									<html:optionsCollection
										property="actionFormUtilMap.getOptionsMap~org|kuali|kfs|module|tem|businessobject|options|AgencyCorrectionGroupEntriesFinder"
										label="label" value="key" />
								</html:select>

								<br /> <br />
								<c:if test="${KualiForm.editMethod eq 'R'}">
									<html:image
										property="methodToCall.confirmDeleteDocument.anchor${currentTabIndex}"
										src="${ConfigProperties.externalizable.images.url}tinybutton-remgrpproc.gif"
										styleClass="tinybutton" alt="Remove Group From Processing"
										title="Remove Group From Processing" />
								</c:if>
								<c:if
									test="${KualiForm.editMethod eq 'M' or KualiForm.editMethod eq 'C'}">
									<html:image
										property="methodToCall.loadGroup.anchor${currentTabIndex}"
										src="${ConfigProperties.externalizable.images.url}tinybutton-loadgroup.gif"
										styleClass="tinybutton" alt="Show All Entries"
										title="Show All Entries" />
								</c:if>
							</center>
						</td>
					</tr>
				</table>
			</div>
		</c:if>
	</kul:tab>
	<kul:tab tabTitle="Search Results" defaultOpen="true"
		tabErrorKey="searchResults">
		<c:if test="${KualiForm.restrictedFunctionalityMode}">
			<div class="tab-container" align="center">
				<table cellpadding=0 class="datatable" summary="">
					<tr>
						<td align="left" valign="middle" class="subhead">Search
							Results</td>
					</tr>
					<tr>
						<td><bean:message
								key="gl.correction.restricted.functionality.search.results.label" />
						</td>
					</tr>
				</table>
			</div>
		</c:if>
		<c:if
			test="${KualiForm.restrictedFunctionalityMode && KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.document.correctionInputFileName}">
			<div class="tab-container" align="center">
				<table cellpadding=0 class="datatable" summary="">
					<tr>
						<td align="left" valign="middle" class="subhead">Search
							Results</td>
					</tr>
					<tr>
						<td><bean:message
								key="gl.correction.persisted.origin.entries.missing" /></td>
					</tr>
				</table>
			</div>
		</c:if>
		<c:if
			test="${KualiForm.chooseSystem != null and KualiForm.editMethod != null and KualiForm.dataLoadedFlag == true and !KualiForm.restrictedFunctionalityMode and !(KualiForm.inputGroupIdFromLastDocumentLoad eq KualiForm.document.correctionInputFileName)}">
			<div class="tab-container" align="left"
				style="overflow: scroll; width: 100%;">
				<table cellpadding=0 class="datatable" summary="">
					<tr>
						<c:choose>
							<c:when
								test="${KualiForm.showOutputFlag == true and KualiForm.editMethod == 'C'}">
								<td align="left" valign="middle" class="subhead"><span
									class="subhead-left">Search Results - Output Group</span></td>
							</c:when>
							<c:when
								test="${KualiForm.showOutputFlag == false and KualiForm.editMethod == 'C'}">
								<td align="left" valign="middle" class="subhead"><span
									class="subhead-left">Search Results - Input Group</span></td>
							</c:when>
							<c:when
								test="${KualiForm.showOutputFlag == true and KualiForm.editMethod == 'M'}">
								<td align="left" valign="middle" class="subhead"><span
									class="subhead-left">Search Results - Matching Entries
										Only</span></td>
							</c:when>
							<c:when
								test="${KualiForm.showOutputFlag == false and KualiForm.editMethod == 'M'}">
								<td align="left" valign="middle" class="subhead"><span
									class="subhead-left">Search Results - All Entries</span></td>
							</c:when>
						</c:choose>
					</tr>
					<tr>
						<td>
						<c:if test="${empty KualiForm.allEntries}">
                        No Agency Entries found.
                        </c:if> 
                        <c:if test="${!empty KualiForm.allEntries}">
								<kul:tableRenderPagingBanner
									pageNumber="${KualiForm.agencyEntrySearchResultTableMetadata.viewedPageNumber}"
									totalPages="${KualiForm.agencyEntrySearchResultTableMetadata.totalNumberOfPages}"
									firstDisplayedRow="${KualiForm.agencyEntrySearchResultTableMetadata.firstRowIndex}"
									lastDisplayedRow="${KualiForm.agencyEntrySearchResultTableMetadata.lastRowIndex}"
									resultsActualSize="${KualiForm.agencyEntrySearchResultTableMetadata.resultsActualSize}"
									resultsLimitedSize="${KualiForm.agencyEntrySearchResultTableMetadata.resultsLimitedSize}"
									buttonExtraParams=".anchor${currentTabIndex}" />
								<input type="hidden"
									name="agencyEntrySearchResultTableMetadata.${Constants.TableRenderConstants.PREVIOUSLY_SORTED_COLUMN_INDEX_PARAM}"
									value="${KualiForm.agencyEntrySearchResultTableMetadata.columnToSortIndex}" />
								<input type="hidden"
									name="agencyEntrySearchResultTableMetadata.sortDescending"
									value="${KualiForm.agencyEntrySearchResultTableMetadata.sortDescending}" />
								<input type="hidden"
									name="agencyEntrySearchResultTableMetadata.viewedPageNumber"
									value="${KualiForm.agencyEntrySearchResultTableMetadata.viewedPageNumber}" />
								<table class="datatable-100" id="agencyEntry" cellpadding="0"
									cellspacing="0">
									<thead>
										<tr>
											<c:if test="${KualiForm.editableFlag == true}">
												<th>Manual Edit</th>
											</c:if>
											<c:forEach items="${KualiForm.tableRenderColumnMetadata}"
												var="column">
												<th class="sortable"><c:out
														value="${column.columnTitle}" /> <c:if
														test="${empty column.columnTitle}">$nbsp;</c:if></th>
											</c:forEach>
										</tr>
										<tr>
											<c:if
												test="${KualiForm.editableFlag == true and KualiForm.showOutputFlag == false}">
												<td>&nbsp;</td>
											</c:if>
											<c:forEach items="${KualiForm.tableRenderColumnMetadata}"
												var="column" varStatus="columnLoopStatus">
												<td class="sortable" style="text-align: center;"><c:if
														test="${column.sortable}">
														<html:image
															property="methodToCall.sort.${columnLoopStatus.index}.anchor${currentTabIndex}"
															src="${ConfigProperties.kr.externalizable.images.url}sort.gif"
															styleClass="tinybutton" alt="Sort column"
															title="Sort column ${column.columnTitle}" />
													</c:if> <c:if test="${!column.sortable}">
                            &nbsp;
                        </c:if></td>
											</c:forEach>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${KualiForm.allEntries}" var="agencyEntry"
											varStatus="loopStatus"
											begin="${KualiForm.agencyEntrySearchResultTableMetadata.firstRowIndex}"
											end="${KualiForm.agencyEntrySearchResultTableMetadata.lastRowIndex}">
											<c:set var="rowclass" value="odd" />
											<c:if test="${loopStatus.count % 2 == 0}">
												<c:set var="rowclass" value="even" />
											</c:if>
											<tr class="${rowclass}">
												<c:if
													test="${KualiForm.editableFlag == true and KualiForm.editMethod == 'M'}">
													<td><html:image
															property="methodToCall.editManualEntry.entryId${agencyEntry.entryId}.anchor${currentTabIndex}"
															src="${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif"
															styleClass="tinybutton" alt="edit" title="edit" /> <html:image
															property="methodToCall.deleteManualEntry.entryId${agencyEntry.entryId}.anchor${currentTabIndex}"
															src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
															styleClass="tinybutton" alt="delete" title="delete" /></td>
												</c:if>
												<td class="infocell"><c:out
														value="${agencyEntry.creditCardOrAgencyCode}" />&nbsp;</td>
												<td class="infocell"><c:out
														value="${agencyEntry.agency}" />&nbsp;</td>
												<td class="infocell"><c:out
														value="${agencyEntry.merchantName}" />&nbsp;</td>
												<td class="infocell"><c:out
														value="${agencyEntry.tripInvoiceNumber}" />&nbsp;</td>
												<td class="infocell"><c:out
														value="${agencyEntry.travelerName}" />&nbsp;</td>
												<td class="infocell"><c:out
														value="${agencyEntry.travelerId}" />&nbsp;</td>
												<td class="infocell"><c:out
														value="${agencyEntry.tripExpenseAmount}" />&nbsp;</td>
												<td class="infocell"><c:out
                                                        value="${agencyEntry.tripArrangerName}" />&nbsp;</td>
                                                <td class="infocell"><c:out
                                                        value="${agencyEntry.tripDepartureDate}" />&nbsp;</td>
												<td class="infocell"><c:out
                                                        value="${agencyEntry.airBookDate}" />&nbsp;</td>
                                                <td class="infocell"><c:out
                                                        value="${agencyEntry.airCarrierCode}" />&nbsp;</td>
                                                <td class="infocell"><c:out
                                                        value="${agencyEntry.airTicketNumber}" />&nbsp;</td>
                                                <td class="infocell"><c:out
                                                        value="${agencyEntry.pnrNumber}" />&nbsp;</td>
                                                <td class="infocell"><c:out
                                                        value="${agencyEntry.transactionUniqueId}" />&nbsp;</td>
                                                <td class="infocell"><c:out
                                                        value="${agencyEntry.transactionPostingDate}" />&nbsp;</td>
											</tr>
										</c:forEach>
									<tbody>
								</table>
							</c:if></td>
					</tr>
					<c:if
						test="${KualiForm.editMethod == 'M' and KualiForm.editableFlag == true}">
						<tr>
							<td align="left" valign="middle" class="subhead"><span
								class="subhead-left">Manual Editing</span></td>
						</tr>
						<tr>
							<td>
								<table id="eachEntryForManualEdit">
									<thead>
										<tr>
											<th>Manual Edit</th>

											<c:forEach items="${KualiForm.tableRenderColumnMetadata}"
												var="column">

												<th class="sortable"><label
													for="<c:out value="${column.propertyName}"/>"> <c:out
															value="${column.columnTitle}" /> <c:if
															test="${empty column.columnTitle}">$nbsp;</c:if> </label>
												</th>
											</c:forEach>
										</tr>
									</thead>
									<tbody>
										<tr class="odd">

											<c:choose>
												<c:when test="${KualiForm.entryForManualEdit.entryId == 0}">
													<td><html:image
															property="methodToCall.addManualEntry.anchor${currentTabIndex}"
															src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
															styleClass="tinybutton" alt="edit" title="edit" />
													</td>
												</c:when>
												<c:otherwise>
													<td><html:image
															property="methodToCall.saveManualEntry.anchor${currentTabIndex}"
															src="${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif"
															styleClass="tinybutton" alt="edit" title="edit" /></td>
												</c:otherwise>
											</c:choose>
											<td><html:text
													property="entryForManualEdit.creditCardOrAgencyCode" size="5"
													styleId="entryForManualEdit.creditCardOrAgencyCode" />
											</td>
											<td><html:text
													property="entryForManualEdit.agency" size="7"
													styleId="entryForManualEdit.agency" />
											</td>
											<td><html:text
													property="entryForManualEdit.merchantName" size="7"
													styleId="entryForManualEdit.merchantName" />
											</td>
											<td><html:text
													property="entryForManualEdit.tripInvoiceNumber" size="5"
													styleId="entryForManualEdit.tripInvoiceNumber" />
											</td>
											<td><html:text
													property="entryForManualEdit.travelerName"
													size="6"
													styleId="entryForManualEdit.travelerName" />
											</td>
											<td><html:text
													property="entryForManualEdit.travelerId"
													size="8"
													styleId="entryForManualEdit.travelerId" />
											</td>
											<td><html:text
													property="entryForManualEdit.tripExpenseAmount"
													size="6"
													styleId="entryForManualEdit.tripExpenseAmount" />
											</td>
											<td><html:text
													property="entryForManualEdit.tripArrangerName"
													size="6"
													styleId="entryForManualEdit.tripArrangerName" />
											</td>
											<td><html:text
													property="entryForManualEdit.tripDepartureDate"
													size="10"
													styleId="entryForManualEdit.tripDepartureDate" />
											</td>
											<td><html:text
													property="entryForManualEdit.airBookDate"
													size="6"
													styleId="entryForManualEdit.airBookDate" />
											</td>
											<td><html:text
													property="entryForManualEdit.airCarrierCode" size="14"
													styleId="entryForManualEdit.airCarrierCode" />
											</td>
											<td><html:text
													property="entryForManualEdit.airTicketNumber"
													size="11"
													styleId="entryForManualEdit.airTicketNumber" />
											</td>
											<td><html:text
													property="entryForManualEdit.pnrNumber" size="7"
													styleId="entryForManualEdit.pnrNumber" />
											</td>
											<td><html:text
													property="entryForManualEdit.transactionUniqueId"
													size="9"
													styleId="entryForManualEdit.transactionUniqueId" />
											</td>
											<td><html:text property="entryForManualEdit.transactionPostingDate" size="12"
													styleId="entryForManualEdit.transactionPostingDate" />
											</td>
										</tr>
									</tbody>
								</table>
							</td>
						</tr>
					</c:if>
					<c:if test="${KualiForm.manualEditFlag == true}">
						<td><STRONG> Do you want to edit this document? </STRONG> <html:image
								property="methodToCall.manualEdit.anchor${currentTabIndex}"
								src="${ConfigProperties.kr.externalizable.images.url}tinybutton-edit1.gif"
								styleClass="tinybutton" alt="show edit" title="show edit" />
						</td>
					</c:if>
				</table>
			</div>
		</c:if>
	</kul:tab>
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<sys:documentControls transactionalDocument="false" />
</kul:documentPage>
