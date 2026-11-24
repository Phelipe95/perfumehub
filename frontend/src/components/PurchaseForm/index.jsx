import styles from './styles.module.css';
import { DefaultButton } from '../DefaultButton';
import { PurchaseItemCard } from '../PurchaseItemCard';
import { ProductSearch } from '../ProductSearch';
import { QuantityInput } from '../QuantityInput';
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

    function resetItemForm() {
        setProductSearch('');
        setSelectedProduct(null);
        setUnitCost('');
        setQuantity(1);
        setItemError('');
    }

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

        resetItemForm();
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

        resetItemForm();
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

                    <ProductSearch
                        id='purchase-product-search'
                        products={products}
                        search={productSearch}
                        selectedProduct={selectedProduct}
                        onSearchChange={(text) => {
                            setProductSearch(text);
                            setSelectedProduct(null);
                        }}
                        onSelect={(product) => {
                            setSelectedProduct(product);
                            setProductSearch(product.name);
                        }}
                    />

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

                        <QuantityInput
                            id='purchase-quantity'
                            name='quantidade'
                            value={quantity}
                            onChange={setQuantity}
                        />
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