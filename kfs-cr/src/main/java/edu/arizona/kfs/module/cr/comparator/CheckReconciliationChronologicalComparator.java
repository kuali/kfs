package edu.arizona.kfs.module.cr.comparator;

import java.util.Comparator;

import edu.arizona.kfs.module.cr.businessobject.CheckReconciliation;

public class CheckReconciliationChronologicalComparator implements Comparator<CheckReconciliation> {

    @Override
    public int compare(CheckReconciliation o1, CheckReconciliation o2) {
        if (!o1.getCheckDate().equals(o2.getCheckDate())) {
            return o1.getCheckDate().compareTo(o2.getCheckDate());
        }
        return o1.getCheckNumber().compareTo(o2.getCheckNumber());
    }

}
