import API_BASE_URL from '../config.js';

export const ProfileModalComponent = {
    props: ['usuario'],
    template: `
    <div class="modal-overlay">
        <div class="modal-content-custom shadow">
            <h4>✏️ Editar Perfil</h4>
            <form @submit.prevent="salvarPerfil">
                <div class="mb-3">
                    <label class="form-label">URL da Foto</label>
                    <input type="text" v-model="formEdicao.foto" class="form-control">
                </div>
                <div class="mb-3">
                    <label class="form-label">Bio</label>
                    <textarea v-model="formEdicao.bio" class="form-control" rows="3"></textarea>
                </div>
                <div class="d-flex justify-content-end gap-2">
                    <button type="button" @click="$emit('close')" class="btn btn-secondary">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Salvar</button>
                </div>
            </form>
        </div>
    </div>
    `,
    data() { return { formEdicao: { bio: '', foto: '' } } },
    mounted() { this.formEdicao.bio = this.usuario.bio; this.formEdicao.foto = this.usuario.foto; },
    methods: {
        async salvarPerfil() {
            try {
                const payload = { ...this.usuario, bio: this.formEdicao.bio, foto: this.formEdicao.foto };
                const res = await fetch(`${API_BASE_URL}/api/usuarios/${this.usuario.id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
                if (res.ok) { alert("Perfil atualizado!"); this.$emit('update'); this.$emit('close'); }
            } catch (e) { alert("Erro de conexão."); }
        }
    }
};

export const PublicProfileComponent = {
    props: ['usuarioId', 'usuarioLogado'],
    template: `
    <div>
        <div class="container mt-4" v-if="!loading && perfil">
            <div class="card shadow mb-4">
                <div class="card-body text-center">
                    <img :src="perfil.usuario.foto" class="rounded-circle mb-3 border border-4 border-white shadow" width="120" height="120" style="object-fit: cover;">
                    <h2 class="card-title">{{ perfil.usuario.nome }}</h2>
                    <p class="text-muted">{{ perfil.usuario.bio || 'Sem biografia.' }}</p>
                    
                    <div class="d-flex justify-content-center gap-4 mt-3">
                        <div class="text-center">
                            <h5 class="fw-bold text-primary">{{ perfil.usuario.pontos }}</h5>
                            <small class="text-uppercase text-muted">Pontos</small>
                        </div>
                        <div class="text-center">
                            <h5 class="fw-bold text-danger">🔥 {{ perfil.usuario.streak }}</h5>
                            <small class="text-uppercase text-muted">Dias Seguidos</small>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-4 mb-3">
                    <div class="card shadow-sm h-100">
                        <div class="card-header bg-warning text-dark">
                            <h5 class="mb-0">🏆 Conquistas</h5>
                        </div>
                        <div class="card-body p-0">
                            <ul class="list-group list-group-flush">
                                <li v-for="c in perfil.conquistas" :key="c.nome" class="list-group-item" :class="{ 'bg-light text-muted': !c.alcancada }">
                                    <div class="d-flex justify-content-between align-items-center mb-1">
                                        <div class="fw-bold">
                                            <span v-if="c.alcancada">🏅</span>
                                            <span v-else>🔒</span>
                                            {{ c.nome }}
                                        </div>
                                        <span class="badge rounded-pill" :class="c.alcancada ? 'bg-success' : 'bg-secondary'">
                                            +{{ c.bonus }} pts
                                        </span>
                                    </div>
                                    <small class="d-block mb-1">{{ c.descricao }}</small>
                                    <div class="progress" style="height: 10px;">
                                        <div class="progress-bar" role="progressbar" 
                                            :style="{ width: c.porcentagem + '%' }" 
                                            :class="c.alcancada ? 'bg-success' : 'bg-warning'"
                                            :aria-valuenow="c.porcentagem" aria-valuemin="0" aria-valuemax="100">
                                        </div>
                                    </div>
                                    <small class="text-muted" style="font-size: 0.75rem;">
                                        {{ c.progressoAtual }} / {{ c.progressoAlvo }}
                                    </small>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div class="col-md-4 mb-3">
                    <div class="card shadow-sm h-100">
                        <div class="card-header bg-primary text-white">
                            <h5 class="mb-0">👥 Clubes</h5>
                        </div>
                        <div class="card-body p-0">
                            <div v-if="!perfil.clubes || perfil.clubes.length === 0" class="p-3 text-muted">Não participa de nenhum clube.</div>
                            <ul class="list-group list-group-flush">
                                <li v-for="clube in perfil.clubes" :key="clube.id" class="list-group-item d-flex align-items-center">
                                    <img :src="clube.foto" class="rounded-circle me-2" width="30" height="30" style="object-fit: cover;">
                                    <span>{{ clube.nome }}</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div class="col-md-4 mb-3">
                    <div class="card shadow-sm h-100">
                        <div class="card-header bg-info text-white">
                            <h5 class="mb-0">📅 Últimas Atividades</h5>
                        </div>
                        <div class="card-body p-0">
                            <div v-if="perfil.historicoRecente.length === 0" class="p-3 text-muted">Nenhuma atividade recente.</div>
                            <ul class="list-group list-group-flush">
                                <li v-for="h in perfil.historicoRecente" :key="h.id" class="list-group-item">
                                    <div><strong>{{ h.livroTitulo }}</strong></div>
                                    <small class="text-muted">Leu {{ h.paginasLidas }} páginas em {{ h.dataLeitura }}</small>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Nova Seção: Estante -->
            <div class="row mt-3">
                <div class="col-12">
                    <div class="card shadow-sm">
                        <div class="card-header bg-success text-white">
                            <h5 class="mb-0">📚 Estante de Livros</h5>
                        </div>
                        <div class="card-body">
                            <div v-if="!perfil.estante || perfil.estante.length === 0" class="text-muted text-center p-4">
                                A estante está vazia.
                            </div>
                            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4" v-else>
                                <div class="col" v-for="livro in perfil.estante" :key="livro.id">
                                    <div class="card h-100 border-0 shadow-sm bg-light">
                                        <div class="card-body">
                                            <div class="d-flex justify-content-between align-items-start mb-2">
                                                <h6 class="card-title fw-bold mb-0 text-truncate" :title="livro.titulo">{{ livro.titulo }}</h6>
                                                <span class="badge" :class="livro.status === 'CONCLUIDO' ? 'bg-success' : 'bg-primary'">
                                                    {{ livro.status }}
                                                </span>
                                            </div>
                                            <p class="card-text small text-muted mb-2">{{ livro.autor }}</p>
                                            
                                            <div class="progress" style="height: 8px;">
                                                <div class="progress-bar bg-success" role="progressbar" 
                                                    :style="{ width: (livro.paginasLidas / livro.paginasTotal * 100) + '%' }">
                                                </div>
                                            </div>
                                            <div class="d-flex justify-content-between mt-1">
                                                <small class="text-muted">{{ livro.paginasLidas }} / {{ livro.paginasTotal }} pág</small>
                                                <small class="fw-bold">{{ Math.round(livro.paginasLidas / livro.paginasTotal * 100) }}%</small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="text-center mt-3">
                <button @click="$emit('voltar')" class="btn btn-outline-secondary">⬅️ Voltar</button>
            </div>
        </div>
        <div v-if="loading" class="text-center mt-5">
            <div class="spinner-border text-primary" role="status"></div>
            <p>Carregando perfil...</p>
        </div>
    </div>
    `,
    data() { return { perfil: null, loading: true } },
    mounted() { this.carregarPerfil(); },
    watch: { usuarioId() { this.carregarPerfil(); } },
    methods: {
        async carregarPerfil() {
            this.loading = true;
            this.perfil = null;
            try {
                const res = await fetch(`${API_BASE_URL}/api/perfil/${this.usuarioId}`);
                if (res.ok) {
                    this.perfil = await res.json();
                } else {
                    alert("Erro ao carregar perfil.");
                    this.$emit('voltar');
                }
            } catch (e) { console.error(e); }
            finally {
                this.loading = false;
            }
        }
    }
};

