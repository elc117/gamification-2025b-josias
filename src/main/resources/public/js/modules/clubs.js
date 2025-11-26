import API_BASE_URL from '../config.js';

export const ClubsComponent = {
    props: ['usuario'],
    template: `
    <div>
        <!-- Lista de Clubes -->
        <div v-if="!clubeSelecionado">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h3>Explorar Clubes</h3>
                <button class="btn btn-primary" @click="exibindoFormClube = !exibindoFormClube">{{ exibindoFormClube ? 'Cancelar' : '+ Criar Clube' }}</button>
            </div>
            <div v-if="exibindoFormClube" class="card mb-4 border-primary">
                <div class="card-body">
                    <h5>Novo Clube</h5>
                    <form @submit.prevent="criarClube">
                        <div class="row">
                            <div class="col-md-4 mb-2"><input type="text" v-model="novoClube.nome" class="form-control" placeholder="Nome do Clube" required></div>
                            <div class="col-md-4 mb-2"><input type="text" v-model="novoClube.descricao" class="form-control" placeholder="Descrição curta" required></div>
                            <div class="col-md-2 mb-2 d-flex align-items-center">
                                <div class="form-check form-switch">
                                    <input class="form-check-input" type="checkbox" id="publicoCheck" v-model="novoClube.publico">
                                    <label class="form-check-label" for="publicoCheck">{{ novoClube.publico ? 'Público' : 'Privado' }}</label>
                                </div>
                            </div>
                            <div class="col-md-2 mb-2"><button type="submit" class="btn btn-success w-100">Salvar</button></div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="row">
                <div class="col-md-4 mb-4" v-for="clube in clubes" :key="clube.id">
                    <div class="card h-100 shadow-sm" style="cursor: pointer;" @click="verDetalhesClube(clube)">
                        <img :src="clube.foto" class="card-img-top" style="height: 150px; object-fit: cover;">
                        <div class="card-body">
                            <h5 class="card-title">
                                {{ clube.nome }}
                                <span v-if="!clube.publico" title="Clube Privado">🔒</span>
                            </h5>
                            <p class="card-text text-muted small">{{ clube.descricao }}</p>
                            <button v-if="isMembro(clube.id)" class="btn btn-success w-100" disabled>
                                ✅ Membro
                            </button>
                            <button v-else-if="isSolicitacaoPendente(clube.id)" class="btn btn-secondary w-100" disabled>
                                ⏳ Entrada Solicitada
                            </button>
                            <button v-else @click.stop="entrarClube(clube)" class="btn w-100" :class="clube.publico ? 'btn-outline-primary' : 'btn-outline-warning'">
                                {{ clube.publico ? 'Participar' : 'Solicitar Entrada' }}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Detalhes do Clube -->
        <div v-else>
            <button class="btn btn-secondary mb-3" @click="clubeSelecionado = null">← Voltar para Clubes</button>
            
            <div class="card mb-4 border-0 shadow-sm overflow-hidden">
                <!-- Capa do Clube -->
                <div v-if="clubeSelecionado.capa" :style="{ backgroundImage: 'url(' + clubeSelecionado.capa + ')', height: '200px', backgroundSize: 'cover', backgroundPosition: 'center' }"></div>
                <div v-else class="bg-secondary" style="height: 100px;"></div>

                <div class="card-body text-center position-relative" style="margin-top: -50px;">
                    <img :src="clubeSelecionado.foto" class="rounded-circle mb-3 border border-4 border-white shadow" width="100" height="100" style="object-fit: cover; background: white;">
                    <h2>
                        {{ clubeSelecionado.nome }}
                        <span v-if="!clubeSelecionado.publico" title="Clube Privado">🔒</span>
                    </h2>
                    <p class="text-muted">{{ clubeSelecionado.descricao }}</p>
                    
                    <div class="d-flex justify-content-center gap-2 mb-3">
                        <button v-if="isMembro(clubeSelecionado.id)" class="btn btn-success" disabled>
                            ✅ Membro
                        </button>
                        <button v-else-if="isSolicitacaoPendente(clubeSelecionado.id)" class="btn btn-secondary" disabled>
                            ⏳ Entrada Solicitada
                        </button>
                        <button v-else @click="entrarClube(clubeSelecionado)" class="btn btn-primary">
                            {{ clubeSelecionado.publico ? 'Participar' : 'Solicitar Entrada' }}
                        </button>
                        
                        <!-- Botão de Convidar (Copiar Link) -->
                        <button @click="copiarLinkConvite" class="btn btn-outline-primary">
                            🔗 Convidar
                        </button>
                    </div>

                    <!-- Ações de Gestão -->
                    <div class="mt-3 d-flex justify-content-center gap-2 flex-wrap">
                        <button v-if="podeEditar" @click="abrirEdicao" class="btn btn-outline-dark btn-sm">
                            ✏️ Editar Clube
                        </button>
                        <button v-if="clubeSelecionado.donoId === usuario.id" @click="excluirClube(clubeSelecionado)" class="btn btn-danger btn-sm">
                            🗑️ Excluir Clube
                        </button>
                        <button v-if="isMembro(clubeSelecionado.id) && clubeSelecionado.donoId !== usuario.id" @click="sairDoClube(clubeSelecionado)" class="btn btn-outline-danger btn-sm">
                            🚪 Sair do Clube
                        </button>
                    </div>
                </div>
            </div>

            <!-- Formulário de Edição (Modal Simples) -->
            <div v-if="editandoClube" class="card mb-4 border-primary">
                <div class="card-body">
                    <h5>Editar Clube</h5>
                    <form @submit.prevent="salvarEdicaoClube">
                        <div class="mb-2"><label>Nome</label><input type="text" v-model="clubeEdicao.nome" class="form-control" required></div>
                        <div class="mb-2"><label>Descrição</label><input type="text" v-model="clubeEdicao.descricao" class="form-control" required></div>
                        <div class="mb-2"><label>URL da Foto (Ícone)</label><input type="text" v-model="clubeEdicao.foto" class="form-control"></div>
                        <div class="mb-2"><label>URL da Capa (Banner)</label><input type="text" v-model="clubeEdicao.capa" class="form-control"></div>
                        <div class="form-check form-switch mb-3">
                            <input class="form-check-input" type="checkbox" id="publicoEdit" v-model="clubeEdicao.publico">
                            <label class="form-check-label" for="publicoEdit">{{ clubeEdicao.publico ? 'Público' : 'Privado' }}</label>
                        </div>
                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-success">Salvar Alterações</button>
                            <button type="button" @click="editandoClube = false" class="btn btn-secondary">Cancelar</button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Área de Gestão (Apenas Dono) -->
            <div v-if="clubeSelecionado.donoId === usuario.id && solicitacoes.length > 0" class="card mb-4 border-warning">
                <div class="card-header bg-warning text-dark fw-bold">Solicitações Pendentes</div>
                <ul class="list-group list-group-flush">
                    <li v-for="sol in solicitacoes" :key="sol.id" class="list-group-item d-flex justify-content-between align-items-center">
                        <div class="d-flex align-items-center">
                            <img :src="sol.fotoUsuario" class="rounded-circle me-2" width="40" height="40">
                            <span><strong>{{ sol.nomeUsuario }}</strong> quer entrar</span>
                        </div>
                        <div>
                            <button @click="responderSolicitacao(sol.id, true)" class="btn btn-sm btn-success me-1">Aceitar</button>
                            <button @click="responderSolicitacao(sol.id, false)" class="btn btn-sm btn-danger">Rejeitar</button>
                        </div>
                    </li>
                </ul>
            </div>

            <h4 class="mb-3">Membros do Clube</h4>
            <div class="list-group">
                <div v-for="membro in membrosClube" :key="membro.id" 
                     class="list-group-item list-group-item-action d-flex align-items-center justify-content-between"
                     @click="$emit('ver-perfil', membro.id)" style="cursor: pointer;">
                    <div class="d-flex align-items-center">
                        <img :src="membro.foto" class="rounded-circle me-3" width="50" height="50" style="object-fit: cover;">
                        <div>
                            <h6 class="mb-0 fw-bold text-primary">
                                {{ membro.nome }}
                                <span v-if="membro.cargo === 'DONO'" class="badge bg-warning text-dark ms-1">Dono</span>
                                <span v-if="membro.cargo === 'MODERADOR'" class="badge bg-success text-white ms-1">Moderador</span>
                            </h6>
                            <small class="text-muted">Nível {{ Math.floor(membro.pontos / 100) + 1 }} • {{ membro.streak }} dias de streak</small>
                        </div>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <span class="badge bg-info text-dark rounded-pill">{{ membro.pontos }} pts</span>
                        
                        <button v-if="podeExpulsar(membro)" @click.stop="expulsarMembro(membro)" class="btn btn-sm btn-outline-danger" title="Expulsar Membro">🚫</button>

                        <!-- Gestão de Cargos (Apenas Dono) -->
                        <div v-if="clubeSelecionado.donoId === usuario.id && membro.id !== usuario.id" class="dropdown" @click.stop>
                            <button class="btn btn-sm btn-light border" type="button" data-bs-toggle="dropdown">⚙️</button>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><h6 class="dropdown-header">Alterar Cargo</h6></li>
                                <li><a class="dropdown-item" href="#" @click.prevent="alterarCargo(membro, 'MEMBRO')">Membro</a></li>
                                <li><a class="dropdown-item" href="#" @click.prevent="alterarCargo(membro, 'MODERADOR')">Moderador</a></li>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item text-danger" href="#" @click.prevent="alterarCargo(membro, 'DONO')">👑 Passar Posse (Dono)</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div v-if="membrosClube.length === 0" class="text-center p-4 text-muted">
                    Nenhum membro encontrado neste clube ainda. Seja o primeiro!
                </div>
            </div>
        </div>
    </div>
    `,
    data() { return { 
        clubes: [], 
        novoClube: { nome: '', descricao: '', foto: '', publico: true }, 
        exibindoFormClube: false,
        clubeSelecionado: null,
        membrosClube: [],
        solicitacoes: [],
        meusClubesIds: [],
        minhasSolicitacoesIds: [],
        editandoClube: false,
        clubeEdicao: {}
    } },
    computed: {
        meuCargo() {
            if (!this.clubeSelecionado || !this.usuario) return null;
            const membro = this.membrosClube.find(m => m.id === this.usuario.id);
            return membro ? membro.cargo : null;
        },
        podeEditar() {
            return this.meuCargo === 'DONO' || this.meuCargo === 'MODERADOR';
        }
    },
    mounted() { 
        this.carregarClubes(); 
        this.carregarMeusClubes();
    },
    methods: {
        abrirEdicao() {
            this.clubeEdicao = { ...this.clubeSelecionado };
            this.editandoClube = true;
        },
        async salvarEdicaoClube() {
            try {
                const res = await fetch(`${API_BASE_URL}/api/clubes/${this.clubeSelecionado.id}?usuarioId=${this.usuario.id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(this.clubeEdicao)
                });
                if (res.ok) {
                    alert("Clube atualizado!");
                    this.editandoClube = false;
                    this.clubeSelecionado = { ...this.clubeSelecionado, ...this.clubeEdicao };
                    this.carregarClubes();
                } else {
                    alert("Erro ao atualizar clube.");
                }
            } catch (e) { alert("Erro de conexão."); }
        },
        podeExpulsar(alvo) {
            if (!this.meuCargo) return false;
            if (this.usuario.id === alvo.id) return false; 
            if (this.meuCargo === 'DONO') return true; 
            if (this.meuCargo === 'MODERADOR' && alvo.cargo === 'MEMBRO') return true; 
            return false;
        },
        async expulsarMembro(membro) {
            if (!confirm(`Tem certeza que deseja expulsar ${membro.nome} do clube?`)) return;
            try {
                const res = await fetch(`${API_BASE_URL}/api/clubes/${this.clubeSelecionado.id}/membros/${membro.id}?usuarioId=${this.usuario.id}`, { method: 'DELETE' });
                if (res.ok) {
                    alert(`${membro.nome} foi removido do clube.`);
                    this.carregarMembros(this.clubeSelecionado.id);
                } else {
                    const msg = await res.text();
                    alert("Erro: " + msg);
                }
            } catch (e) { alert("Erro de conexão."); }
        },
        copiarLinkConvite() {
            const url = window.location.origin + "/?clube=" + this.clubeSelecionado.id;
            navigator.clipboard.writeText(url).then(() => alert("Link copiado!"));
        },
        async carregarMeusClubes() {
            try {
                const res = await fetch(`${API_BASE_URL}/api/perfil/${this.usuario.id}`);
                if (res.ok) {
                    const perfil = await res.json();
                    this.meusClubesIds = perfil.clubes.map(c => c.id);
                    this.minhasSolicitacoesIds = perfil.solicitacoesPendentes || [];
                }
            } catch (e) { console.error(e); }
        },
        isMembro(clubeId) {
            return this.meusClubesIds.includes(clubeId);
        },
        isSolicitacaoPendente(clubeId) {
            return this.minhasSolicitacoesIds.includes(clubeId);
        },
        async carregarClubes() { try { const res = await fetch(`${API_BASE_URL}/api/clubes`); this.clubes = await res.json(); } catch (e) { console.error(e); } },
        async criarClube() {
            if(!this.novoClube.foto) this.novoClube.foto = `https://ui-avatars.com/api/?name=${this.novoClube.nome}&background=random`;
            try {
                const res = await fetch(`${API_BASE_URL}/api/clubes`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ ...this.novoClube, donoId: this.usuario.id }) });
                if (res.ok) { alert("Clube criado!"); this.exibindoFormClube = false; this.novoClube = { nome: '', descricao: '', foto: '', publico: true }; this.carregarClubes(); }
            } catch (e) { alert("Erro de conexão."); }
        },
        async entrarClube(clube) {
            try {
                const res = await fetch(`${API_BASE_URL}/api/clubes/${clube.id}/entrar?usuarioId=${this.usuario.id}`, { method: 'POST' });
                const msg = await res.text();
                
                if (res.status === 200) {
                    alert("Você entrou no clube!");
                    this.carregarMeusClubes(); // Atualiza lista de membros
                    if (this.clubeSelecionado && this.clubeSelecionado.id === clube.id) {
                        this.carregarMembros(clube.id);
                    }
                } else if (res.status === 202) {
                    alert("Solicitação enviada! Aguarde a aprovação do dono.");
                } else {
                    alert(msg);
                }
            } catch (e) { alert("Erro de conexão."); }
        },
        async verDetalhesClube(clube) {
            this.clubeSelecionado = clube;
            this.carregarMembros(clube.id);
            if (clube.donoId === this.usuario.id) {
                this.carregarSolicitacoes(clube.id);
            }
        },
        async carregarMembros(clubeId) {
            try {
                const res = await fetch(`${API_BASE_URL}/api/clubes/${clubeId}/membros`);
                this.membrosClube = await res.json();
            } catch (e) { console.error(e); }
        },
        async carregarSolicitacoes(clubeId) {
            try {
                const res = await fetch(`${API_BASE_URL}/api/clubes/${clubeId}/solicitacoes?usuarioId=${this.usuario.id}`);
                this.solicitacoes = await res.json();
            } catch (e) { console.error(e); }
        },
        async responderSolicitacao(solicitacaoId, aceitar) {
            try {
                const res = await fetch(`${API_BASE_URL}/api/solicitacoes/${solicitacaoId}/responder?aceitar=${aceitar}&usuarioId=${this.usuario.id}`, { method: 'POST' });
                if (res.ok) {
                    this.carregarSolicitacoes(this.clubeSelecionado.id);
                    this.carregarMembros(this.clubeSelecionado.id);
                } else {
                    alert("Erro ao responder solicitação.");
                }
            } catch (e) { alert("Erro de conexão."); }
        },
        async excluirClube(clube) {
            if (!confirm("Tem certeza que deseja excluir este clube?")) return;
            try {
                const res = await fetch(`${API_BASE_URL}/api/clubes/${clube.id}?usuarioId=${this.usuario.id}`, { method: 'DELETE' });
                if (res.ok) {
                    alert("Clube excluído.");
                    this.clubeSelecionado = null;
                    this.carregarClubes();
                } else {
                    alert("Erro ao excluir clube.");
                }
            } catch (e) { alert("Erro de conexão."); }
        },
        async sairDoClube(clube) {
            if (!confirm("Tem certeza que deseja sair do clube?")) return;
            try {
                const res = await fetch(`${API_BASE_URL}/api/clubes/${clube.id}/sair?usuarioId=${this.usuario.id}`, { method: 'DELETE' });
                if (res.ok) {
                    alert("Você saiu do clube.");
                    this.clubeSelecionado = null;
                    this.carregarClubes();
                    this.carregarMeusClubes();
                } else {
                    alert("Erro ao sair do clube.");
                }
            } catch (e) { alert("Erro de conexão."); }
        },
        async alterarCargo(membro, novoCargo) {
            if (!confirm(`Alterar cargo de ${membro.nome} para ${novoCargo}?`)) return;
            try {
                const res = await fetch(`${API_BASE_URL}/api/clubes/${this.clubeSelecionado.id}/membros/${membro.id}/cargo?usuarioId=${this.usuario.id}&novoCargo=${novoCargo}`, { method: 'PUT' });
                if (res.ok) {
                    alert("Cargo alterado!");
                    this.carregarMembros(this.clubeSelecionado.id);
                } else {
                    const msg = await res.text();
                    alert("Erro: " + msg);
                }
            } catch (e) { alert("Erro de conexão."); }
        }
    }
};
