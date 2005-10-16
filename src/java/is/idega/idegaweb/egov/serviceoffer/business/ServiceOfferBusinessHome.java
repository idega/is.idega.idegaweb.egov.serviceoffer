/*
 * $Id: ServiceOfferBusinessHome.java,v 1.3 2005/10/16 16:19:50 eiki Exp $
 * Created on Oct 16, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2005/10/16 16:19:50 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public interface ServiceOfferBusinessHome extends IBOHome {

	public ServiceOfferBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
