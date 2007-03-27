package is.idega.idegaweb.egov.serviceoffer.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class ServiceOfferSessionHomeImpl extends IBOHomeImpl implements ServiceOfferSessionHome {

	public Class getBeanInterfaceClass() {
		return ServiceOfferSession.class;
	}

	public ServiceOfferSession create() throws CreateException {
		return (ServiceOfferSession) super.createIBO();
	}
}