/*
 * $Id: IWBundleStarter.java,v 1.4 2009/01/30 07:29:54 laddi Exp $
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


public class IWBundleStarter implements IWBundleStartable,ServiceOfferConstants {

	public void start(IWBundle starterBundle) {
		CaseCodeManager caseCodeManager = CaseCodeManager.getInstance();
		caseCodeManager.addCaseBusinessForCode( CASE_CODE_KEY_SERVICE_OFFER_PARENT, ServiceOfferBusiness.class);
		caseCodeManager.addCaseBusinessForCode( CASE_CODE_KEY_SERVICE_OFFER, ServiceOfferBusiness.class);
	}

	public void stop(IWBundle starterBundle) {
	}
}
