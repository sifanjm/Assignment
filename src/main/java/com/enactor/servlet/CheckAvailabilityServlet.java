package com.enactor.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enactor.service.BusService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/seats/availability")
public class CheckAvailabilityServlet extends HttpServlet {

	private BusService busService;

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public void init() throws ServletException {
		busService = BusService.getInstance();
		mapper = new ObjectMapper();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		try {

			String passengerCountStr = request.getParameter("passengerCount");
			String origin = request.getParameter("origin");
			String destination = request.getParameter("destination");

			if (passengerCountStr == null || origin == null || destination == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				Map<String, Object> error = new HashMap<>();
				error.put("error", "Missing required parameters: passengerCount, origin, destination");
				out.print(mapper.writeValueAsString(error));
				return;
			}

			int passengerCount;
			try {
				passengerCount = Integer.parseInt(passengerCountStr);
			} catch (NumberFormatException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				Map<String, Object> error = new HashMap<>();
				error.put("error", "Invalid passenger count format");
				out.print(mapper.writeValueAsString(error));
				return;
			}

			List<String> availableSeats = busService.getAvailableSeats(origin, destination, passengerCount);

			int pricePerTicket = busService.getPrice(origin, destination);
			double totalPrice = pricePerTicket * passengerCount;

			Map<String, Object> result = new LinkedHashMap<>();

			result.put("availableSeats",
					availableSeats.size() >= passengerCount ? availableSeats.subList(0, passengerCount)
							: availableSeats);
			result.put("totalPrice", totalPrice);

			response.setStatus(HttpServletResponse.SC_OK);
			out.print(mapper.writeValueAsString(result));

		} catch (IllegalArgumentException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			Map<String, Object> error = new HashMap<>();
			error.put("error", e.getMessage());
			out.print(mapper.writeValueAsString(error));
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Internal server error: " + e.getMessage());
			out.print(mapper.writeValueAsString(error));
		} finally {
			out.flush();
		}
	}

}
