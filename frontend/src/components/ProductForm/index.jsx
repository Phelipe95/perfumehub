import { useState } from 'react';
import { createProduct } from '../../services/productService';
import { CategoryRegistration } from '../CategoryRegistration';
import { DefaultButton } from '../DefaultButton';
import { QuantityInput } from '../QuantityInput';
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
      compraInicial: registerInitialPurchase
        ? {
            dataCompra: formData.get('dataCompra'),
            quantidade: Number(formData.get('quantidade')),
            custoUnitario: Number(formData.get('custoUnitario')),
          }
        : null,
    };

    setSavingProduct(true);
    setProductError('');

    try {
      const createdProduct = await createProduct(productData);
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

            <QuantityInput
                id='initial-quantity'
                name='quantidade'
                value={initialQuantity}
                onChange={setInitialQuantity}
            />

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
