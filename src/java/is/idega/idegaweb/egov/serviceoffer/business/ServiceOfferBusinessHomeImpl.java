package is.idega.idegaweb.egov.serviceoffer.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class ServiceOfferBusinessHomeImpl extends IBOHomeImpl implements ServiceOfferBusinessHome {

	public Class getBeanInterfaceClass() {
		return ServiceOfferBusiness.class;
	}

	public ServiceOfferBusiness create() throws CreateException {
		return (ServiceOfferBusiness) super.createIBO();
	}
}