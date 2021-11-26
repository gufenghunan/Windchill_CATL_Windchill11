package com.catl.promotion.processor;

import java.util.List;

import wt.util.WTException;

import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.maturity.commands.PromotionItemQueryCommands;

public class PromotionItemCollector extends PromotionItemQueryCommands {

   
    public static List<Object> getPromotionItems(NmCommandBean clientData)
            throws WTException
          {
              System.out.println("Enter into CATL Auto Collector");
               return PromotionItemQueryCommands.getPromotionItems(clientData);
          }
    
    
}
