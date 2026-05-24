package pt.ubi.gruposd.loja.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import pt.ubi.gruposd.loja.dto.PageResponse;
import pt.ubi.gruposd.loja.dto.ProductRequest;
import pt.ubi.gruposd.loja.dto.ProductResponse;
import pt.ubi.gruposd.loja.exception.NotFoundException;
import pt.ubi.gruposd.loja.model.Category;
import pt.ubi.gruposd.loja.model.Product;
import pt.ubi.gruposd.loja.repository.ProductRepository;

// Testa o ProductService isoladamente com Mockito a simular o repositório e o CategoryService, cobre a listagem paginada com cap de 100 itens por página, a consulta por id, a criação, a atualização e a remoção, e verifica os caminhos de exceção quando o produto não existe.
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    private ProductService productService;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, categoryService);

        category = new Category();
        category.setId(1L);
        category.setName("Calçado");

        product = new Product();
        product.setId(10L);
        product.setName("Sapatilha X");
        product.setDescription("Descrição");
        product.setPrice(new BigDecimal("49.99"));
        product.setStock(20);
        product.setActive(true);
        product.setCategory(category);
    }

    // Confirma que findAll devolve um PageResponse com o conteúdo mapeado para DTO e os metadados de paginação corretos.
    @Test
    void findAll_returnsPagedResponse() {
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageResponse<ProductResponse> result = productService.findAll(null, false, null, 0, 20);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("Sapatilha X");
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.page()).isEqualTo(0);
    }

    // Garante que pedir uma página com size superior a 100 é silenciosamente limitado a 100 itens, protegendo a API contra pedidos abusivos que pudessem sobrecarregar o servidor.
    @Test
    void findAll_capsPageSizeAt100() {
        Page<Product> page = new PageImpl<>(List.of());
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        productService.findAll(null, false, null, 0, 500);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findAll(any(Specification.class), captor.capture());
        assertThat(captor.getValue().getPageSize()).isEqualTo(100);
    }

    // Verifica que findById devolve o produto pelo id mapeado para o DTO incluindo o nome da categoria associada.
    @Test
    void findById_returnsProduct_whenExists() {
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        ProductResponse result = productService.findById(10L);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.name()).isEqualTo("Sapatilha X");
        assertThat(result.categoryName()).isEqualTo("Calçado");
    }

    // Garante que tentar consultar um produto inexistente lança NotFoundException em vez de devolver null.
    @Test
    void findById_throwsNotFoundException_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
            .isInstanceOf(NotFoundException.class);
    }

    // Confirma que criar um produto resolve a categoria, persiste a entidade e devolve a resposta já com o id atribuído pela base de dados.
    @Test
    void create_savesProductAndReturnsResponse() {
        ProductRequest request = new ProductRequest(
            "Novo Produto", "Desc", new BigDecimal("29.99"), 10, 1L, true, null
        );
        when(categoryService.findEntityById(1L)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(99L);
            return p;
        });

        ProductResponse result = productService.create(request);

        assertThat(result.id()).isEqualTo(99L);
        assertThat(result.name()).isEqualTo("Novo Produto");
        assertThat(result.price()).isEqualTo(new BigDecimal("29.99"));
        assertThat(result.categoryId()).isEqualTo(1L);
    }

    // Verifica que update altera nome, preço e stock do produto existente e devolve a resposta com os valores novos.
    @Test
    void update_modifiesExistingProduct() {
        ProductRequest request = new ProductRequest(
            "Sapatilha Y", "Nova desc", new BigDecimal("59.99"), 5, 1L, true, null
        );
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(categoryService.findEntityById(1L)).thenReturn(category);

        ProductResponse result = productService.update(10L, request);

        assertThat(result.name()).isEqualTo("Sapatilha Y");
        assertThat(result.price()).isEqualTo(new BigDecimal("59.99"));
        assertThat(result.stock()).isEqualTo(5);
    }

    // Confirma que delete sobre um produto existente chama efetivamente o repositório para o remover da base de dados.
    @Test
    void delete_callsRepositoryDelete() {
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        productService.delete(10L);

        verify(productRepository).delete(product);
    }

    // Garante que tentar apagar um produto inexistente lança NotFoundException antes de qualquer alteração à base de dados.
    @Test
    void delete_throwsNotFoundException_whenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(999L))
            .isInstanceOf(NotFoundException.class);
    }
}
