SELECT * FROM clinica;

SELECT * FROM paciente;

SELECT * FROM profissional;

SELECT * FROM atendimento;

SELECT a.id_atendimento, a.id_paciente, a.observacoes, a.data_atendimento, 
p.nome_paciente, pr.id_profissional, pr.nome AS nome_profissional, pr.especialidade
FROM atendimento a
JOIN paciente p ON p.id_paciente = a.id_paciente
JOIN profissional pr ON pr.id_profissional = a.id_profissional
WHERE p.id_clinica = ?
ORDER BY a.data_atendimento DESC;

SELECT a.id_atendimento, a.id_paciente, a.observacoes, a.data_atendimento, 
p.nome_paciente, pr.id_profissional, pr.nome AS nome_profissional, pr.especialidade
FROM atendimento a
JOIN paciente p ON p.id_paciente = a.id_paciente
JOIN profissional pr ON pr.id_profissional = a.id_profissional
WHERE p.id_clinica = ? AND p.nome_paciente ILIKE ?
ORDER BY a.data_atendimento DESC;

SELECT * FROM paciente WHERE id_clinica = ? ORDER BY nome_paciente;
  
SELECT * FROM paciente WHERE id_clinica = ? AND nome_paciente ILIKE ? ORDER BY nome_paciente;

SELECT id_paciente, nome_paciente FROM paciente ORDER BY nome_paciente;           

SELECT * FROM profissional WHERE usuario_login = ? AND senha_login = ? AND status_ativo = TRUE;

SELECT * FROM profissional WHERE id_profissional = ?;

SELECT id_profissional, nome, especialidade FROM profissional WHERE id_clinica = ? ORDER BY nome ASC;

SELECT id_profissional, nome, especialidade FROM profissional WHERE id_clinica = ? AND LOWER(nome) LIKE ? ORDER BY nome ASC;