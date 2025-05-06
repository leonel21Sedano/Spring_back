<template>
  <div>
    <!-- Encabezado -->
    <h1>Gestión de Usuarios</h1>
    
    <!-- Barra de búsqueda -->
    <div>
      <input v-model="searchQuery" placeholder="Buscar por código" />
      <button @click="searchUser">Buscar</button>
    </div>
    
    <!-- Mensajes de error -->
    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
    
    <!-- Tabla de usuarios -->
    <table v-if="users.length > 0">
      <thead>
        <tr>
          <th>ID</th>
          <th>Nombre</th>
          <th>Correo</th>
          <th>Código</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(user, index) in users" :key="user.id">
          <td>{{ user.id }}</td>
          <td>{{ user.nombre }}</td>
          <td>{{ user.apellidos }}</td>
          <td>{{ user.correo }}</td>
          <td>{{ user.codigoEstudiante }}</td>
          <td>
            <button @click="editUser(user)">Editar</button>
            <button @click="deleteUserFromList(index)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
    
    <!-- Resultados de búsqueda -->
    <div v-if="showResults">
      <h2>Resultados de búsqueda</h2>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Apellidos</th>
            <th>Correo</th>
            <th>Código</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(user, index) in filteredUsers" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.nombre }}</td>
            <td>{{ user.apellidos }}</td>
            <td>{{ user.correo }}</td>
            <td>{{ user.codigoEstudiante }}</td>
            <td>
              <button @click="editUser(user)">Editar</button>
              <button @click="deleteUser(user.id)">Eliminar</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Formulario de edición -->
    <div v-if="editMode">
      <h2>{{ isNewUser ? 'Agregar Usuario' : 'Editar Usuario' }}</h2>
      <form @submit.prevent="saveEdit">
        <div>
          <label>Nombre:</label>
          <input v-model="editUserData.nombre" required />
        </div>
        <div>
          <label>Apellidos:</label>
          <input v-model="editUserData.apellidos" required />
        </div>
        <div>
          <label>Correo:</label>
          <input v-model="editUserData.correo" required />
        </div>
        <div>
          <label>Código de Estudiante:</label>
          <input v-model="editUserData.codigoEstudiante" required />
        </div>
        <div>
          <label>Contraseña:</label>
          <input v-model="editUserData.contraseña" type="password" placeholder="Nueva contraseña" />
        </div>
        <div>
          <label>Rol:</label>
          <select v-model="editUserData.rol">
            <option value="ESTUDIANTE">ESTUDIANTE</option>
            <option value="ADMIN">ADMIN</option>
            <option value="ENCARGADO">ENCARGADO</option>
          </select>
        </div>
        <button type="submit">Guardar</button>
        <button type="button" @click="cancelEdit">Cancelar</button>
      </form>
    </div>
    
    <!-- Botón para agregar nuevo usuario -->
    <div>
      <button @click="addNewUser">Agregar nuevo usuario</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      users: [],
      filteredUsers: [],
      searchQuery: '',
      editUserData: {
        nombre: '',
        apellidos: '',
        correo: '',
        codigoEstudiante: '',
        contraseña: '',
        rol: 'ESTUDIANTE',
        activo: true
      },
      isNewUser: false,
      editMode: false,
      showResults: false,
      loading: false,
      errorMessage: '',
    };
  },
  methods: {
    // Método para cargar todos los usuarios
    fetchAllUsers() {
      this.loading = true;
      this.errorMessage = '';
      
      fetch('/api/usuarios')
        .then(response => {
          if (!response.ok) {
            throw new Error('Error al cargar usuarios');
          }
          return response.json();
        })
        .then(data => {
          this.users = data;
          console.log('Usuarios cargados:', this.users.length);
        })
        .catch(error => {
          console.error('Error al cargar usuarios:', error);
          this.errorMessage = error.message || 'Error al cargar la lista de usuarios';
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // Método para buscar usuario por código
    searchUser() {
      if (!this.searchQuery.trim()) {
        this.errorMessage = 'Por favor, ingrese un código para buscar';
        return;
      }
      
      console.log('Buscando código:', this.searchQuery.trim());
      this.loading = true;
      this.errorMessage = '';
      this.showResults = false;
      
      fetch(`/api/usuarios/codigo/${this.searchQuery.trim()}`)
        .then(response => {
          if (!response.ok) {
            if (response.status === 404) {
              this.filteredUsers = [];
              this.showResults = true;
              return null;
            }
            throw new Error('Error al buscar usuario');
          }
          return response.json();
        })
        .then(data => {
          if (data) {
            this.filteredUsers = [data];
            this.showResults = true;
            console.log('Resultados encontrados:', this.filteredUsers.length);
          }
        })
        .catch(error => {
          console.error('Error al buscar usuario:', error);
          this.errorMessage = error.message || 'Error al buscar el usuario';
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // Método para guardar un usuario (crear o actualizar)
    saveEdit() {
      if (!this.editUserData.nombre.trim() || !this.editUserData.apellidos.trim() || 
          !this.editUserData.correo.trim() || !this.editUserData.codigoEstudiante.trim()) {
        this.errorMessage = 'Por favor, complete todos los campos obligatorios';
        return;
      }
      
      this.loading = true;
      this.errorMessage = '';
      
      // Determinar si es creación o actualización
      const method = this.isNewUser ? 'POST' : 'PUT';
      const url = this.isNewUser 
        ? '/api/usuarios/' 
        : `/api/usuarios/${this.editUserData.id}`;
      
      // Preparar datos para enviar al backend
      const userData = {
        nombre: this.editUserData.nombre,
        apellidos: this.editUserData.apellidos,
        correo: this.editUserData.correo,
        codigoEstudiante: this.editUserData.codigoEstudiante,
        rol: this.editUserData.rol,
        activo: true
      };
      
      // Solo incluir contraseña si se proporcionó una (nueva o modificada)
      if (this.editUserData.contraseña && this.editUserData.contraseña.trim()) {
        userData.contraseña = this.editUserData.contraseña;
      }
      
      fetch(url, {
        method: method,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
      })
        .then(response => {
          if (!response.ok) {
            return response.json().then(data => {
              throw new Error(data.mensaje || data.error || 'Error al guardar usuario');
            });
          }
          return response.json();
        })
        .then(data => {
          // Manejar respuesta - obtener el usuario guardado
          const savedUser = this.isNewUser ? (data.usuario || data) : data;
          
          if (this.isNewUser) {
            // Agregar nuevo usuario a la lista
            this.users.push(savedUser);
            console.log('Usuario creado:', savedUser);
          } else {
            // Actualizar usuario existente en la lista
            const index = this.users.findIndex(u => u.id === savedUser.id);
            if (index !== -1) {
              this.users.splice(index, 1, savedUser);
            }
            
            // Actualizar en resultados de búsqueda si es necesario
            if (this.showResults) {
              const filteredIndex = this.filteredUsers.findIndex(u => u.id === savedUser.id);
              if (filteredIndex !== -1) {
                this.filteredUsers.splice(filteredIndex, 1, savedUser);
              }
            }
            
            console.log('Usuario actualizado:', savedUser);
          }
          
          // Cerrar modo edición
          this.editMode = false;
        })
        .catch(error => {
          console.error('Error al guardar usuario:', error);
          this.errorMessage = error.message || 'Error al guardar los cambios';
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // Método para eliminar un usuario por id (para resultados de búsqueda)
    deleteUser(userId) {
      if (confirm('¿Está seguro de que desea eliminar este usuario?')) {
        this.loading = true;
        this.errorMessage = '';
        
        fetch(`/api/usuarios/${userId}`, {
          method: 'DELETE'
        })
          .then(response => {
            if (!response.ok) {
              throw new Error('Error al eliminar usuario');
            }
            return response.json();
          })
          .then(() => {
            console.log('Usuario eliminado con ID:', userId);
            
            // Eliminar de la lista principal
            const index = this.users.findIndex(u => u.id === userId);
            if (index !== -1) {
              this.users.splice(index, 1);
            }
            
            // Eliminar de los resultados de búsqueda
            if (this.showResults) {
              const filteredIndex = this.filteredUsers.findIndex(u => u.id === userId);
              if (filteredIndex !== -1) {
                this.filteredUsers.splice(filteredIndex, 1);
              }
              
              // Si no quedan resultados, ocultar sección
              if (this.filteredUsers.length === 0) {
                this.showResults = false;
              }
            }
          })
          .catch(error => {
            console.error('Error al eliminar usuario:', error);
            this.errorMessage = error.message || 'Error al eliminar el usuario';
          })
          .finally(() => {
            this.loading = false;
          });
      }
    },
    
    // Método para eliminar un usuario desde la lista principal
    deleteUserFromList(index) {
      const userId = this.users[index].id;
      this.deleteUser(userId);
    },
    
    // Método para editar un usuario
    editUser(user) {
      this.isNewUser = false;
      this.editMode = true;
      // Crear una copia del usuario para editar
      this.editUserData = { 
        id: user.id,
        nombre: user.nombre,
        apellidos: user.apellidos,
        correo: user.correo,
        codigoEstudiante: user.codigoEstudiante,
        contraseña: '', // Campo vacío para nueva contraseña
        rol: user.rol,
        activo: user.activo
      };
    },
    
    // Método para iniciar la creación de un nuevo usuario
    addNewUser() {
      this.isNewUser = true;
      this.editMode = true;
      this.editUserData = {
        nombre: '',
        apellidos: '',
        correo: '',
        codigoEstudiante: '',
        contraseña: '',
        rol: 'ESTUDIANTE',
        activo: true
      };
    },
    
    // Método para cancelar la edición
    cancelEdit() {
      this.editMode = false;
      this.editUserData = {};
    }
  },
  mounted() {
    // Cargar la lista de usuarios al montar el componente
    this.fetchAllUsers();
  }
};
</script>

<style>
.error {
  color: red;
  background-color: #ffecec;
  padding: 10px;
  margin: 10px 0;
  border-radius: 4px;
  border: 1px solid #f5c6cb;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f2f2f2;
  font-weight: bold;
}

tr:nth-child(even) {
  background-color: #f9f9f9;
}

tr:hover {
  background-color: #f1f1f1;
}

button {
  background-color: #4CAF50;
  color: white;
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 5px;
}

button:hover {
  background-color: #45a049;
}

button[type="button"] {
  background-color: #f44336;
}

button[type="button"]:hover {
  background-color: #d32f2f;
}

form {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  margin: 20px 0;
}

form div {
  margin-bottom: 15px;
}

form label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

form input, form select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

form button {
  margin-top: 10px;
}
</style>