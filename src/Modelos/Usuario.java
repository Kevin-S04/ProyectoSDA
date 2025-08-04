package Modelos;

/**
 * Clase de modelo para representar un usuario completo de la base de datos,
 * incluyendo su ID.
 */
public class Usuario {
    private int id;
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;
    private String telefono;
    private String direccion;

    /**
     * Constructor para la clase Usuario.
     * @param id El ID del usuario.
     * @param nombre El nombre del usuario.
     * @param correo El correo electrónico del usuario.
     * @param contrasena La contraseña del usuario.
     * @param rol El rol del usuario (ej. Administrador, Ganadero).
     * @param telefono El número de teléfono del usuario.
     * @param direccion La dirección del usuario.
     */
    public Usuario(int id, String nombre, String correo, String contrasena, String rol, String telefono, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    /**
     * Obtiene el ID del usuario.
     * @return El ID del usuario.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el ID del usuario.
     * @param id El nuevo ID del usuario.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del usuario.
     * @return El nombre del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     * @param nombre El nuevo nombre del usuario.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     * @return El correo del usuario.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo electrónico del usuario.
     * @param correo El nuevo correo del usuario.
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
     * @param contrasena La nueva contraseña del usuario.
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Obtiene el rol del usuario.
     * @return El rol del usuario.
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol del usuario.
     * @param rol El nuevo rol del usuario.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Obtiene el teléfono del usuario.
     * @return El teléfono del usuario.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el teléfono del usuario.
     * @param telefono El nuevo teléfono del usuario.
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
     * @param direccion La nueva dirección del usuario.
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}