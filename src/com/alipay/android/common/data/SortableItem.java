/**
 * 
 */
package com.alipay.android.common.data;


/**
 * @author sanping.li
 *
 */
public abstract class SortableItem implements Comparable<SortableItem> {


    @Override
    public int compareTo(SortableItem another) {
        return another.getSortKey().compareTo(getSortKey());
    }
    
    public abstract String getSortKey();

}
