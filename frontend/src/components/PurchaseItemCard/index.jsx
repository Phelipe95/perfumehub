import styles from './styles.module.css';
import { formatCurrency } from '../../utils/formatCurrency';

export function PurchaseItemCard({ item, onRemove }) {
    return (
        <article className={styles.card}>
            <button
                type='button'
                className={styles.removeButton}
                aria-label={`Remover ${item.name} da compra`}
                onClick={() => onRemove(item.id)}
            >
                ×
            </button>

            <h3>{item.name}</h3>

            <div className={styles.details}>
                <span>{item.quantity} un.</span>
                <span>{formatCurrency(item.unitCost)} por unidade</span>
                <strong>
                    Subtotal: {formatCurrency(item.unitCost * item.quantity)}
                </strong>
            </div>
        </article>
    );
}