import { getJson, postJson } from './api';

export function createProduct(productData) {
    return postJson(
        '/api/produtos',
        productData,
        'Não foi possível cadastrar o produto.',
    );
}

export function getProducts(signal) {
    return getJson(
        '/api/produtos',
        signal,
        'Não foi possível carregar os produtos.',
    );
}