CREATE TABLE Desarrollador (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    pais VARCHAR(100) NOT NULL,
    fecha_fundacion DATE NOT NULL,
    activo BOOLEAN NOT NULL,
    codigo VARCHAR(50),
    descripcion TEXT
);

CREATE TABLE Plataforma (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    fabricante VARCHAR(255) NOT NULL,
    fecha_lanzamiento DATE NOT NULL,
    activo BOOLEAN NOT NULL,
    codigo VARCHAR(50),
    descripcion TEXT
);

CREATE TABLE Videojuego (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    fecha_lanzamiento DATE NOT NULL,
    genero VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    disponible BOOLEAN NOT NULL,
    id_desarrollador INT,
    id_ficha_tecnica INT,
    FOREIGN KEY (id_desarrollador) REFERENCES Desarrollador(id)
);

CREATE TABLE FichaTecnica (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tamano_archivo DECIMAL(10, 2) NOT NULL,
    requisitos_minimos TEXT,
    requisitos_recomendados TEXT,
    motor_grafico VARCHAR(255),
    codigo VARCHAR(50),
    descripcion TEXT,
    id_videojuego INT,
    FOREIGN KEY (id_videojuego) REFERENCES Videojuego(id)
);

CREATE TABLE Videojuego_Plataforma (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_videojuego INT NOT NULL,
    id_plataforma INT NOT NULL,
    fecha_lanzamiento_plataforma DATE NOT NULL,
    FOREIGN KEY (id_videojuego) REFERENCES Videojuego(id),
    FOREIGN KEY (id_plataforma) REFERENCES Plataforma(id)
);
