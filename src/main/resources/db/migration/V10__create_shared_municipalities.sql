CREATE SCHEMA IF NOT EXISTS app_shared;

CREATE TABLE app_shared.municipalities (
    code       VARCHAR(5)   PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,

    CONSTRAINT chk_municipalities_code
        CHECK (code ~ '^[0-9]{5}$'),
    CONSTRAINT chk_municipalities_name
        CHECK (BTRIM(name) <> ''),
    CONSTRAINT chk_municipalities_department
        CHECK (BTRIM(department) <> '')
);

INSERT INTO app_shared.municipalities (code, name, department) VALUES
    ('05002', 'Abejorral', 'Antioquia'),
    ('05021', 'Alejandría', 'Antioquia'),
    ('05055', 'Argelia', 'Antioquia'),
    ('05148', 'El Carmen de Viboral', 'Antioquia'),
    ('05197', 'Cocorná', 'Antioquia'),
    ('05206', 'Concepción', 'Antioquia'),
    ('05313', 'Granada', 'Antioquia'),
    ('05318', 'Guarne', 'Antioquia'),
    ('05321', 'Guatapé', 'Antioquia'),
    ('05376', 'La Ceja', 'Antioquia'),
    ('05400', 'La Unión', 'Antioquia'),
    ('05440', 'Marinilla', 'Antioquia'),
    ('05483', 'Nariño', 'Antioquia'),
    ('05541', 'El Peñol', 'Antioquia'),
    ('05607', 'El Retiro', 'Antioquia'),
    ('05615', 'Rionegro', 'Antioquia'),
    ('05649', 'San Carlos', 'Antioquia'),
    ('05652', 'San Francisco', 'Antioquia'),
    ('05660', 'San Luis', 'Antioquia'),
    ('05667', 'San Rafael', 'Antioquia'),
    ('05674', 'San Vicente Ferrer', 'Antioquia'),
    ('05697', 'El Santuario', 'Antioquia'),
    ('05756', 'Sonsón', 'Antioquia');
