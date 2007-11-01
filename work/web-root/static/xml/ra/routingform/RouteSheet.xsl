<?xml version="1.0"?>
<!--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<!--                           VERSION HISTORY
		- 05/2007: refactoring for KRA.
	-->

	<xsl:variable name="imagesUrl" select="/PROPOSAL/ROUTING_FORM/@IMAGES_URL" />

	<xsl:variable name="routingForm" select="PROPOSAL/ROUTING_FORM" />
	<xsl:variable name="agency" select="PROPOSAL/ROUTING_FORM/AGENCY" />
	<xsl:variable name="types" select="PROPOSAL/ROUTING_FORM/TYPES" />
	<xsl:variable name="principals"
		select="PROPOSAL/ROUTING_FORM/PRINCIPLES" />
	<xsl:variable name="details"
		select="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL" />
	<xsl:variable name="amounts_dates"
		select="PROPOSAL/ROUTING_FORM/AMOUNTS_DATES" />

	<xsl:variable name="emailContact"
		select="PROPOSAL/ROUTING_FORM/PRINCIPLES/CONTACT_PERSON/@EMAIL" />
	<xsl:variable name="faxContact"
		select="PROPOSAL/ROUTING_FORM/PRINCIPLES/CONTACT_PERSON/@FAX_NUMBER" />
	<xsl:variable name="phoneContact"
		select="PROPOSAL/ROUTING_FORM/PRINCIPLES/CONTACT_PERSON/@PHONE_NUMBER" />
	<xsl:variable name="firstNameContact"
		select="PROPOSAL/ROUTING_FORM/PRINCIPLES/CONTACT_PERSON/@FIRST_NAME" />
	<xsl:variable name="lastNameContact"
		select="PROPOSAL/ROUTING_FORM/PRINCIPLES/CONTACT_PERSON/@LAST_NAME" />

	<xsl:variable name="lastNamePrincipal"
		select="PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/@LAST_NAME" />
	<xsl:variable name="firstNamePrincipal"
		select="PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/@FIRST_NAME" />

	<!-- 4/26/2005 dterret: An additional bit of data to be displayed.-->
	<xsl:variable name="submittingOrg"
		select="/PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/SUBMITTING_ORG" />
	<xsl:variable name="chart"
		select="PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/SUBMITTING_ORG/@SUBMITTING_CHART" />
	<xsl:variable name="org"
		select="PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/SUBMITTING_ORG/@SUBMITTING_ORG" />

	<xsl:variable name="chartPD"
		select="PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/HOME_ORG/@HOME_CHART" />
	<xsl:variable name="orgPD"
		select="PROPOSAL/ROUTING_FORM/PRINCIPLES/PROJECT_DIRECTOR/HOME_ORG/@HOME_ORG" />


	<!--   The following attribute sets are intuitively named.  "a" refers to the left side of a cell, "b" refers to the top of the cell, "c" refers the right side, and "d" refers to the bottom.    Attribute
		set "abc" shows solid borders on the left side, top, and bottom of a cell.             -->

	<xsl:attribute-set name="abcd">
		<xsl:attribute name="border-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="abd">
		<xsl:attribute name="border-style">solid</xsl:attribute>
		<xsl:attribute name="border-right-width">0mm</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="ab">
		<xsl:attribute name="border-left-style">solid</xsl:attribute>
		<xsl:attribute name="border-top-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="bc">
		<xsl:attribute name="border-right-style">solid</xsl:attribute>
		<xsl:attribute name="border-top-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="ad">
		<xsl:attribute name="border-left-style">solid</xsl:attribute>
		<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="cd">
		<xsl:attribute name="border-right-style">solid</xsl:attribute>
		<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="bcd">
		<xsl:attribute name="border-right-style">solid</xsl:attribute>
		<xsl:attribute name="border-top-style">solid</xsl:attribute>
		<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="abd">
		<xsl:attribute name="border-style">solid</xsl:attribute>
		<xsl:attribute name="border-right-width">0mm</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="abc">
		<xsl:attribute name="border-left-style">solid</xsl:attribute>
		<xsl:attribute name="border-top-style">solid</xsl:attribute>
		<xsl:attribute name="border-right-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="a">
		<xsl:attribute name="border-left-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="b">
		<xsl:attribute name="border-top-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="c">
		<xsl:attribute name="border-right-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="d">
		<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
	</xsl:attribute-set>

	<!--  Defining an all-encompassing template here is standard procedure.  The same is true of "fo:root."    -->
	<xsl:template match="/">

		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">


			<fo:layout-master-set>
				<fo:simple-page-master master-name="first"
					page-height="11in" page-width="8.5in" margin-bottom="0.2in"
					margin-left="0.5in" margin-right="0.5in" margin-top="0.5in">

					<fo:region-after extent="0.25in" />

					<fo:region-body margin-bottom="0.35in" />
				</fo:simple-page-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="first">

				<!--  The following code controls page numbering  It is used in conjunction with <fo:block id="End" ></fo:block> at the end of the stylesheet.-->

				<fo:static-content flow-name="xsl-region-after">
					<fo:block text-align="center" font-size="8pt"
						padding="3mm">
						<fo:page-number />
						of
						<fo:page-number-citation ref-id="End" />
					</fo:block>
				</fo:static-content>

				<!--   This table was intended for use throughout the entire document.  A numbered grid (below the header block) can be temporarily placed anywhere on the page.  The grid makes it convenient to place items precisely the first time, spanning and eliminating cells with minimal experimentation.  The original plan was modified, and a table almost identical to this one is used in a template to display pages after page 1..  -->


				<fo:flow flow-name="xsl-region-body">

					<fo:table table-layout="fixed">


						<fo:table-column column-width="4mm" />
						4
						<fo:table-column column-width="7mm" />
						11
						<fo:table-column column-width="4mm" />
						15
						<fo:table-column column-width="11mm" />
						26
						<fo:table-column column-width="4mm" />
						30

						<fo:table-column column-width="5mm" />
						35
						<fo:table-column column-width="10mm" />
						45
						<fo:table-column column-width="11mm" />
						56
						<fo:table-column column-width="22mm" />
						78
						<fo:table-column column-width="22mm" />
						100

						<fo:table-column column-width="5mm" />
						105
						<fo:table-column column-width="10mm" />
						115
						<fo:table-column column-width="5mm" />
						120
						<fo:table-column column-width="5mm" />
						125
						<fo:table-column column-width="5mm" />
						130

						<fo:table-column column-width="4mm" />
						134
						<fo:table-column column-width="6mm" />
						140
						<fo:table-column column-width="3mm" />
						143

						<fo:table-column column-width="9mm" />
						152
						<fo:table-column column-width="8mm" />
						160

						<fo:table-column column-width="12mm" />
						177

						<fo:table-column column-width="18.5mm" />
						190.5


						<fo:table-header font-size="9pt">

							<fo:table-row height="5mm">

								<fo:table-cell
									number-columns-spanned="5" />

								<fo:table-cell
									number-columns-spanned="12" padding-left="52mm">
									<fo:block>
										<fo:inline font-weight="bold"
											font-size="11pt">
											Routing Form
										</fo:inline>
									</fo:block>
								</fo:table-cell>


								<fo:table-cell
									number-columns-spanned="4">
									<fo:block text-align="right">
										<fo:inline font-weight="bold">
											Tracking #
										</fo:inline>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block text-align="right">
										<xsl:value-of
											select="$routingForm/@TRACKING_NUMBER" />
									</fo:block>
								</fo:table-cell>

							</fo:table-row>

							<fo:table-row height="5mm">

								<fo:table-cell
									number-columns-spanned="18">
									<fo:block></fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="3">
									<fo:block text-align="right">
										<fo:inline font-weight="bold">
											Proposal #
										</fo:inline>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block text-align="right">
										<xsl:value-of
											select="$routingForm/@PROPOSAL_NUMBER" />
									</fo:block>
								</fo:table-cell>

							</fo:table-row>




						</fo:table-header>

						<fo:table-body font-size="9pt">

							<!--       ** NUMBERED GRID **   This is the grid that can be temporarily displayed as a guide.  Just cut and paste it where it is needed, and delete it when you are done.  This grid is for use inside the root template.  An extra cell was added for the template called "Next."  Grid code is located in that template.
								
								<fo:table-row >
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>1</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd" >
								<fo:block>2</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>3</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>4</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>5</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>6</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>7</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>8</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>9</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>10</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>11</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>12</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>13</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>14</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>15</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>16</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>17</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>18</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>19</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>20</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abd">
								<fo:block>21</fo:block>
								</fo:table-cell>
								
								<fo:table-cell xsl:use-attribute-sets = "abcd">
								<fo:block>22</fo:block>
								</fo:table-cell>
								
								</fo:table-row>
								
							-->

							<fo:table-row>

								<fo:table-cell
									number-columns-spanned="23" text-align="center"
									font-weight="bold">
									<fo:block>
										<fo:block>
											To assure on time delivery,
											each request and approved
											routing form must be
											received by
										</fo:block>
										the sponsored research office at
										least
										<fo:inline
											text-decoration="underline">
											three (3) full business days
										</fo:inline>
										prior to the due date
									</fo:block>

								</fo:table-cell>

							</fo:table-row>

							<fo:table-row height="5mm" />

							<fo:table-row>

								<fo:table-cell
									number-columns-spanned="12">
									<fo:block>
										<fo:inline font-weight="bold">
											AGENCY:
											<xsl:value-of
												select=" $agency/AGENCY_DATA/AGENCY_FULL_NAME" />
										</fo:inline>
									</fo:block>
								</fo:table-cell>

								<!--
									<fo:table-cell number-columns-spanned="8">
									<fo:block>
									<xsl:value-of select=" $agency/AGENCY_DATA/AGENCY_FULL_NAME" />
									</fo:block>
									</fo:table-cell>
								-->

								<!-- 10/06/2003 pcberg: Added
									fo:block-container. See
									Keystone #1424. -->
								<fo:table-cell
									number-columns-spanned="10" number-rows-spanned="3">
									<fo:block-container height="15mm"
										width="70mm">
										<fo:block>
											<fo:inline
												font-weight="bold">
												COPIES:
											</fo:inline>
											Send
											<xsl:choose>
												<!-- 06/25/2003 pcberg: Changed
													condition, was broken before.. -->
												<xsl:when
													test="$agency/AGENCY_DELIVERY/@COPIES != ''">
													<fo:inline
														text-decoration="underline">
														<xsl:value-of
															select="$agency/AGENCY_DELIVERY/@COPIES" />
													</fo:inline>
													to the agency.
												</xsl:when>
												<xsl:otherwise>
													___ to the agency.
												</xsl:otherwise>
											</xsl:choose>
											Submit 2 additional copies
											plus the number required by
											your department and school.
										</fo:block>
									</fo:block-container>
								</fo:table-cell>
							</fo:table-row>

							<!-- blank row to push heading down. -->
							<fo:table-row height="5mm">
								<fo:table-cell
									number-columns-spanned="12">
									<fo:block>
										<fo:inline font-weight="bold"></fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row>
								<fo:table-cell
									number-columns-spanned="12">
									<fo:block>
										<fo:inline font-weight="bold">
											PRIMARY DELIVERY ADDRESS
										</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row height="2mm">

								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>
							</fo:table-row>


							<fo:table-row
								white-space-collapse="false">

								<fo:table-cell
									xsl:use-attribute-sets="abcd" number-columns-spanned="11"
									number-rows-spanned="7" padding-left="1mm" padding="0.5mm">
									<fo:block-container height="28mm"
										width="105mm">
										<fo:block>
											<xsl:value-of
												select="$agency/AGENCY_DELIVERY/DELIVERY_INSTRUCTIONS" />
										</fo:block>
									</fo:block-container>
								</fo:table-cell>

							</fo:table-row>

							<fo:table-row height="5mm">

								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="8" display-align="after">
									<fo:block>
										<fo:inline font-weight="bold">
											DUE DATE
										</fo:inline>
										(mm/dd/yyyy)
									</fo:block>
								</fo:table-cell>

								<fo:table-cell display-align="after">
									<fo:block text-align="center">
										<fo:inline font-weight="bold">
											TIME
										</fo:inline>
									</fo:block>
								</fo:table-cell>

							</fo:table-row>


							<fo:table-row height="5mm">

								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>

								<fo:table-cell number-columns-spanned="4" line-height="7pt" display-align="after">
									<fo:block><xsl:value-of select="/PROPOSAL/ROUTING_FORM/AGENCY/DUE_DATE/@DUE_DATE" /></fo:block>
								</fo:table-cell>

								<fo:table-cell number-columns-spanned="3" display-align="after" line-height="7pt">
									<fo:block text-align="center"></fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>

								<fo:table-cell display-align="after" line-height="7pt" number-columns-spanned="3">
									<fo:block text-align="left">
										<xsl:value-of select="/PROPOSAL/ROUTING_FORM/AGENCY/DUE_DATE/@DUE_TIME" />
									</fo:block>
								</fo:table-cell>

							</fo:table-row>


							<fo:table-row height="5mm">

								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>

								<fo:table-cell number-columns-spanned="9" line-height="7pt" display-align="after">
									<fo:block>
									    <fo:inline font-weight="bold">
											DUE DATE TYPE
										</fo:inline>
									</fo:block>
								</fo:table-cell>

							</fo:table-row>


							<fo:table-row height="5mm">

								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>

								<fo:table-cell number-columns-spanned="9" display-align="after" line-height="7pt">
									<fo:block><xsl:value-of select="/PROPOSAL/ROUTING_FORM/AGENCY/DUE_DATE/@DUE_DATE_TYPE" /></fo:block>
								</fo:table-cell>

							</fo:table-row>


							<fo:table-row height="5mm">
								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>
							</fo:table-row>


							<fo:table-row height="1mm">
								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row height="4.5mm">

								<fo:table-cell display-align="after">
									<fo:block>
										<xsl:call-template
											name="ToggleAdditionalInst_Yes" />
									</fo:block>
								</fo:table-cell>

								<fo:table-cell display-align="after">
									<fo:block>Yes</fo:block>
								</fo:table-cell>

								<fo:table-cell display-align="after">
									<fo:block>
										<xsl:call-template
											name="ToggleAdditionalInst_No" />
									</fo:block>
								</fo:table-cell>

								<fo:table-cell display-align="after">
									<fo:block>No</fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="8" display-align="after">
									<xsl:call-template
										name="SeeAttached" />
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="10" display-align="after">
									<fo:block>
										CFDA #:
										<xsl:value-of
											select="$routingForm/PROJECT_INFORMATION/@CFDA_TXT" />
									</fo:block>
								</fo:table-cell>

							</fo:table-row>

							<fo:table-row height="5mm">

								<fo:table-cell
									number-columns-spanned="12" />

								<fo:table-cell
									number-columns-spanned="5">
									<fo:block>PA, RFA, RFP #:</fo:block>
								</fo:table-cell>

								<!--     TO DO:   Get data from the XML file   -->

								<fo:table-cell
									number-columns-spanned="5" number-rows-spanned="2">
									<fo:block>
										<xsl:value-of
											select="$agency/AGENCY_DATA/@PROGRAM_ANNOUNCEMENT_NUMBER" />
									</fo:block>
								</fo:table-cell>

							</fo:table-row>

							<fo:table-row height="5mm">

								<fo:table-cell
									number-columns-spanned="12" />

								<fo:table-cell
									number-columns-spanned="5">
									<fo:block></fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="5">
									<fo:block></fo:block>
								</fo:table-cell>

							</fo:table-row>

							<fo:table-row height="5mm">

								<fo:table-cell number-columns-spanned="6">
									<fo:block>
										<fo:inline font-weight="bold">
											PROJECT DIRECTOR:
										</fo:inline>
									</fo:block>
									<fo:block wrap-option="no-wrap">
										Submitting Org: <xsl:value-of select="concat($chart,'-',$org)" />
									</fo:block>
									<fo:block wrap-option="no-wrap">
										Campus Address: <xsl:value-of select="substring($principals/PROJECT_DIRECTOR/PD_CAMPUS_ADDRESS,1,40)" />
									</fo:block>
									<fo:block>
										Phone: <xsl:value-of select="substring($principals/PROJECT_DIRECTOR/PD_PHONE,1,40)" />
									</fo:block>
								</fo:table-cell>

								<fo:table-cell number-columns-spanned="4">
									<fo:block>
										<xsl:value-of select="substring(concat($lastNamePrincipal,', ',$firstNamePrincipal), 1, 20)" />
									</fo:block>
									<fo:block text-align="right">
										&#160;<xsl:value-of select="substring($submittingOrg,0,25)" />
									</fo:block>
									<fo:block>
									&#160;
									</fo:block>
									<fo:block text-align="right">
										E-mail: <xsl:value-of select="substring($principals/PROJECT_DIRECTOR/PD_EMAIL,1,40)" />
									</fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block text-align="right">
										<fo:inline text-align="right">
											<xsl:value-of select="concat($chartPD,'-',$orgPD) " />
										</fo:inline>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell />

								<fo:table-cell>
									<fo:block>
										<fo:inline font-weight="bold">
											PURPOSE
										</fo:inline>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block>
									&#160;
									</fo:block>
								    <xsl:apply-templates select="PROPOSAL/ROUTING_FORM/PURPOSES/PURPOSE" mode="checkboxes"/>
								</fo:table-cell>

								<fo:table-cell number-columns-spanned="8">
									<fo:block>
									&#160;
									</fo:block>
								    <xsl:apply-templates select="PROPOSAL/ROUTING_FORM/PURPOSES/PURPOSE" mode="labels"/>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row height="5mm">

								<fo:table-cell>
									<fo:block><xsl:call-template name="ToggleCOPD_Yes" /></fo:block>
								</fo:table-cell>

								<fo:table-cell display-align="after">
									<fo:block>Yes</fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block><xsl:call-template name="ToggleCOPD_No" /></fo:block>
								</fo:table-cell>

								<fo:table-cell number-columns-spanned="9" display-align="after">
									<fo:block white-space-collapse="false">No Co-Project Director(s)</fo:block>
								</fo:table-cell>

							</fo:table-row>

							<fo:table-row height="0.8mm" />

							<!-- For the next three rows, we will calll a
							template which will provide a row for each
							Fellow, Contact, Phone, E-mail address and
							Fax number. -->

							<fo:table-row height="4mm">

								<fo:table-cell
									number-columns-spanned="11">
									<fo:block>
										Fellow:
										<xsl:value-of
											select="substring($principals/FELLOW,1, 50)" />
									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="4" />

							</fo:table-row>


							<fo:table-row height="4.3mm">

								<!--  The substring function says to begin with the first character of the name, and truncate at the 30th character   -->

								<fo:table-cell
									number-columns-spanned="8">
									<fo:block wrap-option="no-wrap">
										Contact:
										<xsl:value-of
											select="substring(concat($lastNameContact,',  ', $firstNameContact), 1,30)" />
									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="3">
									<fo:block text-align="right">
										Phone:
										<xsl:value-of
											select="$phoneContact" />
									</fo:block>
								</fo:table-cell>

							</fo:table-row>


							<fo:table-row height="4.3mm">
								<fo:table-cell
									number-columns-spanned="8">
									<fo:block>
										E-mail:
										<xsl:value-of
											select="$emailContact" />
									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="3">

									<xsl:if
										test="not(PROPOSAL/ROUTING_FORM/PRINCIPLES/CONTACT_PERSON/@FAX_NUMBER = '')">
										<fo:block text-align="right">
											Fax:
											<xsl:value-of
												select="$faxContact" />
										</fo:block>
									</xsl:if>
								</fo:table-cell>

							</fo:table-row>

							<fo:table-row height="4mm" />

							<fo:table-row>

								<!--    white-space-collapse="true" is the default.  That code appears below to accommodate changing the attribute value to "false" if need be.   When the value is "false," new lines and multiple spaces are reproduced in PDF output just as they appear in the XML file.    -->

								<fo:table-cell
									number-columns-spanned="22" number-rows-spanned="3">

									<fo:block
										white-space-collapse="true">
										<fo:inline font-weight="bold">
											PROJECT TITLE:
										</fo:inline>
										<xsl:value-of
											select="substring($routingForm/PROJECT_INFORMATION/PROJECT_TITLE, 1, 200)" />

									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row height="5mm" />
							<fo:table-row height="5mm" />
							<fo:table-row height="2mm" />
							<fo:table-row>

								<fo:table-cell
									number-columns-spanned="22" number-rows-spanned="3">
									<fo:block
										white-space-collapse="true">
										<fo:inline font-weight="bold">
											LAY DESCRIPTION:
										</fo:inline>
										<xsl:value-of
											select="substring($routingForm/PROJECT_INFORMATION/LAY_DESCRIPTION, 1, 300)" />

									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row height="5mm" />
							<fo:table-row height="5mm" />
							<fo:table-row height="2mm" />

							<fo:table-row>
								<fo:table-cell
									number-columns-spanned="22" number-rows-spanned="3">
									<fo:block
										white-space-collapse="true">
										<fo:inline font-weight="bold">
											ABSTRACT:
										</fo:inline>
										<xsl:value-of
											select="substring($routingForm/PROJECT_INFORMATION/ABSTRACT, 1, 300)" />

									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row height="5mm" />
							<fo:table-row height="5mm" />
							<fo:table-row height="2mm" />

							<fo:table-row>
								<fo:table-cell
									number-columns-spanned="22">
									<fo:block font-weight="bold">
										AMOUNTS &amp; DATES:
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row>

								<fo:table-cell
									number-columns-spanned="6">
									<fo:block></fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="2">
									<fo:block text-align="center">
										Direct Costs
									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="2">
									<fo:block text-align="center">
										Indirect Costs
									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="3">
									<fo:block text-align="center">
										Total
									</fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="4">
									<fo:block text-align="center"
										text-indent="3mm">
										Start
									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="3">
									<fo:block text-align="center"
										text-indent="3mm">
										End
									</fo:block>
								</fo:table-cell>

							</fo:table-row>


							<fo:table-row>

								<fo:table-cell
									number-columns-spanned="6">
									<fo:block>Current Period</fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="2">
									<fo:block text-align="center">

										<xsl:if
											test="$amounts_dates/DIRECT_COSTS &gt; 0">
											$
											<xsl:value-of
												select="format-number($amounts_dates/DIRECT_COSTS, '###,###') " />
										</xsl:if>

									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="2">
									<fo:block text-align="center">

										<xsl:if
											test="$amounts_dates/INDIRECT_COSTS &gt; 0">
											$
											<xsl:value-of
												select="format-number($amounts_dates/INDIRECT_COSTS, '###,###') " />
										</xsl:if>

									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="3">
									<fo:block text-align="center">

										<xsl:if
											test="$amounts_dates/TOTAL_COSTS &gt; 0">
											$
											<xsl:value-of
												select="format-number($amounts_dates/TOTAL_COSTS, '###,###') " />
										</xsl:if>

									</fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="4">
									<fo:block text-align="center">
										<xsl:value-of
											select="$amounts_dates/START_DATE" />
									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									number-columns-spanned="3">
									<fo:block text-align="center">
										<xsl:value-of
											select="$amounts_dates/STOP_DATE" />
									</fo:block>
								</fo:table-cell>

							</fo:table-row>

							<!--  The following row serves merely to create space.   -->

							<fo:table-row height="7mm" />

							<fo:table-row height="4mm">
								<fo:table-cell>
									<fo:block>
										<fo:inline font-weight="bold">
											TYPE
										</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row height="2mm" />

							<fo:table-row height="4mm">

								<fo:table-cell>
								    <xsl:apply-templates select="PROPOSAL/ROUTING_FORM/TYPES/TYPE" mode="checkboxes"/>
								</fo:table-cell>

								<fo:table-cell number-columns-spanned="8">
								    <xsl:apply-templates select="PROPOSAL/ROUTING_FORM/TYPES/TYPE" mode="labels"/>
								</fo:table-cell>

								<fo:table-cell number-columns-spanned="13">
									<fo:block text-indent="2mm">
										<xsl:text disable-output-escaping="yes">
											Prior Agency Grant #
										</xsl:text>
										<xsl:value-of select="$types/PRIOR_GRANT" />
									</fo:block>
									<fo:block text-indent="2mm">
										<xsl:text disable-output-escaping="yes">
											Current Grant #
										</xsl:text>
										<xsl:value-of select="$types/CURRENT_GRANT" />
									</fo:block>
									<fo:block text-indent="2mm">
										<xsl:text disable-output-escaping="yes">
											Institution Account #
										</xsl:text>
										<xsl:value-of select="$types/INSTITUTION_ACCOUNT" />
									</fo:block>
									<fo:block text-indent="2mm">
										<xsl:text disable-output-escaping="yes">
											Institution Proposal #
										</xsl:text>
										<xsl:value-of select="$types/CURRENT_PROPOSAL" />
									</fo:block>
								</fo:table-cell>

							</fo:table-row>

							<fo:table-row height="4mm">

								<fo:table-cell
									number-columns-spanned="3" />

								<fo:table-cell
									number-columns-spanned="16">
									<fo:block></fo:block>
								</fo:table-cell>
							</fo:table-row>


							<fo:table-row height="5mm">
								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row height="4mm">
								<fo:table-cell
									number-columns-spanned="18">
									<fo:block>
										<fo:inline font-weight="bold">
											RESEARCH RISK
										</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row height="2mm"
								keep-with-next="always" />

							<fo:table-row height="4mm">

								<fo:table-cell number-columns-spanned="1">
									<fo:block>YES</fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block text-indent="5mm">NO</fo:block>
								</fo:table-cell>

							</fo:table-row>

                            <xsl:apply-templates select="PROPOSAL/ROUTING_FORM/RESEARCH_RISKS/RESEARCH_RISK"/>

							<fo:table-row height="5mm">
								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row>
								<fo:table-cell
									number-columns-spanned="5">
									<fo:block>
										<fo:inline font-weight="bold">
											PROJECT DETAILS
										</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row height="2mm" />

							<fo:table-row height="5mm">

								<fo:table-cell number-columns-spanned="1">
									<fo:block>YES</fo:block>
								</fo:table-cell>

								<fo:table-cell>
									<fo:block text-indent="5mm">NO</fo:block>
								</fo:table-cell>

							</fo:table-row>

                            <xsl:apply-templates select="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/QUESTION"/>

							<fo:table-row height="5mm" />

							<fo:table-row>
								<fo:table-cell
									number-columns-spanned="5">
									<fo:block>
										<fo:inline font-weight="bold">
											APPROVALS
										</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>

							<fo:table-row height="2mm" />

							<fo:table-row height="6mm">

								<fo:table-cell
									xsl:use-attribute-sets="abd" number-columns-spanned="9"
									display-align="center" padding-top="0.9mm">
									<fo:block text-indent="1mm">
										<fo:inline font-weight="bold">
											Name
										</fo:inline>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									xsl:use-attribute-sets="abd" number-columns-spanned="3"
									display-align="center" padding-top="0.9mm">
									<fo:block text-indent="1mm">
										<fo:inline font-weight="bold">
											Title/Role
										</fo:inline>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									xsl:use-attribute-sets="abd" number-columns-spanned="2"
									display-align="center" padding-top="0.9mm">
									<fo:block text-indent="0.5mm">
										<fo:inline font-weight="bold">
											Chart
										</fo:inline>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									xsl:use-attribute-sets="abd" number-columns-spanned="2"
									display-align="center" padding-top="0.9mm">
									<fo:block text-indent="1mm">
										<fo:inline font-weight="bold">
											Org
										</fo:inline>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									xsl:use-attribute-sets="abd" number-columns-spanned="4"
									display-align="center" padding-top="0.9mm">
									<fo:block text-indent="1mm">
										<fo:inline font-weight="bold">
											Action
										</fo:inline>
									</fo:block>
								</fo:table-cell>

								<fo:table-cell
									xsl:use-attribute-sets="abcd" number-columns-spanned="4"
									display-align="center" padding-top="0.9mm">
									<fo:block text-indent="1mm">
										<fo:inline font-weight="bold">
											Action Date
										</fo:inline>
									</fo:block>
								</fo:table-cell>

							</fo:table-row>
							
							<!-- The two sorts assure that items without an action date appear last. -->
							<xsl:apply-templates select="PROPOSAL/ROUTING_FORM/APPROVALS/APPROVER">
							    <xsl:sort select="@ACTION_DATE=''"/>
							    <xsl:sort select="@ACTION_DATE"/>
							</xsl:apply-templates>

							<!-- Those variables define the existance (or non existance)
								of Additional Detail items. If no Additional Detail item
								exists then the Additional Detail item heading isn't
								shows. Purpose of the variables is so not to replicate
								the conditions. -->
                            <xsl:variable name="ADDITIONAL_DEL_SET" select="not(/PROPOSAL/ROUTING_FORM/AGENCY/AGENCY_DELIVERY/ADDITIONAL_DELIVERY_INSTRUCTIONS = '')"/>
                            <xsl:variable name="RESEARCH_RISKS_SET" select="/PROPOSAL/ROUTING_FORM/RESEARCH_RISKS/@ANY_STUDY_SELECTED = 'Y'"/>
							<xsl:variable name="COST_SHARE_SET" select="/PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/INST_COST_SHARE or /PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/OTHER_COST_SHARE" />
                            <xsl:variable name="SUBCONTRACTS_SET" select="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/SUBCONTRACTOR"/>
							<xsl:variable name="CO-PD_SET" select="$principals/@CO-PD_IND = 'Y'" />
                            <xsl:variable name="OTHER_SET" select="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/OTHER_INST_ORG"/>
                            <xsl:variable name="ALLOCATE_CREDIT_SET" select="count(PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/PERCENT_CREDIT) > 0"/>
                            <xsl:variable name="KEYWORDS_SET" select="count(PROPOSAL/ROUTING_FORM/KEYWORDS/KEYWORD) > 0"/>
							<xsl:variable name="COMMENTS_SET" select="count(PROPOSAL/ROUTING_FORM/COMMENTS/COMMENT) > 0" />

			                <xsl:if test="$ADDITIONAL_DEL_SET
			                              or $RESEARCH_RISKS_SET
			                              or $COST_SHARE_SET
			                              or $SUBCONTRACTS_SET
			                              or $CO-PD_SET
			                              or $OTHER_SET
			                              or $ALLOCATE_CREDIT_SET
			                              or $KEYWORDS_SET
			                              or $COMMENTS_SET">
								<fo:table-row height="7mm">
									<fo:table-cell
										number-columns-spanned="23" xsl:use-attribute-sets="d">
										<fo:block></fo:block>
									</fo:table-cell>
								</fo:table-row>
	
								<fo:table-row font-size="11pt"
									height="8mm">
									<fo:table-cell
										number-columns-spanned="12" display-align="center">
										<fo:block>
											<fo:inline font-weight="bold">
												ADDITIONAL DETAIL
											</fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row height="2mm" />
							</xsl:if>

							<!-- ADDITIONAL DELIVERY INSTRUCTIONS TABLE -->
							<xsl:if test="$ADDITIONAL_DEL_SET">
								<fo:table-row height="5mm">
									<fo:table-cell height="5mm" number-columns-spanned="12" display-align="center">
										<fo:block>
											<fo:inline font-weight="bold">
												ADDITIONAL DELIVERY INSTRUCTIONS
											</fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<!-- Additional Delivery Instructions Values -->
								<fo:table-row height="10mm">
									<fo:table-cell number-columns-spanned="23" xsl:use-attribute-sets="abcd" padding-left="1mm">
										<fo:block>
											<xsl:value-of select="/PROPOSAL/ROUTING_FORM/AGENCY/AGENCY_DELIVERY/ADDITIONAL_DELIVERY_INSTRUCTIONS" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row height="5mm" />
							</xsl:if>

							<!-- RESEARCH RISKS TABLE -->
							<xsl:if test="$RESEARCH_RISKS_SET">
								<fo:table-row keep-with-next="always">
									<fo:table-cell
										number-columns-spanned="5">
										<fo:block>
											<fo:inline
												font-weight="bold">
												RESEARCH RISK
											</fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row keep-with-next="always"
									height="2mm" />
								<fo:table-row keep-with-next="always"
									height="6mm">

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="6"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline
												font-weight="bold">
												Study Type
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="3"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline
												font-weight="bold">
												Approval Status
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="2"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline
												font-weight="bold">
												Study Number
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="4"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline
												font-weight="bold">
												Approval Date
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abcd" number-columns-spanned="5"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline
												font-weight="bold">
												Review Code
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abcd" number-columns-spanned="3"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline font-weight="bold">
												Exemption #
											</fo:inline>
										</fo:block>
									</fo:table-cell>

								</fo:table-row>

								<!-- Research Risks Values -->
								<xsl:apply-templates select="PROPOSAL/ROUTING_FORM/RESEARCH_RISKS/RESEARCH_RISK/STUDY" />

								<fo:table-row height="5mm" />
							</xsl:if>

							<!-- COST SHARE TABLE -->
							<xsl:if test="$COST_SHARE_SET">
								<fo:table-row keep-with-next="always">
									<fo:table-cell
										number-columns-spanned="5">
										<fo:block>
											<fo:inline
												font-weight="bold">
												COST SHARE
											</fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row keep-with-next="always"
									height="2mm" />

								<fo:table-row keep-with-next="always"
									height="6mm">

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="7"
										display-align="center" padding-top="0.9mm"
										padding-left="1mm">
										<fo:block text-indent="1mm">
											<fo:inline
												font-weight="bold">
												Chart
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="2"
										display-align="center" padding-top="0.9mm"
										padding-left="1mm">
										<fo:block>
											<fo:inline
												font-weight="bold">
												Org
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="8"
										display-align="center" padding-top="0.9mm"
										padding-left="1mm">
										<fo:block>
											<fo:inline
												font-weight="bold">
												Account
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abcd" number-columns-spanned="6"
										display-align="center" padding-top="0.9mm">
										<fo:block text-align="center">
											<fo:inline
												font-weight="bold">
												Amount
											</fo:inline>
										</fo:block>
									</fo:table-cell>

								</fo:table-row>

								<!-- Institution Cost Share Values -->
								<!-- 06/25/2003 pcberg: Added. -->
								<xsl:apply-templates
									select="$details/INST_COST_SHARE" />

								<fo:table-row height="6mm">

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="17"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											Total Institution Cost Share
										</fo:block>
									</fo:table-cell>

									<!--  The condition " [. &gt; 0] " prevents "NAN" from being 	displayed when there is no entry for AMOUT under INST_COST_SHARE   Amount is a required field under the first DTD, but it is not clear that this will always be enforced, or that a user might not enter non-numerical character values.  Attempts to include empty strings or non-numerical characters in the sum() argument results in "NAN" being displayed.   -->

									<fo:table-cell
										xsl:use-attribute-sets="abcd" number-columns-spanned="6"
										display-align="center" padding-top="0.9mm" text-align="right"
										padding-right="1mm">
										<fo:block text-indent="1mm">
											$
											<xsl:value-of
												select="format-number(sum(PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/INST_COST_SHARE/@AMOUNT [. &gt; 0]), '###,###'  )" />
										</fo:block>
									</fo:table-cell>

								</fo:table-row>
								<fo:table-row keep-with-next="always"
									height="6mm">

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="17"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm"
											padding-right="1mm">
											Total Third Party Cost Share
										</fo:block>
									</fo:table-cell>


									<fo:table-cell
										xsl:use-attribute-sets="abcd" number-columns-spanned="6"
										display-align="center" padding-top="0.9mm" text-align="right"
										padding-right="1mm">
										<fo:block text-indent="1mm">
											$
											<xsl:value-of
												select="format-number(sum($details/OTHER_COST_SHARE/@AMOUNT[. &gt; 0]), '###,###') " />
										</fo:block>
									</fo:table-cell>

								</fo:table-row>

								<fo:table-row keep-with-next="always"
									height="6mm">

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="17"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											Total Cost Share
										</fo:block>
									</fo:table-cell>


									<fo:table-cell
										xsl:use-attribute-sets="abcd" number-columns-spanned="6"
										display-align="center" padding-top="0.9mm"
										padding-right="1mm">
										<fo:block text-indent="1mm"
											text-align="right">
											$
											<xsl:value-of
												select="format-number(sum(PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/INST_COST_SHARE/@AMOUNT [. &gt; 0]) +  sum($details/OTHER_COST_SHARE/@AMOUNT[. &gt; 0]), '###,###'  )" />
										</fo:block>
									</fo:table-cell>

								</fo:table-row>

								<fo:table-row height="6mm" />
							</xsl:if>

							<!--  The keep-with-next="always" row attribute definition assures that there is no page break between succeeding rows with that attribute.  The row definition in the PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/SUBCONTRACTOR template below also has the keep-with-next attribute.  -->

							<!-- SUBCONTRACTS TABLE -->
							<xsl:if test="$SUBCONTRACTS_SET">
								<fo:table-row keep-with-next="always">
									<fo:table-cell
										number-columns-spanned="15">
										<fo:block>
											<fo:inline
												font-weight="bold">
												SUBCONTRACT(S)
											</fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row keep-with-next="always"
									height="2mm" />

								<fo:table-row keep-with-next="always"
									height="6mm">

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="17"
										display-align="center" padding-top="0.9mm"
										padding-left="1mm">
										<fo:block>
											<fo:inline
												font-weight="bold">
												Source
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abcd" number-columns-spanned="6"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm"
											text-align="center">
											<fo:inline
												font-weight="bold">
												Amount
											</fo:inline>
										</fo:block>
									</fo:table-cell>

								</fo:table-row>

								<!-- s Values -->
								<xsl:apply-templates select="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/SUBCONTRACTOR" />

								<fo:table-row height="6mm" />
							</xsl:if>

							<!-- CO-PROJECT DIRECTORS TABLE -->
							<xsl:if test="$CO-PD_SET">
								<fo:table-row keep-with-next="always">
									<fo:table-cell
										number-columns-spanned="15">
										<fo:block>
											<fo:inline
												font-weight="bold">
												CO-PROJECT DIRECTOR(S)
											</fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row height="2mm"
									keep-with-next="always" />

								<fo:table-row height="6mm">

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="19"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline
												font-weight="bold">
												Name
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="2"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline
												font-weight="bold">
												Chart
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abcd" number-columns-spanned="1"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline
												font-weight="bold">
												Org
											</fo:inline>
										</fo:block>
									</fo:table-cell>

								</fo:table-row>

								<!-- Co-Project Directors Values -->
								<xsl:apply-templates
									select="$principals/CO-PROJECT_DIRECTORS/CO-PROJECT_DIRECTOR"
									mode="one" />

								<fo:table-row height="6mm" />
							</xsl:if>

							<!-- OTHER INSTITUTION CAMPUSES etc. TABLE -->
							<xsl:if test="$OTHER_SET">
								<fo:table-row keep-with-next="always">
									<fo:table-cell number-columns-spanned="15">
										<fo:block>
											<fo:inline font-weight="bold">
												OTHER INSTITUTION CAMPUSES, SCHOOLS OR UNITS
											</fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row keep-with-next="always" height="2mm" />

								<fo:table-row keep-with-next="always" height="6mm">

									<fo:table-cell xsl:use-attribute-sets="abd" number-columns-spanned="3" display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline font-weight="bold">
												Chart
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell xsl:use-attribute-sets="abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline font-weight="bold">
												Org
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell xsl:use-attribute-sets="abcd" number-columns-spanned="18" display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline font-weight="bold">
												Org Name
											</fo:inline>
										</fo:block>
									</fo:table-cell>

								</fo:table-row>

								<!-- Other Institution Campuses etc. Values -->
								<xsl:apply-templates select="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/OTHER_INST_ORG" />
								<fo:table-row height="6mm" />
							</xsl:if>

                            <!-- % INTELLECTUAL CREDIT TABLE -->
                            <xsl:if test="$ALLOCATE_CREDIT_SET">
								<fo:table-row keep-with-next="always">
									<fo:table-cell
										number-columns-spanned="15">
										<fo:block>
											<fo:inline font-weight="bold">
												% INTELLECTUAL CREDIT
											</fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
	
								<fo:table-row keep-with-next="always"
									height="2mm" />
	
								<fo:table-row keep-with-next="always"
									height="6mm">
	
									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="14"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline font-weight="bold">
												Name
											</fo:inline>
										</fo:block>
									</fo:table-cell>
	
									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="4"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline font-weight="bold">
												Chart
											</fo:inline>
										</fo:block>
									</fo:table-cell>
	
									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="2"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline font-weight="bold">
												Org
											</fo:inline>
										</fo:block>
									</fo:table-cell>
	
									<fo:table-cell
										xsl:use-attribute-sets="abcd" number-columns-spanned="1"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline font-weight="bold">
												% F&amp;A
											</fo:inline>
										</fo:block>
									</fo:table-cell>
	
									<fo:table-cell
										xsl:use-attribute-sets="abcd" number-columns-spanned="1"
										display-align="center" padding-top="0.9mm">
										<fo:block text-indent="1mm">
											<fo:inline font-weight="bold">
												% Credit
											</fo:inline>
										</fo:block>
									</fo:table-cell>
	
								</fo:table-row>
	
								<!-- % Interlectual Credit Values -->
								<xsl:apply-templates select="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/PERCENT_CREDIT" mode="three" />
								
								<fo:table-row height="6mm" />
							</xsl:if>

							<!-- KEYWORDS TABLE -->
							<xsl:if test="$KEYWORDS_SET">
								<fo:table-row keep-with-next="always"
									height="6mm">
									<fo:table-cell
										number-columns-spanned="22">
										<fo:block>
											<fo:inline
												font-weight="bold">
												KEY WORDS:
											</fo:inline>
											<xsl:value-of
												select="substring(PROPOSAL/ROUTING_FORM/KEY_WORDS, 1, 200)" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row>
									<fo:table-cell
										number-columns-spanned="22">
										<fo:block font-size="10pt">
											<!-- Keywords Values -->
											<xsl:apply-templates
												select="PROPOSAL/ROUTING_FORM/KEYWORDS" />
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<fo:table-row height="6mm" />
							</xsl:if>

							<!-- pcberg 08/14/03: Added Comments. -->
							<!-- COMMENTS TABLE -->
							<xsl:if test="$COMMENTS_SET">
								<fo:table-row keep-with-next="always">
									<fo:table-cell
										number-columns-spanned="22">
										<fo:block>
											<fo:inline
												font-weight="bold">
												COMMENTS
											</fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row height="2mm"
									keep-with-next="always" />

								<fo:table-row height="6mm">
									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="9"
										display-align="center" padding-top="0.9mm"
										padding-left="1mm">
										<fo:block>
											<fo:inline
												font-weight="bold">
												Entered By
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abd" number-columns-spanned="4"
										display-align="center" padding-top="0.9mm"
										padding-left="1mm">
										<fo:block>
											<fo:inline
												font-weight="bold">
												Date and Time Stamp
											</fo:inline>
										</fo:block>
									</fo:table-cell>

									<fo:table-cell
										xsl:use-attribute-sets="abcd" number-columns-spanned="11"
										display-align="center" padding-top="0.9mm"
										padding-left="1mm">
										<fo:block>
											<fo:inline
												font-weight="bold">
												Topic
											</fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>

								<!-- Comment Values -->
								<xsl:apply-templates
									select="PROPOSAL/ROUTING_FORM/COMMENTS" />
							</xsl:if>

						</fo:table-body>
					</fo:table>

					<!--  The following block is used to determine the length of the document.  This information is used to output page numbering.    -->

					<fo:block id="End" />

				</fo:flow>
			</fo:page-sequence>

		</fo:root>
	</xsl:template>

	<xsl:template name="Toggle">

		<xsl:param name="checkboxNode" />

		<xsl:if test="$checkboxNode = 'Y'">
			<fo:external-graphic height="4mm" src="url({$imagesUrl}/xslt-checkboxChecked.gif)" />
		</xsl:if>

		<xsl:if test="$checkboxNode = 'N'  or $checkboxNode = 'Z' or $checkboxNode = ''">
			<fo:external-graphic height="4mm" src="url({$imagesUrl}/xslt-checkbox.gif)" />
		</xsl:if>

	</xsl:template>


	<xsl:template name="Toggle_No">

		<xsl:param name="checkboxNode" />

		<xsl:if test="$checkboxNode = 'N'">
			<fo:external-graphic height="4mm"
				src="url({$imagesUrl}/xslt-checkboxChecked.gif)" />
		</xsl:if>

		<xsl:if test="$checkboxNode = 'Y' or $checkboxNode = 'Z' or $checkboxNode = ''">
			<fo:external-graphic height="4mm"
				src="url({$imagesUrl}/xslt-checkbox.gif)" />
		</xsl:if>

	</xsl:template>


	<xsl:template name="ToggleCOPD_Yes">

		<xsl:choose>

			<xsl:when test="$principals/@CO-PD_IND = 'Y' ">
				<fo:external-graphic height="4mm" src="url({$imagesUrl}/xslt-checkboxChecked.gif)" />
			</xsl:when>

			<xsl:otherwise>
				<fo:external-graphic height="4mm" src="url({$imagesUrl}/xslt-checkbox.gif)" />
			</xsl:otherwise>

		</xsl:choose>

	</xsl:template>


	<xsl:template name="ToggleCOPD_No">

		<xsl:choose>

			<xsl:when test="$principals/@CO-PD_IND = 'N' ">
				<fo:external-graphic height="4mm" src="url({$imagesUrl}/xslt-checkboxChecked.gif)" />
			</xsl:when>

			<xsl:otherwise>
				<fo:external-graphic height="4mm" src="url({$imagesUrl}/xslt-checkbox.gif)" />
			</xsl:otherwise>

		</xsl:choose>

	</xsl:template>


	<xsl:template name="ToggleAdditionalInst_Yes">

		<xsl:choose>

			<xsl:when
				test="not(/PROPOSAL/ROUTING_FORM/AGENCY/AGENCY_DELIVERY/ADDITIONAL_DELIVERY_INSTRUCTIONS = '')">
				<fo:external-graphic height="4mm"
					src="url({$imagesUrl}/xslt-checkboxChecked.gif)" />
			</xsl:when>

			<xsl:otherwise>
				<fo:external-graphic height="4mm"
					src="url({$imagesUrl}/xslt-checkbox.gif)" />
			</xsl:otherwise>

		</xsl:choose>

	</xsl:template>


	<xsl:template name="ToggleAdditionalInst_No">

		<xsl:choose>

			<xsl:when
				test="/PROPOSAL/ROUTING_FORM/AGENCY/AGENCY_DELIVERY/ADDITIONAL_DELIVERY_INSTRUCTIONS = ''">
				<fo:external-graphic height="4mm"
					src="url({$imagesUrl}/xslt-checkboxChecked.gif)" />
			</xsl:when>

			<xsl:otherwise>
				<fo:external-graphic height="4mm"
					src="url({$imagesUrl}/xslt-checkbox.gif)" />
			</xsl:otherwise>

		</xsl:choose>

	</xsl:template>


	<!--  The following template iterates through all the PROPOSAL/ROUTING_FORM/APPROVALS/APPROVER nodes in the XML file.  These correspond to rows in the database  -->

	<xsl:template match="PROPOSAL/ROUTING_FORM/APPROVALS/APPROVER">

		<fo:table-row height="6mm">

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="9" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<xsl:value-of
						select="concat(NAME/@LAST, ', ', NAME/@FIRST)" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="3" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<fo:inline font-weight="bold">
						<xsl:value-of select="@TITLE" />
					</fo:inline>
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="2" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<xsl:value-of select="@CHART" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="2" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<xsl:value-of select="@ORG" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="4" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<xsl:value-of select="@ACTION" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abcd"
				number-columns-spanned="4" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<xsl:value-of select="@ACTION_DATE" />
				</fo:block>
			</fo:table-cell>

		</fo:table-row>
	</xsl:template>


	<xsl:template match="PROPOSAL/ROUTING_FORM/RESEARCH_RISKS/RESEARCH_RISK/STUDY">
			<fo:table-row height="6mm">
				<fo:table-cell xsl:use-attribute-sets="abd" number-columns-spanned="6" display-align="center" padding-top="0.9mm">
					<fo:block text-indent="1mm">
					    <xsl:value-of select="../@TYPE_DESCRIPTION" />
					</fo:block>
				</fo:table-cell>
	
				<fo:table-cell xsl:use-attribute-sets="abd" number-columns-spanned="3" display-align="center" padding-top="0.9mm">
					<fo:block text-indent="1mm">
						<fo:block text-indent="1mm">
							<xsl:value-of select="APPROVAL_STATUS" />
						</fo:block>
					</fo:block>
				</fo:table-cell>
	
				<fo:table-cell xsl:use-attribute-sets="abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
					<fo:block text-indent="1mm">
						<xsl:value-of select="STUDY_NUMBER" />
					</fo:block>
				</fo:table-cell>
	
				<fo:table-cell xsl:use-attribute-sets="abd" number-columns-spanned="4" display-align="center" padding-top="0.9mm">
					<fo:block text-indent="1mm">
						<xsl:value-of select="APPROVAL_DATE" />
					</fo:block>
				</fo:table-cell>
	
				<fo:table-cell xsl:use-attribute-sets="abcd" number-columns-spanned="5" display-align="center" padding-top="0.9mm">
					<fo:block text-indent="1mm">
					    <xsl:value-of select="STUDY_REVIEW_STATUS" />
					</fo:block>
				</fo:table-cell>
	
				<fo:table-cell xsl:use-attribute-sets="abcd" number-columns-spanned="3" display-align="center" padding-top="0.9mm">
					<fo:block text-indent="1mm">
						<xsl:value-of select="EXEMPTION_NBR" />
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
	</xsl:template>

	<xsl:template match="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/SUBCONTRACTOR">

		<fo:table-row height="6mm">

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="17" display-align="center"
				padding-left="1mm">
				<fo:block>
					<xsl:value-of select="@SOURCE" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abcd"
				number-columns-spanned="6" display-align="center"
				padding-right="1mm">

				<fo:block text-indent="1mm" text-align="right">
					$
					<xsl:value-of
						select="format-number(@AMOUNT, '###,###')" />
				</fo:block>

			</fo:table-cell>

		</fo:table-row>

	</xsl:template>


	<xsl:template name="Other">


		<fo:table-row height="6mm">

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="10" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">BL</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="8" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">CHEM</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abcd"
				number-columns-spanned="5" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm" text-align="center">
					COAS
				</fo:block>
			</fo:table-cell>

		</fo:table-row>

	</xsl:template>


	<xsl:template
		match="PROPOSAL/ROUTING_FORM/PRINCIPLES/CO-PROJECT_DIRECTORS/CO-PROJECT_DIRECTOR"
		mode="one">

		<fo:table-row height="6mm">

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="19" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<fo:block text-indent="1mm">
						<xsl:value-of
							select="concat(@LAST_NAME, ', ' ,@FIRST_NAME)" />
					</fo:block>
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="2" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<xsl:value-of select="@CHART" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abcd"
				number-columns-spanned="1" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<xsl:value-of select="@ORG" />
				</fo:block>
			</fo:table-cell>

		</fo:table-row>

	</xsl:template>

	<xsl:template match="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/OTHER_INST_ORG">

		<fo:table-row height="6mm">

			<fo:table-cell xsl:use-attribute-sets="abd" number-columns-spanned="3" display-align="center" padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<fo:block text-indent="1mm">
						<xsl:value-of select="@CHART" />
					</fo:block>
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abd" number-columns-spanned="2" display-align="center" padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<xsl:value-of select="@ORG" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abcd" number-columns-spanned="18" display-align="center" padding-top="0.9mm" padding-left="1mm">
				<fo:block>
					<xsl:value-of select="substring(@ORG_NAME,1,70)" />
				</fo:block>
			</fo:table-cell>

		</fo:table-row>

	</xsl:template>

	<xsl:template
		match="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/PERCENT_CREDIT"
		mode="three">

		<!--  I DO NOT UNDERSTAND WHY " DOES NOT WORK HERE.  IT WORKS FIND IN COST SHARE!    -->

		<fo:table-row height="6mm">

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="14" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<xsl:value-of select="@NAME" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="4" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<xsl:value-of select="@CHART" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="2" display-align="center"
				padding-top="0.9mm">
				<fo:block text-indent="1mm">
					<xsl:value-of select="@ORG" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abcd"
				number-columns-spanned="1" display-align="center"
				padding-top="0.9mm" padding-right="1mm">
				<fo:block text-indent="1mm" text-align="right">
					<xsl:value-of select="@FA" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abcd"
				number-columns-spanned="1" display-align="center"
				padding-top="0.9mm" padding-right="1mm">
				<fo:block text-indent="1mm" text-align="right">
					<xsl:value-of select="@CREDIT" />
				</fo:block>
			</fo:table-cell>

		</fo:table-row>


	</xsl:template>


	<xsl:template
		match="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/INST_COST_SHARE">


		<fo:table-row height="6mm">

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="7" display-align="center"
				padding-top="0.9mm" padding-left="1mm">
				<fo:block>
					<xsl:value-of select=" @CHART" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="2" display-align="center"
				padding-top="0.9mm" padding-left="1mm">
				<fo:block>
					<xsl:value-of select=" @ORG" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="8" display-align="center"
				padding-top="0.9mm" padding-left="1mm">
				<fo:block>
					<xsl:value-of select=" @ACCOUNT" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abcd"
				number-columns-spanned="6" display-align="center"
				padding-top="0.9mm" text-align="right" padding-right="1mm">
				<fo:block text-indent="1mm" padding-right="1mm">
					$
					<xsl:value-of
						select="format-number(@AMOUNT, '###,###')" />
				</fo:block>
			</fo:table-cell>

		</fo:table-row>

	</xsl:template>

	<xsl:template name="SeeAttached">

		<xsl:choose>

			<xsl:when test="not(PROPOSAL/ROUTING_FORM/AGENCY/AGENCY_DELIVERY/ADDITIONAL_DELIVERY_INSTRUCTIONS = '')">
				<fo:block>
					Additional delivery instructions
					<fo:inline font-weight="bold">
						**SEE ATTACHED**
					</fo:inline>
				</fo:block>
			</xsl:when>

			<xsl:otherwise>
				<fo:block>Additional delivery instructions</fo:block>
			</xsl:otherwise>

		</xsl:choose>

	</xsl:template>


	<xsl:template match="PROPOSAL/ROUTING_FORM/KEYWORDS/KEYWORD">

		<xsl:value-of select="concat(., ', ')   " />

	</xsl:template>

	<!-- pcberg 08/14/03: Added Comments. -->
	<xsl:template match="PROPOSAL/ROUTING_FORM/COMMENTS/COMMENT">
		<fo:table-row height="6mm">
			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="9" display-align="center"
				padding-top="0.9mm" padding-left="1mm">
				<fo:block>
					<xsl:value-of select="COMMENTATOR" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abd"
				number-columns-spanned="4" display-align="center"
				padding-top="0.9mm" padding-left="1mm">
				<fo:block>
					<xsl:value-of select="COMMENT_TIMESTAMP" />
				</fo:block>
			</fo:table-cell>

			<fo:table-cell xsl:use-attribute-sets="abcd"
				number-columns-spanned="11" display-align="center"
				padding-top="0.9mm" padding-left="1mm">
				<fo:block>
					<xsl:value-of select="COMMENT_TOPIC" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>

		<fo:table-row height="6mm">
			<fo:table-cell xsl:use-attribute-sets="abcd"
				number-columns-spanned="24" display-align="center"
				padding-top="0.9mm" padding-left="2mm">
				<fo:block padding-right="1mm">
					<fo:inline font-weight="bold">Comment:</fo:inline>
					<xsl:value-of select="COMMENT_TEXT" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<xsl:template match="PROPOSAL/ROUTING_FORM/PURPOSES/PURPOSE" mode="checkboxes">
	    <fo:block>
	        <xsl:call-template name="Toggle">
			    <xsl:with-param name="checkboxNode" select="@SELECTED" />
			</xsl:call-template>
	    </fo:block>
	</xsl:template>

	<xsl:template match="PROPOSAL/ROUTING_FORM/PURPOSES/PURPOSE" mode="labels">
	    <fo:block text-indent="1.5mm" padding-top="1.05mm">
    	    <xsl:value-of select="." />
    	    <xsl:if test="@CODE = 'F'">
    	        <xsl:text>: </xsl:text><xsl:value-of select="substring(../PURPOSE_OTHER_DESCRIPTION, 1, 100)" />
    	    </xsl:if>
  	    </fo:block>
	</xsl:template>

	<xsl:template match="PROPOSAL/ROUTING_FORM/TYPES/TYPE" mode="checkboxes">
	    <fo:block>
	        <xsl:call-template name="Toggle">
			    <xsl:with-param name="checkboxNode" select="@SELECTED" />
			</xsl:call-template>
	    </fo:block>
	</xsl:template>

	<xsl:template match="PROPOSAL/ROUTING_FORM/TYPES/TYPE" mode="labels">
	    <fo:block text-indent="1.5mm" padding-top="1.05mm">
    	    <xsl:value-of select="." />
    	    <xsl:if test="@CODE = 'O'">
    	        <xsl:text> </xsl:text><xsl:value-of select="substring(../TYPE_OTHER_DESCRIPTION, 1, 30)" />
    	    </xsl:if>
  	    </fo:block>
	</xsl:template>

	<xsl:template match="PROPOSAL/ROUTING_FORM/PROJECT_DETAIL/QUESTION">
		<fo:table-row height="4mm" display-align="after">
			<fo:table-cell number-columns-spanned="1">
				<fo:block>
					<xsl:call-template name="Toggle">
						<xsl:with-param name="checkboxNode" select="@SELECTED" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
	
			<fo:table-cell number-columns-spanned="2">
				<fo:block text-indent="5mm">
					<xsl:call-template name="Toggle_No">
						<xsl:with-param name="checkboxNode" select="@SELECTED" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
	
			<fo:table-cell number-columns-spanned="18">
				<fo:block>
					<xsl:value-of select="." />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

	<xsl:template match="PROPOSAL/ROUTING_FORM/RESEARCH_RISKS/RESEARCH_RISK">
		<fo:table-row height="4mm" display-align="after">
			<fo:table-cell number-columns-spanned="1">
				<fo:block>
					<xsl:call-template name="Toggle">
						<xsl:with-param name="checkboxNode" select="@SELECTED" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
	
			<fo:table-cell number-columns-spanned="2">
				<fo:block text-indent="5mm">
					<xsl:call-template name="Toggle_No">
						<xsl:with-param name="checkboxNode" select="@SELECTED" />
					</xsl:call-template>
				</fo:block>
			</fo:table-cell>
	
			<fo:table-cell number-columns-spanned="8">
				<fo:block>
					<xsl:value-of select="@TYPE_DESCRIPTION" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>

</xsl:stylesheet>
