import { useState } from 'react';
import { CategoryRegistration } from '../CategoryRegistration';
import { DefaultButton } from '../DefaultButton';
import styles from './styles.module.css';

export function ProductForm({ categories, onCategoryCreated }) {
  const [registerInitialPurchase, setRegisterInitialPurchase] = useState(false);
  const [initialQuantity, setInitialQuantity] = useState(1);

  return (
    <form
      className={styles.productForm}
      onSubmit={(event) => event.preventDefault()}
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

      <DefaultButton type='submit' variant='primary'>
        Cadastrar produto
      </DefaultButton>
    </form>
  );
}
