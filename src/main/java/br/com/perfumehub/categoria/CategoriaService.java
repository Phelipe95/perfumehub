package br.com.perfumehub.categoria;

import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria cadastrar(CategoriaCadastroDTO dto) {
        Categoria categoria = new Categoria(
                dto.nome(),
                dto.descricao()
        );

        return categoriaRepository.save(categoria);
    }
}
