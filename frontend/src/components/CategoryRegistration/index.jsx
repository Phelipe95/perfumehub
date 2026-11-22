import { useState } from 'react';
import { createCategory } from '../../services/categoryService';
import { DefaultButton } from '../DefaultButton';
import styles from './styles.module.css';

export function CategoryRegistration({ categories, onCategoryCreated }) {
  const [showNewCategory, setShowNewCategory] = useState(false);
  const [categoryName, setCategoryName] = useState('');
  const [savingCategory, setSavingCategory] = useState(false);
  const [categoryError, setCategoryError] = useState('');
  const [selectedCategoryId, setSelectedCategoryId] = useState('');

  async function handleSaveCategory() {
      const name = categoryName.trim();

      if (!name) {
          setCategoryError('Informe o nome da categoria.');
          return;
      }

      setSavingCategory(true);
      setCategoryError('');

      try {
          const createdCategory = await createCategory({ nome: name });

          onCategoryCreated(createdCategory);
          setSelectedCategoryId(String(createdCategory.id));

          setCategoryName('');
          setShowNewCategory(false);
      } catch (failure) {
          console.error('Erro ao cadastrar a categoria:', failure);

          setCategoryError(
              failure instanceof TypeError
              ? 'Não foi possível salvar a categoria.'
              : failure.message,
          );
      } finally {
          setSavingCategory(false);
      }
  }

  return (
    <div className={styles.categoryRegistration}>
      <label htmlFor='product-category'>Categoria</label>

      <div className={styles.categorySelection}>
        <select
          id='product-category'
          name='categoriaId'
          value={selectedCategoryId}
          onChange={event => setSelectedCategoryId(event.target.value)}
          required
        >
          <option value='' disabled>Selecione uma categoria</option>

          {categories.map((category) => (
            <option key={category.id} value={category.id}>
              {category.nome}
            </option>
          ))}
        </select>

        <DefaultButton
          type='button'
          variant='secondary'
          aria-expanded={showNewCategory}
          aria-controls='new-category-registration'
          onClick={() => setShowNewCategory((current) => !current)}
          disabled={savingCategory}
        >
          {showNewCategory ? 'Cancelar' : 'Criar categoria'}
        </DefaultButton>
      </div>

      {showNewCategory && (
        <div id='new-category-registration' className={styles.newCategory}>
          <label htmlFor='new-category-name'>Nome da nova categoria</label>

            <div className={styles.categoryNameRow}>
                <input
                    id='new-category-name'
                    name='newCategory'
                    type='text'
                    maxLength={100}
                    placeholder='Ex.: Feminino'
                    value={categoryName}
                    onChange={event => setCategoryName(event.target.value)}
                    disabled={savingCategory}
                />

                <DefaultButton
                    type='button'
                    variant='primary'
                    onClick={handleSaveCategory}
                    disabled={savingCategory}
                >
                    {savingCategory ? 'Salvando' : 'Salvar'}
                </DefaultButton>
            </div>

            {categoryError && (
                <p role='alert'>{categoryError}</p>
            )}
        </div>
      )}
    </div>
  );
}
