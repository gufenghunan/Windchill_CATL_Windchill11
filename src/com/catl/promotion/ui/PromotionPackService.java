package com.catl.promotion.ui;

import java.util.ArrayList;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public interface PromotionPackService {
	public ArrayList getPromotionPackItems(ArrayList promotablelist) throws Exception;
}
