import { DefaultButton } from '../DefaultButton';
import { ProductForm } from '../ProductForm';
import styles from './styles.module.css';

export function ProductDialog({ dialogRef, categories, onCategoryCreated }) {
  return (
    <dialog
      ref={dialogRef}
      className={styles.productDialog}
      aria-labelledby='new-product-title'
    >
      <div className={styles.dialogHeader}>
        <h2 id='new-product-title'>Novo produto</h2>
        <DefaultButton onClick={() => dialogRef.current.close()}>
          Fechar
        </DefaultButton>
      </div>
      <ProductForm categories={categories} onCategoryCreated={onCategoryCreated} />
    </dialog>
  );
}
