package pt.ubi.gruposd.loja.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ubi.gruposd.loja.dto.CategoryRequest;
import pt.ubi.gruposd.loja.dto.CategoryResponse;
import pt.ubi.gruposd.loja.exception.ConflictException;
import pt.ubi.gruposd.loja.exception.NotFoundException;
import pt.ubi.gruposd.loja.model.Category;
import pt.ubi.gruposd.loja.repository.CategoryRepository;
import pt.ubi.gruposd.loja.repository.ProductRepository;

// Testa o CategoryService de forma isolada com Mockito a simular os repositórios de categorias e produtos, cobrindo as operações CRUD, a deteção de nomes duplicados ao criar e atualizar e o bloqueio da remoção de categorias que ainda têm produtos associados.
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    private CategoryService categoryService;
    private Category category;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository, productRepository);

        category = new Category();
        category.setId(1L);
        category.setName("Calçado");
        category.setDescription("Todo o calçado desportivo");
    }

    // Confirma que findAll devolve todas as categorias mapeadas para o DTO CategoryResponse, mantendo o nome inalterado.
    @Test
    void findAll_returnsAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryResponse> result = categoryService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Calçado");
    }

    // Verifica que findById devolve corretamente a categoria existente identificada pelo id.
    @Test
    void findById_returnsCategory_whenExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponse result = categoryService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Calçado");
    }

    // Garante que tentar consultar uma categoria inexistente lança NotFoundException em vez de devolver null.
    @Test
    void findById_throwsNotFoundException_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(99L))
            .isInstanceOf(NotFoundException.class);
    }

    // Confirma que criar uma categoria com nome único é gravado e devolve a resposta já com o id atribuído pela base de dados.
    @Test
    void create_savesCategory_whenNameIsUnique() {
        CategoryRequest request = new CategoryRequest("Vestuário", "Roupas desportivas");
        when(categoryRepository.existsByNameIgnoreCase("Vestuário")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            c.setId(5L);
            return c;
        });

        CategoryResponse result = categoryService.create(request);

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.name()).isEqualTo("Vestuário");
        verify(categoryRepository).save(any(Category.class));
    }

    // Garante que criar uma categoria com nome já existente é rejeitado com ConflictException, mantendo a unicidade do campo nome na tabela.
    @Test
    void create_throwsConflict_whenNameAlreadyExists() {
        CategoryRequest request = new CategoryRequest("Calçado", null);
        when(categoryRepository.existsByNameIgnoreCase("Calçado")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(request))
            .isInstanceOf(ConflictException.class);
    }

    // Verifica que tentar renomear uma categoria para um nome já usado por outra é rejeitado com ConflictException.
    @Test
    void update_throwsConflict_whenNewNameConflicts() {
        CategoryRequest request = new CategoryRequest("Vestuário", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCase("Vestuário")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.update(1L, request))
            .isInstanceOf(ConflictException.class);
    }

    // Garante que atualizar uma categoria mantendo o mesmo nome é permitido, evitando que o check de unicidade falsamente bloqueie alterações apenas à descrição.
    @Test
    void update_allowsSameName_forSameCategory() {
        CategoryRequest request = new CategoryRequest("Calçado", "Desc atualizada");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponse result = categoryService.update(1L, request);

        assertThat(result.name()).isEqualTo("Calçado");
        assertThat(result.description()).isEqualTo("Desc atualizada");
    }

    // Confirma que apagar uma categoria sem produtos associados chama efetivamente o delete do repositório.
    @Test
    void delete_removesCategory_whenNoProductsAssociated() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(1L)).thenReturn(false);

        categoryService.delete(1L);

        verify(categoryRepository).delete(category);
    }

    // Verifica que apagar uma categoria que ainda tem produtos é bloqueado com ConflictException, protegendo a integridade referencial da tabela products.
    @Test
    void delete_throwsConflict_whenCategoryHasProducts() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.delete(1L))
            .isInstanceOf(ConflictException.class);
    }
}
