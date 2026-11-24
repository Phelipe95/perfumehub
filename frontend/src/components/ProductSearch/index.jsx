import styles from './styles.module.css';

export function ProductSearch({
                                  id,
                                  products,
                                  search,
                                  selectedProduct,
                                  onSearchChange,
                                  onSelect,
                              }) {
    const normalizedSearch = search.trim().toLocaleLowerCase('pt-BR');

    const suggestedProducts = normalizedSearch === '' || selectedProduct
        ? []
        : products.filter((product) =>
            product.name.toLocaleLowerCase('pt-BR').includes(normalizedSearch)
        );

    return (
        <div className={styles.productSearch}>
            <label htmlFor={id}>Buscar produto</label>

            <input
                id={id}
                type='search'
                autoComplete='off'
                placeholder='Digite o nome do produto'
                value={search}
                onChange={(event) => onSearchChange(event.target.value)}
            />

            {suggestedProducts.length > 0 && (
                <ul className={styles.suggestions}>
                    {suggestedProducts.map((product) => (
                        <li key={product.id}>
                            <button
                                type='button'
                                onClick={() => onSelect(product)}
                            >
                                {product.name}
                            </button>
                        </li>
                    ))}
                </ul>
            )}

            {normalizedSearch !== '' &&
                !selectedProduct &&
                suggestedProducts.length === 0 && (
                    <p role='status'>Nenhum produto encontrado.</p>
                )}
        </div>
    );
}