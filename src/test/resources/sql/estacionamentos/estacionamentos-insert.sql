insert into usuarios(id, username, password, role) values
(100, 'ana@email.com', '$2a$12$wvqtlae/N6cMHnVknNMgGuvFZ3L42eNEJJBMlRgwenkayDZ0Lyl22', 'ROLE_ADMIN'),
(101, 'bia@email.com', '$2a$12$wvqtlae/N6cMHnVknNMgGuvFZ3L42eNEJJBMlRgwenkayDZ0Lyl22', 'ROLE_CLIENTE'),
(102, 'bob@email.com', '$2a$12$wvqtlae/N6cMHnVknNMgGuvFZ3L42eNEJJBMlRgwenkayDZ0Lyl22', 'ROLE_CLIENTE');

insert into clientes(id, nome, cpf, usuario_if) values
(21, 'Beatriz Rodrigues', '09191773016', 101),
(22, 'Rodrigo Silva', '98481203015, 102');

insert into vagas (id, codigo, status) values
(10, 'A-01', 'OCUPADA'),
(20, 'A-02', 'OCUPADA'),
(30, 'A-03', 'LIVRE'),
(40, 'A-04', 'LIVRE');

insert into clientes_tem_vagas (numero_recibo, placa, marca, modelo, cor, data_entrada, cliente_id, vaga_id) values
('20260403-102900', 'FIT-1020', 'Fiat', 'Palio', 'Verde', '2026-04-03 10:29:00', 22, 10),
('20260403-104409', 'TLG-2345', 'Porsche', 'Cayene', 'Cinza', '2026-04-03 10:44:09', 21, 20);