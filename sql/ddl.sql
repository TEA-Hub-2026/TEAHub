CREATE TABLE clinica (
id_clinica SERIAL PRIMARY KEY,
nome_clinica VARCHAR(150) NOT NULL,
cnpj VARCHAR(18) NOT NULL UNIQUE,
usuario_admin VARCHAR(50) NOT NULL UNIQUE, 
senha_admin VARCHAR(100) NOT NULL,         
telefone VARCHAR(15)
);

CREATE TABLE profissional (
id_profissional SERIAL PRIMARY KEY,
id_clinica INT NOT NULL,
nome VARCHAR(150) NOT NULL,
email VARCHAR(100) NOT NULL UNIQUE,
usuario_login VARCHAR(50) NOT NULL UNIQUE, 
senha_login VARCHAR(100) NOT NULL,         
especialidade VARCHAR(100) NOT NULL,
registro_profissional VARCHAR(20),        
status_ativo BOOLEAN DEFAULT TRUE NOT NULL,
        
CONSTRAINT fk_profissional_clinica 
FOREIGN KEY (id_clinica) 
REFERENCES clinica(id_clinica) 
ON DELETE CASCADE
);

CREATE TABLE paciente (
id_paciente SERIAL PRIMARY KEY,
id_clinica INT NOT NULL,
nome_paciente VARCHAR(150) NOT NULL,
data_nascimento DATE NOT NULL,
responsavel VARCHAR(150),
telefone_responsavel VARCHAR(20) NOT NULL,
nivel_tea INT NOT NULL CHECK (nivel_tea BETWEEN 1 AND 3),
status_ativo BOOLEAN DEFAULT TRUE NOT NULL,
medicacoes_em_uso TEXT, 
restricoes_alimentares TEXT, 
data_cadastro DATE DEFAULT CURRENT_DATE NOT NULL, 
        
CONSTRAINT fk_paciente_clinica 
FOREIGN KEY (id_clinica) 
REFERENCES clinica(id_clinica) 
ON DELETE CASCADE
);

CREATE TABLE atendimento (
id_atendimento SERIAL PRIMARY KEY,
id_profissional INT NOT NULL,
id_paciente INT NOT NULL,
data_atendimento TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
observacoes TEXT NOT NULL,
        
CONSTRAINT fk_atendimento_profissional 
FOREIGN KEY (id_profissional) 
REFERENCES profissional(id_profissional) 
ON DELETE RESTRICT,

CONSTRAINT fk_atendimento_paciente 
FOREIGN KEY (id_paciente) 
REFERENCES paciente(id_paciente) 
ON DELETE CASCADE
);