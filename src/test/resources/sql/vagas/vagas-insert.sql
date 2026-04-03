insert into usuarios (id, username, password, role) values                                                      (100, 'ana@email.com', '$2a$12$wvqtlae/N6cMHnVknNMgGuvFZ3L42eNEJJBMlRgwenkayDZ0Lyl22', 'ROLE_ADMIN'),
(100, 'ana@email.com', '$2a$12$wvqtlae/N6cMHnVknNMgGuvFZ3L42eNEJJBMlRgwenkayDZ0Lyl22', 'ROLE_ADMIN'),
(101, 'bia@email.com', '$2a$12$znLFiw4S3V7zQLpZkSYOle7hsocYiSwfxbXRsl29/Fvsf8Eu43n9y', 'ROLE_CLIENTE'),
(102, 'byd@email.com', '$2a$12$3ffLcbqfSYPGfnVJIN8H7.arL4FogotvASbixuGcQyxzMXPqOvWZG', 'ROLE_CLIENTE');

insert into vagas (id, codigo, status) values
(10, 'A-01', 'LIVRE'),
(20, 'A-02', 'LIVRE'),
(30, 'A-03', 'OCUPADA'),
(40, 'A-04', 'LIVRE');