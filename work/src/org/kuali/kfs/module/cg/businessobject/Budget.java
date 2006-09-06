/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */
package org.kuali.module.kra.budget.bo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.cg.bo.Agency;
import org.kuali.module.cg.bo.ProjectDirector;

/**
 * 
 * This class...
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class Budget extends BusinessObjectBase {

    private static final long serialVersionUID = 8113894775967293272L;
    private String documentHeaderId;
    private Long budgetParentTrackNumber;
    private String budgetName;
    private boolean universityCostShareIndicator;
    private String budgetProgramAnnouncementNumber;
    private String budgetProjectDirectorSystemId;
    private boolean budgetThirdPartyCostShareIndicator;
    private String budgetStatusCode;
    private KualiDecimal budgetPersonnelInflationRate;
    private KualiDecimal budgetNonpersonnelInflationRate;
    private String electronicResearchAdministrationGrantNumber;
    private Long routeSheetTrackNumber;
    private String budgetFringeRateDescription;
    private boolean agencyModularIndicator;
    private String budgetAgencyNumber;
    private String federalPassThroughAgencyNumber;
    private String budgetProgramAnnouncementName;
    private String costShareFinChartOfAccountCd;
    private String costShareOrgCd;
    private String budgetTypeCodeText;

    private boolean agencyToBeNamedIndicator;
    private boolean projectDirectorToBeNamedIndicator;

    private Agency budgetAgency;
    private Agency federalPassThroughAgency;
    private ProjectDirector projectDirector;
    private BudgetModular modularBudget;
    private List tasks;
    private List periods;
    private List fringeRates;
    private List graduateAssistantRates;
    private List nonpersonnelItems;
    private List personnel;
    private List universityCostShareItems;
    private List thirdPartyCostShareItems;
    private List universityCostSharePersonnelItems;
    private List<BudgetAdHocPermission> adHocPermissions;
    private List<BudgetAdHocOrg> adHocOrgs;

    private BudgetIndirectCost indirectCost;

    private List allUserAppointmentTasks;
    private List allUserAppointmentTaskPeriods;
    private List allUniversityCostSharePeriods;
    private List allThirdPartyCostSharePeriods;
    private List<BudgetIndirectCostLookup> budgetIndirectCostLookups;
    
    public Budget() {
        super();

        budgetPersonnelInflationRate = new KualiDecimal(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("KraDevelopmentGroup", "defaultPersonnelInflationRate"));
        budgetNonpersonnelInflationRate = new KualiDecimal(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("KraDevelopmentGroup", "defaultNonpersonnelInflationRate"));

        tasks = new ArrayList();
        periods = new ArrayList();
        fringeRates = new ArrayList();
        graduateAssistantRates = new ArrayList();
        nonpersonnelItems = new ArrayList();
        personnel = new ArrayList();
        universityCostShareItems = new ArrayList();
        thirdPartyCostShareItems = new ArrayList();
        universityCostSharePersonnelItems = new ArrayList();
        adHocPermissions = new ArrayList<BudgetAdHocPermission>();
        adHocOrgs = new ArrayList<BudgetAdHocOrg>();
        budgetIndirectCostLookups = new ArrayList<BudgetIndirectCostLookup>();

    }

    public Budget(String documentHeaderId) {
        this();
        this.documentHeaderId = documentHeaderId;
    }

    /**
     * Gets the budgetAgencyNumber attribute.
     * 
     * @return Returns the budgetAgencyNumber.
     */
    public String getBudgetAgencyNumber() {
        return budgetAgencyNumber;
    }

    /**
     * Sets the budgetAgencyNumber attribute value.
     * 
     * @param budgetAgencyNumber The budgetAgencyNumber to set.
     */
    public void setBudgetAgencyNumber(String budgetAgencyNumber) {
        this.budgetAgencyNumber = budgetAgencyNumber;
    }

    /**
     * @return Returns the projectDirector.
     */
    public ProjectDirector getProjectDirector() {
        return projectDirector;
    }

    /**
     * @param projectDirector The projectDirector to set.
     */
    public void setProjectDirector(ProjectDirector projectDirector) {
        this.projectDirector = projectDirector;
    }

    /**
     * @return Returns the budgetFederalPassThroughIndicator.
     */
    /**
     * @return Returns the budgetFringeRateDescription.
     */
    public String getBudgetFringeRateDescription() {
        return budgetFringeRateDescription;
    }

    /**
     * @param budgetFringeRateDescription The budgetFringeRateDescription to set.
     */
    public void setBudgetFringeRateDescription(String budgetFringeRateDescription) {
        this.budgetFringeRateDescription = budgetFringeRateDescription;
    }

    /**
     * @return Returns the budgetName.
     */
    public String getBudgetName() {
        return budgetName;
    }

    /**
     * @param budgetName The budgetName to set.
     */
    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    /**
     * @return Returns the budgetParentTrackNumber.
     */
    public Long getBudgetParentTrackNumber() {
        return budgetParentTrackNumber;
    }

    /**
     * @param budgetParentTrackNumber The budgetParentTrackNumber to set.
     */
    public void setBudgetParentTrackNumber(Long budgetParentTrackNumber) {
        this.budgetParentTrackNumber = budgetParentTrackNumber;
    }

    /**
     * @return Returns the budgetProgramAnnouncementNumber.
     */
    public String getBudgetProgramAnnouncementNumber() {
        return budgetProgramAnnouncementNumber;
    }

    /**
     * @param budgetProgramAnnouncementNumber The budgetProgramAnnouncementNumber to set.
     */
    public void setBudgetProgramAnnouncementNumber(String budgetProgramAnnouncementNumber) {
        this.budgetProgramAnnouncementNumber = budgetProgramAnnouncementNumber;
    }

    /**
     * @return Returns the budgetProjectDirectorSystemId.
     */
    public String getBudgetProjectDirectorSystemId() {
        return budgetProjectDirectorSystemId;
    }

    /**
     * @param budgetProjectDirectorSystemId The budgetProjectDirectorSystemId to set.
     */
    public void setBudgetProjectDirectorSystemId(String budgetProjectDirectorSystemId) {
        this.budgetProjectDirectorSystemId = budgetProjectDirectorSystemId;
    }

    /**
     * @return Returns the budgetStatusCode.
     */
    public String getBudgetStatusCode() {
        return budgetStatusCode;
    }

    /**
     * @param budgetStatusCode The budgetStatusCode to set.
     */
    public void setBudgetStatusCode(String budgetStatusCode) {
        this.budgetStatusCode = budgetStatusCode;
    }

    /**
     * @return Returns the agencyModularIndicator.
     */
    public boolean isAgencyModularIndicator() {
        return agencyModularIndicator;
    }

    /**
     * @param agencyModularIndicator The agencyModularIndicator to set.
     */
    public void setAgencyModularIndicator(boolean agencyModularIndicator) {
        this.agencyModularIndicator = agencyModularIndicator;
    }

    /**
     * @return Returns the budgetNonpersonnelInflationRate.
     */
    public KualiDecimal getBudgetNonpersonnelInflationRate() {
        return budgetNonpersonnelInflationRate;
    }

    /**
     * @param budgetNonpersonnelInflationRate The budgetNonpersonnelInflationRate to set.
     */
    public void setBudgetNonpersonnelInflationRate(KualiDecimal budgetNonpersonnelInflationRate) {
        this.budgetNonpersonnelInflationRate = budgetNonpersonnelInflationRate;
    }

    /**
     * @return Returns the budgetPersonnelInflationRate.
     */
    public KualiDecimal getBudgetPersonnelInflationRate() {
        return budgetPersonnelInflationRate;
    }

    /**
     * @param budgetPersonnelInflationRate The budgetPersonnelInflationRate to set.
     */
    public void setBudgetPersonnelInflationRate(KualiDecimal budgetPersonnelInflationRate) {
        this.budgetPersonnelInflationRate = budgetPersonnelInflationRate;
    }

    /**
     * @return Returns the budgetThirdPartyCostShareIndicator.
     */
    public boolean isBudgetThirdPartyCostShareIndicator() {
        return budgetThirdPartyCostShareIndicator;
    }

    /**
     * @param budgetThirdPartyCostShareIndicator The budgetThirdPartyCostShareIndicator to set.
     */
    public void setBudgetThirdPartyCostShareIndicator(boolean budgetThirdPartyCostShareIndicator) {
        this.budgetThirdPartyCostShareIndicator = budgetThirdPartyCostShareIndicator;
    }

    /**
     * @return Returns the universityCostShareIndicator.
     */
    public boolean isUniversityCostShareIndicator() {
        return universityCostShareIndicator;
    }

    /**
     * @param universityCostShareIndicator The universityCostShareIndicator to set.
     */
    public void setUniversityCostShareIndicator(boolean universityCostShareIndicator) {
        this.universityCostShareIndicator = universityCostShareIndicator;
    }

    /**
     * @return Returns the electronicResearchAdministrationGrantNumber.
     */
    public String getElectronicResearchAdministrationGrantNumber() {
        return electronicResearchAdministrationGrantNumber;
    }

    /**
     * @param electronicResearchAdministrationGrantNumber The electronicResearchAdministrationGrantNumber to set.
     */
    public void setElectronicResearchAdministrationGrantNumber(String electronicResearchAdministrationGrantNumber) {
        this.electronicResearchAdministrationGrantNumber = electronicResearchAdministrationGrantNumber;
    }

    /**
     * @return Returns the periods.
     */
    public List<BudgetPeriod> getPeriods() {
        return periods;
    }

    /**
     * @param periods The periods to set.
     */
    public void setPeriods(List periods) {
        this.periods = periods;
    }

    /**
     * Retrieve a particular task at a given index in the list of tasks.
     * 
     * @param index
     * @return
     */
    public BudgetPeriod getPeriod(int index) {
        while (getPeriods().size() <= index) {
            getPeriods().add(new BudgetPeriod());
        }
        return (BudgetPeriod) getPeriods().get(index);
    }

    /**
     * @return Returns the routeSheetTrackNumber.
     */
    public Long getRouteSheetTrackNumber() {
        return routeSheetTrackNumber;
    }

    /**
     * @param routeSheetTrackNumber The routeSheetTrackNumber to set.
     */
    public void setRouteSheetTrackNumber(Long routeSheetTrackNumber) {
        this.routeSheetTrackNumber = routeSheetTrackNumber;
    }

    /**
     * @return Returns the tasks.
     */
    public List<BudgetTask> getTasks() {
        return tasks;
    }

    /**
     * @param tasks The tasks to set.
     */
    public void setTasks(List tasks) {
        this.tasks = tasks;
    }

    /**
     * Retrieve a particular task at a given index in the list of tasks.
     * 
     * @param index
     * @return
     */
    public BudgetTask getTask(int index) {
        while (getTasks().size() <= index) {
            getTasks().add(new BudgetTask());
        }
        return (BudgetTask) getTasks().get(index);
    }

    /**
     * @return Returns the budgetAgency.
     */
    public Agency getBudgetAgency() {
        return budgetAgency;
    }

    /**
     * @param budgetAgency The budgetAgency to set.
     */
    public void setBudgetAgency(Agency budgetAgency) {
        this.budgetAgency = budgetAgency;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        LinkedHashMap map = new LinkedHashMap();
        map.put("documentHeaderId", this.documentHeaderId);
        return map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.bo.Document#populateDocumentForRouting()
     */
    public void populateDocumentForRouting() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.bo.Document#getDocumentTitle()
     */
    public String getDocumentTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return Returns the documentHeaderId.
     */
    public String getDocumentHeaderId() {
        return documentHeaderId;
    }

    /**
     * @param documentHeaderId The documentHeaderId to set.
     */
    public void setDocumentHeaderId(String documentHeaderId) {
        this.documentHeaderId = documentHeaderId;
    }

    // /**
    // * @return Returns the budgetDocument.
    // */
    // public BudgetDocument getBudgetDocument() {
    // return budgetDocument;
    // }
    // /**
    // * @param budgetDocument The budgetDocument to set.
    // */
    // public void setBudgetDocument(BudgetDocument budgetDocument) {
    // this.budgetDocument = budgetDocument;
    // }

    /**
     * @return Returns the fringeRates.
     */
    public List getFringeRates() {
        return fringeRates;
    }

    /**
     * @param fringeRates The fringeRates to set.
     */
    public void setFringeRates(List fringeRates) {
        this.fringeRates = fringeRates;
    }

    /**
     * Retrieve a particular fringe rate at a given index in the list of rates.
     * 
     * @param index
     * @return
     */
    public BudgetFringeRate getFringeRate(int index) {
        while (getFringeRates().size() <= index) {
            getFringeRates().add(new BudgetFringeRate());
        }
        return (BudgetFringeRate) getFringeRates().get(index);
    }

    /**
     * 
     * @return Returns the graduate assistant rates
     */
    public List getGraduateAssistantRates() {
        return graduateAssistantRates;
    }

    /**
     * 
     * @param graduateAssistantRates The graduate assistant rates to set
     */
    public void setGraduateAssistantRates(List graduateAssistantRates) {
        this.graduateAssistantRates = graduateAssistantRates;
    }

    /**
     * Retreive a particular graduate assistant rate at a given index in the list of rates.
     * 
     * @param index
     * @return
     */
    public BudgetGraduateAssistantRate getGraduateAssistantRate(int index) {
        while (getGraduateAssistantRates().size() <= index) {
            getGraduateAssistantRates().add(new BudgetGraduateAssistantRate());
        }
        return (BudgetGraduateAssistantRate) getGraduateAssistantRates().get(index);
    }


    /**
     * Gets the nonpersonnelItems attribute.
     * 
     * @return Returns the nonpersonnelItems.
     */
    public List getNonpersonnelItems() {
        return nonpersonnelItems;
    }

    /**
     * Sets the nonpersonnelItems attribute value.
     * 
     * @param nonpersonnelItems The nonpersonnelItems to set.
     */
    public void setNonpersonnelItems(List nonpersonnelItems) {
        this.nonpersonnelItems = nonpersonnelItems;
    }

    /**
     * Retreive a particular nonpersonnelitem at the given index in the list of personnel.
     * 
     * @param index
     * @return
     */
    public BudgetNonpersonnel getNonpersonnelItem(int index) {
        while (getNonpersonnelItems().size() <= index) {
            getNonpersonnelItems().add(new BudgetNonpersonnel());
        }
        return (BudgetNonpersonnel) getNonpersonnelItems().get(index);
    }

    /**
     * Gets the federalPassThroughAgency attribute.
     * 
     * @return Returns the federalPassThroughAgency.
     */
    public Agency getFederalPassThroughAgency() {
        return federalPassThroughAgency;
    }

    /**
     * Sets the federalPassThroughAgency attribute value.
     * 
     * @param federalPassThroughAgency The federalPassThroughAgency to set.
     */
    public void setFederalPassThroughAgency(Agency federalPassThroughAgency) {
        this.federalPassThroughAgency = federalPassThroughAgency;
    }

    /**
     * Gets the federalPassThroughAgencyNumber attribute.
     * 
     * @return Returns the federalPassThroughAgencyNumber.
     */
    public String getFederalPassThroughAgencyNumber() {
        return federalPassThroughAgencyNumber;
    }

    /**
     * Sets the federalPassThroughAgencyNumber attribute value.
     * 
     * @param federalPassThroughAgencyNumber The federalPassThroughAgencyNumber to set.
     */
    public void setFederalPassThroughAgencyNumber(String federalPassThroughAgencyNumber) {
        this.federalPassThroughAgencyNumber = federalPassThroughAgencyNumber;
    }

    /**
     * Gets the personnel attribute.
     * 
     * @return Returns the personnel.
     */
    public List<BudgetUser> getPersonnel() {
        return personnel;
    }

    /**
     * Sets the personnel attribute value.
     * 
     * @param personnel The personnel to set.
     */
    public void setPersonnel(List personnel) {
        this.personnel = personnel;
    }

    /**
     * Retreive a particular person at the given index in the list of personnel.
     * 
     * @param index
     * @return
     */
    public BudgetUser getPersonFromList(int index) {
        while (getPersonnel().size() <= index) {
            getPersonnel().add(new BudgetUser());
        }
        return (BudgetUser) getPersonnel().get(index);
    }

    /**
     * Gets the modularBudget attribute.
     * 
     * @return Returns the modularBudget.
     */
    public BudgetModular getModularBudget() {
        return modularBudget;
    }

    /**
     * Sets the modularBudget attribute value.
     * 
     * @param modularBudget The modularBudget to set.
     */
    public void setModularBudget(BudgetModular modularBudget) {
        this.modularBudget = modularBudget;
    }

    /**
     * Gets the budgetProgramAnnouncementName attribute.
     * 
     * @return Returns the budgetProgramAnnouncementName.
     */
    public String getBudgetProgramAnnouncementName() {
        return budgetProgramAnnouncementName;
    }

    /**
     * Sets the budgetProgramAnnouncementName attribute value.
     * 
     * @param budgetProgramAnnouncementName The budgetProgramAnnouncementName to set.
     */
    public void setBudgetProgramAnnouncementName(String budgetProgramAnnouncementName) {
        this.budgetProgramAnnouncementName = budgetProgramAnnouncementName;
    }

    /**
     * @return Returns the costShareFinChartOfAccountCd.
     */
    public String getCostShareFinChartOfAccountCd() {
        return costShareFinChartOfAccountCd;
    }

    /**
     * @param costShareFinChartOfAccountCd The costShareFinChartOfAccountCd to set.
     */
    public void setCostShareFinChartOfAccountCd(String costShareFinChartOfAccountCd) {
        this.costShareFinChartOfAccountCd = costShareFinChartOfAccountCd;
    }

    /**
     * @return Returns the costShareOrgCd.
     */
    public String getCostShareOrgCd() {
        return costShareOrgCd;
    }

    /**
     * @param costShareOrgCd The costShareOrgCd to set.
     */
    public void setCostShareOrgCd(String costShareOrgCd) {
        this.costShareOrgCd = costShareOrgCd;
    }

    /**
     * @return Returns the budgetTypeCodeText.
     */
    public String getBudgetTypeCodeText() {
        return budgetTypeCodeText;
    }

    /**
     * @param budgetTypeCodeText The budgetTypeCodeText to set.
     */
    public void setBudgetTypeCodeText(String budgetTypeCodeText) {
        this.budgetTypeCodeText = budgetTypeCodeText;
    }

    /**
     * Getters & setters for BudgetTypeCodes in array form - for checkboxes
     * 
     * @return String[]
     */
    public String[] getBudgetTypeCodeArray() {
        return this.getBudgetTypeCodeText().split("-");
    }

    public void setBudgetTypeCodeArray(String[] budgetTypeCodeArray) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < budgetTypeCodeArray.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            sb.append(budgetTypeCodeArray[i]);
        }
        this.setBudgetTypeCodeText(sb.toString());
    }

    /**
     * @return Returns the universityCostShareItems.
     */
    public List getUniversityCostShareItems() {
        return universityCostShareItems;
    }

    public BudgetUniversityCostShare getUniversityCostShareItem(int index) {
        while (getUniversityCostShareItems().size() <= index) {
            getUniversityCostShareItems().add(new BudgetUniversityCostShare());
        }
        return (BudgetUniversityCostShare) getUniversityCostShareItems().get(index);
    }

    /**
     * @param universityCostShareItems The universityCostShareItems to set.
     */
    public void setUniversityCostShareItems(List universityCostShareItems) {
        this.universityCostShareItems = universityCostShareItems;
    }

    /**
     * @return Returns the agencyToBeNamedIndicator.
     */
    public boolean isAgencyToBeNamedIndicator() {
        return agencyToBeNamedIndicator;
    }

    /**
     * @param agencyToBeNamedIndicator The agencyToBeNamedIndicator to set.
     */
    public void setAgencyToBeNamedIndicator(boolean agencyToBeNamedIndicator) {
        this.agencyToBeNamedIndicator = agencyToBeNamedIndicator;
    }

    /**
     * @return Returns the projectDirectorToBeNamedIndicator.
     */
    public boolean isProjectDirectorToBeNamedIndicator() {
        return projectDirectorToBeNamedIndicator;
    }

    /**
     * @param projectDirectorToBeNamedIndicator The projectDirectorToBeNamedIndicator to set.
     */
    public void setProjectDirectorToBeNamedIndicator(boolean projectDirectorToBeNamedIndicator) {
        this.projectDirectorToBeNamedIndicator = projectDirectorToBeNamedIndicator;
    }

    /**
     * @return budgetIndirectCost;
     */
    public BudgetIndirectCost getIndirectCost() {
        return indirectCost;
    }

    /**
     * @param budgetIndirectCost
     */
    public void setIndirectCost(BudgetIndirectCost indirectCost) {
        this.indirectCost = indirectCost;
    }

    public void setAllUserAppointmentTasks(List list) {
        this.allUserAppointmentTasks = list;
    }

    public List getAllUserAppointmentTasks() {
        return getAllUserAppointmentTaskPeriods(false);
    }

    public List getAllUserAppointmentTasks(boolean forceRefreshPriorToSave) {
        if (allUserAppointmentTasks == null) {
            List list = new ArrayList();
            for (Iterator i = personnel.iterator(); i.hasNext();) {
                BudgetUser budgetUser = (BudgetUser) i.next();
                if (forceRefreshPriorToSave) {
                    budgetUser = new BudgetUser(budgetUser);
                    budgetUser.refreshReferenceObject("userAppointmentTasks");
                }
                list.addAll(budgetUser.getUserAppointmentTasks());
            }
            return list;
        }
        else {
            return allUserAppointmentTasks;
        }
    }


    public void setAllUserAppointmentTaskPeriods(List list) {
        this.allUserAppointmentTaskPeriods = list;
    }

    public List getAllUserAppointmentTaskPeriods() {
        return getAllUserAppointmentTaskPeriods(false);
    }

    public List getAllUserAppointmentTaskPeriods(boolean forceRefreshPriorToSave) {
        if (allUserAppointmentTaskPeriods == null) {
            List list = new ArrayList();
            for (Iterator i = getAllUserAppointmentTasks(forceRefreshPriorToSave).iterator(); i.hasNext();) {
                UserAppointmentTask userAppointmentTask = (UserAppointmentTask) i.next();
                if (forceRefreshPriorToSave) {
                    userAppointmentTask = new UserAppointmentTask(userAppointmentTask);
                    userAppointmentTask.refreshReferenceObject("userAppointmentTaskPeriods");
                }
                list.addAll(userAppointmentTask.getUserAppointmentTaskPeriods());
            }
            return list;
        }
        else {
            return allUserAppointmentTaskPeriods;
        }
    }

    /**
     * @return Returns the thirdPartyCostShareItems.
     */
    public List getThirdPartyCostShareItems() {
        return thirdPartyCostShareItems;
    }

    public BudgetThirdPartyCostShare getThirdPartyCostShareItem(int index) {
        while (getThirdPartyCostShareItems().size() <= index) {
            getThirdPartyCostShareItems().add(new BudgetThirdPartyCostShare());
        }
        return (BudgetThirdPartyCostShare) getThirdPartyCostShareItems().get(index);
    }

    /**
     * @param thirdPartyCostShareItems The thirdPartyCostShareItems to set.
     */
    public void setThirdPartyCostShareItems(List thirdPartyCostShareItems) {
        this.thirdPartyCostShareItems = thirdPartyCostShareItems;
    }

    /**
     * @return Returns the allThirdPartyCostSharePeriods.
     */
    public List getAllThirdPartyCostSharePeriods() {
        return allThirdPartyCostSharePeriods;
    }

    /**
     * @param allThirdPartyCostSharePeriods The allThirdPartyCostSharePeriods to set.
     */
    public void setAllThirdPartyCostSharePeriods(List allThirdPartyCostSharePeriods) {
        this.allThirdPartyCostSharePeriods = allThirdPartyCostSharePeriods;
    }


    public List getAllThirdPartyCostSharePeriods(boolean forceRefreshPriorToSave) {
        if (allThirdPartyCostSharePeriods == null) {
            List list = new ArrayList();
            for (Iterator i = thirdPartyCostShareItems.iterator(); i.hasNext();) {
                BudgetThirdPartyCostShare costShareItem = (BudgetThirdPartyCostShare) i.next();
                if (forceRefreshPriorToSave) {
                    costShareItem = new BudgetThirdPartyCostShare(costShareItem);
                    costShareItem.refreshReferenceObject("budgetPeriodCostShare");
                }
                list.addAll(costShareItem.getBudgetPeriodCostShare());
            }
            return list;
        }
        else {
            return allThirdPartyCostSharePeriods;
        }
    }

    /**
     * @return Returns the allUniversityCostSharePeriods.
     */
    public List getAllUniversityCostSharePeriods() {
        return allUniversityCostSharePeriods;
    }

    /**
     * @param allUniversityCostSharePeriods The allUniversityCostSharePeriods to set.
     */
    public void setAllUniversityCostSharePeriods(List allUniversityCostSharePeriods) {
        this.allUniversityCostSharePeriods = allUniversityCostSharePeriods;
    }


    public List getAllUniversityCostSharePeriods(boolean forceRefreshPriorToSave) {
        if (allUniversityCostSharePeriods == null) {
            List list = new ArrayList();
            for (Iterator i = universityCostShareItems.iterator(); i.hasNext();) {
                BudgetUniversityCostShare costShareItem = (BudgetUniversityCostShare) i.next();
                if (forceRefreshPriorToSave) {
                    costShareItem = new BudgetUniversityCostShare(costShareItem);
                    costShareItem.refreshReferenceObject("budgetPeriodCostShare");
                }
                list.addAll(costShareItem.getBudgetPeriodCostShare());
            }
            return list;
        }
        else {
            return allUniversityCostSharePeriods;
        }
    }

    /**
     * @return Returns the universityCostSharePersonnelItems.
     */
    public List getUniversityCostSharePersonnelItems() {
        return universityCostSharePersonnelItems;
    }

    public UniversityCostSharePersonnel getUniversityCostSharePersonnelItem(int index) {
        while (getUniversityCostSharePersonnelItems().size() <= index) {
            getUniversityCostSharePersonnelItems().add(new UniversityCostSharePersonnel());
        }
        return (UniversityCostSharePersonnel) getUniversityCostSharePersonnelItems().get(index);
    }

    /**
     * @param universityCostSharePersonnelItems The universityCostSharePersonnelItems to set.
     */
    public void setUniversityCostSharePersonnelItems(List universityCostSharePersonnelItems) {
        this.universityCostSharePersonnelItems = universityCostSharePersonnelItems;
    }


    /**
     * Gets the adHocPermissions attribute.
     * 
     * @return Returns the adHocPermissions.
     */
    public List<BudgetAdHocPermission> getAdHocPermissions() {
        return adHocPermissions;
    }

    /**
     * Sets the adHocPermissions attribute value.
     * 
     * @param adHocPermissions The adHocPermissions to set.
     */
    public void setAdHocPermissions(List<BudgetAdHocPermission> adHocPermissions) {
        this.adHocPermissions = adHocPermissions;
    }
    
    /**
     * Gets the BudgetAdHocPermission item at given index.
     * 
     * @param index
     * @return BudgetAdHocPermission
     */
    public BudgetAdHocPermission getBudgetAdHocPermissionItem(int index) {
        while (this.getAdHocPermissions().size() <= index) {
            this.getAdHocPermissions().add(new BudgetAdHocPermission());
        }
        return this.getAdHocPermissions().get(index);
    }
        
    /**
     * Gets the adHocOrgs attribute. 
     * @return Returns the adHocOrgs.
     */
    public List<BudgetAdHocOrg> getAdHocOrgs() {
        return adHocOrgs;
    }

    /**
     * Sets the adHocOrgs attribute value.
     * @param adHocOrgs The adHocOrgs to set.
     */
    public void setAdHocOrgs(List<BudgetAdHocOrg> adHocOrgs) {
        this.adHocOrgs = adHocOrgs;
    }
    
    /**
     * Gets the BudgetAdHocOrg item at given index.
     * 
     * @param index
     * @return BudgetAdHocOrg
     */
    public BudgetAdHocOrg getBudgetAdHocOrgItem(int index) {
        while (this.getAdHocOrgs().size() <= index) {
            this.getAdHocOrgs().add(new BudgetAdHocOrg());
        }
        return this.getAdHocOrgs().get(index);
    }

    public Date getDefaultNextPeriodBeginDate() {
        if (this.getPeriods().size() > 0) {
            BudgetPeriod lastPeriod = (BudgetPeriod) this.getPeriods().get(this.getPeriods().size() - 1);
            if (lastPeriod.getBudgetPeriodEndDate() != null) {
                Date oldEndDate = lastPeriod.getBudgetPeriodEndDate();
                Calendar oldEndCal = new GregorianCalendar();
                oldEndCal.setTime(oldEndDate);
                oldEndCal.add(Calendar.DATE, 1);
                return new Date(oldEndCal.getTimeInMillis());
            }
        }
        return null;
    }

    public void setBudgetIndirectCostLookups(List<BudgetIndirectCostLookup> budgetIndirectCostLookupList) {
        this.budgetIndirectCostLookups = budgetIndirectCostLookupList;
    }
    
    public List<BudgetIndirectCostLookup> getBudgetIndirectCostLookups() {
        return this.budgetIndirectCostLookups;
    }
    
    public BudgetIndirectCostLookup getBudgetIndirectCostLookup(int index) {
        while (this.getBudgetIndirectCostLookups().size() <= index) {
            this.getBudgetIndirectCostLookups().add(new BudgetIndirectCostLookup());
        }
        return this.getBudgetIndirectCostLookups().get(index);
    }
}
