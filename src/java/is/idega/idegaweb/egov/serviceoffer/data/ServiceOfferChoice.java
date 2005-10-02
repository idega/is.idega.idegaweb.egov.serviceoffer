/*
 * $Id: ServiceOfferChoice.java,v 1.1 2005/10/02 23:42:29 eiki Exp $
 * Created on Oct 2, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.data;

import is.idega.idegaweb.egov.serviceoffer.util.ServiceOfferConstants;
import com.idega.block.process.data.Case;
import com.idega.data.IDOEntity;
import com.idega.user.data.User;


/**
 * 
 *  Last modified: $Date: 2005/10/02 23:42:29 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public interface ServiceOfferChoice extends IDOEntity, Case, ServiceOfferConstants {

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoiceBMPBean#getCaseCodeKey
	 */
	public String getCaseCodeKey();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoiceBMPBean#getCaseCodeDescription
	 */
	public String getCaseCodeDescription();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoiceBMPBean#getUser
	 */
	public User getUser();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoiceBMPBean#setUser
	 */
	public void setUser(User user);

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoiceBMPBean#getUserPK
	 */
	public Object getUserPK();
}
