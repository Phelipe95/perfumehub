import { Modal } from '../Modal';
import { PurchaseForm } from '../PurchaseForm';
import { useState } from 'react';

export function PurchaseDialog({ dialogRef, products }) {
    const [formKey, setFormKey] = useState(0);

    return (
        <Modal
            dialogRef={dialogRef}
            title='Registrar compra'
            titleId='purchase-dialog-title'
            onClose={() => setFormKey((current) => current + 1)}
        >
            <PurchaseForm
                key={formKey}
                products={products}
                onClose={() => dialogRef.current.close()}
            />
        </Modal>
    );
}