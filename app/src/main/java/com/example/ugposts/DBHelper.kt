package com.example.ugposts
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_USUARIOS)
        db.execSQL(CREATE_TABLE_PAGINAS)
        db.execSQL(CREATE_TABLE_PUBLICACIONES)
        db.execSQL(CREATE_TABLE_ENLACES_EXTERIORES)
        db.execSQL(CREATE_TABLE_EVENTOS)

        db.execSQL(CREATE_INDEX_PUBLICACIONES)
        db.execSQL(CREATE_INDEX_EVENTOS)

        db.execSQL(INSERT_PAGINAS)
        db.execSQL(INSERT_EVENTOS)
        db.execSQL(INSERT_POSTS)
        db.execSQL(INSERT_USERS)
        db.execSQL(INSERT_ENLACES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Publicaciones")
        db.execSQL("DROP TABLE IF EXISTS Eventos")
        db.execSQL("DROP TABLE IF EXISTS Enlaces_Exteriores")
        db.execSQL("DROP TABLE IF EXISTS Paginas")
        db.execSQL("DROP TABLE IF EXISTS Usuarios")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON;")
    }

    fun insertPost(title: String, content: String, timestamp: String, category: String, userId: Int, pageId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("titulo", title)
            put("contenido", content)
            put("fecha", timestamp)
            put("categoria", category)
            put("ID_usuario", userId)
            put("ID_pagina", pageId)
        }
        val result = db.insert("Publicaciones", null, values)
        return result != -1L
    }

    fun insertEvent(eventName: String, description: String, startDate: String, endDate: String, location: String, userId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre_evento", eventName)
            put("descripcion", description)
            put("fecha_inicio", startDate)
            put("fecha_fin", endDate)
            put("ubicacion", location)
            put("ID_usuario", userId)
        }
        val result = db.insert("Eventos", null, values)
        return result != -1L
    }

    fun getAllPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        val db = this.readableDatabase

        val query = """
        SELECT 
            p.ID_publicacion, 
            p.titulo, 
            p.contenido, 
            p.fecha, 
            p.categoria, 
            u.nombre AS usuario_nombre, 
            pg.nombre_pagina AS pagina_nombre 
        FROM Publicaciones p
        LEFT JOIN Usuarios u ON p.ID_usuario = u.ID_usuario
        LEFT JOIN Paginas pg ON p.ID_pagina = pg.ID_pagina
    """
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val post = Post(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_publicacion")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                    content = cursor.getString(cursor.getColumnIndexOrThrow("contenido")),
                    timestamp = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    userName = cursor.getString(cursor.getColumnIndexOrThrow("usuario_nombre")),
                    pageName = cursor.getString(cursor.getColumnIndexOrThrow("pagina_nombre")),
                    category = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                )
                posts.add(post)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return posts
    }

    fun getAllEvents(): List<Evento> {
        val eventos = mutableListOf<Evento>()
        val db = this.readableDatabase

        val query = """
        SELECT 
            e.ID_evento,
            e.nombre_evento,
            e.descripcion,
            e.fecha_inicio,
            e.fecha_fin,
            e.ubicacion,
            u.nombre AS usuario_nombre
        FROM Eventos e
        LEFT JOIN Usuarios u ON e.ID_usuario = u.ID_usuario
    """
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val evento = Evento(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_evento")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_evento")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    fechaInicio = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inicio")),
                    fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fecha_fin")),
                    ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")),
                    userName = cursor.getString(cursor.getColumnIndexOrThrow("usuario_nombre"))
                )
                eventos.add(evento)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return eventos
    }

    fun getAllPages(): List<Pagina> {
        val paginas = mutableListOf<Pagina>()
        val db = this.readableDatabase

        val query = """
        SELECT 
            ID_pagina,
            nombre_pagina,
            URL_pagina,
            categoria
        FROM Paginas
    """
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val pagina = Pagina(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_pagina")),
                    nombrePagina = cursor.getString(cursor.getColumnIndexOrThrow("nombre_pagina")),
                    urlPagina = cursor.getString(cursor.getColumnIndexOrThrow("URL_pagina")),
                    categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
                )
                paginas.add(pagina)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return paginas
    }

    fun getAllEnlaces(): List<Enlace> {
        val enlaces = mutableListOf<Enlace>()
        val db = this.readableDatabase

        val query = """
        SELECT 
            ID_enlace,
            nombre,
            URL,
            descripcion
        FROM Enlaces_Exteriores
    """
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val enlace = Enlace(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_enlace")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    url = cursor.getString(cursor.getColumnIndexOrThrow("URL")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                )
                enlaces.add(enlace)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return enlaces
    }

    fun getPageIdByName(pageName: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT ID_pagina FROM Paginas WHERE nombre_pagina = ?", arrayOf(pageName))

        return if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow("ID_pagina"))
        } else {
            -1
        }
    }

    companion object {
        private const val DATABASE_NAME = "UGPosts.db"
        private const val DATABASE_VERSION = 1

        private const val CREATE_TABLE_USUARIOS = """
            CREATE TABLE Usuarios (
            ID_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            rol TEXT NOT NULL CHECK(rol IN ('Usuario', 'Administrador')),
            correo TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            pagina_id INTEGER,
            FOREIGN KEY (pagina_id) REFERENCES Paginas(ID_pagina)
             )
        """

        private const val CREATE_TABLE_PAGINAS = """
            CREATE TABLE Paginas (
                ID_pagina INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_pagina TEXT NOT NULL,
                URL_pagina TEXT UNIQUE NOT NULL,
                categoria TEXT NOT NULL
            )
        """

        private const val CREATE_TABLE_PUBLICACIONES = """
            CREATE TABLE Publicaciones (
                ID_publicacion INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT NOT NULL,
                contenido TEXT NOT NULL,
                fecha DATE NOT NULL,
                categoria TEXT NOT NULL,
                ID_usuario INTEGER,
                ID_pagina INTEGER,
                FOREIGN KEY (ID_usuario) REFERENCES Usuarios(ID_usuario),
                FOREIGN KEY (ID_pagina) REFERENCES Paginas(ID_pagina)
            )
        """

        private const val CREATE_TABLE_ENLACES_EXTERIORES = """
            CREATE TABLE Enlaces_Exteriores (
                ID_enlace INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                URL TEXT NOT NULL,
                descripcion TEXT
            )
        """

        private const val CREATE_TABLE_EVENTOS = """
            CREATE TABLE Eventos (
                ID_evento INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_evento TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                fecha_inicio DATE NOT NULL,
                fecha_fin DATE,
                ubicacion TEXT,
                ID_usuario INTEGER,
                FOREIGN KEY (ID_usuario) REFERENCES Usuarios(ID_usuario)
            )
        """

        private const val CREATE_INDEX_PUBLICACIONES = """
            CREATE INDEX idx_publicaciones_fecha_categoria ON Publicaciones(fecha, categoria)
        """

        private const val CREATE_INDEX_EVENTOS = """
            CREATE INDEX idx_eventos_fecha_inicio ON Eventos(fecha_inicio)
        """

        private const val INSERT_PAGINAS = """
            INSERT OR IGNORE INTO Paginas (URL_pagina, nombre_pagina, categoria)
VALUES
('https://www.facebook.com/@becascis/', 'Becas', 'Servicios'),
('https://www.facebook.com/UG.DICIS.SecAcad', 'Secretaria Academica', 'Servicios'),
('https://www.facebook.com/UdeGuanajuato', 'Universidad de Guanajuato', 'Servicios'),
('https://www.facebook.com/profile.php?id=100063692584233', 'Orientacion Psicologica', 'Servicios'),
('https://www.facebook.com/propedicis', 'Propedeutico', 'Servicios'),
('https://www.facebook.com/EnlaceDeportesDICIS', 'Enlace Deportes', 'Servicios'),
('https://www.facebook.com/ssppdicis', 'Servicio Social', 'Servicios'),
('https://www.facebook.com/Coordinacion.SSU.SSP.Vinculacion', 'CAFE', 'Servicios'),
('https://www.facebook.com/profile.php?id=100009292455750', 'Modulo Enfermeria', 'Servicios'),
('https://www.facebook.com/profile.php?id=61550929133873', 'Activación Fisica', 'Servicios'),
('https://www.facebook.com/TutoriayEvaluacionDocenteDICIS', 'Tutoria y Evaluacion Docente', 'Servicios'),
('https://www.facebook.com/ModulonutricionDicis', 'Modulo Nutrición', 'Servicios'),
('https://www.facebook.com/DH.DICIS', 'Desarrollo Humano', 'Servicios'),
('https://www.facebook.com/profile.php?id=100090651351397', 'Biblioteca', 'Servicios'),
('https://www.facebook.com/RelacionesInternacionalesCIS', 'Relaciones Internacionales', 'Servicios'),
('https://www.facebook.com/Cisvinculacion', 'Vinculación', 'Servicios'),
('https://www.facebook.com/C.IrapuatoSalamanca', 'Pagina Principal Campus Irapuato Salamanca', 'Servicios'),
('https://www.facebook.com/EstudiantinaDIS/', 'Estudiantina DICIS', 'Servicios'),
('https://www.facebook.com/CAADIFIMEE/', 'Centro de Auto Aprendizaje de Idiomas', 'Servicios'),
('https://www.facebook.com/profile.php?id=100063676352073', 'Seguro y Prevención de Accidentes Campus Irapuato-Salamanca', 'Servicios'),
('https://www.facebook.com/ImpulsoEstudiantilCIS/', 'Impulso Estudiantil UG Campus Irapuato-Salamanca', 'Servicios'),
('https://www.facebook.com/profile.php?id=100093854412634', 'Maestria Ing. Mecanica', 'Servicios'),
('https://www.facebook.com/UG.DICIS.ICE/', 'Coordinacion Comunicaciones y Electronica', 'Coordinaciones'),
('https://www.facebook.com/IngenieriaElectricaUG/', 'Coordinación Ing. Electrica', 'Coordinaciones'),
('https://www.facebook.com/Coordinacion.IngMecanica.DICIS', 'Coordinación Ing. Mecanica', 'Coordinaciones'),
('https://www.facebook.com/ugto.dicis.lisc', 'Coordinación Ing. Sistemas Computacionales', 'Coordinaciones'),
('https://www.facebook.com/profile.php?id=100070778540462', 'Coordinación Ing. Mecatronica', 'Coordinaciones'),
('https://www.facebook.com/profile.php?id=100083004801793', 'Coordinacion LIDIA', 'Coordinaciones'),
('https://www.facebook.com/profile.php?id=100035569916302', 'Coordinacion Lic. Artes Digitales', 'Coordinaciones'),
('https://www.facebook.com/profile.php?id=100064211197105', 'Coordinación Lic. Gestion Empresarial', 'Coordinaciones'),
('https://www.facebook.com/profile.php?id=100092383143509', 'Cineclub DICIS', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/SODALDICIS/', 'Mesa Directiva Sociedad de Alumnos', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/profile.php?id=100057425272766', 'Labinco', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/EsportsUG', 'Esports', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/profile.php?id=61552103349697', 'DICIS Verde', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/rasdicis/', 'RAS IEEE', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/softronics7', 'Softronics DICIS', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/DICISArtesMarciales', 'Artes Marciales', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/comite.electoral.dicis', 'Comité Electoral', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/profile.php?id=100089826648229', 'IEEE DICIS', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/profile.php?id=100092649406207', 'POLEN', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/profile.php?id=61554186936139', 'Apoyo computacional', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/ACM.DICIS', 'ACM DICIS', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/SOMIBUG/', 'Capítulo Estudiantil SOMIB', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/ESSOCIETYDICIS/', 'Engineering Students Society DICIS', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/profile.php?id=100090937660843', 'StreetWorkout DICIS', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/AUGEDicis/', 'AUGE DICIS', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/AIMDICIS/', 'AIM DICIS', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/asmeug/', 'ASME DICIS', 'Grupos Organizados de Alumnos'),
('https://www.facebook.com/UG.Photonics.Society.Student.Chapter', 'Photonics', 'Grupos Organizados de Alumnos');
        """

        private const val INSERT_ENLACES = """
            INSERT INTO Enlaces_Exteriores (URL, nombre, descripcion) VALUES
('http://www.caecis.ugto.mx', 'COORDINACIÓN ASUNTOS ESCOLARES', 'Gestión de trámites académicos y administrativos, incluyendo horarios, solicitudes y servicios estudiantiles.'),
('https://www.dae.ugto.mx/siiaealumnos/', 'Kardex de estudiante', 'Acceso a calificaciones, créditos, seguimiento académico e inscripciones.'),
('https://intraug.ugto.mx', 'Portal Intranet', 'Plataforma integral para servicio social, servicios académicos, horarios semestrales y pagos.'),
('https://www.ugto.mx', 'Página principal de la universidad', 'Información general, noticias y eventos destacados de la Universidad de Guanajuato.'),
('https://www.ugto.mx/campusirapuatosalamanca/', 'Página del campus Irapuato-Salamanca', 'Noticias, información académica y recursos del campus Irapuato-Salamanca.'),
('https://www.dae.ugto.mx/frontDAE', 'Dirección de Administración Escolar', 'Portales administrativos, académicos y servicios dirigidos al profesorado y alumnado.'),
('https://www.dae.ugto.mx/SIIAProfesores', 'Portal Docente', 'Herramientas para la administración de grupos, cursos, evaluaciones y recursos docentes.');
        """

        private const val INSERT_EVENTOS = """
   INSERT INTO Eventos (nombre_evento, descripcion, fecha_inicio, fecha_fin, ubicacion, ID_usuario)
VALUES
('Taller de Robótica Avanzada', 'Explora conceptos avanzados de robótica aplicada con proyectos prácticos. Abierto a estudiantes de ingeniería y tecnología.', '2024/12/10 09:00', '2024/12/10 18:00', 'Sala de Innovación, Edificio A', 1),
('Concurso de Programación ACM', 'Competencia de programación para resolver problemas en equipo. Participan las mejores universidades del estado.', '2024/12/11 10:00', '2024/12/11 19:00', 'Auditorio B, Edificio Central', 2),
('Feria de Proyectos DICIS', 'Exposición de los proyectos finales de los estudiantes. ¡Conoce las ideas más innovadoras de nuestros alumnos!', '2024/12/12 08:00', '2024/12/12 17:00', 'Plaza Central, Campus DICIS', 3),
('Ciclo de Conferencias: Futuro de la Ingeniería', 'Ponencias impartidas por expertos internacionales sobre tendencias y tecnologías emergentes en ingeniería.', '2024/12/13 11:00', '2024/12/13 20:00', 'Sala Magna, Edificio C', 4),
('Noche de Ciencia y Tecnología', 'Evento interactivo para toda la familia con experimentos, talleres y demostraciones de tecnología.', '2024/12/14 14:00', '2024/12/14 22:00', 'Teatro D, Campus UG', 5);
"""

        private const val INSERT_POSTS = """
  INSERT INTO Publicaciones (titulo, contenido, fecha, categoria, ID_usuario, ID_pagina)
VALUES
('Convocatoria para Becas DICIS', 'La Secretaría Académica invita a los estudiantes a postularse para las becas de apoyo. Consulta los requisitos y fechas.', '2024/12/02 10:00', 'Anuncio', 1, 1),
('Simulacro de Evacuación', 'El próximo lunes se realizará un simulacro en todo el campus para reforzar los protocolos de seguridad. Participa y sigue las instrucciones.', '2024/12/02 11:00', 'Aviso', 2, 2),
('Entrega de Reconocimientos', 'Ceremonia de entrega de reconocimientos a los estudiantes destacados del semestre. Asiste y celebra con tus compañeros.', '2024/12/02 12:00', 'Noticia', 3, 3),
('Cambio de Horarios', 'Los horarios de las asignaturas del próximo semestre ya están disponibles en la plataforma. Realiza ajustes antes del 10 de diciembre.', '2024/12/02 13:00', 'Recordatorio', 4, 4),
('Convocatoria para Ingeniería 2025', 'Abierta la convocatoria para el próximo ciclo escolar. Consulta el proceso de admisión en la página oficial.', '2024/12/02 14:00', 'Urgente', 5, 5);"""

        private const val INSERT_USERS = """
            INSERT INTO Usuarios (nombre, rol, correo, password, pagina_id)
            VALUES
            ('Karim', 'Usuario', 'karim@ugto.mx', '123', NULL),
            ('Emi', 'Usuario', 'emi@ugto.mx', '1234', NULL),
            ('Ana', 'Usuario', 'ana@ugto.mx', 'password1', NULL),
            ('Carlos', 'Administrador', 'carlos@ugto.mx', 'password2', 2),
            ('Luis', 'Administrador', 'luis@ugto.mx', '12345', 1);
        """
    }
}

