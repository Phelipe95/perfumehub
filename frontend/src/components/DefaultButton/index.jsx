import styles from './styles.module.css';

export function DefaultButton({ children, variant = 'secondary', type = 'button', ...props }) {
  return (
    <button type={type} className={styles[variant]} {...props}>
      {children}
    </button>
  );
}
