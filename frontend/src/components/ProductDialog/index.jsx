import { useState } from "react";
import { Modal } from '../Modal';
import { ProductForm } from '../ProductForm';

export function ProductDialog({ dialogRef, categories, onCategoryCreated, onProductCreated }) {

  const [formKey, setFormKey] = useState(0);
  const [savingProduct, setSavingProduct] = useState(false);
  const [savingCategory, setSavingCategory] = useState(false);

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
          isSaving={savingProduct || savingCategory}
      >
        <ProductForm
            key={formKey}
            categories={categories}
            onCategoryCreated={onCategoryCreated}
            onProductCreated={handleProductCreated}
            savingProduct={savingProduct}
            setSavingProduct={setSavingProduct}
            savingCategory={savingCategory}
            setSavingCategory={setSavingCategory}
        />
      </Modal>
  );
}
