package com.catl.line.monitor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TomcatLoadListener implements ServletContextListener{  
	  public void contextInitialized (ServletContextEvent sce){  
		  new TomcatMonitor();
	  }  
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}  
	}  
