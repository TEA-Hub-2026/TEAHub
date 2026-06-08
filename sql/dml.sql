INSERT INTO paciente (id_clinica, nome_paciente, data_nascimento, responsavel, telefone_responsavel, nivel_tea, medicacoes_em_uso, restricoes_alimentares) VALUES
(1, 'Alice Oliveira', '2019-03-12', 'Bruna Oliveira', '(11) 91111-1111', 1, 'Nenhuma', 'Nenhuma'),
(1, 'Bernardo Costa', '2020-05-20', 'Carlos Costa', '(11) 92222-2222', 2, 'Risperidona', 'Intolerância a Lactose'),
(1, 'Clara Mendes', '2018-11-05', 'Daniela Mendes', '(11) 93333-3333', 3, 'Nenhuma', 'Sem glúten'),
(1, 'Davi Santos', '2021-01-15', 'Eduardo Santos', '(11) 94444-4444', 1, 'Melatonina', 'Nenhuma'),
(2, 'Elena Rocha', '2019-07-22', 'Fabio Rocha', '(21) 95555-5555', 2, 'Nenhuma', 'Alergia a amendoim'),
(2, 'Gabriel Lima', '2020-09-10', 'Gisele Lima', '(21) 96666-6666', 3, 'Aripiprazol', 'Nenhuma'),
(2, 'Heloísa Alves', '2018-12-30', 'Hugo Alves', '(21) 97777-7777', 1, 'Nenhuma', 'Nenhuma'),
(2, 'Igor Nunes', '2021-04-18', 'Isabela Nunes', '(21) 98888-8888', 2, 'Nenhuma', 'Dieta cetogênica'),
(3, 'Julia Farias', '2019-02-14', 'Jorge Farias', '(31) 99999-9999', 3, 'Risperidona', 'Nenhuma'),
(3, 'Kevin Souza', '2020-10-02', 'Karina Souza', '(31) 90000-0000', 1, 'Nenhuma', 'Intolerância a Lactose');

INSERT INTO atendimento (id_profissional, id_paciente, data_atendimento, observacoes) VALUES
(1, 1, '2026-06-01 08:00:00', 'Bom contato visual, evoluiu na coordenação motora fina.'),
(1, 2, '2026-06-01 09:30:00', 'Sessão agitada, necessário realizar pausas sensoriais.'),
(2, 3, '2026-06-02 10:00:00', 'Excelente interação social com o terapeuta.'),
(2, 4, '2026-06-02 14:00:00', 'Trabalhamos comandos simples, paciente respondeu bem.'),
(3, 5, '2026-06-03 09:00:00', 'Paciente demonstrou foco prolongado em atividade de encaixe.'),
(3, 6, '2026-06-03 11:00:00', 'Dificuldade na transição de tarefas, mas reagiu bem a suporte.'),
(1, 7, '2026-06-04 15:00:00', 'Aumento na iniciativa de comunicação verbal.'),
(2, 8, '2026-06-04 16:30:00', 'Trabalho de regulação sensorial concluído com sucesso.'),
(3, 9, '2026-06-05 10:00:00', 'Paciente estava sonolento, mas participou das atividades.'),
(1, 10, '2026-06-05 13:00:00', 'Avanço significativo na tolerância ao toque.');

-- As linhas de comando a seguir não foram digitadas e selecionadas diretamente no banco de dados --
-- Elas foram utilizadas no código de interface do Java ---

INSERT INTO atendimento (id_paciente, id_profissional, observacoes, data_atendimento)
VALUES (?, ?, ?, ?);

UPDATE atendimento
SET id_paciente = ?, id_profissional = ?, observacoes = ?, data_atendimento = ?
WHERE id_atendimento = ?;

DELETE FROM atendimento WHERE id_atendimento = ?;

INSERT INTO paciente (nome_paciente, data_nascimento, responsavel, telefone_responsavel, nivel_tea, status_ativo, id_clinica)
VALUES (?, ?, ?, ?, ?, ?, ?);

UPDATE paciente SET nome_paciente = ?, data_nascimento = ?, responsavel = ?, telefone_responsavel = ?, nivel_tea = ?, status_ativo = ? 
WHERE id_paciente = ?;

DELETE FROM paciente WHERE id_paciente = ?;

UPDATE paciente SET status_ativo = false WHERE id_paciente = ?;

INSERT INTO profissional (id_clinica, nome, email, usuario_login, senha_login, especialidade, registro_profissional) 
VALUES (?, ?, ?, ?, ?, ?, ?);

UPDATE profissional SET id_clinica = ?, nome = ?, email = ?, usuario_login = ?, senha_login = ?, especialidade = ?, registro_profissional = ?, status_ativo = ? 
WHERE id_profissional = ?;

DELETE FROM profissional WHERE id_profissional = ?;
