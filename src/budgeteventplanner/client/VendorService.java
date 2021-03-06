package budgeteventplanner.client;

import java.util.List;
import java.util.Map;

import budgeteventplanner.client.entity.Service;
import budgeteventplanner.client.entity.ServiceRequest;
import budgeteventplanner.shared.Pair;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("vendor")
public interface VendorService extends RemoteService {
	void addService(String categoryId, String vendorId, String name,
			Double price, String description);

	void deleteService(String serviceId);

	List<Pair<String, Service>> getServiceByVendorId(String vendorId);

	List<Pair<String, ServiceRequest>> getServiceRequestByStatus(String vendorId,
			Integer status);

	void updateServiceRequestStatus(String serviceRequestId, Integer status);
}
