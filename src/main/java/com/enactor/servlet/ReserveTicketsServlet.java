package com.enactor.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enactor.model.Reservation;
import com.enactor.service.BusService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/seats/reserve")
public class ReserveTicketsServlet extends HttpServlet {

	private BusService busService;

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void init() throws ServletException {
		busService = BusService.getInstance();
		mapper = new ObjectMapper();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		try {

			Map<String, Object> requestData = mapper.readValue(request.getReader(), Map.class);

			Object passengerCountObj = requestData.get("passengerCount");
			String origin = (String) requestData.get("origin");
			String destination = (String) requestData.get("destination");
			Object totalPriceObj = requestData.get("totalPrice");

			if (passengerCountObj == null || origin == null || destination == null || totalPriceObj == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				Map<String, Object> error = new HashMap<>();
				error.put("error", "Missing required parameters: passengerCount, origin, destination, totalPrice");
				out.print(mapper.writeValueAsString(error));
				return;
			}

			int passengerCount = passengerCountObj instanceof Integer ? (Integer) passengerCountObj
					: Integer.parseInt(passengerCountObj.toString());

			double totalPrice = totalPriceObj instanceof Double ? (Double) totalPriceObj
					: Double.parseDouble(totalPriceObj.toString());

			Reservation reservation = busService.reserveTickets(passengerCount, origin, destination, totalPrice);

			Map<String, Object> result = new LinkedHashMap<>();
			result.put("success", true);
			result.put("reservationNumber", reservation.getReservationNumber());
			result.put("origin", reservation.getOrigin().toUpperCase());
			result.put("destination", reservation.getDestination().toUpperCase());
			result.put("seatNumbers", reservation.getSeatNumbers());
			result.put("totalPrice", reservation.getTotalPrice());
			

			response.setStatus(HttpServletResponse.SC_CREATED);
			out.print(mapper.writeValueAsString(result));

		} catch (IllegalArgumentException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			Map<String, Object> error = new HashMap<>();
			error.put("success", false);
			error.put("error", e.getMessage());
			out.print(mapper.writeValueAsString(error));
		} catch (IllegalStateException e) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			Map<String, Object> error = new HashMap<>();
			error.put("success", false);
			error.put("error", e.getMessage());
			out.print(mapper.writeValueAsString(error));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			Map<String, Object> error = new HashMap<>();
			error.put("success", false);
			error.put("error", "Internal server error: " + e.getMessage());
			out.print(mapper.writeValueAsString(error));
		} finally {
			out.flush();
		}
	}

}
