import { DefaultButton } from '../DefaultButton';
import styles from './styles.module.css';

export function Modal({
                          dialogRef,
                          title,
                          titleId,
                          onClose,
                          children,
                          isSaving = false
                      }) {
    return (
        <dialog
            ref={dialogRef}
            className={styles.modal}
            aria-labelledby={titleId}
            onClose={onClose}
            aria-busy={isSaving}
            onCancel={(event) => {
                if (isSaving) {
                    event.preventDefault();
                }
            }}
        >
            <div className={styles.header}>
                <h2 id={titleId}>{title}</h2>

                <DefaultButton
                    onClick={() => dialogRef.current.close()} disabled={isSaving}
                >
                    Fechar
                </DefaultButton>
            </div>

            {children}
        </dialog>
    );
}