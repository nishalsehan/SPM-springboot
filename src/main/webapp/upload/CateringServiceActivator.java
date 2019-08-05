package catering_service_subscriber;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import catering_service.ICateringService;

public class CateringServiceActivator implements BundleActivator{

	ServiceReference CateringServiceReference;

	@Override
	public void start(BundleContext mContext) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Catering Subscriber Service started...");
		CateringServiceReference = mContext.getServiceReference(ICateringService.class.getName());
		
		ICateringService service = (ICateringService)mContext.getService(CateringServiceReference);
		service.selectWork();
	}

	@Override
	public void stop(BundleContext mContext) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("End the Subscriber");
		mContext.ungetService(CateringServiceReference);
	}

}
