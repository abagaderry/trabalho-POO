import os
import csv
import pymysql

# =========================================================
# CONFIGURAÇÃO DO BANCO
# =========================================================
# ALTERE MANUALMENTE:
# - host
# - user
# - password
# - database
# =========================================================

DB_CONFIG = {
    "host": "localhost",
    "user": "root",
    "password": "",
    "database": "loja",
    "port": 3306
}

# =========================================================
# PASTA DOS CSV
# =========================================================
# ALTERE MANUALMENTE:
# Caminho onde estão os arquivos .csv
# =========================================================

PASTA_CSV = os.getcwd()
PASTA_CSV = os.path.join(PASTA_CSV, "CSV")

os.chdir(PASTA_CSV)

# =========================================================
# CONEXÃO COM MYSQL
# =========================================================

try:
    conexao = pymysql.connect(
        host=DB_CONFIG["host"],
        user=DB_CONFIG["user"],
        password=DB_CONFIG["password"],
        database=DB_CONFIG["database"],
        port=DB_CONFIG["port"],
        charset='utf8mb4'
    )

    cursor = conexao.cursor()

    print("Conexão realizada com sucesso.")

except Exception as e:
    print(f"Erro ao conectar no banco:\n{e}")
    exit()

# =========================================================
# FUNÇÃO PARA EXECUTAR SQL
# =========================================================

def executar_sql(sql):

    try:
        cursor.execute(sql)

    except Exception as e:
        print("\n===================================")
        print("ERRO AO EXECUTAR SQL")
        print("===================================")
        print(e)
        print("\nSQL:")
        print(sql)

# =========================================================
# FUNÇÃO GENÉRICA DE IMPORTAÇÃO
# =========================================================

def importar_csv(nome_arquivo, sql_insert):

    try:

        with open(nome_arquivo, mode='r', encoding='utf-8') as arquivo:

            leitor = csv.reader(arquivo, delimiter=';')

            # Pula cabeçalho
            next(leitor)

            dados = []

            for linha in leitor:
                dados.append(tuple(linha))

            cursor.executemany(sql_insert, dados)

            conexao.commit()

            print(f"{nome_arquivo} importado com sucesso.")

    except FileNotFoundError:
        print(f"Arquivo não encontrado: {nome_arquivo}")

    except Exception as e:
        print(f"Erro ao importar {nome_arquivo}:")
        print(e)

# =========================================================
# REMOÇÃO DAS TABELAS
# =========================================================
# Ordem invertida por causa das foreign keys:
# pedido depende de cliente e produto
# =========================================================

tabelas = [
    "pedido",
    "produto",
    "cliente"
]

for tabela in tabelas:
    executar_sql(f"DROP TABLE IF EXISTS {tabela}")

# =========================================================
# CRIAÇÃO DAS TABELAS
# =========================================================

executar_sql("""
CREATE TABLE cliente (
    id        INT(11)      NOT NULL AUTO_INCREMENT,
    cpf       VARCHAR(14)  NOT NULL,
    nome      VARCHAR(255) NOT NULL,
    datanasc  VARCHAR(255) NOT NULL,
    cep       VARCHAR(255) NOT NULL,
    endereco  VARCHAR(255) NOT NULL,
    num_comp  VARCHAR(255) NOT NULL,
    bairro    VARCHAR(255) NOT NULL,
    cidade    VARCHAR(255) NOT NULL,
    uf        VARCHAR(255) NOT NULL,
    fone      VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    usuario   VARCHAR(255) NOT NULL,
    senha     VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY cpf   (cpf),
    UNIQUE KEY email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
""")

executar_sql("""
CREATE TABLE produto (
    id         INT(11)      NOT NULL AUTO_INCREMENT,
    nome       VARCHAR(100) DEFAULT NULL,
    prescricao TINYINT(1)   DEFAULT NULL,
    descricao  VARCHAR(100) DEFAULT NULL,
    preco      FLOAT        DEFAULT NULL,

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
""")

executar_sql("""
CREATE TABLE pedido (
    id         INT(11)     NOT NULL AUTO_INCREMENT,
    idCliente  INT(11)     NOT NULL,
    idProduto  INT(11)     NOT NULL,
    cpf        VARCHAR(14) NOT NULL,
    data       VARCHAR(10) NOT NULL,
    quantidade INT(11)     NOT NULL,
    precoTotal FLOAT       NOT NULL,

    PRIMARY KEY (id),
    KEY fk_pedido_cliente (idCliente),
    KEY fk_pedido_produto (idProduto),

    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (idCliente) REFERENCES cliente (id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    CONSTRAINT fk_pedido_produto
        FOREIGN KEY (idProduto) REFERENCES produto (id)
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
""")

print("\nTabelas criadas com sucesso.")

# =========================================================
# IMPORTAÇÃO DOS CSV
# =========================================================

importar_csv(
    "cliente.csv",
    """
    INSERT INTO cliente (cpf, nome, datanasc, cep, endereco, num_comp,
                         bairro, cidade, uf, fone, email, usuario, senha)
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """
)

importar_csv(
    "produto.csv",
    """
    INSERT INTO produto (nome, prescricao, descricao, preco)
    VALUES (%s, %s, %s, %s)
    """
)

importar_csv(
    "pedido.csv",
    """
    INSERT INTO pedido (idCliente, idProduto, cpf, data, quantidade, precoTotal)
    VALUES (%s, %s, %s, %s, %s, %s)
    """
)

# =========================================================
# FINALIZAÇÃO
# =========================================================

cursor.close()
conexao.close()

print("\n===================================")
print("BANCO POPULADO COM SUCESSO")
print("===================================")
