import { useState } from "react";
import { Modal } from '../Modal';
import { ProductForm } from '../ProductForm';

export function ProductDialog({ dialogRef, categories, onCategoryCreated, onProductCreated }) {

  const [formKey, setFormKey] = useState(0);

  function handleProductCreated(createdProduct) {
    onProductCreated(createdProduct);
    dialogRef.current.close();
  }

  return (
      <Modal
          dialogRef={dialogRef}
          title='Novo produto'
          titleId='new-product-title'
          onClose={() => setFormKey((current) => current + 1)}
      >
        <ProductForm
            key={formKey}
            categories={categories}
            onCategoryCreated={onCategoryCreated}
            onProductCreated={handleProductCreated}
        />
      </Modal>
  );
}
