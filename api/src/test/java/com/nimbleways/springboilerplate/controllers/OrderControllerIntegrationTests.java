package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.contollers.OrderController;
import com.nimbleways.springboilerplate.services.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



// Specify the controller class you want to test
// This indicates to spring boot to only load UsersController into the context
// Which allows a better performance and needs to do less mocks
@WebMvcTest(OrderController.class)
public class OrderControllerIntegrationTests {
        @Autowired
        MockMvc mockMvc;
       @MockBean
      OrderService orderService;

        @Test
        public void processOrderShouldReturn() throws Exception {
              Long orderId =1L;
            Mockito.when(orderService.processOrder(1L)).thenReturn(orderId);

                mockMvc.perform(post("/orders/{orderId}/processOrder", 1L)
                                .contentType("application/json"))
                                .andExpect(status().isOk());

        }
}
