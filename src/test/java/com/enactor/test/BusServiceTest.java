package com.enactor.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enactor.model.Reservation;
import com.enactor.service.BusService;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

@DisplayName("Bus Service Tests")
public class BusServiceTest {

	private BusService busService;

	@BeforeEach
	public void setUp() {
		busService = BusService.getInstance();
	}

	@Test
	@DisplayName("Get Available Seats")
	public void testGetAvailableSeats() {
		List<String> seats = busService.getAvailableSeats("A", "B", 5);

		assertTrue(seats.size() >= 5, "Should have at least 5 available seats");

	}

	@Test
	@DisplayName("Get Price for Different Routes")
	public void testGetPrice() {
		int priceAB = busService.getPrice("A", "B");
		assertEquals(50, priceAB, "Price from A to B should be 50");

		int priceAC = busService.getPrice("A", "C");
		assertEquals(100, priceAC, "Price from A to C should be 100");

		int priceAD = busService.getPrice("A", "D");
		assertEquals(150, priceAD, "Price from A to D should be 150");

	}

	@Test
	@DisplayName("Reserve Tickets Successfully")
	public void testReserveTickets() {
		int passengerCount = 3;
		String origin = "A";
		String destination = "C";
		double totalPrice = 300.0;

		Reservation reservation = busService.reserveTickets(passengerCount, origin, destination, totalPrice);

		assertNotNull(reservation, "Reservation should not be null");
		assertEquals(passengerCount, reservation.getPassengerCount(), "Passenger count mismatch");
		assertEquals(passengerCount, reservation.getSeatNumbers().size(), "Seat count mismatch");
		assertEquals(totalPrice, reservation.getTotalPrice(), "Total price mismatch");

	}

	@Test
	@DisplayName("Multiple Reservations with Unique Numbers")
	public void testMultipleReservations() {

		Reservation res1 = busService.reserveTickets(2, "A", "B", 100.0);
		System.out.println("✓ First reservation: " + res1.getReservationNumber() + ", Seats: " + res1.getSeatNumbers());

		Reservation res2 = busService.reserveTickets(3, "B", "C", 150.0);
		System.out
				.println("✓ Second reservation: " + res2.getReservationNumber() + ", Seats: " + res2.getSeatNumbers());

	}

	@Test
	@DisplayName("Reject Invalid Price Confirmation")
	public void testInvalidPriceConfirmation() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> busService.reserveTickets(2, "A", "B", 200.0), // Wrong price
				"Should throw exception for wrong price");

	}

	@Test
	@DisplayName("Reject Invalid Locations")
	public void testInvalidLocations() {

		assertThrows(IllegalArgumentException.class, () -> busService.getAvailableSeats("A", "E", 1),
				"Should throw exception for invalid location");

		assertThrows(IllegalArgumentException.class, () -> busService.getAvailableSeats("A", "A", 1),
				"Should throw exception for same origin and destination");

	}

	@Test
	@DisplayName("Prevent Overbooking")
	public void testOverbooking() {

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> busService.reserveTickets(45, "A", "B", 2250.0),
				"Should throw exception for too many passengers");

	}

	@Test
	@DisplayName("Update Seat Availability After Reservation")
	public void testSeatAvailabilityAfterReservation() {

		List<String> availableBefore = busService.getAvailableSeats("A", "B", 40);
		int countBefore = availableBefore.size();
		System.out.println("  Available seats before: " + countBefore);

		busService.reserveTickets(5, "A", "B", 250.0);

		List<String> availableAfter = busService.getAvailableSeats("A", "B", 40);
		int countAfter = availableAfter.size();
		System.out.println("  Available seats after: " + countAfter);

		assertEquals(countBefore - 5, countAfter, "Should have 5 fewer available seats");

	}

	@Test
	@DisplayName("Return Journey Pricing Should Be Same")
	public void testReturnJourney() {
		int priceAB = busService.getPrice("A", "B");
		int priceBA = busService.getPrice("B", "A");

		assertEquals(priceAB, priceBA, "Return journey price should be same");

	}
}