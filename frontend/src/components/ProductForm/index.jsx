import { useState } from 'react';
import { CategoryRegistration } from '../CategoryRegistration';
import { DefaultButton } from '../DefaultButton';
import styles from './styles.module.css';

export function ProductForm({ categories, onCategoryCreated, onProductCreated }) {
  const [registerInitialPurchase, setRegisterInitialPurchase] = useState(false);
  const [initialQuantity, setInitialQuantity] = useState(1);
  const [savingProduct, setSavingProduct] = useState(false);
  const [productError, setProductError] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();

    const formData = new FormData(event.currentTarget);

    const productData = {
      nome: formData.get('nome').trim(),
      preco: Number(formData.get('preco')),
      quantidadeEstoque: 0,
      categoriaId: Number(formData.get('categoriaId')),
    };

    setSavingProduct(true);
    setProductError('');

    try {
      const response = await fetch('api/produtos', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(productData),
      });

      if (!response.ok) {
        const failure = await response.json().catch(() => null);

        throw new Error(
            failure?.erros?.join(' ') || 'Não foi possível cadastrar o produto.',
        );
      }

      const createdProduct = await response.json();

      onProductCreated(createdProduct);

    } catch (failure) {
      setProductError(
          failure instanceof TypeError
          ? 'Não foi possível conectar ao servidor.'
              : failure.message,
      );
    } finally {
      setSavingProduct(false);
    }
  }

  return (
    <form
      className={styles.productForm}
      onSubmit={handleSubmit}
    >
      <label>
        <span>Nome do produto</span>
        <input
          name='nome'
          type='text'
          maxLength={150}
          required
        />
      </label>

      <label>
        <span>Preço de venda (R$)</span>
        <input
          name='preco'
          type='number'
          min='0'
          step='0.01'
          required
        />
      </label>

      <CategoryRegistration categories={categories} onCategoryCreated={onCategoryCreated} />

      <label className={styles.purchaseOption}>
        <input
          type='checkbox'
          checked={registerInitialPurchase}
          onChange={(event) =>
            setRegisterInitialPurchase(event.target.checked)
          }
        />
        <span>Registrar compra inicial</span>
      </label>

      {registerInitialPurchase && (
        <fieldset className={styles.initialPurchase}>
          <legend>Compra inicial</legend>

          <label>
            <span>Data da compra</span>
            <input name='dataCompra' type='date' required/>
          </label>

          <label>
            <span>Custo de compra por unidade (R$)</span>
            <input
              name='custoUnitario'
              type='number'
              min='0'
              step='0.01'
              required
            />
          </label>

          <div className={styles.quantityField}>
            <label htmlFor='initial-quantity'>Quantidade comprada</label>

            <div className={styles.quantityControl}>
              <button
                type='button'
                aria-label='Diminuir quantidade'
                disabled={initialQuantity === '' || initialQuantity <= 1}
                onClick={() =>
                  setInitialQuantity((current) => Math.max(1, current - 1))
                }
              >
                -
              </button>

              <input
                id='initial-quantity'
                name='quantidade'
                type='number'
                min='1'
                max='2147483647'
                step='1'
                required
                value={initialQuantity}
                onChange={(event) => {
                  const value = event.target.value;
                  setInitialQuantity(value === '' ? '' : Number(value));
                }}
              />

              <button
                type='button'
                aria-label='Aumentar quantidade'
                disabled={
                  initialQuantity !== '' &&
                  initialQuantity >= 2147483647
                }
                onClick={() =>
                  setInitialQuantity((current) =>
                    current === '' ? 1 : Math.min(2147483647, current + 1)
                  )
                }
              >
                +
              </button>
            </div>
          </div>
        </fieldset>
      )}

      {productError && (
          <p role='alert'>{productError}</p>
      )}

      <DefaultButton
          type='submit'
          variant='primary'
          disabled={savingProduct}
      >
        {savingProduct ? 'Salvando...' : 'Cadastrar produto'}
      </DefaultButton>
    </form>
  );
}
