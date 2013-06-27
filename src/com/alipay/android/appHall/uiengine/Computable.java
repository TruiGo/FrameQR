/**
 * 
 */
package com.alipay.android.appHall.uiengine;

import java.util.ArrayList;

/**
 * @author sanping.li
 *
 */
public interface Computable {
    public Object compute(ArrayList<Object> params);
    public void reset();
}
