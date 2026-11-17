import { useEffect, useState } from 'react';

export function useCatalog() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const controller = new AbortController();

    async function loadCatalog() {
      try {
        const [productsResponse, categoriesResponse] = await Promise.all([
          fetch('/api/produtos', { signal: controller.signal }),
          fetch('/api/categorias', { signal: controller.signal }),
        ]);

        if (!productsResponse.ok || !categoriesResponse.ok) {
          throw new Error('Não foi possível carregar o catálogo.');
        }

        const productsData = await productsResponse.json();
        const categoriesData = await categoriesResponse.json();

        const adaptedProducts = productsData.map(product => {
          const category = categoriesData.find(
            item => item.id === product.categoriaId,
          );

          return {
            id: product.id,
            name: product.nome,
            price: product.preco,
            quantity: product.quantidadeEstoque,
            category: category?.nome ?? 'Categoria não encontrada.',
          };
        });

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
    setCategories(current => [...current, category])
  }

  function addProduct(createdProduct) {
    const category = categories.find(
        (category) => category.id === createdProduct.categoriaId,
    );

    const product = {
      id: createdProduct.id,
      name: createdProduct.nome,
      price: createdProduct.preco,
      quantity: createdProduct.quantidadeEstoque,
      category: category?.nome ?? 'Categoria não encontrada',
    };

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
