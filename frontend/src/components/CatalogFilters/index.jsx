import styles from './styles.module.css';

export function CatalogFilters({ categories, search, setSearch, selectedCategory, setSelectedCategory }) {
  return (
    <div className={styles.catalogFilters}>
      <label className={styles.searchField}>
        <span>Buscar produto</span>
        <input
          type='search'
          placeholder='Digite o nome do produto'
          value={search}
          onChange={(event) => setSearch(event.target.value)}
        />
      </label>

      <label className={styles.categoryField}>
        <span>Categoria</span>
        <select
          value={selectedCategory}
          onChange={(event) => setSelectedCategory(event.target.value)}
        >
          <option value=''>Todas as categorias</option>
          {categories.map((category) => (
            <option key={category.id} value={category.nome}>
            {category.nome}
            </option>
          ))}
        </select>
      </label>
    </div>
  );
}
