ALTER TABLE produto
    ADD COLUMN custo_medio NUMERIC(14,6),
    ADD CONSTRAINT chk_produto_custo_medio
        CHECK (custo_medio >= 0)