package org.kuali.module.kra.budget.web.struts.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetTask;
import org.kuali.module.kra.budget.bo.BudgetTaskPeriodIndirectCost;

/**
 * Used by UI to get totals, counts, aggregations, and other things to render the Indirect Cost page.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetIndirectCostFormHelper {

    /**
     * List of taskPeriod objects to store task totals across all periods. Each taskPeriod BO will be attached to only a task, not a
     * period.
     */
    private List taskTotals;

    /**
     * List of taskPeriod objects to store task totals across all periods. Each taskPeriod BO will be attached to only a task, not a
     * period.
     */
    private List periodTotals;

    /**
     * Object to hold period subtotal values. These are for the subtotal line after the period summary.
     */
    private BudgetTaskPeriodIndirectCost periodSubTotal;

    /**
     * Number of periods, so we know when to echo our subtotal lines.
     */
    private Integer numPeriods;

    /**
     * Default constructor.
     */
    public BudgetIndirectCostFormHelper() {
        taskTotals = new ArrayList();
        periodTotals = new ArrayList();
        numPeriods = new Integer(0);
    }

    /**
     * Non-default constructor, sets default values based on passed BudgetForm object.
     * 
     * @param budgetForm
     */
    public BudgetIndirectCostFormHelper(BudgetForm budgetForm) {
        this(budgetForm.getBudgetDocument().getBudget().getTasks(), budgetForm.getBudgetDocument().getBudget().getPeriods(), budgetForm.getBudgetDocument().getBudget().getIndirectCost().getBudgetTaskPeriodIndirectCostItems());
    }

    /**
     * Update all Indirect Cost totals. This is the function generally called from the action class. Used this method to consolidate
     * all calls.
     * 
     * @param tasks
     * @param periods
     */
    public BudgetIndirectCostFormHelper(List tasks, List periods, List idcItems) {
        this();
        this.initializeTotals(tasks, periods); // First make sure we have at least zero values.
        this.updateTotals(idcItems);
        this.setNumPeriods(new Integer(periods.size()));
    }

    /**
     * Iterate over all tasks and periods to set up HashMaps with default (zero) values.
     * 
     * @param tasks
     * @param periods
     */
    public void initializeTotals(List tasks, List periods) {
        // Set up zero-values for the taskHash for all types.
        for (Iterator taskIterator = tasks.iterator(); taskIterator.hasNext();) {
            BudgetTask task = (BudgetTask) taskIterator.next();

            BudgetTaskPeriodIndirectCost taskPeriod = new BudgetTaskPeriodIndirectCost();
            taskPeriod.setBudgetTaskSequenceNumber(task.getBudgetTaskSequenceNumber());
            taskPeriod.setTask(task);

            this.getTaskTotals().add(taskPeriod);
        }

        // Set up zero-values for the periodHash for all types.
        for (Iterator periodIterator = periods.iterator(); periodIterator.hasNext();) {
            BudgetPeriod period = (BudgetPeriod) periodIterator.next();

            BudgetTaskPeriodIndirectCost taskPeriod = new BudgetTaskPeriodIndirectCost();
            taskPeriod.setBudgetPeriodSequenceNumber(period.getBudgetPeriodSequenceNumber());
            taskPeriod.setPeriod(period);

            this.getPeriodTotals().add(taskPeriod);
        }
    }


    /**
     * Iterate over all taskPeriods. For each one, add their totals to the HashMaps.
     * 
     * @param tasks
     * @param periods
     * @param idcItems We should now have a list, since createTaskPeriodIdcList would have set it up for us just now, or on any
     *        previous save.
     */
    public void updateTotals(List idcItems) {
        // Place to store aggregated period values for the period subtotal.
        BudgetTaskPeriodIndirectCost subTotal = new BudgetTaskPeriodIndirectCost();

        // Iterate over all existing idc list items and add the appropriate values for each taskPeriodLine
        // to the corresponding HashMap[#][type].
        for (Iterator idcItemsIterator = idcItems.iterator(); idcItemsIterator.hasNext();) {
            BudgetTaskPeriodIndirectCost taskPeriod = (BudgetTaskPeriodIndirectCost) idcItemsIterator.next();


            // Update task totals.
            for (Iterator taskTotalIterator = this.getTaskTotals().iterator(); taskTotalIterator.hasNext();) {
                BudgetTaskPeriodIndirectCost taskTotal = (BudgetTaskPeriodIndirectCost) taskTotalIterator.next();

                if (taskTotal.getBudgetTaskSequenceNumber().equals(taskPeriod.getBudgetTaskSequenceNumber())) {
                    taskTotal.setTotalDirectCost(taskTotal.getTotalDirectCost().add(taskPeriod.getTotalDirectCost()));
                    taskTotal.setBaseCost(taskTotal.getBaseCost().add(taskPeriod.getBaseCost()));
                    taskTotal.setCalculatedIndirectCost(taskTotal.getCalculatedIndirectCost().add(taskPeriod.getCalculatedIndirectCost()));
                    taskTotal.setCostShareBaseCost(taskTotal.getCostShareBaseCost().add(taskPeriod.getCostShareBaseCost()));
                    taskTotal.setCostShareCalculatedIndirectCost(taskTotal.getCostShareCalculatedIndirectCost().add(taskPeriod.getCostShareCalculatedIndirectCost()));
                    taskTotal.setCostShareUnrecoveredIndirectCost(taskTotal.getCostShareUnrecoveredIndirectCost().add(taskPeriod.getCostShareUnrecoveredIndirectCost()));
                }
            }

            // Update period totals.
            for (Iterator periodTotalIterator = this.getPeriodTotals().iterator(); periodTotalIterator.hasNext();) {
                BudgetTaskPeriodIndirectCost periodTotal = (BudgetTaskPeriodIndirectCost) periodTotalIterator.next();

                if (periodTotal.getBudgetPeriodSequenceNumber().equals(taskPeriod.getBudgetPeriodSequenceNumber())) {
                    periodTotal.setTotalDirectCost(periodTotal.getTotalDirectCost().add(taskPeriod.getTotalDirectCost()));
                    periodTotal.setBaseCost(periodTotal.getBaseCost().add(taskPeriod.getBaseCost()));
                    periodTotal.setCalculatedIndirectCost(periodTotal.getCalculatedIndirectCost().add(taskPeriod.getCalculatedIndirectCost()));
                    periodTotal.setCostShareBaseCost(periodTotal.getCostShareBaseCost().add(taskPeriod.getCostShareBaseCost()));
                    periodTotal.setCostShareCalculatedIndirectCost(periodTotal.getCostShareCalculatedIndirectCost().add(taskPeriod.getCostShareCalculatedIndirectCost()));
                    periodTotal.setCostShareUnrecoveredIndirectCost(periodTotal.getCostShareUnrecoveredIndirectCost().add(taskPeriod.getCostShareUnrecoveredIndirectCost()));
                }
            }

            // Set the period subtotal for each period, since the subtotal spans all periods.
            subTotal.setTotalDirectCost(subTotal.getTotalDirectCost().add(taskPeriod.getTotalDirectCost()));
            subTotal.setBaseCost(subTotal.getBaseCost().add(taskPeriod.getBaseCost()));
            subTotal.setCalculatedIndirectCost(subTotal.getCalculatedIndirectCost().add(taskPeriod.getCalculatedIndirectCost()));
            subTotal.setCostShareBaseCost(subTotal.getCostShareBaseCost().add(taskPeriod.getCostShareBaseCost()));
            subTotal.setCostShareCalculatedIndirectCost(subTotal.getCostShareCalculatedIndirectCost().add(taskPeriod.getCostShareCalculatedIndirectCost()));
            subTotal.setCostShareUnrecoveredIndirectCost(subTotal.getCostShareUnrecoveredIndirectCost().add(taskPeriod.getCostShareUnrecoveredIndirectCost()));
        }

        this.setPeriodSubTotal(subTotal);
    }

    /**
     * @return Returns the periodTotals.
     */
    public List<BudgetTaskPeriodIndirectCost> getPeriodTotals() {
        return periodTotals;
    }

    /**
     * @return Returns a periodTotal.
     */
    public BudgetTaskPeriodIndirectCost getPeriodTotal(int index) {
        while (getPeriodTotals().size() <= index) {
            getPeriodTotals().add(new BudgetTaskPeriodIndirectCost());
        }
        return (BudgetTaskPeriodIndirectCost) getPeriodTotals().get(index);
    }

    /**
     * @param periodTotals The periodTotals to set.
     */
    public void setPeriodTotals(List periodTotals) {
        this.periodTotals = periodTotals;
    }

    /**
     * @return Returns the taskTotals.
     */
    public List getTaskTotals() {
        return taskTotals;
    }

    /**
     * @param taskTotals The taskTotals to set.
     */
    public void setTaskTotals(List taskTotals) {
        this.taskTotals = taskTotals;
    }

    /**
     * @return Returns the numPeriods.
     */
    public Integer getNumPeriods() {
        return numPeriods;
    }

    /**
     * @param numPeriods The numPeriods to set.
     */
    public void setNumPeriods(Integer numPeriods) {
        this.numPeriods = numPeriods;
    }

    /**
     * @return Returns the periodSubTotal.
     */
    public BudgetTaskPeriodIndirectCost getPeriodSubTotal() {
        return periodSubTotal;
    }

    /**
     * @param periodSubTotal The periodSubTotal to set.
     */
    public void setPeriodSubTotal(BudgetTaskPeriodIndirectCost periodSubTotal) {
        this.periodSubTotal = periodSubTotal;
    }
}
