import { useRef, useState } from 'react';
import { MainTemplate } from '../../templates/MainTemplate';
import { CatalogFilters } from '../../components/CatalogFilters';
import { DefaultButton } from '../../components/DefaultButton';
import { ProductCard } from '../../components/ProductCard';
import { ProductDialog } from '../../components/ProductDialog';
import { useCatalog } from '../../hooks/useCatalog';
import styles from './styles.module.css';

export function Inventory() {
  const { products, categories, loading, error, addCategory } = useCatalog();
  const [search, setSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const productDialog = useRef(null);

  const filteredProducts = products.filter(product => {
    const matchesSearch = product.name.toLocaleLowerCase('pt-BR')
      .includes(search.trim().toLocaleLowerCase('pt-BR'));
    const matchesCategory = selectedCategory === '' ||
      product.category === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <MainTemplate>
      <section aria-labelledby='inventory-title'>
        <ProductDialog
            dialogRef={productDialog}
            categories={categories}
            onCategoryCreated={addCategory}
        />
        <div className={styles.catalogHeader}>
          <div>
            <h1 id='inventory-title'>Meu estoque</h1>
            <p>Acompanhe os produtos da sua perfumaria.</p>
          </div>
          <div className={styles.catalogActions}>
            <DefaultButton variant='primary' onClick={() => productDialog.current.showModal()}>
              Novo produto
            </DefaultButton>
            <DefaultButton>Registrar compra</DefaultButton>
            <DefaultButton>Registrar venda</DefaultButton>
          </div>
        </div>

        <CatalogFilters
          categories={categories}
          search={search}
          setSearch={setSearch}
          selectedCategory={selectedCategory}
          setSelectedCategory={setSelectedCategory}
        />

        {loading ? (
          <p className={styles.emptyMessage} role='status'>Carregando catálogo...</p>
        ) : error ? (
          <p className={styles.emptyMessage} role='alert'>{error}</p>
        ) : filteredProducts.length === 0 ? (
          <p className={styles.emptyMessage} role='status'>
            Nenhum produto encontrado para os filtros selecionados.
          </p>
        ) : (
          <div className={styles.productGrid}>
            {filteredProducts.map(product => (
              <ProductCard
                key={product.id}
                category={product.category}
                name={product.name}
                price={product.price}
                quantity={product.quantity}
              />
            ))}
          </div>
        )}
      </section>
    </MainTemplate>
  );
}
