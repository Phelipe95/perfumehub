import { Header } from '../../components/Header';
import styles from './styles.module.css';

export function MainTemplate({ children }) {
  return (
    <div className={styles.app}>
      <Header />
      <main className={styles.content}>{children}</main>
    </div>
  );
}
