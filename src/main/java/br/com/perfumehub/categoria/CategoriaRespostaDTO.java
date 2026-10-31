package br.com.perfumehub.categoria;

public record CategoriaRespostaDTO(
        Long id,
        String nome,
        String descricao
) {

    public static CategoriaRespostaDTO fromEntity(Categoria categoria) {
        return new CategoriaRespostaDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao()
        );
    }
}
