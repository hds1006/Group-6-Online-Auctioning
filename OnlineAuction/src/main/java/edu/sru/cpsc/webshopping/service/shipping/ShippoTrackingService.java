package edu.sru.cpsc.webshopping.service.shipping;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shippo.Shippo;
import com.shippo.exception.ShippoException;
import com.shippo.model.Track;

import edu.sru.cpsc.webshopping.domain.market.Shipping;

@Service
public class ShippoTrackingService {

	private final String shippoApiKey = "shippo_test_3dde1f5f7f457ed0646d4eb9ce4c19f3e584b1ae";

	public TrackDTO getTrackingInformation(String carrier, String trackingNumber, Shipping shipping) {
		Shippo.setApiKey(shippoApiKey);

		// test values: carrier = "shippo", trackingNumber = "SHIPPO_TRANSIT"
		try {
			Track track = Track.getTrackingInfo(carrier, trackingNumber, shippoApiKey);
			return mapTrackToTrackDTO(track, shipping);
		} catch (ShippoException e) {
			throw new RuntimeException("Failed to get tracking information", e);
		}
	}

	private TrackDTO mapTrackToTrackDTO(Track track, Shipping shipping) {
		TrackDTO trackDTO = new TrackDTO();
		trackDTO.setTrackingNumber(track.getTrackingNumber());
		trackDTO.setCarrier(track.getCarrier());
		trackDTO.setAddressFrom(mapAddressToAddressDTO(track.getAddressFrom()));
		trackDTO.setAddressTo(mapAddressToAddressDTO(track.getAddressTo()));
		trackDTO.setEta(track.getETA());

		TrackingStatusDTO currentStatus = mapTrackingStatusToTrackingStatusDTO(track.getTrackingStatus());
		List<TrackingEventDTO> history = mapTrackingEventToTrackingEventDTO(track.getTrackingHistory());

		trackDTO.setTrackingStatus(currentStatus);
		trackDTO.setTrackingHistory(history);

		// Get shipped date
		Date shippedDate = getOrDetermineShippedDate(shipping, track.getTrackingHistory(), currentStatus);
		trackDTO.setDateShipped(shippedDate);

		return trackDTO;
	}

	private Date getOrDetermineShippedDate(Shipping shipping, Track.TrackingEvent[] history,
			TrackingStatusDTO currentStatus) {
		if (shipping != null && shipping.getDateShipped() != null) {
			return shipping.getDateShipped();
		}

		Date earliestDate = null;
		if (history != null) {
			for (Track.TrackingEvent event : history) {
				try {
					// Get status
					Field statusField = Track.TrackingEvent.class.getDeclaredField("status");
					statusField.setAccessible(true);
					Enum<?> statusEnum = (Enum<?>) statusField.get(event);
					String status = (statusEnum != null) ? statusEnum.name() : "";

					// Get status details
					Field statusDetailsField = Track.TrackingEvent.class.getDeclaredField("statusDetails");
					statusDetailsField.setAccessible(true);
					String statusDetails = (String) statusDetailsField.get(event);

					boolean isShippingEvent = status.equals("PRE_TRANSIT") || status.equals("TRANSIT")
							|| (statusDetails != null && (statusDetails.toLowerCase().contains("shipment information")
									|| statusDetails.toLowerCase().contains("label created")));

					if (isShippingEvent) {
						Field statusDateField = Track.TrackingEvent.class.getDeclaredField("statusDate");
						statusDateField.setAccessible(true);
						Date eventDate = (Date) statusDateField.get(event);

						if (earliestDate == null || (eventDate != null && eventDate.before(earliestDate))) {
							earliestDate = eventDate;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (earliestDate != null) {
			return earliestDate;
		}

		if (currentStatus != null && (currentStatus.getStatus().equals("PRE_TRANSIT")
				|| currentStatus.getStatus().equals("TRANSIT") || currentStatus.getStatus().equals("DELIVERED"))) {
			try {
				return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(currentStatus.getStatusDate());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private AddressDTO mapAddressToAddressDTO(Track.Address address) {
		if (address == null) {
			return null;
		}
		AddressDTO addressDTO = new AddressDTO();
		try {
			Field cityField = Track.Address.class.getDeclaredField("city");
			cityField.setAccessible(true);
			String city = (String) cityField.get(address);
			addressDTO.setCity(city);

			Field stateField = Track.Address.class.getDeclaredField("state");
			stateField.setAccessible(true);
			String state = (String) stateField.get(address);
			addressDTO.setState(state);

			Field zipField = Track.Address.class.getDeclaredField("zip");
			zipField.setAccessible(true);
			String zip = (String) zipField.get(address);
			addressDTO.setZip(zip);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Failed to access address fields", e);
		}
		return addressDTO;
	}

	private TrackingStatusDTO mapTrackingStatusToTrackingStatusDTO(Track.TrackingEvent trackingStatus) {
		if (trackingStatus == null) {
			return null;
		}
		TrackingStatusDTO trackingStatusDTO = new TrackingStatusDTO();
		try {
			Field statusField = Track.TrackingEvent.class.getDeclaredField("status");
			statusField.setAccessible(true);
			Enum<?> statusEnum = (Enum<?>) statusField.get(trackingStatus);

			// Add this check and logic for PRE_TRANSIT
			String status;
			if (statusEnum == null) {
				// Check statusDetails for pre-transit condition
				Field statusDetailsField = Track.TrackingEvent.class.getDeclaredField("statusDetails");
				statusDetailsField.setAccessible(true);
				String statusDetails = (String) statusDetailsField.get(trackingStatus);

				if (statusDetails != null && statusDetails.contains("received the electronic shipment information")) {
					status = "PRE_TRANSIT";
				} else {
					status = "UNKNOWN";
				}
			} else {
				status = statusEnum.name();
			}
			trackingStatusDTO.setStatus(status);
			Field statusDetailsField = Track.TrackingEvent.class.getDeclaredField("statusDetails");
			statusDetailsField.setAccessible(true);
			String statusDetails = (String) statusDetailsField.get(trackingStatus);
			trackingStatusDTO.setStatusDetails(statusDetails);

			Field statusDateField = Track.TrackingEvent.class.getDeclaredField("statusDate");
			statusDateField.setAccessible(true);
			Date statusDate = (Date) statusDateField.get(trackingStatus);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			String statusDateString = sdf.format(statusDate);
			trackingStatusDTO.setStatusDate(statusDateString);

			trackingStatusDTO.setLocation(mapAddressToAddressDTO(trackingStatus.getLocation()));
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Failed to access tracking status fields", e);
		}
		return trackingStatusDTO;
	}

	private List<TrackingEventDTO> mapTrackingEventToTrackingEventDTO(Track.TrackingEvent[] trackingEvents) {
		if (trackingEvents == null) {
			return null;
		}
		List<TrackingEventDTO> trackingEventDTOs = new ArrayList<>();
		for (Track.TrackingEvent trackingEvent : trackingEvents) {
			TrackingEventDTO trackingEventDTO = new TrackingEventDTO();
			try {
				// Get and check status
				Field statusField = Track.TrackingEvent.class.getDeclaredField("status");
				statusField.setAccessible(true);
				Enum<?> statusEnum = (Enum<?>) statusField.get(trackingEvent);

				String status;
				if (statusEnum == null) {
					Field statusDetailsField = Track.TrackingEvent.class.getDeclaredField("statusDetails");
					statusDetailsField.setAccessible(true);
					String statusDetails = (String) statusDetailsField.get(trackingEvent);

					if (statusDetails != null
							&& statusDetails.contains("received the electronic shipment information")) {
						status = "PRE_TRANSIT";
					} else {
						status = "UNKNOWN";
					}
				} else {
					status = statusEnum.name();
				}
				trackingEventDTO.setStatus(status);

				// Get status details
				Field statusDetailsField = Track.TrackingEvent.class.getDeclaredField("statusDetails");
				statusDetailsField.setAccessible(true);
				String statusDetails = (String) statusDetailsField.get(trackingEvent);
				trackingEventDTO.setStatusDetails(statusDetails);

				// Get status date
				Field statusDateField = Track.TrackingEvent.class.getDeclaredField("statusDate");
				statusDateField.setAccessible(true);
				Date statusDate = (Date) statusDateField.get(trackingEvent);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				String statusDateString = sdf.format(statusDate);
				trackingEventDTO.setStatusDate(statusDateString);

				// Get location
				trackingEventDTO.setLocation(mapAddressToAddressDTO(trackingEvent.getLocation()));

			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException("Failed to access tracking event fields", e);
			}
			trackingEventDTOs.add(trackingEventDTO);
		}
		return trackingEventDTOs;
	}

}
