import { useState } from "react";
import { DefaultButton } from '../DefaultButton';
import { ProductForm } from '../ProductForm';
import styles from './styles.module.css';

export function ProductDialog({ dialogRef, categories, onCategoryCreated, onProductCreated }) {

  const [formKey, setFormKey] = useState(0);

  function handleProductCreated(createdProduct) {
    onProductCreated(createdProduct);
    dialogRef.current.close();
  }

  return (
    <dialog
      ref={dialogRef}
      className={styles.productDialog}
      aria-labelledby='new-product-title'
      onClose={() => setFormKey((current) => current + 1)}
    >
      <div className={styles.dialogHeader}>
        <h2 id='new-product-title'>Novo produto</h2>
        <DefaultButton onClick={() => dialogRef.current.close()}>
          Fechar
        </DefaultButton>
      </div>
      <ProductForm categories={categories} onCategoryCreated={onCategoryCreated} onProductCreated={handleProductCreated} key={formKey} />
    </dialog>
  );
}
