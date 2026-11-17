import styles from './styles.module.css';

export function ProductCardSkeleton() {
    return (
        <article className={styles.card} aria-hidden='true'>
            <div className={styles.visual} />

            <div className={styles.content}>
                <div className={styles.title} />

                <div className={styles.summary}>
                    <div className={styles.price} />
                    <div className={styles.quantity} />
                </div>

                <div className={styles.actions}>
                    <div className={styles.button} />
                    <div className={styles.button} />
                </div>
            </div>
        </article>
    );
}