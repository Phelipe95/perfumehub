import styles from './styles.module.css';
import { DefaultButton } from '../DefaultButton';
import { PurchaseItemCard } from '../PurchaseItemCard';
import { useState } from 'react';

function getTodayDate() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
}

export function PurchaseForm({ products, onClose }) {
    const [quantity, setQuantity] = useState(1);
    const [productSearch, setProductSearch] = useState('');
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [items, setItems] = useState([]);
    const [purchaseDate, setPurchaseDate] = useState(getTodayDate);
    const [unitCost, setUnitCost] = useState('');
    const [itemError, setItemError] = useState('');
    const [showItemForm, setShowItemForm] = useState(true);

    const normalizedSearch = productSearch.trim().toLocaleLowerCase('pt-BR');

    const suggestedProducts = normalizedSearch === '' || selectedProduct
        ? []
        : products.filter((product) =>
            product.name.toLocaleLowerCase('pt-BR').includes(normalizedSearch)
        );

    function handleSubmit(event) {
        event.preventDefault();
    }

    function handleAddItem() {
        setItemError('');

        if (!selectedProduct) {
            setItemError('Selecione um produto da lista de sugestões.');
            return;
        }

        const parsedCost = Number(unitCost);

        if (
            unitCost.trim() === '' ||
            !Number.isFinite(parsedCost) ||
            parsedCost < 0 ||
            parsedCost > 99999999.99 ||
            !/^\d+(\.\d{1,2})?$/.test(unitCost)
        ) {
            setItemError('Informe um custo válido com até duas casas decimais.');
            return;
        }

        if (
            !Number.isInteger(quantity) ||
            quantity < 1 ||
            quantity > 2147483647
        ) {
            setItemError('Informe uma quantidade inteira maior que zero.');
            return;
        }

        const item = {
            id: crypto.randomUUID(),
            productId: selectedProduct.id,
            name: selectedProduct.name,
            unitCost: parsedCost,
            quantity,
        };

        setItems((current) => [...current, item]);

        setProductSearch('');
        setSelectedProduct(null);
        setUnitCost('');
        setQuantity(1);
        setShowItemForm(false);
    }

    function handleRemoveItem(itemId) {
        setItems((current) =>
            current.filter((item) => item.id !== itemId)
        );
    }

    function handleCancelItem() {
        if (items.length === 0) {
            onClose();
            return;
        }

        setProductSearch('');
        setSelectedProduct(null);
        setUnitCost('');
        setQuantity(1);
        setItemError('');
        setShowItemForm(false);
    }

    return (
        <form
            className={styles.purchaseForm}
            onSubmit={handleSubmit}
        >
            <label>
                <span>Data da compra</span>
                <input
                    name='dataCompra'
                    type='date'
                    required
                    value={purchaseDate}
                    onChange={(event) => setPurchaseDate(event.target.value)}
                />
            </label>

            {items.length > 0 && (
                <section
                    className={styles.purchaseItems}
                    aria-label='Produtos adicionados à compra'
                >
                    {items.map((item) => (
                        <PurchaseItemCard
                            key={item.id}
                            item={item}
                            onRemove={handleRemoveItem}
                        />
                    ))}
                </section>
            )}

            {showItemForm && (
                <div className={styles.itemForm}>

                    <div className={styles.productSearch}>
                        <label htmlFor='purchase-product-search'>Buscar produto</label>

                        <input
                            id='purchase-product-search'
                            type='search'
                            autoComplete='off'
                            placeholder='Digite o nome do produto'
                            value={productSearch}
                            onChange={(event) => {
                                setProductSearch(event.target.value);
                                setSelectedProduct(null);
                            }}
                        />

                        {suggestedProducts.length > 0 && (
                            <ul className={styles.suggestions}>
                                {suggestedProducts.map((product) => (
                                    <li key={product.id}>
                                        <button
                                            type='button'
                                            onClick={() => {
                                                setSelectedProduct(product);
                                                setProductSearch(product.name);
                                            }}
                                        >
                                            {product.name}
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        )}

                        {normalizedSearch !== '' &&
                            !selectedProduct &&
                            suggestedProducts.length === 0 && (
                                <p role='status'>Nenhum produto encontrado.</p>
                            )}

                    </div>

                    <label>
                        <span>Custo de compra por unidade (R$)</span>
                        <input
                            name='custoUnitario'
                            type='number'
                            min='0'
                            max='99999999.99'
                            step='0.01'
                            required
                            value={unitCost}
                            onChange={(event) => setUnitCost(event.target.value)}
                        />
                    </label>

                    <div className={styles.quantityField}>
                        <label htmlFor='purchase-quantity'>Quantidade comprada</label>

                        <div className={styles.quantityControl}>
                            <button
                                type='button'
                                aria-label='Diminuir quantidade'
                                disabled={quantity === '' || quantity <= 1}
                                onClick={() => setQuantity((current) => Math.max(1, current - 1))}
                            >
                                −
                            </button>

                            <input
                                id='purchase-quantity'
                                name='quantidade'
                                type='number'
                                min='1'
                                max='2147483647'
                                step='1'
                                required
                                value={quantity}
                                onChange={(event) => {
                                    const value = event.target.value;
                                    setQuantity(value === '' ? '' : Number(value));
                                }}
                            />

                            <button
                                type='button'
                                aria-label='Aumentar quantidade'
                                disabled={quantity !== '' && quantity >= 2147483647}
                                onClick={() =>
                                    setQuantity((current) =>
                                        current === '' ? 1 : Math.min(2147483647, current + 1)
                                    )
                                }
                            >
                                +
                            </button>
                        </div>
                    </div>

                    {itemError && (
                        <p role='alert'>{itemError}</p>
                    )}

                    <div className={styles.itemActions}>
                        <DefaultButton
                            type='button'
                            variant='secondary'
                            onClick={handleCancelItem}
                        >
                            Cancelar
                        </DefaultButton>

                        <DefaultButton
                            type='button'
                            variant='primary'
                            onClick={handleAddItem}
                        >
                            Salvar item
                        </DefaultButton>
                    </div>

                </div>
            )}

            {!showItemForm && (
                <DefaultButton
                    type='button'
                    variant='secondary'
                    onClick={() => {
                        setItemError('');
                        setShowItemForm(true);
                    }}
                >
                    Adicionar produto
                </DefaultButton>
            )}

            <DefaultButton type='submit' variant='primary' disabled>
                Registrar compra
            </DefaultButton>
        </form>
    );
}