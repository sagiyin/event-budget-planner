package budgeteventplanner.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import budgeteventplanner.client.entity.Event;
import budgeteventplanner.client.entity.Service;
import budgeteventplanner.client.entity.ServiceRequest;
import budgeteventplanner.shared.Pent;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EventServiceAsync {
	void createEvent(String organizerId, String name, Date startTime,
			Date endTime, String address, AsyncCallback<Void> callback);

	void updateEventByEventId(String eventId, String name, Date startTime,
			Date endTime, String address, Integer status,
			AsyncCallback<Void> callback);

	void getEventsByOrganizerId(String organizerId,
			AsyncCallback<List<Event>> callback);

	void getEventsByOrganizerIdAndStatus(String organizerId, Integer status,
			AsyncCallback<List<Event>> callback);

	void addServiceRequest(String serviceId, String eventId, String name,
			Integer quantity, Date dueDate, AsyncCallback<Void> callback);

	void getServiceRequestsByEventId(String eventId,
			AsyncCallback<List<ServiceRequest>> callback);

	void getServicesByCategoryId(String categoryId,
			AsyncCallback<List<Service>> callback);

	void changeEventStatusByEventId(String eventId, Integer status,
			AsyncCallback<Void> callback);

	void deleteEventByEventId(String eventId, AsyncCallback<Void> callback);
	
	void getAllCostInfoByEventId(String eventId,
			AsyncCallback<List<Pent<String, String, String, Integer, Double>>> callback);
}
