import { useEffect, useRef, useState } from 'react';
import { MainTemplate } from '../../templates/MainTemplate';
import { CatalogFilters } from '../../components/CatalogFilters';
import { DefaultButton } from '../../components/DefaultButton';
import { ProductCard } from '../../components/ProductCard';
import { ProductCardSkeleton } from "../../components/ProductCardSkeleton/index.jsx";
import { ProductDialog } from '../../components/ProductDialog';
import { PurchaseDialog } from '../../components/PurchaseDialog';
import { useCatalog } from '../../hooks/useCatalog';
import styles from './styles.module.css';

export function Inventory() {
  const {
    products,
    categories,
    loading,
    error,
    addCategory,
    addProduct
  } = useCatalog();
  const [search, setSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const productDialog = useRef(null);
  const purchaseDialog = useRef(null);

  const filteredProducts = products.filter(product => {
    const matchesSearch = product.name.toLocaleLowerCase('pt-BR')
      .includes(search.trim().toLocaleLowerCase('pt-BR'));
    const matchesCategory = selectedCategory === '' ||
      product.category === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  useEffect(() => {
    document.title = 'PerfumeHub | Meu estoque';
  }, []);

  return (
    <MainTemplate>
      <section aria-labelledby='inventory-title'>
        <ProductDialog
            dialogRef={productDialog}
            categories={categories}
            onCategoryCreated={addCategory}
            onProductCreated={addProduct}
        />
        <PurchaseDialog dialogRef={purchaseDialog} products={products} />
        <div className={styles.catalogHeader}>
          <div>
            <h1 id='inventory-title'>Meu estoque</h1>
            <p>Acompanhe os produtos da sua perfumaria.</p>
          </div>
          <div className={styles.catalogActions}>
            <DefaultButton variant='primary' onClick={() => productDialog.current.showModal()}>
              Novo produto
            </DefaultButton>
            <DefaultButton onClick={() => purchaseDialog.current.showModal()}>
              Registrar compra
            </DefaultButton>
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
          <div role='status' aria-label='Carregando catálogo'>
            <div className={styles.productGrid}>
              {Array.from({ length: 4 }, (_, index) => (
                  <ProductCardSkeleton key={index} />
              ))}
            </div>
          </div>
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
