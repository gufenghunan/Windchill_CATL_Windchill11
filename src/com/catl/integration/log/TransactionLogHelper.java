package com.catl.integration.log;

import org.apache.log4j.Logger;

import wt.pom.PersistentObjectManager;
import wt.pom.Transaction;

import com.ptc.xworks.util.XWorksHelper;
import com.ptc.xworks.xmlobject.store.XmlObjectStoreManager;

public class TransactionLogHelper {

	private static final Logger logger = Logger.getLogger(TransactionLogHelper.class);

	public static void logTransaction(WebServiceTransactionLog transactionLog) {
		Transaction tx = null;
		try {
			if (!PersistentObjectManager.getTransactionManager().isTransactionActive()) {
				 tx = new Transaction();
            	 tx.start();
            } 
			XmlObjectStoreManager storeManager = XWorksHelper.getXmlObjectStoreManager();
			storeManager.save(transactionLog);
			if(tx != null){
				tx.commit();
				tx = null;
			}
		} catch (Throwable e) {
			logger.error("Exception when try to save WebServiceTransactionLog", e);
			if (tx != null) {
				tx.rollback();
				tx = null;
			}
		}

	}

}
