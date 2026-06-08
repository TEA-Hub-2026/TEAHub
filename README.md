# 🧩 TEAHub: Sistema de Gestão para Clínicas e Profissionais em TEA

## 🚀 a. Integrantes do Grupo: 
* Giovane Ferreira Paes Ribeiro
* Guilherme Edilson de Almeida Cavalcante Soares
* João Vitor Rodrigues Santos
* Julia Beatriz Borges
* Késsedy Rodrigues Araujo
* Lia Rachel Ferreira de Sousa
* Pamella Roberta dos Santos Silva
* Thiago Borges Menezes Silva

## 🖥️ b. Apresentação do Sistema:
O TEAHub é uma plataforma digital desenvolvida para apoiar clínicas e profissionais que atuam no atendimento de crianças com Transtorno do Espectro Autista (TEA). O sistema moderniza o acompanhamento terapêutico, centralizando o registro de pacientes, profissionais e atendimentos em um ambiente seguro. A plataforma auxilia na tomada de decisões clínicas através de um monitoramento baseado em dados, substituindo processos manuais e dispersos por uma gestão organizada e eficiente. Com base nisso, aqui estão algumas de suas principais funcionalidades desenvolvidas para essa etapa:

| Principais Funcionalidades: | Descrição: |
| :--- | :--- |
| **Gestão de Acessos e Perfis** | Módulo de autenticação com fluxos de cadastro distintos para Clínicas (Administradores) e Profissionais. O fluxo de trabalho é iniciado no login, direcionando o usuário para o seu respectivo painel de operações. |
| **Gestão Completa de Pacientes e Atendimentos** | Operações de CRUD (Criar, Ler, Atualizar, Deletar) robustas, permitindo o cadastro, edição de prontuários, atualização de registros e exclusão controlada de pacientes e atendimentos. |
| **Interface de Operação Profissional** | O painel do profissional oferece acesso direto ao histórico, possibilitando visualizar detalhes específicos de atendimentos anteriores e prontuários detalhados de cada paciente, facilitando a continuidade do tratamento. |
| **Módulo de Gestão de Clínicas** | O sistema inclui uma tela de "Menu Clínica", estruturada como um protótipo para demonstrar a arquitetura das futuras funcionalidades administrativas que serão integradas ao ecossistema TEAHub. |
| **Segurança de Dados e Prevenção de Erros** | Priorizando a integridade dos dados, todas as ações críticas — como salvar alterações, atualizar registros ou excluir cadastros — possuem um sistema de feedback imediato, exibindo mensagens de confirmação ao usuário para evitar erros operacionais e ações irreversíveis. |

## 📄 Diagramas:
### c. Diagrama de Entidade-Relacionamento
<div align="center">
  <img src="./diagramas/Diagrama%20DER%20-%20TeaHub.drawio..png" width="500">
</div>

### d. Diagrama de Componentes
<div align="center">
  <img src="./diagramas/Diagrama%20de%20Componentes%20-%20TeaHub.drawio.jpeg" width="500">
</div>

## ✔️ e. Checklist de inspeção de Qualidade:
**🛠️ Engenharia de Software**
* [x] Arquitetura em Camadas: Separação clara entre view, model e dao, facilitando a manutenção.
* [x] Persistência Segura: Utilização de PreparedStatement em todas as interações com o banco de dados para eliminar vulnerabilidades de SQL Injection.
* [x] Gerenciamento de Recursos: Implementação de try-with-resources garantindo o fechamento automático de conexões e ResultSets (Memory Leak Prevention).
* [x] Padrão de Código: Nomenclatura seguindo as convenções da linguagem Java, garantindo legibilidade e coesão.

**🛡️ Usabilidade e Experiência do Usuário (UX)**
* [x] Prevenção de Erros: Implementação de diálogos de confirmação (JOptionPane) para todas as ações destrutivas ou permanentes (excluir, salvar ou editar), reduzindo o risco de ações acidentais.
* [x] Feedback Visual: Interação responsiva através de mensagens claras que orientam o usuário sobre o sucesso ou falha de cada operação.
* [x] UI/UX Consistente: Uso de Look and Feel profissional (FlatLaf) para proporcionar uma interface moderna e acessível.
* [x] Fluxo de Navegação: Transições de tela controladas via dispose() para gerenciamento eficiente de memória e estado da aplicação.

**🧪 Validação Funcional**
* [x] Autenticação: Validação de login com verificação de credenciais e distinção de perfis (Clínica vs. Profissional).
* [x] CRUD Completo: Funcionalidades de criar, ler, atualizar e deletar operacionais para Pacientes e Atendimentos.
* [x] Prototipagem: Módulo administrativo ("Menu Clínica") documentado como funcionalidade de desenvolvimento futuro.

## ⚙️ f. Instruções de Execução:
1. Clone este repositório para sua máquina local e importe o projeto na sua IDE

2. O sistema utiliza PostgreSQL. Para preparar o banco:
* Abra o seu gerenciador de banco de dados (ex: pgAdmin).
* Crie um novo banco de dados chamado TEAHub.
* Utilize o script localizado em /sql/ddl.sql para realizar a criação de todas as tabelas necessárias
* Execute o arquivo ddl.sql no seu banco para estruturar o esquema do sistema.
* (Opcional) Execute o dml.sql caso deseje popular o banco com registros iniciais para testes.
**OBS: Não elaboramos inserts para clínica ou profissional para realizarem o teste de Cadastro e Login (Cadastrar uma clínica é obrigatório para o uso do Sistema)**

3. Conexão com o Banco
* O projeto gerencia a comunicação via classe ConexaoBanco.java. Para garantir que a aplicação se conecte corretamente:
* Navegue até o pacote br.com.teahub.config.
* Abra o arquivo ConexaoBanco.java.
* Atualize os parâmetros de conexão (URL, usuário e senha) conforme a sua configuração local do PostgreSQL.

4. Bibliotecas e Dependências
* Este projeto utiliza a biblioteca FlatLaf para garantir uma interface gráfica moderna e consistente. Certifique-se de que o arquivo .jar da biblioteca (localizado na pasta lib ou referenciado na aba Libraries do seu projeto) esteja corretamente incluído no Classpath da sua IDE. Caso o projeto não identifique automaticamente ao abrir, adicione-o manualmente nas configurações de Project Properties > Libraries.

5. Execução
Após configurar a conexão e verificar as bibliotecas:
* Localize a classe principal no pacote teahub chamada TEAHub.java.
* Clique com o botão direito sobre o arquivo e selecione "Run File".
