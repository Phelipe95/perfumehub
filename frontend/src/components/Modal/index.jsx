import { DefaultButton } from '../DefaultButton';
import styles from './styles.module.css';

export function Modal({
                          dialogRef,
                          title,
                          titleId,
                          onClose,
                          children,
                      }) {
    return (
        <dialog
            ref={dialogRef}
            className={styles.modal}
            aria-labelledby={titleId}
            onClose={onClose}
        >
            <div className={styles.header}>
                <h2 id={titleId}>{title}</h2>

                <DefaultButton
                    onClick={() => dialogRef.current.close()}
                >
                    Fechar
                </DefaultButton>
            </div>

            {children}
        </dialog>
    );
}