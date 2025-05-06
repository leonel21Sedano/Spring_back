import axios from 'axios';

const API_URL = 'http://localhost:8081/api/usuarios';

// Configuración base de Axios
const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Interceptor para añadir el token a las solicitudes
apiClient.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

const UserService = {
  // Obtener todos los usuarios
  getAllUsers() {
    return apiClient.get('/')
      .then(response => {
        // Mapear respuesta del servidor al formato que espera el frontend
        return response.data.map(user => ({
          id: user.id,
          username: user.nombre, // Mapear nombre del backend al campo username del frontend
          email: user.correo,    // Mapear correo del backend al campo email del frontend
          code: user.codigoEstudiante, // Mapear codigoEstudiante del backend al campo code del frontend
          active: user.activo
        }));
      });
  },

  // Obtener usuario por ID
  getUserById(id) {
    return apiClient.get(`/${id}`)
      .then(response => {
        const user = response.data;
        return {
          id: user.id,
          username: user.nombre,
          email: user.correo,
          code: user.codigoEstudiante,
          active: user.activo
        };
      });
  },

  // Obtener usuario por código de estudiante
  getUserByCode(code) {
    return apiClient.get(`/codigo/${code}`)
      .then(response => {
        const user = response.data;
        return {
          id: user.id,
          username: user.nombre,
          email: user.correo,
          code: user.codigoEstudiante,
          active: user.activo
        };
      });
  },

  // Buscar usuarios por término (búsqueda flexible)
  searchUsers(term) {
    return apiClient.get(`/busqueda-flexible?termino=${term}`)
      .then(response => {
        return response.data.map(user => ({
          id: user.id,
          username: user.nombre,
          email: user.correo,
          code: user.codigoEstudiante,
          active: user.activo
        }));
      });
  },

  // Crear nuevo usuario
  createUser(userData) {
    // Convertir los datos del frontend al formato que espera el backend
    const backendData = {
      nombre: userData.username,
      apellidos: userData.username.split(' ').slice(1).join(' ') || '-', // Asumimos que el apellido es parte del username
      correo: userData.email,
      codigoEstudiante: userData.code,
      contraseña: userData.password || 'password123', // Contraseña por defecto si no se proporciona
      activo: true,
      rol: "ESTUDIANTE"
    };

    return apiClient.post('/', backendData)
      .then(response => {
        const savedUser = response.data.usuario || response.data; // Manejar diferentes formatos de respuesta
        return {
          id: savedUser.id,
          username: savedUser.nombre,
          email: savedUser.correo,
          code: savedUser.codigoEstudiante,
          active: savedUser.activo
        };
      });
  },

  // Actualizar usuario existente
  updateUser(id, userData) {
    // Convertir los datos del frontend al formato que espera el backend
    const backendData = {
      nombre: userData.username,
      apellidos: userData.username.split(' ').slice(1).join(' ') || '-', // Asumimos que el apellido es parte del username
      correo: userData.email,
      codigoEstudiante: userData.code
    };

    // Solo incluir contraseña si se proporciona
    if (userData.password) {
      backendData.contraseña = userData.password;
      backendData.actualizarContraseña = true;
    }

    return apiClient.put(`/${id}`, backendData)
      .then(response => {
        const updatedUser = response.data;
        return {
          id: updatedUser.id,
          username: updatedUser.nombre,
          email: updatedUser.correo,
          code: updatedUser.codigoEstudiante,
          active: updatedUser.activo
        };
      });
  },

  // Eliminar usuario
  deleteUser(id) {
    return apiClient.delete(`/${id}`)
      .then(response => response.data);
  },

  // Cambiar estado de activación
  changeUserStatus(id, active) {
    return apiClient.patch(`/${id}/estado?activo=${active}`)
      .then(response => {
        const user = response.data;
        return {
          id: user.id,
          username: user.nombre,
          email: user.correo,
          code: user.codigoEstudiante,
          active: user.activo
        };
      });
  },

  // Cambiar contraseña de usuario
  changePassword(id, currentPassword, newPassword) {
    const formData = new URLSearchParams();
    formData.append('contraseñaActual', currentPassword);
    formData.append('nuevaContraseña', newPassword);

    return apiClient.post(`/${id}/cambiarPassword`, formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    }).then(response => response.data);
  },

  // Verificar si un código de estudiante existe
  checkCodeExists(code) {
    return apiClient.get(`/verificar-codigo/${code}`)
      .then(response => response.data);
  },

  // Verificar si un correo existe
  checkEmailExists(email) {
    return apiClient.get(`/verificar-correo/${email}`)
      .then(response => response.data);
  },

  // Obtener usuarios paginados
  getPaginatedUsers(page = 0, size = 10, sortBy = 'id', direction = 'asc') {
    return apiClient.get(`/paginados?pagina=${page}&tamaño=${size}&ordenarPor=${sortBy}&direccion=${direction}`)
      .then(response => {
        const data = response.data;
        return {
          content: data.usuarios.map(user => ({
            id: user.id,
            username: user.nombre,
            email: user.correo,
            code: user.codigoEstudiante,
            active: user.activo
          })),
          totalElements: data.totalElementos,
          totalPages: data.totalPaginas,
          currentPage: data.paginaActual
        };
      });
  }
};

export default UserService;
