package pt.ubi.gruposd.loja;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void catalogEndpointsArePublic() throws Exception {
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(3)));

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(4)));
    }

    @Test
    void registerAndLoginReturnJwtToken() throws Exception {
        String email = "cliente" + System.nanoTime() + "@store.test";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                    "name", "Cliente Teste",
                    "email", email,
                    "password", "password123"
                ))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", notNullValue()))
            .andExpect(jsonPath("$.customer.email", is(email)));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                    "email", email,
                    "password", "password123"
                ))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void clientCannotCreateProductButAdminCan() throws Exception {
        String clientToken = registerClientAndToken();

        mockMvc.perform(post("/api/products")
                .header("Authorization", bearer(clientToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(productPayload(1L, "Produto Cliente Bloqueado", 5))))
            .andExpect(status().isForbidden());

        String adminToken = loginAdminAndToken();

        mockMvc.perform(post("/api/products")
                .header("Authorization", bearer(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(productPayload(1L, "Produto Admin " + System.nanoTime(), 5))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.categoryId", is(1)));
    }

    @Test
    void checkoutCreatesSaleInvoiceAndReducesStock() throws Exception {
        String adminToken = loginAdminAndToken();
        int stock = 4;
        long productId = createProduct(adminToken, 1L, "Produto Checkout " + System.nanoTime(), stock);

        String clientToken = registerClientAndToken();

        mockMvc.perform(post("/api/sales/checkout")
                .header("Authorization", bearer(clientToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                    "items", List.of(Map.of("productId", productId, "quantity", 2))
                ))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.total", is(5.50)))
            .andExpect(jsonPath("$.invoice.invoiceNumber", notNullValue()))
            .andExpect(jsonPath("$.items[0].quantity", is(2)));

        mockMvc.perform(get("/api/products/" + productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stock", is(2)));
    }

    @Test
    void checkoutRejectsInsufficientStock() throws Exception {
        String adminToken = loginAdminAndToken();
        long productId = createProduct(adminToken, 1L, "Produto Sem Stock " + System.nanoTime(), 1);
        String clientToken = registerClientAndToken();

        mockMvc.perform(post("/api/sales/checkout")
                .header("Authorization", bearer(clientToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                    "items", List.of(Map.of("productId", productId, "quantity", 2))
                ))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void statsRequireAdminRole() throws Exception {
        String clientToken = registerClientAndToken();
        String adminToken = loginAdminAndToken();

        mockMvc.perform(get("/api/stats/products/top-selling")
                .header("Authorization", bearer(clientToken)))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/stats/products/top-selling")
                .header("Authorization", bearer(adminToken)))
            .andExpect(status().isOk());
    }

    @Test
    void adminCanListAllSalesButClientCannot() throws Exception {
        String adminToken = loginAdminAndToken();
        long productId = createProduct(adminToken, 1L, "Produto Venda Admin " + System.nanoTime(), 3);
        String clientToken = registerClientAndToken();

        mockMvc.perform(post("/api/sales/checkout")
                .header("Authorization", bearer(clientToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                    "items", List.of(Map.of("productId", productId, "quantity", 1))
                ))))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/sales")
                .header("Authorization", bearer(clientToken)))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/sales")
                .header("Authorization", bearer(adminToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$[0].customerName", notNullValue()))
            .andExpect(jsonPath("$[0].invoice.invoiceNumber", notNullValue()));
    }

    private String registerClientAndToken() throws Exception {
        String email = "cliente" + System.nanoTime() + "@store.test";
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                    "name", "Cliente Teste",
                    "email", email,
                    "password", "password123"
                ))))
            .andExpect(status().isOk())
            .andReturn();

        return tokenFrom(result);
    }

    private String loginAdminAndToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                    "email", "admin@store.test",
                    "password", "password"
                ))))
            .andExpect(status().isOk())
            .andReturn();

        return tokenFrom(result);
    }

    private long createProduct(String adminToken, Long categoryId, String name, int stock) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/products")
                .header("Authorization", bearer(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(productPayload(categoryId, name, stock))))
            .andExpect(status().isCreated())
            .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Map<String, Object> productPayload(Long categoryId, String name, int stock) {
        return Map.of(
            "name", name,
            "description", "Produto criado em teste",
            "price", new BigDecimal("2.75"),
            "stock", stock,
            "categoryId", categoryId,
            "active", true
        );
    }

    private String tokenFrom(MvcResult result) throws Exception {
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("token").asText();
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
