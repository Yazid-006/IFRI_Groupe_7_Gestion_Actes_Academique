CREATE DATABASE IF NOT EXISTS gestion_actes_db;
USE gestion_actes_db;

-- Table usager
CREATE TABLE IF NOT EXISTS usager (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    dateCreation DATETIME NOT NULL,
    numeroUsager VARCHAR(20) UNIQUE NOT NULL,
    adresse TEXT,
    dateNaissance DATE NOT NULL,
    pieceIdentite VARCHAR(50)
);

-- Table agent
CREATE TABLE IF NOT EXISTS agent (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    dateCreation DATETIME NOT NULL,
    matricule VARCHAR(20) UNIQUE NOT NULL,
    service VARCHAR(50) NOT NULL,
    fonction VARCHAR(50) NOT NULL,
    niveauAcces VARCHAR(20) NOT NULL,
    motDePasse VARCHAR(255) NOT NULL DEFAULT 'agent123'
);

-- Table administrateur
CREATE TABLE IF NOT EXISTS administrateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telephone VARCHAR(20),
    dateCreation DATETIME NOT NULL,
    login VARCHAR(30) UNIQUE NOT NULL,
    motDePasse VARCHAR(255) NOT NULL,
    derniereConnexion DATETIME
);

-- Table acte
CREATE TABLE IF NOT EXISTS acte (
    id INT PRIMARY KEY AUTO_INCREMENT,
    numeroActe VARCHAR(30) UNIQUE NOT NULL,
    typeActe VARCHAR(30) NOT NULL,
    objet VARCHAR(200) NOT NULL,
    contenu TEXT,
    dateEmission DATETIME NOT NULL,
    dateValidite DATE,
    statut VARCHAR(20) NOT NULL,
    cheminFichier VARCHAR(255),
    usager_id INT NOT NULL,
    agent_id INT NOT NULL,
    FOREIGN KEY (usager_id) REFERENCES usager(id),
    FOREIGN KEY (agent_id) REFERENCES agent(id)
);

-- Table demande
CREATE TABLE IF NOT EXISTS demande (
    id INT PRIMARY KEY AUTO_INCREMENT,
    numeroDemande VARCHAR(30) UNIQUE NOT NULL,
    typeActeDemande VARCHAR(30) NOT NULL,
    dateDemande DATETIME NOT NULL,
    statut VARCHAR(20) NOT NULL,
    motif VARCHAR(500),
    dateTraitement DATETIME,
    demandeur_id INT NOT NULL,
    agentTraiteur_id INT,
    acteGenere_id INT,
    FOREIGN KEY (demandeur_id) REFERENCES usager(id),
    FOREIGN KEY (agentTraiteur_id) REFERENCES agent(id),
    FOREIGN KEY (acteGenere_id) REFERENCES acte(id)
);

-- Table document
CREATE TABLE IF NOT EXISTS document (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nomFichier VARCHAR(255) NOT NULL,
    typeFichier VARCHAR(50) NOT NULL,
    chemin VARCHAR(255) NOT NULL,
    dateUpload DATETIME NOT NULL,
    demande_id INT NOT NULL,
    FOREIGN KEY (demande_id) REFERENCES demande(id)
);

-- Table Configuration

CREATE TABLE IF NOT EXISTS configuration (
    id              INT             NOT NULL DEFAULT 1,
    nomEtablissement VARCHAR(100)   NOT NULL,
    adresse         VARCHAR(255),
    anneeAcademique VARCHAR(20),
    prefixeDemande  VARCHAR(20),
    delaiTraitement INT             DEFAULT 5,
    nomSignataire   VARCHAR(100),
    fonctionSignataire VARCHAR(100),
    CONSTRAINT pk_configuration PRIMARY KEY (id),
    CONSTRAINT chk_configuration_unique CHECK (id = 1)
);

-- Insertion des données de test
INSERT INTO administrateur (nom, prenom, email, telephone, dateCreation, login, motDePasse) VALUES
('Admin', 'System', 'admin@ifri.bj', '97000000', NOW(), 'admin', 'admin');

INSERT INTO agent (nom, prenom, email, telephone, dateCreation, matricule, service, fonction, niveauAcces) VALUES
('Agent', 'Test', 'agent@ifri.bj', '97111111', NOW(), 'AGT001', 'Scolarité', 'Gestionnaire', 'GESTION_DEMANDE');

INSERT INTO usager (nom, prenom, email, telephone, dateCreation, numeroUsager, adresse, dateNaissance, pieceIdentite) VALUES
('Usager', 'Test', 'usager@ifri.bj', '97222222', NOW(), 'USR001', 'Cotonou', '1990-01-01', 'ID001');

INSERT INTO configuration (id, nomEtablissement, adresse, anneeAcademique, prefixeDemande, delaiTraitement, nomSignataire, fonctionSignataire)
VALUES (1, 'IFRI', 'Cotonou, Bénin', '2024-2025', 'IFRI', 5, '', '')
ON DUPLICATE KEY UPDATE id = id;
