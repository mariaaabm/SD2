package pt.ubi.gruposd.loja;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.HashMap;
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

// Testes de integração ponta-a-ponta que sobem o ApplicationContext completo do Spring Boot com perfil test e H2 in-memory, e exercitam a API real através do MockMvc cobrindo catálogo público, paginação, registo e login, autorização por role, checkout com criação de fatura e abate de stock, e regras de acesso a endpoints administrativos.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Garante que os endpoints públicos do catálogo, /api/categories e /api/products, respondem com 200 OK e devolvem os dados do seed sem precisar de autenticação.
    @Test
    void catalogEndpointsArePublic() throws Exception {
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(3)));

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", notNullValue()))
            .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(4)))
            .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)));
    }

    // Verifica que os parâmetros page e size do endpoint de produtos são respeitados e que a resposta inclui os metadados de paginação esperados.
    @Test
    void productsPaginationWorks() throws Exception {
        mockMvc.perform(get("/api/products?page=0&size=5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()", greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.size", is(5)))
            .andExpect(jsonPath("$.page", is(0)))
            .andExpect(jsonPath("$.first", is(true)));
    }

    // Confirma o fluxo completo de registo seguido de login, garantindo que ambos os endpoints devolvem um JWT válido e os dados do cliente associado.
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

    // Valida a regra de segurança que restringe a criação de produtos a administradores, confirmando que um cliente comum recebe 403 Forbidden e que um admin consegue criar normalmente.
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

    // Testa o fluxo principal de checkout de ponta a ponta, criando um produto novo via admin, fazendo checkout como cliente, e confirmando que a fatura é emitida, os dados de envio e pagamento são guardados e o stock é reduzido na quantidade comprada.
    @Test
    void checkoutCreatesSaleInvoiceAndReducesStock() throws Exception {
        String adminToken = loginAdminAndToken();
        int stock = 4;
        long productId = createProduct(adminToken, 1L, "Produto Checkout " + System.nanoTime(), stock);

        String clientToken = registerClientAndToken();

        mockMvc.perform(post("/api/sales/checkout")
                .header("Authorization", bearer(clientToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(checkoutPayload(productId, 2))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.invoice.invoiceNumber", notNullValue()))
            .andExpect(jsonPath("$.items[0].quantity", is(2)))
            .andExpect(jsonPath("$.shippingCity", is("Lisboa")))
            .andExpect(jsonPath("$.paymentMethod", is("CARD")));

        mockMvc.perform(get("/api/products/" + productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stock", is(2)));
    }

    // Garante que a API recusa um checkout que peça mais unidades do que existem em stock devolvendo 400 Bad Request, validando a regra também ao nível HTTP e não apenas no service.
    @Test
    void checkoutRejectsInsufficientStock() throws Exception {
        String adminToken = loginAdminAndToken();
        long productId = createProduct(adminToken, 1L, "Produto Sem Stock " + System.nanoTime(), 1);
        String clientToken = registerClientAndToken();

        mockMvc.perform(post("/api/sales/checkout")
                .header("Authorization", bearer(clientToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(checkoutPayload(productId, 2))))
            .andExpect(status().isBadRequest());
    }

    // Confirma que os endpoints de estatísticas estão protegidos por role ADMIN, devolvendo 403 a clientes comuns e 200 a administradores.
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

    // Garante que apenas administradores conseguem listar todas as vendas via /api/admin/sales, enquanto clientes recebem 403 mesmo que tenham feito uma compra que conste na lista.
    @Test
    void adminCanListAllSalesButClientCannot() throws Exception {
        String adminToken = loginAdminAndToken();
        long productId = createProduct(adminToken, 1L, "Produto Venda Admin " + System.nanoTime(), 3);
        String clientToken = registerClientAndToken();

        mockMvc.perform(post("/api/sales/checkout")
                .header("Authorization", bearer(clientToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(checkoutPayload(productId, 1))))
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

    // ── helpers ──────────────────────────────────────────────────────

    private Map<String, Object> checkoutPayload(long productId, int quantity) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("items", List.of(Map.of("productId", productId, "quantity", quantity)));
        payload.put("shippingName", "Cliente Teste");
        payload.put("shippingPhone", "910000000");
        payload.put("shippingAddress", "Rua Teste, 123");
        payload.put("shippingAddress2", "");
        payload.put("shippingPostalCode", "1000-001");
        payload.put("shippingCity", "Lisboa");
        payload.put("shippingRegion", "Lisboa");
        payload.put("shippingCountry", "Portugal");
        payload.put("paymentMethod", "CARD");
        return payload;
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
