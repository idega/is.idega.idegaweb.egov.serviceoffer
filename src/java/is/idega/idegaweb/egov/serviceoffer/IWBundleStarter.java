/*
 * $Id: IWBundleStarter.java,v 1.2 2005/10/06 18:06:40 eiki Exp $
 * Created on Sep 29, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer;

import is.idega.idegaweb.egov.serviceoffer.business.ServiceOfferBusiness;
import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import com.idega.block.process.business.CaseCodeManager;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.include.GlobalIncludeManager;


public class IWBundleStarter implements IWBundleStartable,ServiceOfferConstants {

	public void start(IWBundle starterBundle) {
		GlobalIncludeManager includeManager = GlobalIncludeManager.getInstance();
		includeManager.addBundleStyleSheet(IW_BUNDLE_IDENTIFIER, "/style/serviceoffer.css");
		
		CaseCodeManager caseCodeManager = CaseCodeManager.getInstance();
		caseCodeManager.addCaseBusinessForCode( CASE_CODE_KEY_SERVICE_OFFER_PARENT, ServiceOfferBusiness.class);
		caseCodeManager.addCaseBusinessForCode( CASE_CODE_KEY_SERVICE_OFFER, ServiceOfferBusiness.class);
	}

	public void stop(IWBundle starterBundle) {
	}
}
