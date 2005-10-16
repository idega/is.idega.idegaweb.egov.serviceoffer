/*
 * $Id: ServiceOfferChoiceHome.java,v 1.2 2005/10/16 16:19:51 eiki Exp $
 * Created on Oct 16, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.data;

import com.idega.data.IDOHome;


/**
 * 
 *  Last modified: $Date: 2005/10/16 16:19:51 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
 */
public interface ServiceOfferChoiceHome extends IDOHome {

	public ServiceOfferChoice create() throws javax.ejb.CreateException;

	public ServiceOfferChoice findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
}
