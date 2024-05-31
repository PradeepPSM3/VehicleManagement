package com.example.demo;

import com.example.demo.controller.VehicleController;
import com.example.demo.domain.Vehicle;
import com.example.demo.services.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DemoApplicationTests {


	private MockMvc mockMvc;

	@Mock
	private VehicleService vehicleService;

	@InjectMocks
	private VehicleController vehicleController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
	}

	@Test
	void testGetAllVehicles() throws Exception {
		Vehicle vehicle1 = new Vehicle(1L, "Toyota", "Camry", 2022, "VIN123");
		Vehicle vehicle2 = new Vehicle(2L, "Honda", "Accord", 2023, "VIN456");
		when(vehicleService.getAllVehicles()).thenReturn(Arrays.asList(vehicle1, vehicle2));

		mockMvc.perform(get("/api/vehicles"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].make").value("Toyota"))
				.andExpect(jsonPath("$[1].make").value("Honda"));
	}

	@Test
	void testGetVehicleById() throws Exception {
		Vehicle vehicle = new Vehicle(1L, "Toyota", "Camry", 2022, "VIN123");
		when(vehicleService.getVehicleById(1L)).thenReturn(Optional.of(vehicle));

		mockMvc.perform(get("/api/vehicles/{id}", 1L))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.make").value("Toyota"))
				.andExpect(jsonPath("$.model").value("Camry"));
	}

	@Test
	void testAddVehicle() throws Exception {
		Vehicle vehicle = new Vehicle(1L, "Toyota", "Camry", 2022, "VIN123");
		when(vehicleService.addVehicle(any(Vehicle.class))).thenReturn(vehicle);

		mockMvc.perform(post("/api/vehicles")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"make\":\"Toyota\",\"model\":\"Camry\",\"year\":2022,\"vin\":\"VIN123\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.make").value("Toyota"))
				.andExpect(jsonPath("$.model").value("Camry"));
	}

	@Test
	void testUpdateVehicle() throws Exception {
		Vehicle updatedVehicle = new Vehicle(1L, "Toyota", "Corolla", 2022, "VIN123");
		when(vehicleService.updateVehicle(eq(1L), any(Vehicle.class))).thenReturn(updatedVehicle);

		mockMvc.perform(put("/api/vehicles/{id}", 1L)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"make\":\"Toyota\",\"model\":\"Corolla\",\"year\":2022,\"vin\":\"VIN123\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.make").value("Toyota"))
				.andExpect(jsonPath("$.model").value("Corolla"));
	}

	@Test
	void testDeleteVehicle() throws Exception {
		Vehicle vehicle = new Vehicle(1L, "Toyota", "Camry", 2022, "VIN123");
		when(vehicleService.getVehicleById(1L)).thenReturn(Optional.of(vehicle));
		doNothing().when(vehicleService).deleteVehicle(1L);

		mockMvc.perform(delete("/api/vehicles/{id}", 1L))
				.andExpect(status().isNoContent());
	}

}
