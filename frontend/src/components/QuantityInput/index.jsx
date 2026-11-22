import styles from './styles.module.css';

export function QuantityInput({ id, name, value, onChange }) {
    return (
        <div className={styles.quantityControl}>
            <button
                type='button'
                aria-label='Diminuir quantidade'
                disabled={value === '' || value <= 1}
                onClick={() => onChange(Math.max(1, value - 1))}
            >
                −
            </button>

            <input
                id={id}
                name={name}
                type='number'
                min='1'
                max='2147483647'
                step='1'
                required
                value={value}
                onChange={(event) => {
                    const inputValue = event.target.value;
                    onChange(inputValue === '' ? '' : Number(inputValue));
                }}
            />

            <button
                type='button'
                aria-label='Aumentar quantidade'
                disabled={value !== '' && value >= 2147483647}
                onClick={() =>
                    onChange(
                        value === ''
                            ? 1
                            : Math.min(2147483647, value + 1)
                    )
                }
            >
                +
            </button>
        </div>
    );
}