import { DefaultButton } from '../DefaultButton';
import { PurchaseForm } from '../PurchaseForm';
import { useState } from 'react';
import styles from './styles.module.css';

export function PurchaseDialog({ dialogRef, products }) {
    const [formKey, setFormKey] = useState(0);

    return (
        <dialog
            ref={dialogRef}
            className={styles.purchaseDialog}
            aria-labelledby='purchase-dialog-title'
            onClose={() => setFormKey((current) => current + 1)}
        >
            <div className={styles.dialogHeader}>
                <h2 id='purchase-dialog-title'>Registrar compra</h2>

                <DefaultButton onClick={() => dialogRef.current.close()}>
                    Fechar
                </DefaultButton>
            </div>

            <PurchaseForm
                key={formKey}
                products={products}
                onClose={() => dialogRef.current.close()}
            />
        </dialog>
    );
}