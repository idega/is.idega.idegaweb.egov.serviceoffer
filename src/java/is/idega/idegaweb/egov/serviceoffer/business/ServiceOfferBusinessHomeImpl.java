/*
 * $Id: ServiceOfferBusinessHomeImpl.java,v 1.5 2006/03/20 08:09:34 laddi Exp $
 * Created on Mar 20, 2006
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.egov.serviceoffer.business;





import com.idega.business.IBOHomeImpl;


/**
 * <p>
 * TODO laddi Describe Type ServiceOfferBusinessHomeImpl
 * </p>
 *  Last modified: $Date: 2006/03/20 08:09:34 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.5 $
 */
public class ServiceOfferBusinessHomeImpl extends IBOHomeImpl implements ServiceOfferBusinessHome {

	protected Class getBeanInterfaceClass() {
		return ServiceOfferBusiness.class;
	}

	public ServiceOfferBusiness create() throws javax.ejb.CreateException {
		return (ServiceOfferBusiness) super.createIBO();
	}

}
