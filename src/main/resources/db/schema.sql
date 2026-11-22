USE task_manager_db;

INSERT INTO tb_tarefa (
    titulo,
    descricao,
    status,
    prioridade,
    data_de_inicio,
    data_de_fim,
    concluida
) VALUES
(
    'Lavar a Louça',
    'Organizar e lavar a louça do almoço',
    'FAZER',
    'MEDIA',
    '2026-09-14 17:00:00',
    '2026-09-14 17:30:00',
    FALSE
);
