import styles from './styles.module.css';

export function Header() {
  return (
    <header className={styles.header}>
      <a className={styles.brand} href='/'>
        PerfumeHub
      </a>

      <nav className={styles.menu} aria-label='Menu principal'>
        <button
          type='button'
          className={`${styles.menuItem} ${styles.active}`}
          aria-current='page'>
          Estoque
        </button>

        <button type='button' className={styles.menuItem}>
          Vendas
        </button>

        <button type='button' className={styles.menuItem}>
          Categorias
        </button>

        <button type='button' className={styles.menuItem}>
          Relatórios
        </button>
      </nav>
    </header>
  );
}
