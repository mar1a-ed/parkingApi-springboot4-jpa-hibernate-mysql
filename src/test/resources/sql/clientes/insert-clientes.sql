insert into usuarios (id, username, password, role) values
(100, 'ana@email.com', '$2a$12$wvqtlae/N6cMHnVknNMgGuvFZ3L42eNEJJBMlRgwenkayDZ0Lyl22', 'ROLE_ADMIN'),
(101, 'bia@email.com', '$2a$12$znLFiw4S3V7zQLpZkSYOle7hsocYiSwfxbXRsl29/Fvsf8Eu43n9y', 'ROLE_CLIENTE'),
(102, 'byd@email.com', '$2a$12$3ffLcbqfSYPGfnVJIN8H7.arL4FogotvASbixuGcQyxzMXPqOvWZG', 'ROLE_CLIENTE');
(103, 'tobby@email.com', '$2a$12$3ffLcbqfSYPGfnVJIN8H7.arL4FogotvASbixuGcQyxzMXPqOvWZG', 'ROLE_CLIENTE');

insert into clientes(id, nome, cpf, usuario_id) values
(10, 'Bianca Silva', '48601342060', 101),
(20, 'Byd Ferreira', '44797978074', 102);
