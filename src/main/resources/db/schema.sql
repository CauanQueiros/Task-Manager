USE task_manager_db;

-- A tarefa precisa de um dono (usuario_id NOT NULL). Este exemplo usa o primeiro
-- usuário cadastrado; crie sua conta pela tela de login antes de executar.
INSERT INTO tb_tarefa (
    titulo, 
    descricao, 
    status, 
    data_de_inicio, 
    data_de_fim, 
    concluida,
    usuario_id
) VALUES 
(
    'Lavar a Louça', 
    'Organizar e lavar a louça do almoço', 
    'FAZER', 
    '2026-09-14 17:00:00', 
    '2026-09-14 17:30:00', 
    FALSE,
    (SELECT MIN(id) FROM tb_usuario)
);
