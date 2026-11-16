import { DefaultButton } from '../DefaultButton';
import styles from './styles.module.css';

export function ProductCard({ category, name, price, quantity }) {
  return (
    <article className={styles.productCard}>
      <div className={styles.productVisual}>
        <span className={styles.productCategory}>{category}</span>
      </div>

      <div className={styles.productDetails}>
        <h2>{name}</h2>

        <div className={styles.productSummary}>
          <span className={styles.productPrice}>
            {price.toLocaleString('pt-BR', {
              style: 'currency',
              currency: 'BRL',
            })}
          </span>
          <span>{quantity}un</span>
        </div>

        <div className={styles.productActions}>
          <DefaultButton type='button' variant='secondary'>
            Editar
          </DefaultButton>

          <DefaultButton type='button' variant='danger'>
            Excluir
          </DefaultButton>
        </div>
      </div>
    </article>
  );
}
