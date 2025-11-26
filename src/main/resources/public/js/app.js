import { AuthComponent } from './modules/auth.js';
import { ReadingComponent } from './modules/reading.js?v=2';
import { ClubsComponent } from './modules/clubs.js?v=2';
import { ProfileModalComponent, PublicProfileComponent } from './modules/profile.js?v=2';
import API_BASE_URL from './config.js';

const { createApp } = Vue

const app = createApp({
    data() { return { usuarioLogado: null, abaAtual: 'home', exibindoEdicao: false, perfilIdSelecionado: null } },
    methods: {
        setUsuario(usuario) { this.usuarioLogado = usuario; },
        logout() { this.usuarioLogado = null; this.abaAtual = 'home'; },
        async atualizarDadosUsuario() {
            try {
                const res = await fetch(`${API_BASE_URL}/api/usuarios/${this.usuarioLogado.id}`);
                if (res.ok) this.usuarioLogado = await res.json();
            } catch (e) { console.error(e); }
        },
        abrirPerfil(id) {
            this.perfilIdSelecionado = id;
            this.abaAtual = 'perfil';
        }
    }
});

// Registrando os componentes
app.component('auth-component', AuthComponent);
app.component('reading-component', ReadingComponent);
app.component('clubs-component', ClubsComponent);
app.component('profile-modal', ProfileModalComponent);
app.component('public-profile-component', PublicProfileComponent);

app.mount('#app');
