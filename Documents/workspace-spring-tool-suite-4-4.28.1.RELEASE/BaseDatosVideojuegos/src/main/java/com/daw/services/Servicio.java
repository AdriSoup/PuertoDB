package com.daw.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.daw.controllers.Autor;
import com.daw.controllers.Categoria;
import com.daw.controllers.Editorial;
import com.daw.controllers.Libro;

@Service
public class Servicio {

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<Autor> buscarAutores(String nombreBusqueda) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_datos_biblioteca", "usuario","usuario");
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM autor WHERE nombre LIKE '%" + nombreBusqueda + "%'");
		List<Autor> autores = new ArrayList<Autor>();
		while (rs.next()) {
			Long id = rs.getLong("id");
			String nombre = rs.getString("nombre");
			String direccion = rs.getString("direccion");
			String dni = rs.getString("dni");
			Date fecha = rs.getDate("fecha_nacimiento");
			Autor autor = new Autor(id, nombre, direccion, dni, fecha);
			autores.add(autor);
		}
		rs.close();
		stmt.close();
		conn.close();
		return autores;
	}
	
	public List<Libro> buscarLibros(String titulo, String descripcion, Boolean favorite) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_datos_biblioteca", "usuario","usuario");
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM libro l JOIN info_adicional ia ON l.id = ia.libro WHERE l.titulo LIKE '%" + titulo + "%' AND l.descripcion LIKE '%" + descripcion + "%' AND l.favorite = " + favorite );
		List<Libro> libros = new ArrayList<Libro>();
		while (rs.next()) {
			Long id = rs.getLong("id");
			String tit = rs.getString("titulo");
			String desc = rs.getString("descripcion");
			Boolean fav = rs.getBoolean("favorite");
			String isbn = rs.getString("isbn");
			String idioma = rs.getString("idioma");
			Date fechaPublicacion = rs.getDate("fecha_publicacion");
			Libro libro = new Libro(id, desc, tit, fav, fechaPublicacion, isbn, idioma);
			libros.add(libro);
		}
		rs.close();
		stmt.close();
		conn.close();
		return libros;
	}
	
	public Autor crearAutor(String nombre, String dni, String direccion, Date fechaNacimiento) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_datos_biblioteca", "usuario","usuario");
		
		String sql = "INSERT INTO autor (nombre, dni, direccion, fecha_nacimiento) VALUES(?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, nombre);
		ps.setString(2, dni);
		ps.setString(3, direccion);
		ps.setDate(4, fechaNacimiento);

		int respuesta = ps.executeUpdate();
		if(respuesta==1) {
			System.out.println("Insercion correcta.");
		} else {
			throw new SQLException("No se ha podido insertar el nuevo autor.");
		}
		ps.close();
		conn.close();
		return new Autor(null, nombre, dni, direccion, fechaNacimiento);
	}
	
	public Libro crearLibro(String titulo,
			String descripcion,			
			Boolean favorite,
			Date fechaPublicacion,
			String isbn,
			String idioma,
			Long idCategoria,
			Long idEditorial) throws SQLException {

		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_datos_biblioteca", "usuario","usuario");
		conn.setAutoCommit(false);
		
		try {
			String sql = "INSERT INTO libro (titulo, descripcion, favorite, categoria_id, editorial) VALUES(?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, titulo);
			ps.setString(2, descripcion);
			ps.setBoolean(3, favorite);
			ps.setLong(4, idCategoria);
			ps.setLong(5, idEditorial);
	
			int respuesta = ps.executeUpdate();
			if(respuesta==1) {
				System.out.println("Insercion correcta.");
			} else {
				throw new SQLException("No se ha podido insertar el nuevo autor.");
			}
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM libro WHERE titulo = '"+titulo+"' AND descripcion='"+descripcion+"' AND favorite = " + favorite);
			Long id_libro = null;
			while (rs.next()) {
				id_libro = rs.getLong("id");
				break;
			}
			rs.close();
			stmt.close();
			
			sql = "INSERT INTO info_adicional (fecha_publicacion, idioma, isbn, libro) VALUES(?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setDate(1, fechaPublicacion);
			ps.setString(2, idioma);
			ps.setString(3, isbn);
			ps.setLong(4, id_libro);
	
			respuesta = ps.executeUpdate();
			if(respuesta==1) {
				System.out.println("Insercion correcta.");
			} else {
				throw new SQLException("No se ha podido insertar la nueva información adicional.");
			}
			
			conn.commit();
			
			ps.close();
			conn.close();
			
			return new Libro(id_libro, descripcion, titulo, favorite, fechaPublicacion, isbn, idioma);
			
		} catch(SQLException ex) {
			conn.rollback();
			conn.close();
			throw ex;
		}
		
	}	
	
	public Autor modificarAutor(Long idAutor, String nombre, String dni, String direccion, Date fechaNacimiento) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_datos_biblioteca", "usuario","usuario");
		
		String sql = "UPDATE autor SET nombre=?, dni=?, direccion=?, fecha_nacimiento=? WHERE id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, nombre);
		ps.setString(2, dni);
		ps.setString(3, direccion);
		ps.setDate(4, fechaNacimiento);
		ps.setLong(5, idAutor);

		int respuesta = ps.executeUpdate();
		if(respuesta==1) {
			System.out.println("Actualización correcta.");
		} else {
			throw new SQLException("No se ha podido actualizar el autor.");
		}
		ps.close();
		conn.close();
		return new Autor(idAutor, nombre, dni, direccion, fechaNacimiento);
	}



public void eliminarAutor(Long idAutor) throws SQLException {
	Connection conn = null;
	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_datos_biblioteca", "usuario","usuario");
	conn.setAutoCommit(false);
	
	Statement stmt = conn.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM autor_libro WHERE autor_id=" + idAutor);
	while (rs.next());
	
	String sql = "DELETE FROM libro WHERE id_autor=?";
	PreparedStatement ps = conn.prepareStatement(sql);
	ps.setLong (1, idAutor);
	
	
	int respuesta = ps.executeUpdate();
	if(respuesta==1) {
		System.out.println("Actualización correcta.");
	} else {
		throw new SQLException("No se ha podido actualizar el autor.");
	}
	sql = "DELETE FROM autor WHERE id=?";
	ps = conn.prepareStatement(sql);
	ps.setLong (1, idAutor);
	respuesta = ps.executeUpdate();
	if(respuesta==1) {
		System.out.println();
	}
	
	
	ps.close();
	conn.close();

}
public List<Editorial> buscarEditoriales()throws SQLException{
	Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_datos_biblioteca","usuario", "usuario");
	conn.setAutoCommit(false);
	Statement stmt = conn.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM editorial");
	List<Editorial> editoriales = new ArrayList<>();
	while (rs.next()) {
		Editorial editorial = new Editorial (rs.getLong("id"), rs.getString("nombre"));
		editoriales.add(editorial);
	}
	rs.close();
	stmt.close();
	conn.close();
	return editoriales;
}
public List<Categoria> buscarCategorias()throws SQLException{
	Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/base_datos_biblioteca","usuario", "usuario");
	conn.setAutoCommit(false);
	Statement stmt = conn.createStatement();
	ResultSet rs = stmt.executeQuery("SELECT * FROM categoria");
	List<Categoria> categorias = new ArrayList<>();
	while (rs.next()) {
		Categoria categoria = new Categoria (rs.getLong("id"), rs.getString("nombre"));
		categorias.add(categoria);
	}
	rs.close();
	stmt.close();
	conn.close();
	return categorias;

}
}
