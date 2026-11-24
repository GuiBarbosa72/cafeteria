INSERT INTO categorias (nome, descricao, ativa) VALUES
('Cafés Especiais', 'Grãos 100% arábica torrados artesanalmente', TRUE),
('Salgados & Pães', 'Assados na hora e opções artesanais', TRUE),
('Confeitaria', 'Bolos, tortas e doces da casa', TRUE);

INSERT INTO produtos (nome, descricao, preco, disponivel, categoria_id) VALUES
('Espresso Duplo', 'Extração curta com notas de chocolate amargo', 9.50, TRUE, 1),
('Cappuccino Italiano', 'Espresso com leite vaporizado cremoso e cacau', 14.00, TRUE, 1),
('Pão de Queijo da Serra', 'Porção com 3 unidades quentes', 12.00, TRUE, 2),
('Bolo de Cenoura com Ganache', 'Fatia generosa com cobertura meio amarga', 16.50, TRUE, 3);

INSERT INTO clientes (nome, telefone, email, pontos_fidelidade) VALUES
('Mariana Costa', '(11) 98877-6655', 'mariana.costa@email.com', 15),
('Lucas Andrade', '(11) 97766-5544', 'lucas.andrade@email.com', 8);

INSERT INTO mesas (numero, capacidade, status) VALUES
(1, 2, 'LIVRE'),
(2, 4, 'OCUPADA'),
(3, 6, 'RESERVADA');

INSERT INTO pedidos (cliente_id, mesa_id, produto_id, quantidade, observacoes, valor_total, status, data_hora) VALUES
(1, 2, 2, 2, 'Sem açúcar, canela extra', 28.00, 'EM_PREPARO', CURRENT_TIMESTAMP);
