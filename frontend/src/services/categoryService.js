import { getJson, postJson } from './api';

export function createCategory(categoryData) {
    return postJson(
        '/api/categorias',
        categoryData,
        'Não foi possível salvar a categoria.',
    );
}

export function getCategories(signal) {
    return getJson(
        '/api/categorias',
        signal,
        'Não foi possível carregar as categorias.',
    );
}