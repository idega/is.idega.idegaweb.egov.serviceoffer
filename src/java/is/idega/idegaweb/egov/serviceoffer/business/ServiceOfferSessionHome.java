package is.idega.idegaweb.egov.serviceoffer.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface ServiceOfferSessionHome extends IBOHome {

	public ServiceOfferSession create() throws CreateException, RemoteException;
}