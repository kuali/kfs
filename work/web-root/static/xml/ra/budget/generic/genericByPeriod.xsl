<?xml version="1.0"?>
<!--
 Copyright 2006 The Kuali Foundation.
 
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
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:xalan="http://xml.apache.org/xalan" 
	exclude-result-prefixes="xalan">
	
	<!--
	2005-11-14 dterret:	In order to merge this stylesheet with taskByPeriod, one would have to 
	pass in a parameter in the xml and define variables or keys which would flexibly allow the 
	user to change the nodeset implied in a particular location in the code according to the 
	task/period parameter. These are the nodesets which, in this stylesheet, contain a
	[@PERIOD_NUMBER = $periodNumber]. Some additional logic may be required in places. 
	The Ordering Wrapper template present in this stylesheet (and not in the other one) 
	is a necessary step along this road, if it is desired to use as much as possible of 
	existing code.
	-->
	
	<!--                           VERSION HISTORY
		- 11/2005: dterret@indiana.edu, Copied over from taskByPeriod, modified.
    - 05/2006: pcberg@indiana.edu, refactoring for KRA.
               - Replaced (last() - 1) with last(). Seems a library changed behavior.
               - IU COST = INSTITUTION COST
               - OUTPUT_DETAIL_LEVEL = PARAMETER1
               - IU = INSTITUTION
               - UNIV = INSTITUTION
               - @TASK = @TASK_NUMBER
               - @PERIOD = @PERIOD_NUMBER
               - Added variables myUniversity, baseUrl, and imageSeal.
               - CREATE_TIMESTAMP = SEQUENCE_NUMBER
	-->	
	
	<!-- ******************* **************************** ******************* -->
	<!-- *******************         Variables            ******************* -->
	<!-- ******************* **************************** ******************* -->
	
    <xsl:variable name="myUniversity" select="''"/>
    <xsl:variable name="baseUrl" select="/PROPOSAL/BUDGET/@BASE_URL"/>
    <xsl:variable name="imageSeal" select="'/images-xslt/myuniv.gif'"/>
	
	<xsl:variable name="countPeriods" select="count(/PROPOSAL/BUDGET/PERIODS/PERIOD[*])"/>
	
	<!-- The string for the personnel name which should not be collapsed. -->
	<xsl:variable name="TBN">To Be Named</xsl:variable>
	
	<!-- Following is used for sorting of Non personnel detail categories. -->
	<xsl:variable name="CATEGORYMAP">
		<nonp>
			<category name="Consultants" value="1"/>
			<category name="Equipment" value="2"/>
			<category name="Supplies" value="3"/>
			<category name="Travel" value="4"/>
			<category name="Patient Care Costs" value="5"/>
			<category name="Fellowships" value="6"/>
			<category name="Participant Expenses" value="7"/>
			<category name="Other Expenses" value="8"/>
			<category name="Subcontractors" value="9"/>
		</nonp>
	</xsl:variable>
	
	<!-- Following variables needed for case conversion. -->
	<xsl:variable name="uppercaseChars">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
	<xsl:variable name="lowercaseChars">abcdefghijklmnopqrstuvwxyz</xsl:variable>
	
	<!-- Following are the attribute sets. -->
	<xsl:attribute-set name='tableCell'>
		<xsl:attribute name="padding-left">3px</xsl:attribute>
		<xsl:attribute name="padding-right">3px</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name='tableCellHeader'>
		<xsl:attribute name="border-top-style">double</xsl:attribute>
		<xsl:attribute name="border-top-width">2.0px</xsl:attribute>
		<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
		<xsl:attribute name="border-bottom-width">0.2px</xsl:attribute>
		<xsl:attribute name="display-align">center</xsl:attribute>
		<xsl:attribute name="text-align">center</xsl:attribute>
		<xsl:attribute name="padding-top">3px</xsl:attribute>
		<xsl:attribute name="padding-bottom">3px</xsl:attribute>
		<xsl:attribute name="padding-left">3px</xsl:attribute>
		<xsl:attribute name="padding-right">3px</xsl:attribute>
		<!-- Following line causes a bug in FOP on overflow pages? -->
		<!-- <xsl:attribute name="font-variant">small-caps</xsl:attribute> -->
		<xsl:attribute name="font-family">Times</xsl:attribute>
		<xsl:attribute name="font-size">8pt</xsl:attribute>
		<xsl:attribute name="line-height">10pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name='tableCellLeftBorder' use-attribute-sets='tableCell'>
		<xsl:attribute name="border-left-style">solid</xsl:attribute>
		<xsl:attribute name="border-left-width">0.2px</xsl:attribute>
		<!-- Next attribute is needed so that empty rows are rendered, the
		height of an empty row should probably equal font-size -->
		<xsl:attribute name="height">8pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name='defaultText'>
		<xsl:attribute name="font-family">Times</xsl:attribute>
		<xsl:attribute name="font-size">8pt</xsl:attribute>
		<xsl:attribute name="line-height">10pt</xsl:attribute>
	</xsl:attribute-set>
	<xsl:attribute-set name='defaultTextIndented' use-attribute-sets='defaultText'>
		<xsl:attribute name="start-indent">6pt</xsl:attribute>
	</xsl:attribute-set>
	
	<!-- Following key is used to retrieve people of a certain period with
	a certain name, appointment_type, and timestamp. Used to
	collapse duplicates in personnel summaries. -->
	<xsl:key name="PEOPLE"
		match="PERSON"
		use="concat(../../@PERIOD_NUMBER, ' ',
								NAME, ' ',
								APPOINTMENT/@APPOINTMENT_TYPE, ' ',
								@SEQUENCE_NUMBER)" />
	
	<!-- 4/18/2005 dterret: This key was added as part of the fix to the problem 
	of the Unrecovered Indirect Cost not showing up in the Institution Total Cost Share 
	and Total Cost for each Task and Period. It is used to retrieve the 
	Unrecovered Indirect in each Task_Period.-->
	<xsl:key name="UNRECOVERED"
		match="/PROPOSAL/BUDGET/INDIRECT_COST/INDIRECT_COST_TASK/
		/INDIRECT_COST_TASK_PERIOD
		/INDIRECT_COST_TASK_PERIOD_INSTITUTION
		/UNRECOVERED_INDIRECT_COST"
		use="concat( ../../../@TASK_NUMBER,' ',../../@PERIOD_NUMBER )"/>
	
	<!-- 11/7/2005 dterret: This key was added to help disconnect the order in which
	we print TaskPeriods from their order in the XML. -->
	<xsl:key name="taskPeriodsByPeriod"
		match="/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD"
		use="@PERIOD_NUMBER"/>
	
	<xsl:key name="personSalaryKey"
		match="//PERSON"
		use="concat(@PERIOD_SALARY, ' ',
		            NAME, ' ',
								APPOINTMENT/@APPOINTMENT_CODE, ' ',
								@SEQUENCE_NUMBER)"/>
	
	<xsl:key name="personSalaryPeriodKey"
		match="//PERSON"
		use="concat(@PERIOD_SALARY, ' ',
		            ../../@PERIOD_NUMBER, ' ',
		            NAME, ' ',
		            APPOINTMENT/@APPOINTMENT_CODE, ' ',
		            @SEQUENCE_NUMBER)"/>
	
	<xsl:key name="uniquePersonsKey"
		match="//PERSON"
		use="concat(NAME, ' ',
								APPOINTMENT/@APPOINTMENT_CODE, ' ',
								@SEQUENCE_NUMBER)"/>
		
	<!-- ******************* **************************** ******************* -->
	<!-- *******************         Root Template        ******************* -->
	<!-- ******************* **************************** ******************* -->
	<!-- The root template sets the table layout (size, page numbers, etc.)
	and then calls the main template. -->
	<xsl:template match="/">	
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			
			<fo:layout-master-set>
				<fo:simple-page-master master-name="Legal"
					page-width="216mm" page-height="279mm"
					margin-top="6mm"  margin-bottom="6mm"
					margin-left="12mm" margin-right="6mm">
					<fo:region-body margin-top="2mm"
						margin-bottom="15mm"/>
					<fo:region-after extent="6mm"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			
			<fo:page-sequence master-reference="Legal"
				initial-page-number="1" language="en" country="us">
				
				<fo:static-content flow-name="xsl-region-after">
					<!-- 8/9/2005 dterret: This table added to accomodate Tracking Number. -->
					<fo:table table-layout="fixed">
						<fo:table-column column-width="63mm"/>
						<fo:table-column column-width="63mm"/>
						<fo:table-column column-width="63mm"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-family="Times" font-size="6pt" text-align="left">
										<xsl:value-of select="/PROPOSAL/BUDGET/@XML_CREATE_DATE_TIME"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell/>
								<fo:table-cell>
									<fo:block font-family="Times" font-size="6pt" text-align="right">
										Tracking #: <xsl:value-of select="/PROPOSAL/BUDGET/@BUDGET_NUMBER"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					
					<!-- Page number was not required anymore, thus removed
					(code preserved, just in case & for demonstration purposes:) -->
					<!--
					<fo:block font-family="Times" font-size="6pt" text-align="right">
					Page <fo:page-number/>
					</fo:block>
					-->
				</fo:static-content>
				
				<fo:flow flow-name="xsl-region-body">
					<fo:block>
						<xsl:apply-templates select="PROPOSAL/BUDGET/PERIODS/PERIOD" 
							mode="orderingWrapper"/>
					</fo:block>
				</fo:flow>
				
			</fo:page-sequence>
			
		</fo:root>
	</xsl:template>
	
	<!-- ******************* **************************** ******************* -->
	<!-- *******************      Ordering Wrapper        ******************* -->
	<!-- ******************* **************************** ******************* -->
	<!-- 2005-11-08 dterret: This template was added to call the previously-existing 
	main template in such a way as to change the order in which the TASK_PERIODs are 
	printed from the order they appear in the XML to an order in which they are
	grouped by period.-->
	
	<xsl:template match="PROPOSAL/BUDGET/PERIODS/PERIOD" mode="orderingWrapper">
		<xsl:variable name="period" select="./PERIOD_NUMBER"/>
		<xsl:for-each select="key('taskPeriodsByPeriod', $period )">
			<xsl:call-template name="main">
				<xsl:with-param name="period_num"	select="$period"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	
	<!-- ******************* **************************** ******************* -->
	<!-- *******************         Main Template        ******************* -->
	<!-- ******************* **************************** ******************* -->
	<!-- This template is like a switchboard. It decides what has to be printed
	next (a mere task/period, a period summary or a summary of all periods
	and tasks. -->
	<xsl:template name="main">
		<xsl:param name="period_num"/>
		
		<!-- We always need one Task for one Period (template won't fire if we
		are out of those). -->
		<xsl:call-template name="periodTaskPage"/>
		<!-- We always need a page break, except if there is no summary or
		summary/summary following. -->
		<xsl:if test="not((count(/PROPOSAL/BUDGET/PERIODS/PERIOD[*]) = 1) and (count(/PROPOSAL/BUDGET/TASKS/TASK[*]) = 1))">
			<fo:block break-before="page"/>
		</xsl:if>
		
		<!-- If this was the last TASK in a PERIOD, we need a task summary page
		except if there only was one task. -->
		<xsl:if test="not(following::TASK_PERIOD[@PERIOD_NUMBER = current()/@PERIOD_NUMBER]) and (count(/PROPOSAL/BUDGET/TASKS/TASK[*]) > 1)">
			<xsl:call-template name="summaryPage"/>
			<!-- We always need a page break, except if there isn't a Summary Summary
			following (i.e. "either no summary or if summary then only display
			if there is more then one task"). -->
			<xsl:if test="not(position() = last()) or ((position() = last()) and (count(/PROPOSAL/BUDGET/PERIODS/PERIOD[*]) > 1))">
				<fo:block break-before="page"/>
			</xsl:if>
		</xsl:if>
		
		<!-- If this was the last TASK_PERIOD we need the "summary summary" page
		except if there only was one task. -->
		<xsl:if test="($period_num = $countPeriods) and not(following::TASK_PERIOD[@PERIOD_NUMBER = current()/@PERIOD_NUMBER]) and $countPeriods > 1">
			<xsl:call-template name="summarySummaryPage"/>
			<!-- After Summary Summary there is nothing, thus no page break -->
		</xsl:if>
	</xsl:template>
	
	
	<!-- ******************* **************************** ******************* -->
	<!-- *******************      by Period, by Task      ******************* -->
	<!-- ******************* **************************** ******************* -->
	<xsl:template name="periodTaskPage">
		<!-- ************************* VARIABLES ************************* -->
		<!-- taskNumber is for picking the proper task name in header -->
		<xsl:variable name="taskNumber" select="./@TASK_NUMBER"/>
		<!-- periodNumber is for picking the proper period date range in header -->
		<xsl:variable name="periodNumber" select="./@PERIOD_NUMBER"/>
		<!-- the level of detail used for this output -->
		<xsl:variable name="levelOfDetail" select="/PROPOSAL/BUDGET/@PARAMETER1"/>
		
		<!-- ************************* MAIN TABLE ************************* -->
		<fo:table inline-progression-dimension="190mm" table-layout="fixed">
			<fo:table-column column-width="42mm"/>
			<fo:table-column column-width="34mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="13mm"/>
			<fo:table-column column-width="18mm"/>
			<fo:table-column column-width="13mm"/>
			<fo:table-column column-width="18mm"/>
			<fo:table-column column-width="18mm"/>
			<fo:table-column column-width="18mm"/>
			
			<!-- Table header -->
			<fo:table-header>
				<!-- Header w/Budget information -->
				<fo:table-row>
					<fo:table-cell number-columns-spanned="6">
						<fo:block>
							<xsl:value-of select="/PROPOSAL/BUDGET/PROJECT_DIRECTOR/@FIRST_NAME"/><xsl:text> </xsl:text><xsl:value-of select="/PROPOSAL/BUDGET/PROJECT_DIRECTOR/@LAST_NAME"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="3">
						<fo:block text-align="right" font-size="9pt">
                            <xsl:value-of select="$myUniversity"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell number-columns-spanned="7">
						<fo:block>
							<xsl:value-of
								select="/PROPOSAL/BUDGET/AGENCY/@AGENCY_FULL_NAME"/>
						</fo:block>
						<xsl:for-each select="/PROPOSAL/BUDGET/TASKS/TASK">
							<xsl:if test="$taskNumber = ./TASK_NUMBER">
								<fo:block>
									Task: <xsl:value-of select="./TASK_NAME"/>
								</fo:block>
							</xsl:if> 
						</xsl:for-each>
						<!-- Only put the period if there is more than one -->
						<xsl:if test="/PROPOSAL/BUDGET/PERIODS/PERIOD[2]">
							<fo:block>
								<!-- look for the right one (if out of order) -->
								<xsl:for-each select="/PROPOSAL/BUDGET/PERIODS/PERIOD">
									<xsl:if test="$periodNumber = ./PERIOD_NUMBER">
										<fo:block>
											<xsl:value-of select="./START_DATE_STRING"/> to
											<xsl:value-of select="./STOP_DATE_STRING"/>
										</fo:block>
										<fo:block>
											Period: <xsl:value-of select="./PERIOD_NUMBER"/>
										</fo:block>
									</xsl:if> 
								</xsl:for-each>
							</fo:block>
						</xsl:if>
						<fo:block font-weight="bold" padding-top="16px">
							BUDGET
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block text-align="center" padding-bottom="2px">
							<fo:external-graphic src="url({$baseUrl}{$imageSeal})"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<!-- Headings -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-collapse="collapse" number-columns-spanned="3"/>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-left-style='solid' border-left-width='0.2px' border-collapse="collapse" number-columns-spanned="2">
						<fo:block font-weight="bold">
							AGENCY AMOUNT REQUESTED
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-left-style='solid' border-left-width='0.2px' border-collapse="collapse" number-columns-spanned="2">
						<fo:block font-weight="bold">
							INSTITUTION COST SHARE
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-left-style='solid' border-left-width='0.2px'>
						<fo:block font-weight="bold">
							3RD PARTY COST SHARE
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-left-style='solid' border-left-width='0.2px'>
						<fo:block font-weight="bold">
							TOTAL
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<!-- And empty row so that there is some empty space after the header -->
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
				</fo:table-row>
			</fo:table-header>
			
			<fo:table-body>
				<!-- Static Description -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							PERSONNEL
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
				</fo:table-row>
				
				<!-- Only display personnel if not in low level of detail -->
				<xsl:if test="$levelOfDetail != 'low'">
					<!-- Static Description -->
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultTextIndented' font-weight="bold">
								SALARY
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Role
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Effort
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Effort
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					</fo:table-row>
					
					<!-- List the personnel (salary) -->
					<!-- Sort it, duplicate don't need to be collapsed since this isn't a
					summary. -->
					<xsl:for-each select="./PERSONNEL/PERSON">
						<xsl:sort data-type="text" lang="en" case-order="upper-first" order="ascending" select="@SEQUENCE_NUMBER"/>
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets='tableCell'>
								<fo:block xsl:use-attribute-sets='defaultTextIndented'>
									<xsl:value-of select="./NAME"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCell'>
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="./ROLE"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCell'>
								<xsl:call-template name="appointmentType"/>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
              	<xsl:call-template name="effortPercentOrHours">
                	<xsl:with-param name="hours" select="./@AGENCY_HOURS"/>
                	<xsl:with-param name="percentSalary" select="./@AGENCY_PERCENT_SALARY"/>
                </xsl:call-template>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="format-number(./@AGENCY_AMOUNT_SALARY, '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
              	<xsl:call-template name="effortPercentOrHours">
                	<xsl:with-param name="hours" select="./@INSTITUTION_HOURS"/>
                	<xsl:with-param name="percentSalary" select="./@INSTITUTION_PERCENT_SALARY"/>
                </xsl:call-template>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="format-number(./@INSTITUTION_AMOUNT_SALARY, '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<!-- Next cell: Notice there is no 3rd party cost share for Personnel -->
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>0</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="format-number(./@AGENCY_AMOUNT_SALARY + ./@INSTITUTION_AMOUNT_SALARY, '###,###')"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</xsl:if>
				
				<!-- Calculate Total: Salary -->
				<!-- Note that even though this would be cleaner in a template, it -->
				<!-- can't be put in a template because we want the total printed -->
				<!-- even if there was no person. -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultTextIndented' font-weight="bold">
							TOTAL SALARY
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./PERSONNEL/@TOTAL_AGENCY_SALARY, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./PERSONNEL/@TOTAL_INSTITUTION_SALARY, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">0</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./PERSONNEL/@TOTAL_AGENCY_SALARY + ./PERSONNEL/@TOTAL_INSTITUTION_SALARY, '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Only display personnel if not in low level of detail -->
				<xsl:if test="$levelOfDetail != 'low'">
					<!-- Empty Row -->
					<fo:table-row>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					</fo:table-row>
					
					<!-- Static Description -->
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultTextIndented' font-weight="bold">
								FRINGE BENEFITS
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Rate
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Rate
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					</fo:table-row>
					
					<!-- List the personnel (fringe benefit mode) -->
					<!-- Sort it, duplicate don't need to be collapsed since this isn't a
					summary. -->
					<xsl:for-each select="./PERSONNEL/PERSON">
						<xsl:sort data-type="text" lang="en" case-order="upper-first" order="ascending" select="@SEQUENCE_NUMBER"/>
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
								<fo:block xsl:use-attribute-sets='defaultTextIndented'>
									<xsl:value-of select="./NAME"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="./@AGENCY_FRINGE_BENEFIT_RATE"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="format-number(./@AGENCY_FRINGE_BENEFIT_AMOUNT, '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="./@INSTITUTION_FRINGE_BENEFIT_RATE"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="format-number(./@INSTITUTION_FRINGE_BENEFIT_AMOUNT, '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<!-- Next cell: Notice there is no 3rd party cost share for Personnel -->
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>0</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="format-number(./@AGENCY_FRINGE_BENEFIT_AMOUNT + ./@INSTITUTION_FRINGE_BENEFIT_AMOUNT, '###,###')"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</xsl:if>
				
				<!-- Calculate Total: Fringe Benefit -->
				<!-- Note that even though this would be cleaner in a template, it -->
				<!-- can't be put in a template because we want the total printed -->
				<!-- even if there was no person. -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultTextIndented' font-weight="bold">
							TOTAL FRINGE BENEFITS
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./PERSONNEL/@TOTAL_AGENCY_FRINGE_BENEFITS, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./PERSONNEL/@TOTAL_INSTITUTION_FRINGE_BENEFITS, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">0</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./PERSONNEL/@TOTAL_AGENCY_FRINGE_BENEFITS + ./PERSONNEL/@TOTAL_INSTITUTION_FRINGE_BENEFITS, '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Only display personnel if not in low level of detail -->
				<xsl:if test="$levelOfDetail != 'low'">
					<!-- Empty Row -->
					<fo:table-row>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					</fo:table-row>
				</xsl:if>
				
				<!-- Calculate Total: Personnel -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							TOTAL PERSONNEL
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./PERSONNEL/@TOTAL_AGENCY_PERSONNEL, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./PERSONNEL/@TOTAL_INSTITUTION_PERSONNEL, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">0</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./PERSONNEL/@TOTAL_AGENCY_PERSONNEL + ./PERSONNEL/@TOTAL_INSTITUTION_PERSONNEL, '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Empty Row -->
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
				</fo:table-row>
				
				<!-- List the non personnel items -->
				<xsl:variable name="npItems" select="./NON_PERSONNEL/NON_PERSONNEL_ITEM"/>
				<xsl:for-each select="$npItems">
					<!-- Following line is arbitrary sorting (see nonp:category at top
					of file -->
					<xsl:sort select="xalan:nodeset($CATEGORYMAP)/nonp/category[@name=current()/CATEGORY]/@value" data-type="number" />
					<!-- First Eliminate duplicates: The position check is necessary so
					that the "preceding check" (after that) doesn't fail. -->
					<xsl:if test="generate-id(.)=
						generate-id($npItems[CATEGORY=current()/CATEGORY])">
						
						<!-- Category Description -->
						<xsl:if test="$levelOfDetail != 'low'">
							<fo:table-row>
								<fo:table-cell xsl:use-attribute-sets='tableCell'>
									<fo:block xsl:use-attribute-sets='defaultText' padding-top='2px' font-weight="bold">
										<!-- lower->uppercase FOR ASCII ONLY. XSLT has no support
										for this. -->
										<xsl:value-of select="concat(substring(CATEGORY, 1, 1), translate(substring(CATEGORY,2), $lowercaseChars, $uppercaseChars))"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell/>
								<fo:table-cell/>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
								<fo:table-cell/>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
								<fo:table-cell/>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							</fo:table-row>
						</xsl:if>
						
						<!-- Print the items for this category -->
						<!-- Display category contents if high level of detail -->
						<xsl:choose>
							<xsl:when test="$levelOfDetail = 'high'">
								<!-- List the personnel (salary) -->
								<xsl:apply-templates select="../NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]">
									<xsl:sort data-type="text" select="SUB_CATEGORY"/>
								</xsl:apply-templates>
							</xsl:when>
							<!-- Display category summary if med level of detail -->
							<xsl:when test="$levelOfDetail = 'medium'">
								<xsl:for-each select="$npItems[CATEGORY=current()/CATEGORY]">
									<xsl:sort data-type="text" select="SUB_CATEGORY"/>
									<!-- Only if it wasn't displayed yet on last run -->
									<xsl:if test="generate-id(.)=
										generate-id($npItems[SUB_CATEGORY=current()/SUB_CATEGORY])">
										<fo:table-row>
											<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
												<fo:block xsl:use-attribute-sets='defaultTextIndented'>
													Total <xsl:value-of select="SUB_CATEGORY"/>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
											<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
												<fo:block xsl:use-attribute-sets='defaultText'>
													<xsl:value-of select="format-number(sum(../NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/AGENCY_REQUEST_AMOUNT), '###,###')"/>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
											<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
												<fo:block xsl:use-attribute-sets='defaultText'>
													<xsl:value-of select="format-number(sum(../NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/INSTITUTION_COST_SHARE_AMOUNT), '###,###')"/>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
												<fo:block xsl:use-attribute-sets='defaultText'>
													<xsl:value-of select="format-number(sum(../NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/THIRD_PARTY_COST_SHARE_AMOUNT), '###,###')"/>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
												<fo:block xsl:use-attribute-sets='defaultText'>
													<xsl:value-of select="format-number(sum(../NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/AGENCY_REQUEST_AMOUNT) + sum(../NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/INSTITUTION_COST_SHARE_AMOUNT) + sum(../NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/THIRD_PARTY_COST_SHARE_AMOUNT), '###,###')"/>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</xsl:if>
								</xsl:for-each>
							</xsl:when>
						</xsl:choose>
						
						<!-- Category Total -->
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
								<fo:block xsl:use-attribute-sets='defaultText' padding-top='2px' font-weight="bold">
									<!-- Don't print total in low level of detail.
									Lower->uppercase FOR ASCII ONLY. XSLT has no support
									for this. -->
									<xsl:if test="$levelOfDetail != 'low'">TOTAL </xsl:if><xsl:value-of select="concat(substring(CATEGORY, 1, 1), translate(substring(CATEGORY,2), $lowercaseChars, $uppercaseChars))"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
									<xsl:value-of select="format-number(sum(../NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/AGENCY_REQUEST_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
									<xsl:value-of select="format-number(sum(../NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/INSTITUTION_COST_SHARE_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
									<xsl:value-of select="format-number(sum(../NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/THIRD_PARTY_COST_SHARE_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
									<xsl:value-of select="format-number(sum(../NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/AGENCY_REQUEST_AMOUNT) + sum(../NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/INSTITUTION_COST_SHARE_AMOUNT) + sum(../NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/THIRD_PARTY_COST_SHARE_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						
						<!-- Empty Row -->
						<fo:table-row>
							<fo:table-cell/>
							<fo:table-cell/>
							<fo:table-cell/>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell/>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell/>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						</fo:table-row>
					</xsl:if>
				</xsl:for-each>
				
				<!-- The overline on Total Direct Costs is supposed to stay on the
				previous page if there is a page overflow. Since Total Direct
				Costs has a keep-with-next the overline has to be extracted
				from that and rendered seperatly. -->
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Calculate Total: Direct Costs -->
				<fo:table-row keep-with-next="always">
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							TOTAL DIRECT COSTS
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./@TOTAL_AGENCY_REQUEST_DIRECT_COST, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./@TOTAL_INSTITUTION_DIRECT_COST, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./@TOTAL_THIRD_PARTY_DIRECT_COST, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(./@TOTAL_AGENCY_REQUEST_DIRECT_COST + ./@TOTAL_INSTITUTION_DIRECT_COST + ./@TOTAL_THIRD_PARTY_DIRECT_COST, '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
								
				<!-- Calculate Total: Indirect Costs -->
				<fo:table-row keep-with-next="always">
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultTextIndented' font-style="italic">
							INDIRECT COSTS
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<!-- In the next block we print the rate (percentage) then an X and
						finally the Agency Amount Requested divided by the rate.  To
						accomplish that we have to strip the percent sign from the rate
						and divide it by 100.  Also make sure to if-it first to make
						sure we don't divide by zero. -->
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:variable name="divisor">
								<xsl:value-of select="substring(/PROPOSAL/BUDGET/INDIRECT_COST/INDIRECT_COST_TASK[@TASK_NUMBER = $taskNumber]/INDIRECT_COST_TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@RATE, 0, string-length(/PROPOSAL/BUDGET/INDIRECT_COST/INDIRECT_COST_TASK[@TASK_NUMBER = $taskNumber]/INDIRECT_COST_TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@RATE))"/>
							</xsl:variable>
							<xsl:choose>
								<xsl:when test="$divisor = 0">
									(0.0% X $0)
								</xsl:when>
								<xsl:otherwise>
									(<xsl:value-of select="/PROPOSAL/BUDGET/INDIRECT_COST/INDIRECT_COST_TASK[@TASK_NUMBER = $taskNumber]/INDIRECT_COST_TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@RATE"/> X $<xsl:value-of select="format-number(round(./@TOTAL_AGENCY_REQUEST_INDIRECT_COST div ($divisor div 100)), '###,###')"/>)
								</xsl:otherwise>
							</xsl:choose>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:value-of select="format-number(./@TOTAL_AGENCY_REQUEST_INDIRECT_COST, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:value-of select="format-number(./@TOTAL_INSTITUTION_INDIRECT_COST, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:value-of select="format-number(./@TOTAL_THIRD_PARTY_INDIRECT_COST, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:value-of select="format-number(./@TOTAL_AGENCY_REQUEST_INDIRECT_COST + ./@TOTAL_INSTITUTION_INDIRECT_COST + ./@TOTAL_THIRD_PARTY_INDIRECT_COST, '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Calculate Total: Unrecovered Indirect Costs -->
				<xsl:if test="/PROPOSAL/BUDGET/COST_SHARE/INSTITUTION_INDIRECT_COST_SHARE/@TOTAL_UNRECOVERED_INDIRECT_COST != 0">
					<fo:table-row keep-with-next="always">
						<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
							<fo:block xsl:use-attribute-sets='defaultTextIndented' font-style="italic">
								UNRECOVERED INDIRECT COSTS
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
								<xsl:value-of select="format-number(/PROPOSAL/BUDGET/INDIRECT_COST/INDIRECT_COST_TASK[@TASK_NUMBER=$taskNumber]/INDIRECT_COST_TASK_PERIOD[@PERIOD_NUMBER=$periodNumber]/INDIRECT_COST_TASK_PERIOD_INSTITUTION/UNRECOVERED_INDIRECT_COST, '###,###')"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<!-- 4/18/2005 dterret: The following was changed as part of the addition of Unrecovered 
						Indirect Costs. -->
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
								<xsl:value-of select="format-number( key('UNRECOVERED', concat( $taskNumber,' ',$periodNumber )), '###,###')"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
				
				<!-- Calculate Total: Costs -->
				<fo:table-row keep-with-next="always">
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							TOTAL COSTS
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold" border-bottom-style="solid" border-bottom-width="thin" border-top-style="solid" border-top-width="thin">
							$ <xsl:value-of select="format-number(./@TOTAL_AGENCY_COST, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<!-- 4/18/2005 dterret: The following was changed as part of a temporary fix.-->
						<!-- 5/10/2005 dterret: The temporary change was backed out here. -->
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold" border-bottom-style="solid" border-bottom-width="thin" border-top-style="solid" border-top-width="thin">
							$ <xsl:value-of select="format-number( ./@TOTAL_INSTITUTION_COST, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold" border-bottom-style="solid" border-bottom-width="thin" border-top-style="solid" border-top-width="thin">
							$ <xsl:value-of select="format-number(./@TOTAL_THIRD_PARTY_COST, '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<!-- 4/18/2005 dterret: The following was changed as part of a temporary fix.-->
					<!-- 5/10/2005 dterret: The temporary change was backed out here. -->
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold" border-bottom-style="solid" border-bottom-width="thin" border-top-style="solid" border-top-width="thin">
							$ <xsl:value-of select="format-number(./@TOTAL_AGENCY_COST + 
								./@TOTAL_INSTITUTION_COST + ./@TOTAL_THIRD_PARTY_COST, '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- This row underlines the Total Costs row again so that there is a
				double underline. Unfortunatly that is necessary because the
				"double" style appears not to be working. -->
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
				</fo:table-row>
				
			</fo:table-body>
		</fo:table>
	</xsl:template>
	
	
	<!-- ******************* **************************** ******************* -->
	<!-- ***************  Summary: Many Tasks, One Period  ******************* -->
	<!-- ******************* **************************** ******************* -->
	<xsl:template name="summaryPage">
		<!-- ************************* VARIABLES ************************* -->
		<!-- taskNumber is for picking the proper task name in header -->
		<xsl:variable name="taskNumber" select="./@TASK_NUMBER"/>
		<!-- periodNumber is for picking the proper period date range in header -->
		<xsl:variable name="periodNumber" select="./@PERIOD_NUMBER"/>
		<!-- the level of detail used for this output -->
		<xsl:variable name="levelOfDetail" select="/PROPOSAL/BUDGET/@PARAMETER1"/>
		
		<!-- ************************* MAIN TABLE ************************* -->
		<fo:table inline-progression-dimension="190mm" table-layout="fixed">
			<fo:table-column column-width="42mm"/>
			<fo:table-column column-width="34mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="13mm"/>
			<fo:table-column column-width="18mm"/>
			<fo:table-column column-width="13mm"/>
			<fo:table-column column-width="18mm"/>
			<fo:table-column column-width="18mm"/>
			<fo:table-column column-width="18mm"/>
			
			<!-- Table header -->
			<fo:table-header>
				<!-- Header w/Budget information -->
				<fo:table-row>
					<fo:table-cell number-columns-spanned="6">
						<fo:block>
							<xsl:value-of select="/PROPOSAL/BUDGET/PROJECT_DIRECTOR/@FIRST_NAME"/><xsl:text> </xsl:text><xsl:value-of select="/PROPOSAL/BUDGET/PROJECT_DIRECTOR/@LAST_NAME"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="3">
						<fo:block text-align="right" font-size="9pt">
                            <xsl:value-of select="$myUniversity"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell number-columns-spanned="7">
						<fo:block>
							<xsl:value-of
								select="/PROPOSAL/BUDGET/AGENCY/@AGENCY_FULL_NAME"/>
						</fo:block>
						<fo:block>
							All Tasks
						</fo:block>
						<!-- Only put the period if there is more than one -->
						<xsl:if test="/PROPOSAL/BUDGET/PERIODS/PERIOD[2]">
							<fo:block>
								<!-- look for the right one (if out of order) -->
								<xsl:for-each select="/PROPOSAL/BUDGET/PERIODS/PERIOD">
									<xsl:if test="$periodNumber = ./PERIOD_NUMBER">
										Period: <xsl:value-of select="./PERIOD_NUMBER"/>, <xsl:value-of select="./START_DATE_STRING"/> to <xsl:value-of select="./STOP_DATE_STRING"/>
									</xsl:if> 
								</xsl:for-each>
							</fo:block>
						</xsl:if>
						<fo:block font-weight="bold" padding-top="30px">
							BUDGET
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block text-align="center" padding-bottom="2px">
							<fo:external-graphic src="url({$baseUrl}{$imageSeal})"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<!-- Headings -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-collapse="collapse" number-columns-spanned="3"/>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-left-style='solid' border-left-width='0.2px' border-collapse="collapse" number-columns-spanned="2">
						<fo:block font-weight="bold">
							AGENCY AMOUNT REQUESTED
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-left-style='solid' border-left-width='0.2px' border-collapse="collapse" number-columns-spanned="2">
						<fo:block font-weight="bold">
							INSTITUTION COST SHARE
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-left-style='solid' border-left-width='0.2px'>
						<fo:block font-weight="bold">
							3RD PARTY COST SHARE
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-left-style='solid' border-left-width='0.2px'>
						<fo:block font-weight="bold">
							TOTAL
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<!-- And empty row so that there is some empty space after the header -->
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
				</fo:table-row>
			</fo:table-header>
			
			<fo:table-body>
				<!-- Static Description -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							PERSONNEL
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
				</fo:table-row>
				
				<!-- Only display personnel if not in low level of detail -->
				<xsl:if test="$levelOfDetail != 'low'">
					<!-- Static Description -->
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultTextIndented' font-weight="bold">
								SALARY
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Role
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Effort
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Effort
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					</fo:table-row>
					
					<!-- List the personnel (salary mode) -->
					<!-- We have the below for loop and summation in the body of
					it, to roll up duplicate items. Meaning if a person has the
					same name, and appointment type, and timestamp then the amounts
					are added together and the %-effort is dropped.  -->
					<xsl:for-each select="../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL
						/PERSON[generate-id(.) = 
										generate-id(key('PEOPLE',
																		concat(../../@PERIOD_NUMBER, ' ',
																					NAME, ' ',
																					APPOINTMENT/@APPOINTMENT_TYPE, ' ',
																					@SEQUENCE_NUMBER)))]">
						<!-- Sort it after timestamp per user requirement. -->
						<xsl:sort data-type="text" lang="en" case-order="upper-first" order="ascending" select="@SEQUENCE_NUMBER"/>
							<xsl:variable name="personInstances"
														select="key( 'PEOPLE',
																				  concat( ../../@PERIOD_NUMBER,' ',
																								  NAME,' ',
																								  APPOINTMENT/@APPOINTMENT_TYPE,' ',
																								  @SEQUENCE_NUMBER ) )"/>
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets='tableCell'>
								<fo:block xsl:use-attribute-sets='defaultTextIndented'>
									<xsl:value-of select="./NAME"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCell'>
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="./ROLE"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCell'>
								<xsl:call-template name="appointmentType">
									<xsl:with-param name="summary" select="'true'"/>
								</xsl:call-template>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:variable name="agencyHrsSum"
										select="sum( $personInstances/@AGENCY_HOURS )"/>
									<xsl:call-template name="effortPercentOrHours">
										<xsl:with-param name="hours" select="$agencyHrsSum"/>
										<xsl:with-param name="percentSalary" select="./@AGENCY_PERCENT_SALARY"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@AGENCY_AMOUNT_SALARY), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:variable name="institutionHoursSum"
										 select="sum( $personInstances/@INSTITUTION_HOURS )"/>
									<xsl:call-template name="effortPercentOrHours">
										<xsl:with-param name="hours" select="$institutionHoursSum"/>
										<xsl:with-param name="percentSalary" select="./@INSTITUTION_PERCENT_SALARY"/>
									</xsl:call-template>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@INSTITUTION_AMOUNT_SALARY), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<!-- Next cell: Notice there is no 3rd party cost share for Personnel -->
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>0</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@AGENCY_AMOUNT_SALARY) + sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@INSTITUTION_AMOUNT_SALARY), '###,###')"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</xsl:if>
				
				<!-- Calculate Total: Salary -->
				<!-- Note that even though this would be cleaner in a template, it -->
				<!-- can't be put in a template because we want the total printed -->
				<!-- even if there was no person. -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultTextIndented' font-weight="bold">
							TOTAL SALARY
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL[*]/PERSON/@AGENCY_AMOUNT_SALARY), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL[*]/PERSON/@INSTITUTION_AMOUNT_SALARY), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">0</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL[*]/PERSON/@AGENCY_AMOUNT_SALARY) + sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL[*]/PERSON/@INSTITUTION_AMOUNT_SALARY), '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Only display personnel if not in low level of detail -->
				<xsl:if test="$levelOfDetail != 'low'">
					<!-- Empty Row -->
					<fo:table-row>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					</fo:table-row>
					
					<!-- Static Description -->
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultTextIndented' font-weight="bold">
								FRINGE BENEFITS
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Rate
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Rate
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					</fo:table-row>
					
					<!-- List the personnel (fringe benefit mode) -->
					<!-- We have the below for loop and summation in the body of
					it, to roll up duplicate items. Meaning if a person has the
					same name, and appointment type, and timestamp then the amounts
					are added together and the %-effort is dropped.  -->
					<xsl:for-each select="../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL
						/PERSON[generate-id(.) = 
									  generate-id(key('PEOPLE',
																		concat(../../@PERIOD_NUMBER, ' ',
																					NAME, ' ',
																					APPOINTMENT/@APPOINTMENT_TYPE, ' ',
																					@SEQUENCE_NUMBER)))]">
						<!-- Sort it after timestamp per user requirement. -->
						<xsl:sort data-type="text" lang="en" case-order="upper-first" order="ascending" select="@SEQUENCE_NUMBER"/>
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
								<fo:block xsl:use-attribute-sets='defaultTextIndented'>
									<xsl:value-of select="./NAME"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:if test="count(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]) = 1">
										<xsl:value-of select="./@AGENCY_FRINGE_BENEFIT_RATE"/>
									</xsl:if>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@AGENCY_FRINGE_BENEFIT_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:if test="count(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]) = 1">
										<xsl:value-of select="./@INSTITUTION_FRINGE_BENEFIT_RATE"/>
									</xsl:if>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@INSTITUTION_FRINGE_BENEFIT_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<!-- Next cell: Notice there is no 3rd party cost share for Personnel -->
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>0</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText'>
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@AGENCY_FRINGE_BENEFIT_AMOUNT) + sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@INSTITUTION_FRINGE_BENEFIT_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</xsl:if>
				
				<!-- Calculate Total: Fringe Benefit -->
				<!-- Note that even though this would be cleaner in a template, it -->
				<!-- can't be put in a template because we want the total printed -->
				<!-- even if there was no person. -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultTextIndented' font-weight="bold">
							TOTAL FRINGE BENEFITS
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL[*]/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL[*]/PERSON/@INSTITUTION_FRINGE_BENEFIT_AMOUNT), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">0</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL[*]/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT) + sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL[*]/PERSON/@INSTITUTION_FRINGE_BENEFIT_AMOUNT), '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Only display personnel if not in low level of detail -->
				<xsl:if test="$levelOfDetail != 'low'">
					<!-- Empty Row -->
					<fo:table-row>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					</fo:table-row>
				</xsl:if>
				
				<!-- Calculate Total: Personnel -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							TOTAL PERSONNEL
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL[*]/@TOTAL_AGENCY_PERSONNEL), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL[*]/@TOTAL_INSTITUTION_PERSONNEL), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">0</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL[*]/@TOTAL_AGENCY_PERSONNEL) + sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/PERSONNEL[*]/@TOTAL_INSTITUTION_PERSONNEL), '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Empty Row -->
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
				</fo:table-row>
				
				<!-- List the non personnel items -->
				<xsl:variable name="npItems" select="../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM"/>
				<xsl:for-each select="$npItems">
					<!-- Following line is arbitrary sorting (see nonp:category at top
					of file -->
					<xsl:sort select="xalan:nodeset($CATEGORYMAP)/nonp/category[@name=current()/CATEGORY]/@value" data-type="number" />
					<!-- First Eliminate duplicates: The position check is necessary so
					that the "preceding check" (after that) doesn't fail. -->
					<xsl:if test="generate-id(.)=
						generate-id($npItems[CATEGORY=current()/CATEGORY])">
						
						<!-- Category Description -->
						<xsl:if test="$levelOfDetail != 'low'">
							<fo:table-row>
								<fo:table-cell xsl:use-attribute-sets='tableCell'>
									<fo:block xsl:use-attribute-sets='defaultText' padding-top='2px' font-weight="bold">
										<!-- lower->uppercase FOR ASCII ONLY. XSLT has no support
										for this. -->
										<xsl:value-of select="concat(substring(CATEGORY, 1, 1), translate(substring(CATEGORY,2), $lowercaseChars, $uppercaseChars))"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell/>
								<fo:table-cell/>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
								<fo:table-cell/>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
								<fo:table-cell/>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							</fo:table-row>
						</xsl:if>
						
						<!-- Print the items for this category -->
						<!-- Display category contents if high level of detail -->
						<xsl:choose>
							<xsl:when test="$levelOfDetail = 'high'">
								<!-- List the Subcategories -->
								<xsl:apply-templates select="../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]">
									<xsl:sort data-type="text" select="SUB_CATEGORY"/>
								</xsl:apply-templates>
							</xsl:when>
							<!-- Display category summary if med level of detail -->
							<xsl:when test="$levelOfDetail = 'medium'">
								<xsl:for-each select="$npItems[CATEGORY=current()/CATEGORY]">
									<xsl:sort data-type="text" select="SUB_CATEGORY"/>
									<!-- Only if it wasn't displayed yet on last run -->
									<xsl:if test="generate-id(.)=
										generate-id($npItems[SUB_CATEGORY=current()/SUB_CATEGORY])">
										<fo:table-row>
											<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
												<fo:block xsl:use-attribute-sets='defaultTextIndented'>
													Total <xsl:value-of select="SUB_CATEGORY"/>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
											<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
												<fo:block xsl:use-attribute-sets='defaultText'>
													<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/AGENCY_REQUEST_AMOUNT), '###,###')"/>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
											<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
												<fo:block xsl:use-attribute-sets='defaultText'>
													<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/INSTITUTION_COST_SHARE_AMOUNT), '###,###')"/>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
												<fo:block xsl:use-attribute-sets='defaultText'>
													<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/THIRD_PARTY_COST_SHARE_AMOUNT), '###,###')"/>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
												<fo:block xsl:use-attribute-sets='defaultText'>
													<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/AGENCY_REQUEST_AMOUNT) + sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/INSTITUTION_COST_SHARE_AMOUNT) + sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/THIRD_PARTY_COST_SHARE_AMOUNT), '###,###')"/>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</xsl:if>
								</xsl:for-each>
							</xsl:when>
						</xsl:choose>
						
						
						<!-- Category Total -->
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
								<fo:block xsl:use-attribute-sets='defaultText' padding-top='2px' font-weight="bold">
									<!-- Don't print total in low level of detail.
									Lower->uppercase FOR ASCII ONLY. XSLT has no support
									for this. -->
									<xsl:if test="$levelOfDetail != 'low'">TOTAL </xsl:if><xsl:value-of select="concat(substring(CATEGORY, 1, 1), translate(substring(CATEGORY,2), $lowercaseChars, $uppercaseChars))"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/AGENCY_REQUEST_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/INSTITUTION_COST_SHARE_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/THIRD_PARTY_COST_SHARE_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/AGENCY_REQUEST_AMOUNT) + sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/INSTITUTION_COST_SHARE_AMOUNT) + sum(../../../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/THIRD_PARTY_COST_SHARE_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						
						<!-- Empty Row -->
						<fo:table-row>
							<fo:table-cell/>
							<fo:table-cell/>
							<fo:table-cell/>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell/>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell/>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						</fo:table-row>
					</xsl:if>
				</xsl:for-each>
				
				<!-- The overline on Total Direct Costs is supposed to stay on the
				previous page if there is a page overflow. Since Total Direct
				Costs has a keep-with-next the overline has to be extracted
				from that and rendered seperatly. -->
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Calculate Total: Direct Costs -->
				<fo:table-row keep-with-next="always">
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							TOTAL DIRECT COSTS
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_AGENCY_REQUEST_DIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_INSTITUTION_DIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_THIRD_PARTY_DIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_INSTITUTION_DIRECT_COST) + sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_THIRD_PARTY_DIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Calculate Total: Indirect Costs -->
				<fo:table-row keep-with-next="always">
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultTextIndented' font-style="italic">
							INDIRECT COSTS
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<!-- No percentage on summaries-->
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_AGENCY_REQUEST_INDIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_INSTITUTION_INDIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_THIRD_PARTY_INDIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_AGENCY_REQUEST_INDIRECT_COST) + sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_INSTITUTION_INDIRECT_COST) + sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_THIRD_PARTY_INDIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Calculate Total: Unrecovered Indirect Costs -->
				<xsl:if test="/PROPOSAL/BUDGET/COST_SHARE/INSTITUTION_INDIRECT_COST_SHARE/@TOTAL_UNRECOVERED_INDIRECT_COST != 0">
					<fo:table-row keep-with-next="always">
						<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
							<fo:block xsl:use-attribute-sets='defaultTextIndented' font-style="italic">
								UNRECOVERED INDIRECT COSTS
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
								<xsl:value-of select="format-number(sum(/PROPOSAL/BUDGET/INDIRECT_COST/INDIRECT_COST_TASK[@TASK_NUMBER=$taskNumber]/INDIRECT_COST_TASK_PERIOD[*]/INDIRECT_COST_TASK_PERIOD_INSTITUTION/UNRECOVERED_INDIRECT_COST), '###,###')"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<!-- 4/18/2005 dterret: The following was changed as part of the addition of Unrecovered 
						Indirect Costs. -->
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">         
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
								<xsl:value-of select="format-number(sum(/PROPOSAL/BUDGET/INDIRECT_COST/INDIRECT_COST_TASK[@TASK_NUMBER=$taskNumber]/INDIRECT_COST_TASK_PERIOD[*]/INDIRECT_COST_TASK_PERIOD_INSTITUTION/UNRECOVERED_INDIRECT_COST), '###,###')"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
				
				<!-- Calculate Total: Costs -->
				<fo:table-row keep-with-next="always">
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							TOTAL COSTS
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold" border-bottom-style="solid" border-bottom-width="thin" border-top-style="solid" border-top-width="thin">
							$ <xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_AGENCY_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<!-- The following was changed as part of a temporary fix. DT 4/18/2005 -->
					<!-- 5/10/2005 dterret: The temporary change was backed out here. -->
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold" border-bottom-style="solid" border-bottom-width="thin" border-top-style="solid" border-top-width="thin">
							$ <xsl:value-of select="format-number(sum(../TASK_PERIOD
								[@PERIOD_NUMBER = $periodNumber]/@TOTAL_INSTITUTION_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold" border-bottom-style="solid" border-bottom-width="thin" border-top-style="solid" border-top-width="thin">
							$ <xsl:value-of select="format-number(sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_THIRD_PARTY_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<!-- The following was changed as part of a temporary fix. DT 4/18/2005 -->
					<!-- 5/10/2005 dterret: The temporary change was backed out here. -->
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold" border-bottom-style="solid" border-bottom-width="thin" border-top-style="solid" border-top-width="thin">
							$ <xsl:value-of select="format-number(
								sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_AGENCY_COST) + 
								sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_INSTITUTION_COST) +
								sum(../TASK_PERIOD[@PERIOD_NUMBER = $periodNumber]/@TOTAL_THIRD_PARTY_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- This row underlines the Total Costs row again so that there is a
				double underline. Unfortunatly that is necessary because the
				"double" style appears not to be working. -->
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
				</fo:table-row>
				
			</fo:table-body>
		</fo:table>
	</xsl:template>
	
	
	<!-- ******************* **************************** ******************* -->
	<!-- ******************* Task Summary, Period Summary ******************* -->
	<!-- ******************* **************************** ******************* -->
	<xsl:template name="summarySummaryPage">
		<!-- ************************* VARIABLES ************************* -->
		<!-- taskNumber is for picking the proper task name in header -->
		<xsl:variable name="taskNumber" select="./@TASK_NUMBER"/>
		<!-- periodNumber is for picking the proper period date range in header -->
		<xsl:variable name="periodNumber" select="./@PERIOD_NUMBER"/>
		<!-- the level of detail used for this output -->
		<xsl:variable name="levelOfDetail" select="/PROPOSAL/BUDGET/@PARAMETER1"/>
		
		<!-- ************************* MAIN TABLE ************************* -->
		<fo:table inline-progression-dimension="190mm" table-layout="fixed">
			<fo:table-column column-width="42mm"/>
			<fo:table-column column-width="34mm"/>
			<fo:table-column column-width="16mm"/>
			<fo:table-column column-width="13mm"/>
			<fo:table-column column-width="18mm"/>
			<fo:table-column column-width="13mm"/>
			<fo:table-column column-width="18mm"/>
			<fo:table-column column-width="18mm"/>
			<fo:table-column column-width="18mm"/>
			
			<!-- Table header -->
			<fo:table-header>
				<!-- Header w/Budget information -->
				<fo:table-row>
					<fo:table-cell number-columns-spanned="6">
						<fo:block>
							<xsl:value-of select="/PROPOSAL/BUDGET/PROJECT_DIRECTOR/@FIRST_NAME"/><xsl:text> </xsl:text><xsl:value-of select="/PROPOSAL/BUDGET/PROJECT_DIRECTOR/@LAST_NAME"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="3">
						<fo:block text-align="right" font-size="9pt">
                            <xsl:value-of select="$myUniversity"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell number-columns-spanned="7">
						<fo:block>
							<xsl:value-of
								select="/PROPOSAL/BUDGET/AGENCY/@AGENCY_FULL_NAME"/>
						</fo:block>
						<fo:block>All Tasks</fo:block>
						<fo:block>
							All Periods: <xsl:value-of select="/PROPOSAL/BUDGET/PERIODS/PERIOD[1]/START_DATE_STRING"/> to
							<xsl:for-each select="/PROPOSAL/BUDGET/PERIODS/PERIOD">
								<xsl:if test="$periodNumber = ./PERIOD_NUMBER">
									<xsl:value-of select="./STOP_DATE_STRING"/>
								</xsl:if> 
							</xsl:for-each>
						</fo:block>
						<fo:block font-weight="bold" padding-top="30px">
							BUDGET
						</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block text-align="center" padding-bottom="2px">
							<fo:external-graphic src="url({$baseUrl}{$imageSeal})"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<!-- Headings -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-collapse="collapse" number-columns-spanned="3"/>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-left-style='solid' border-left-width='0.2px' border-collapse="collapse" number-columns-spanned="2">
						<fo:block font-weight="bold">
							AGENCY AMOUNT REQUESTED
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-left-style='solid' border-left-width='0.2px' border-collapse="collapse" number-columns-spanned="2">
						<fo:block font-weight="bold">
							INSTITUTION COST SHARE
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-left-style='solid' border-left-width='0.2px'>
						<fo:block font-weight="bold">
							3RD PARTY COST SHARE
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellHeader' border-left-style='solid' border-left-width='0.2px'>
						<fo:block font-weight="bold">
							TOTAL
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<!-- And empty row so that there is some empty space after the header -->
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
				</fo:table-row>
			</fo:table-header>
			
			<fo:table-body>
				<!-- Static Description -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							PERSONNEL
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
				</fo:table-row>
				
				<!-- Only display personnel if not in low level of detail -->
				<xsl:if test="$levelOfDetail != 'low'">
					<!-- Static Description -->
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultTextIndented' font-weight="bold">
								SALARY
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Role
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Effort
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Effort
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					</fo:table-row>
					
					<!-- List the personnel (salary mode) -->
					<!-- Sort it, so that duplicates can be collapsed. -->
					<xsl:for-each select="../TASK_PERIOD[*]/PERSONNEL/PERSON">
						<xsl:sort data-type="text" lang="en" case-order="upper-first" order="ascending" select="@SEQUENCE_NUMBER"/>
						<!-- We have the below if statement and summation in the body of
						it, to roll up duplicate items. Meaning if a person has the
						same name, appointment type, and timestamp, then the amounts
						are added together and the %-effort is dropped.  -->
						<xsl:variable name="personInstances"
							select="key( 'uniquePersonsKey',
														concat( ./NAME, ' ',
																		./APPOINTMENT/@APPOINTMENT_CODE, ' ',
																		./@SEQUENCE_NUMBER ) )"/>
						<xsl:if test="(position() = 1) or not(preceding::PERSON[NAME = current()/NAME and APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER])">
							<fo:table-row>
								<fo:table-cell xsl:use-attribute-sets='tableCell'>
									<fo:block xsl:use-attribute-sets='defaultTextIndented'>
										<xsl:value-of select="./NAME"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets='tableCell'>
									<fo:block xsl:use-attribute-sets='defaultText'>
										<xsl:value-of select="./ROLE"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets='tableCell'>
									<xsl:call-template name="appointmentType">
										<xsl:with-param name="summary" select="'sumSum'"/>
									</xsl:call-template>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
									<fo:block xsl:use-attribute-sets='defaultText'>
										<xsl:variable name="agencyHoursSum"
											select="sum( $personInstances/@AGENCY_HOURS )"/>
										<xsl:call-template name="effortPercentOrHours">
											<xsl:with-param name="hours" select="$agencyHoursSum"/>
											<xsl:with-param name="percentSalary" select="./@AGENCY_PERCENT_SALARY"/>
										</xsl:call-template>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
									<fo:block xsl:use-attribute-sets='defaultText'>
										<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@AGENCY_AMOUNT_SALARY), '###,###')"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
									<fo:block xsl:use-attribute-sets='defaultText'>
										<xsl:variable name="institutionHoursSum"
											select="sum( $personInstances/@INSTITUTION_HOURS )"/>
										<xsl:call-template name="effortPercentOrHours">
											<xsl:with-param name="hours" select="$institutionHoursSum"/>
											<xsl:with-param name="percentSalary" select="./@INSTITUTION_PERCENT_SALARY"/>
										</xsl:call-template>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
									<fo:block xsl:use-attribute-sets='defaultText'>
										<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@INSTITUTION_AMOUNT_SALARY), '###,###')"/>
									</fo:block>
								</fo:table-cell>
								<!-- Next cell: Notice there is no 3rd party cost share for Personnel -->
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
									<fo:block xsl:use-attribute-sets='defaultText'>0</fo:block>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
									<fo:block xsl:use-attribute-sets='defaultText'>
										<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@AGENCY_AMOUNT_SALARY) + sum(../../../TASK_PERIOD[*]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@INSTITUTION_AMOUNT_SALARY), '###,###')"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
				
				<!-- Calculate Total: Salary -->
				<!-- Note that even though this would be cleaner in a template, it -->
				<!-- can't be put in a template because we want the total printed -->
				<!-- even if there was no person. -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultTextIndented' font-weight="bold">
							TOTAL SALARY
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/PERSONNEL[*]/PERSON/@AGENCY_AMOUNT_SALARY), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/PERSONNEL[*]/PERSON/@INSTITUTION_AMOUNT_SALARY), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">0</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/PERSONNEL[*]/PERSON/@AGENCY_AMOUNT_SALARY) + sum(../TASK_PERIOD[*]/PERSONNEL[*]/PERSON/@INSTITUTION_AMOUNT_SALARY), '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Only display personnel if not in low level of detail -->
				<xsl:if test="$levelOfDetail != 'low'">
					<!-- Empty Row -->
					<fo:table-row>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					</fo:table-row>
					
					<!-- Static Description -->
					<fo:table-row>
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultTextIndented' font-weight="bold">
								FRINGE BENEFITS
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Rate
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
							<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
								Rate
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					</fo:table-row>
					
					<!-- List the personnel (fringe benefit mode) -->
					<!-- Sort it. -->
					<xsl:for-each select="../TASK_PERIOD[*]/PERSONNEL/PERSON">
						<xsl:sort data-type="text" lang="en" case-order="upper-first" order="ascending" select="@SEQUENCE_NUMBER"/>
						<!-- We have the below if statement and summation in the body of
						it, to roll up duplicate items. Meaning if a person has the
						same name, appointment type, and timestamp, then the amounts
						are added together and the %-effort is dropped.  -->
						<xsl:if test="(position() = 1) or not(preceding::PERSON[NAME = current()/NAME and APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER])">
							<fo:table-row>
								<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
									<fo:block xsl:use-attribute-sets='defaultTextIndented'>
										<xsl:value-of select="./NAME"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
									<fo:block xsl:use-attribute-sets='defaultText'>
										<xsl:if test="count(../../../TASK_PERIOD[*]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]) = 1">
											<xsl:value-of select="./@AGENCY_FRINGE_BENEFIT_RATE"/>
										</xsl:if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
									<fo:block xsl:use-attribute-sets='defaultText'>
										<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@AGENCY_FRINGE_BENEFIT_AMOUNT), '###,###')"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="center">
									<fo:block xsl:use-attribute-sets='defaultText'>
										<xsl:if test="count(../../../TASK_PERIOD[*]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]) = 1">
											<xsl:value-of select="./@INSTITUTION_FRINGE_BENEFIT_RATE"/>
										</xsl:if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
									<fo:block xsl:use-attribute-sets='defaultText'>
										<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@INSTITUTION_FRINGE_BENEFIT_AMOUNT), '###,###')"/>
									</fo:block>
								</fo:table-cell>
								<!-- Next cell: Notice there is no 3rd party cost share for Personnel -->
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
									<fo:block xsl:use-attribute-sets='defaultText'>0</fo:block>
								</fo:table-cell>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
									<fo:block xsl:use-attribute-sets='defaultText'>
										<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@AGENCY_FRINGE_BENEFIT_AMOUNT) + sum(../../../TASK_PERIOD[*]/PERSONNEL/PERSON[(NAME = current()/NAME) and (APPOINTMENT/@APPOINTMENT_TYPE = current()/APPOINTMENT/@APPOINTMENT_TYPE and @SEQUENCE_NUMBER = current()/@SEQUENCE_NUMBER)]/@INSTITUTION_FRINGE_BENEFIT_AMOUNT), '###,###')"/>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</xsl:if>
					</xsl:for-each>
				</xsl:if>
				
				<!-- Calculate Total: Fringe Benefit -->
				<!-- Note that even though this would be cleaner in a template, it -->
				<!-- can't be put in a template because we want the total printed -->
				<!-- even if there was no person. -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultTextIndented' font-weight="bold">
							TOTAL FRINGE BENEFITS
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/PERSONNEL[*]/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/PERSONNEL[*]/PERSON/@INSTITUTION_FRINGE_BENEFIT_AMOUNT), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">0</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/PERSONNEL[*]/PERSON/@AGENCY_FRINGE_BENEFIT_AMOUNT) + sum(../TASK_PERIOD[*]/PERSONNEL[*]/PERSON/@INSTITUTION_FRINGE_BENEFIT_AMOUNT), '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Only display personnel if not in low level of detail -->
				<xsl:if test="$levelOfDetail != 'low'">
					<!-- Empty Row -->
					<fo:table-row>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					</fo:table-row>
				</xsl:if>
				
				<!-- Calculate Total: Personnel -->
				<fo:table-row>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							TOTAL PERSONNEL
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/PERSONNEL[*]/@TOTAL_AGENCY_PERSONNEL), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/PERSONNEL[*]/@TOTAL_INSTITUTION_PERSONNEL), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">0</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/PERSONNEL[*]/@TOTAL_AGENCY_PERSONNEL) + sum(../TASK_PERIOD[*]/PERSONNEL[*]/@TOTAL_INSTITUTION_PERSONNEL), '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Empty Row -->
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
				</fo:table-row>
				
				<!-- List the non personnel items -->
				<xsl:variable name="npItems" select="../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM"/>
				<xsl:for-each select="$npItems">
					<!-- Following line is arbitrary sorting (see nonp:category at top
					of file -->
					<xsl:sort select="xalan:nodeset($CATEGORYMAP)/nonp/category[@name=current()/CATEGORY]/@value" data-type="number" />
					<!-- First Eliminate duplicates: The position check is necessary so
					that the "preceding check" (after that) doesn't fail. -->
					<xsl:if test="generate-id(.)=
						generate-id($npItems[CATEGORY=current()/CATEGORY])">
						
						<!-- Category Description -->
						<xsl:if test="$levelOfDetail != 'low'">
							<fo:table-row>
								<fo:table-cell xsl:use-attribute-sets='tableCell'>
									<fo:block xsl:use-attribute-sets='defaultText' padding-top='2px' font-weight="bold">
										<!-- lower->uppercase FOR ASCII ONLY. XSLT has no support
										for this. -->
										<xsl:value-of select="concat(substring(CATEGORY, 1, 1), translate(substring(CATEGORY,2), $lowercaseChars, $uppercaseChars))"/>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell/>
								<fo:table-cell/>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
								<fo:table-cell/>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
								<fo:table-cell/>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
								<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							</fo:table-row>
						</xsl:if>
						
						<!-- Print the items for this category -->
						<!-- Display category contents if high level of detail -->
						<xsl:choose>
							<xsl:when test="$levelOfDetail = 'high'">
								<!-- List the Subcategories -->
								<xsl:apply-templates select="../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]">
									<xsl:sort data-type="text" select="SUB_CATEGORY"/>
								</xsl:apply-templates>
							</xsl:when>
							<!-- Display category summary if med level of detail -->
							<xsl:when test="$levelOfDetail = 'medium'">
								<xsl:for-each select="$npItems[CATEGORY=current()/CATEGORY]">
									<xsl:sort data-type="text" select="SUB_CATEGORY"/>
									<!-- Only if it wasn't displayed yet on last run -->
									<xsl:if test="generate-id(.)=
										generate-id($npItems[SUB_CATEGORY=current()/SUB_CATEGORY])">
										<fo:table-row>
											<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
												<fo:block xsl:use-attribute-sets='defaultTextIndented'>
													Total <xsl:value-of select="SUB_CATEGORY"/>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
											<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
												<fo:block xsl:use-attribute-sets='defaultText'>
													<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/AGENCY_REQUEST_AMOUNT), '###,###')"/>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
											<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
												<fo:block xsl:use-attribute-sets='defaultText'>
													<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/INSTITUTION_COST_SHARE_AMOUNT), '###,###')"/>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
												<fo:block xsl:use-attribute-sets='defaultText'>
													<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/THIRD_PARTY_COST_SHARE_AMOUNT), '###,###')"/>
												</fo:block>
											</fo:table-cell>
											<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
												<fo:block xsl:use-attribute-sets='defaultText'>
													<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/AGENCY_REQUEST_AMOUNT) + sum(../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/INSTITUTION_COST_SHARE_AMOUNT) + sum(../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[SUB_CATEGORY = current()/SUB_CATEGORY]/THIRD_PARTY_COST_SHARE_AMOUNT), '###,###')"/>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</xsl:if>
								</xsl:for-each>
							</xsl:when>
						</xsl:choose>
						
						<!-- Category Total -->
						<fo:table-row>
							<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
								<fo:block xsl:use-attribute-sets='defaultText' padding-top='2px' font-weight="bold">
									<!-- Don't print total in low level of detail.
									Lower->uppercase FOR ASCII ONLY. XSLT has no support
									for this. -->
									<xsl:if test="$levelOfDetail != 'low'">TOTAL </xsl:if><xsl:value-of select="concat(substring(CATEGORY, 1, 1), translate(substring(CATEGORY,2), $lowercaseChars, $uppercaseChars))"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/AGENCY_REQUEST_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/INSTITUTION_COST_SHARE_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/THIRD_PARTY_COST_SHARE_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
								<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
									<xsl:value-of select="format-number(sum(../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/AGENCY_REQUEST_AMOUNT) + sum(../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/INSTITUTION_COST_SHARE_AMOUNT) + sum(../../../TASK_PERIOD[*]/NON_PERSONNEL/NON_PERSONNEL_ITEM[CATEGORY = current()/CATEGORY]/THIRD_PARTY_COST_SHARE_AMOUNT), '###,###')"/>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						
						<!-- Empty Row -->
						<fo:table-row>
							<fo:table-cell/>
							<fo:table-cell/>
							<fo:table-cell/>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell/>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell/>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
							<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						</fo:table-row>
					</xsl:if>
				</xsl:for-each>
				
				<!-- The overline on Total Direct Costs is supposed to stay on the
				previous page if there is a page overflow. Since Total Direct
				Costs has a keep-with-next the overline has to be extracted
				from that and rendered seperatly. -->
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Calculate Total: Direct Costs -->
				<fo:table-row keep-with-next="always">
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							TOTAL DIRECT COSTS
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/@TOTAL_AGENCY_REQUEST_DIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/@TOTAL_INSTITUTION_DIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/@TOTAL_THIRD_PARTY_DIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/@TOTAL_AGENCY_REQUEST_DIRECT_COST) + sum(../TASK_PERIOD[*]/@TOTAL_INSTITUTION_DIRECT_COST) + sum(../TASK_PERIOD[*]/@TOTAL_THIRD_PARTY_DIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<xsl:if test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_DESCRIPTIONS">
					<!-- Calculate Total: Modular Adjustment -->
					<fo:table-row keep-with-next="always">
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultTextIndented' font-style="italic">
								MODULAR ADJUSTMENT
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
								<!-- No percentage on summaries-->
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
								<xsl:value-of select="format-number(sum(../../MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[*]/MODULAR_ADJUSTMENT), '###,###')"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
								<xsl:value-of select="format-number(sum(../../MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[*]/MODULAR_ADJUSTMENT), '###,###')"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					
					<!-- Calculate Total: Adjusted Direct Costs -->
					<fo:table-row keep-with-next="always">
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultTextIndented' font-style="italic">
								ADJUSTED DIRECT COSTS
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCell'>
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
								<!-- No percentage on summaries-->
							</fo:block>
						</fo:table-cell>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
								<xsl:value-of select="format-number(sum(../../MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[*]/ADJUSTED_MODULAR_DIRECT_COST), '###,###')"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
				
				<!-- Calculate Total: Indirect Costs -->
				<fo:table-row keep-with-next="always">
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultTextIndented' font-style="italic">
							INDIRECT COSTS
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<!-- No percentage on summaries-->
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/@TOTAL_AGENCY_REQUEST_INDIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/@TOTAL_INSTITUTION_INDIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/@TOTAL_THIRD_PARTY_INDIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
							<xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/@TOTAL_AGENCY_REQUEST_INDIRECT_COST) + sum(../TASK_PERIOD[*]/@TOTAL_INSTITUTION_INDIRECT_COST) + sum(../TASK_PERIOD[*]/@TOTAL_THIRD_PARTY_INDIRECT_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- Calculate Total: Unrecovered Indirect Costs -->
				<xsl:if test="/PROPOSAL/BUDGET/COST_SHARE/INSTITUTION_INDIRECT_COST_SHARE/@TOTAL_UNRECOVERED_INDIRECT_COST != 0">
					<fo:table-row keep-with-next="always">
						<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
							<fo:block xsl:use-attribute-sets='defaultTextIndented' font-style="italic">
								UNRECOVERED INDIRECT COSTS
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
								<xsl:value-of select="format-number(/PROPOSAL/BUDGET/COST_SHARE/INSTITUTION_INDIRECT_COST_SHARE/@TOTAL_UNRECOVERED_INDIRECT_COST, '###,###')"/>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
						<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
							<fo:block xsl:use-attribute-sets='defaultText' font-style="italic">
								<xsl:value-of select="format-number(/PROPOSAL/BUDGET/COST_SHARE/INSTITUTION_INDIRECT_COST_SHARE/@TOTAL_UNRECOVERED_INDIRECT_COST, '###,###')"/>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</xsl:if>
				
				<!-- Calculate Total: Costs -->
				<fo:table-row keep-with-next="always">
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold">
							TOTAL COSTS
						</fo:block>
					</fo:table-cell>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold" border-bottom-style="solid" border-bottom-width="thin" border-top-style="solid" border-top-width="thin">
							$ <xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/@TOTAL_AGENCY_COST)
								+ sum(../../MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[*]/MODULAR_ADJUSTMENT), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<!-- 4/18/2005 dterret: The following was changed as part of a temporary fix. -->
					<!-- 5/10/2005 dterret: The temporary change was backed out here. -->
					<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold" border-bottom-style="solid" border-bottom-width="thin" border-top-style="solid" border-top-width="thin">
							$ <xsl:value-of select="format-number(sum(../TASK_PERIOD/@TOTAL_INSTITUTION_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold" border-bottom-style="solid" border-bottom-width="thin" border-top-style="solid" border-top-width="thin">
							$ <xsl:value-of select="format-number(sum(../TASK_PERIOD[*]/@TOTAL_THIRD_PARTY_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
					<!-- 4/18/2005 dterret: The following was changed as part of a temporary fix. -->
					<!-- 5/10/2005 dterret: The temporary change was backed out here. -->
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
						<fo:block xsl:use-attribute-sets='defaultText' font-weight="bold" border-bottom-style="solid" border-bottom-width="thin" border-top-style="solid" border-top-width="thin">
							$ <xsl:value-of select="format-number(sum(../TASK_PERIOD/@TOTAL_AGENCY_COST)
								+ sum(../../MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[*]/MODULAR_ADJUSTMENT)
								+ sum(../TASK_PERIOD/@TOTAL_INSTITUTION_COST)
								+ sum(../TASK_PERIOD/@TOTAL_THIRD_PARTY_COST), '###,###')"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<!-- This row underlines the Total Costs row again so that there is a
				double underline. Unfortunatly that is necessary because the
				"double" style appears not to be working. -->
				<fo:table-row>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell/>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
					<fo:table-cell xsl:use-attribute-sets='tableCell'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
					<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'>
						<fo:block padding-bottom="2px" border-bottom-style="solid" border-bottom-width="thin"/>
					</fo:table-cell>
				</fo:table-row>
				
			</fo:table-body>
		</fo:table>
	</xsl:template>
	
	
	<!-- ******************* **************************** ******************* -->
	<!-- *************** Non-Personnel Listing (sub-template) *************** -->
	<!-- ******************* **************************** ******************* -->
	<xsl:template match="NON_PERSONNEL_ITEM">
		<fo:table-row>
			<fo:table-cell xsl:use-attribute-sets='tableCell' number-columns-spanned="3">
				<fo:block xsl:use-attribute-sets='defaultTextIndented'>
					<xsl:value-of select="./SUB_CATEGORY"/> 
					<xsl:if test="string-length(./DESCRIPTION) != 0">
						-- <xsl:value-of select="./DESCRIPTION"/>
					</xsl:if>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
			<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
				<fo:block xsl:use-attribute-sets='defaultText'>
					<xsl:value-of select="format-number(./AGENCY_REQUEST_AMOUNT, '###,###')"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder'/>
			<fo:table-cell xsl:use-attribute-sets='tableCell' text-align="right">
				<fo:block xsl:use-attribute-sets='defaultText'>
					<xsl:value-of select="format-number(./INSTITUTION_COST_SHARE_AMOUNT, '###,###')"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
				<fo:block xsl:use-attribute-sets='defaultText'>
					<xsl:value-of select="format-number(./THIRD_PARTY_COST_SHARE_AMOUNT, '###,###')"/>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell xsl:use-attribute-sets='tableCellLeftBorder' text-align="right">
				<fo:block xsl:use-attribute-sets='defaultText'>
					<xsl:value-of select="format-number(./AGENCY_REQUEST_AMOUNT + ./INSTITUTION_COST_SHARE_AMOUNT + ./THIRD_PARTY_COST_SHARE_AMOUNT, '###,###')"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
	
  <!-- ******************* **************************** ******************* -->
  <!-- *******************   Appointment Type Template  ******************* -->
  <!-- ******************* **************************** ******************* -->
  <!-- This maps two of the appointment types to abbreviated labels and then
       ensures that any other appointment type is dropped. This template can
       be used to accomplish that mapping. It states it own xsl:block
       statements and presumably should be called within a fo:table-cell. -->
  <xsl:template name="appointmentType">
  	<xsl:param name="summary" select="'false'"/>
    <xsl:variable name="app" select="./APPOINTMENT/@APPOINTMENT_CODE"/>
    <xsl:choose>
      <xsl:when test="$app = 'A2'">
        <fo:block xsl:use-attribute-sets='defaultText'>
          ACAD_YR
        </fo:block>
      </xsl:when>
      <xsl:when test="$app = 'AS'">
        <fo:block xsl:use-attribute-sets='defaultText'>
          <xsl:value-of select="./APPOINTMENT/@APPOINTMENT_TYPE"/>
        </fo:block>
      </xsl:when>
      <xsl:otherwise>
      	<xsl:call-template name="hourlyRate">
      		<xsl:with-param name="summary" select="$summary"/>
      	</xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
		
	<!-- ******************* **************************** ******************* -->
	<!-- *******************     Hourly Rate Template     ******************* -->
	<!-- ******************* **************************** ******************* -->
	<xsl:template name="hourlyRate">
		<xsl:param name="summary" select="'false'"/>
		<xsl:variable name="appCd" select="./APPOINTMENT/@APPOINTMENT_CODE"/>
		<xsl:choose>
			<!-- If the position is hourly, print hourly rate.  -->
			<xsl:when test="$appCd = 'SH' or $appCd = 'H1' or $appCd = 'H2' or $appCd = 'WS'">
				<xsl:variable name="rate" select="./@PERIOD_SALARY"/>
				<xsl:choose>
					<!-- If this is the summary of summaries, print the rate only if there is not more
					     than one rate for this person. -->
					<xsl:when test="$summary = 'sumSum'">
						<xsl:variable name="personInstances"
							select="key('uniquePersonsKey',
							            concat( ./NAME, ' ',
							                    ./APPOINTMENT/@APPOINTMENT_CODE, ' ',
							                    ./@SEQUENCE_NUMBER ) )"/>
						<xsl:if test="count($personInstances[@PERIOD_SALARY != $rate]) = 0">
							<fo:block xsl:use-attribute-sets='defaultText'>
								$ <xsl:value-of select="format-number( $rate , '###,##0.00' )"/> / hr.
							</fo:block>
						</xsl:if>
					</xsl:when>
					<!-- Print the rate only if there is not more than one rate for this person. -->
					<xsl:otherwise>
						<xsl:variable name="personInstances"
							select="key('PEOPLE',
											     concat( ../../@PERIOD_NUMBER, ' ',
							                    ./NAME, ' ',
							                    ./APPOINTMENT/@APPOINTMENT_TYPE, ' ',
							                    ./@SEQUENCE_NUMBER ) )"/>
						<xsl:if test="($summary = 'false') or 
												 	(count($personInstances[@PERIOD_SALARY != $rate]) = 0)">
							<fo:block xsl:use-attribute-sets='defaultText'>
								$ <xsl:value-of select="format-number( $rate , '###,##0.00' )"/> / hr.
							</fo:block>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise><!-- left blank --></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- ******************* **************************** ******************* -->
	<!-- ***************  Effort Percent or Hours Template  ***************** -->
	<!-- ******************* **************************** ******************* -->
	<xsl:template name="effortPercentOrHours">
		<xsl:param name="hours"/>
		<xsl:param name="percentSalary"/>
		<xsl:variable name="appCd" select="./APPOINTMENT/@APPOINTMENT_CODE"/>
		<fo:block xsl:use-attribute-sets='defaultText'>
			<xsl:choose>
				<!-- If the position is hourly, print hours to be worked. -->
				<xsl:when test="$appCd = 'SH' or $appCd = 'H1' or $appCd = 'H2' or $appCd = 'WS'">
					<xsl:value-of select="format-number( $hours, '###,##0' )"/> hrs.
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$percentSalary"/>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
	</xsl:template>
	
</xsl:stylesheet>

