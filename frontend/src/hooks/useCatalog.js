import { useEffect, useState } from 'react';
import { getProducts } from '../services/productService';
import { getCategories } from '../services/categoryService';

function adaptProduct(product, categories) {
  const category = categories.find(
      (category) => category.id === product.categoriaId,
  );

  return {
    id: product.id,
    name: product.nome,
    price: product.preco,
    quantity: product.quantidadeEstoque,
    category: category?.nome ?? 'Categoria não encontrada.',
  };
}

export function useCatalog() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const controller = new AbortController();

    async function loadCatalog() {
      try {
        const [productsData, categoriesData] = await Promise.all([
          getProducts(controller.signal),
          getCategories(controller.signal),
        ]);

        const adaptedProducts = productsData.map((product) =>
            adaptProduct(product, categoriesData)
        );

        if (controller.signal.aborted) return;

        setProducts(adaptedProducts);
        setCategories(categoriesData);
      } catch {
        if (!controller.signal.aborted) {
          setError(
            'Não foi possível carregar o catálogo. Verifique a conexão com a API.',
          );
        }
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    }

    loadCatalog();

    return () => controller.abort();
  }, []);

  function addCategory(category) {
    setCategories(current => [...current, category]);
  }

  function addProduct(createdProduct) {
    const product = adaptProduct(createdProduct, categories);

    setProducts((current) => [...current, product]);
  }

  return {
    products,
    categories,
    loading,
    error,
    addCategory,
    addProduct
  };
}
