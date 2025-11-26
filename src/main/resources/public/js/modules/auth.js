import API_BASE_URL from '../config.js';

export const AuthComponent = {
    template: `
    <div class="card login-card shadow">
        <div class="card-header bg-primary text-white text-center py-4">
            <h1 class="h3 mb-0">📚 Literato</h1>
            <small>{{ exibindoCadastro ? 'Crie sua conta' : 'Entre para pontuar' }}</small>
        </div>
        <div class="card-body p-4">
            <form v-if="!exibindoCadastro" @submit.prevent="fazerLogin">
                <div class="mb-3"><label class="form-label">Usuário</label><input type="text" v-model="loginForm.nome" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Senha</label><input type="password" v-model="loginForm.senha" class="form-control" required></div>
                <button type="submit" class="btn btn-primary w-100 btn-lg">Entrar</button>
                <div class="mt-3 text-center"><a href="#" @click.prevent="alternarTela">Não tem conta? Cadastre-se</a></div>
            </form>
            <form v-else @submit.prevent="fazerCadastro">
                <div class="mb-3"><label class="form-label">Escolha um Usuário</label><input type="text" v-model="cadastroForm.nome" class="form-control" required></div>
                <div class="mb-3"><label class="form-label">Escolha uma Senha</label><input type="password" v-model="cadastroForm.senha" class="form-control" required></div>
                <button type="submit" class="btn btn-success w-100 btn-lg">Criar Conta</button>
                <div class="mt-3 text-center"><a href="#" @click.prevent="alternarTela">Já tenho conta. Entrar</a></div>
            </form>
            <div v-if="loginErro" class="alert alert-danger mt-3 text-center">{{ loginErro }}</div>
            <div v-if="cadastroSucesso" class="alert alert-success mt-3 text-center">Conta criada! Faça login.</div>
        </div>
    </div>
    `,
    data() {
        return {
            loginForm: { nome: '', senha: '' },
            cadastroForm: { nome: '', senha: '' },
            loginErro: '',
            cadastroSucesso: false,
            exibindoCadastro: false
        }
    },
    methods: {
        alternarTela() { this.exibindoCadastro = !this.exibindoCadastro; this.loginErro = ''; },
        async fazerLogin() {
            try {
                const res = await fetch(`${API_BASE_URL}/api/login`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(this.loginForm) });
                if (res.ok) {
                    const usuario = await res.json();
                    this.$emit('on-login', usuario); // Avisa o pai (app.js)
                } else { this.loginErro = "Usuário ou senha incorretos."; }
            } catch (e) { this.loginErro = "Erro de conexão."; }
        },
        async fazerCadastro() {
            try {
                const res = await fetch(`${API_BASE_URL}/api/usuarios`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(this.cadastroForm) });
                if (res.ok) {
                    this.cadastroSucesso = true; this.exibindoCadastro = false;
                    this.loginForm.nome = this.cadastroForm.nome; this.cadastroForm.nome = ''; this.cadastroForm.senha = '';
                } else { this.loginErro = "Erro ao criar conta."; }
            } catch (e) { this.loginErro = "Erro de conexão."; }
        }
    }
};
