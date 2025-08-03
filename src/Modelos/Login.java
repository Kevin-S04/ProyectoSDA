package Modelos;

/**
 * Clase de modelo para representar los datos de un usuario.
 * Se alinea con la estructura de la tabla 'usuarios' de la base de datos.
 */
public class Login {
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;
    private String telefono;
    private String direccion;

    /**
     * Obtiene el nombre del usuario.
     * @return El nombre completo del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     * @param nombre El nombre a establecer.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     * @return El correo electrónico del usuario.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo electrónico del usuario.
     * @param correo El correo electrónico a establecer.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Obtiene la contraseña del usuario.
     * @return La contraseña del usuario.
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña del usuario.
     * @param contrasena La contraseña a establecer.
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Obtiene el rol del usuario.
     * @return El rol del usuario (Ganadero, Transportista, etc.).
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol del usuario.
     * @param rol El rol a establecer.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Obtiene el número de teléfono del usuario.
     * @return El número de teléfono del usuario.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el número de teléfono del usuario.
     * @param telefono El teléfono a establecer.
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene la dirección del usuario.
     * @return La dirección del usuario.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección del usuario.
     * @param direccion La dirección a establecer.
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}