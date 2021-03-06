package budgeteventplanner.server;

import java.util.Date;
import java.util.List;

import budgeteventplanner.client.EventService;
import budgeteventplanner.client.entity.Attendee;
import budgeteventplanner.client.entity.Category;
import budgeteventplanner.client.entity.Event;
import budgeteventplanner.client.entity.Service;
import budgeteventplanner.client.entity.ServiceRequest;
import budgeteventplanner.shared.Pent;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;

@SuppressWarnings("serial")
public class EventServiceImpl extends RemoteServiceServlet implements
		EventService {
	static {
		try {
			ObjectifyService.register(Attendee.class);
			ObjectifyService.register(Event.class);
			ObjectifyService.register(Service.class);
			ObjectifyService.register(ServiceRequest.class);
		} catch (Exception e) {
		}
	}

	@Override
	public void createEvent(String organizerId, String name, Date startTime,
			Date endTime, String address) {
		Objectify ofy = ObjectifyService.begin();
		Event event = new Event.Builder(organizerId, name, startTime, endTime,
				address).build();
		ofy.put(event);
	}

	@Override
	public void updateEventByEventId(String eventId, String name,
			Date startTime, Date endTime, String address, Integer status) {
		Objectify ofy = ObjectifyService.begin();
		Event oldEvent = ofy.get(new Key<Event>(Event.class, eventId));
		Event newEvent = new Event.Builder(oldEvent).setName(name)
				.setStartTime(startTime).setEndTime(endTime)
				.setAddress(address).setStatus(status).build();
		ofy.put(newEvent);
	}

	@Override
	public List<Event> getEventsByOrganizerId(String organizerId) {
		Objectify ofy = ObjectifyService.begin();
		Query<Event> q = ofy.query(Event.class).filter("organizerId",
				organizerId);
		return q.list();
	}

	@Override
	public List<Event> getEventsByOrganizerIdAndStatus(String organizerId,
			Integer status) {
		Objectify ofy = ObjectifyService.begin();
		Query<Event> q = ofy.query(Event.class)
				.filter("organizerId", organizerId).filter("status", status);
		return q.list();
	}

	@Override
	public void addServiceRequest(String serviceId, String eventId,
			String name, Integer quantity, Date dueDate) {
		Objectify ofy = ObjectifyService.begin();
		ServiceRequest request = new ServiceRequest.Builder(serviceId, eventId,
				name, quantity, dueDate).build();
		ofy.put(request);
	}

	@Override
	public List<ServiceRequest> getServiceRequestsByEventId(String eventId) {
		Objectify ofy = ObjectifyService.begin();
		Query<ServiceRequest> q = ofy.query(ServiceRequest.class).filter(
				"eventId", eventId);
		return q.list();
	}

	@Override
	public List<Service> getServicesByCategoryId(String categoryId) {
		Objectify ofy = ObjectifyService.begin();
		Query<Service> q = ofy.query(Service.class).filter("categoryId",
				categoryId);
		return q.list();
	}

	@Override
	public void changeEventStatusByEventId(String eventId, Integer status) {
		Objectify ofy = ObjectifyService.begin();
		Event oldEvent = ofy.get(new Key<Event>(Event.class, eventId));
		Event newEvent = new Event.Builder(oldEvent).setStatus(status).build();
		ofy.put(newEvent);
	}

	@Override
	public void deleteEventByEventId(String eventId) {
		Objectify ofy = ObjectifyService.begin();
		ofy.delete(new Key<Event>(Event.class, eventId));
	}

	@Override
	public List<Pent<String, String, String, Integer, Double>> getAllCostInfoByEventId(
			String eventId) {
		Objectify ofy = ObjectifyService.begin();
		
		// SvcReq name | categoryName | serviceName | qty | price
		List<Pent<String, String, String, Integer, Double>> list = Lists.newArrayList();
				
		//String eventName = ofy.get(new Key<Event>(Event.class, eventId)).getName();
		Query<ServiceRequest> q = ofy.query(ServiceRequest.class).filter(
				"eventId", eventId);
		for (ServiceRequest request : q) 
		{
			Service svc = ofy.get(new Key<Service>(Service.class, request.getServiceId()));
			String catName = ofy.get(new Key<Category>(Category.class, svc.getCategoryId())).getName();
			list.add(
					new Pent<String, String, String, Integer, Double>
					(request.getName(), catName, svc.getName(), request.getQuantity(), svc.getPrice())
					);
		}
		

		//
		return list;
	}

}