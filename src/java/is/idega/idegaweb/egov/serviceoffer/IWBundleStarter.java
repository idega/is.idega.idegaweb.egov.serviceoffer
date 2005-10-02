/*
 * $Id: IWBundleStarter.java,v 1.1 2005/10/02 23:42:29 eiki Exp $
 * Created on Sep 29, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer;

import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.include.GlobalIncludeManager;


public class IWBundleStarter implements IWBundleStartable,ServiceOfferConstants {

	public void start(IWBundle starterBundle) {
		GlobalIncludeManager includeManager = GlobalIncludeManager.getInstance();
		includeManager.addBundleStyleSheet(IW_BUNDLE_IDENTIFIER, "/style/serviceoffer.css");
	}

	public void stop(IWBundle starterBundle) {
	}
}
