/*
 * $Id: ServiceOfferChoice.java,v 1.2 2005/10/16 16:19:50 eiki Exp $
 * Created on Oct 16, 2005
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
 *  Last modified: $Date: 2005/10/16 16:19:50 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.2 $
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
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoiceBMPBean#hasBeenViewed
	 */
	public boolean hasBeenViewed();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoiceBMPBean#setAsViewed
	 */
	public void setAsViewed();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoiceBMPBean#setAsNotViewed
	 */
	public void setAsNotViewed();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoiceBMPBean#hasBeenPaidFor
	 */
	public boolean hasBeenPaidFor();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoiceBMPBean#setAsPaidFor
	 */
	public void setAsPaidFor();

	/**
	 * @see is.idega.idegaweb.egov.serviceoffer.data.ServiceOfferChoiceBMPBean#setAsUnPaidFor
	 */
	public void setAsUnPaidFor();
}
