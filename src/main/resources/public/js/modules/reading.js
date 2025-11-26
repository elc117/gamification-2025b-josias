import API_BASE_URL from '../config.js';

export const ReadingComponent = {
    props: ['usuario'],
    emits: ['atualizar-usuario', 'ver-perfil'],
    template: `
    <div class="row">
        <!-- Coluna da Esquerda: Meus Livros (Estante) -->
        <div class="col-md-6">
            <div class="card shadow-sm mb-3">
                <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">📚 Minha Estante</h5>
                    <button @click="mostrarFormNovo = !mostrarFormNovo" class="btn btn-sm btn-light">
                        {{ mostrarFormNovo ? 'Cancelar' : '+ Novo Livro' }}
                    </button>
                </div>
                <div class="card-body">
                    
                    <!-- Formulário de Adicionar Livro (Toggle) -->
                    <div v-if="mostrarFormNovo" class="mb-4 p-3 border rounded bg-light">
                        <h6>Começar um novo livro</h6>
                        <div class="mb-3 position-relative">
                            <input type="text" v-model="buscaLivro" @input="buscarLivros" class="form-control" placeholder="Busque um título..." autocomplete="off">
                            <ul v-if="sugestoes.length > 0" class="list-group position-absolute w-100 shadow" style="z-index: 1000; max-height: 200px; overflow-y: auto;">
                                <li v-for="livro in sugestoes" :key="livro.id" @click="adicionarAEstante(livro)" class="list-group-item list-group-item-action cursor-pointer">
                                    <strong>{{ livro.titulo }}</strong> <br>
                                    <small class="text-muted">{{ livro.autor }} ({{ livro.paginas }} pág)</small>
                                </li>
                            </ul>
                        </div>
                    </div>

                    <!-- Lista de Livros Lendo -->
                    <div v-if="estante.length === 0" class="text-center text-muted py-3">
                        Você não está lendo nenhum livro no momento.
                    </div>

                    <div v-for="item in estante" :key="item.id" class="card mb-2 border-start border-4" :class="{'border-success': item.status === 'CONCLUIDO', 'border-warning': item.status === 'LENDO', 'border-secondary': item.status === 'ABANDONADO'}">
                        <div class="card-body p-2">
                            <div class="d-flex justify-content-between">
                                <h6 class="mb-1">{{ item.titulo }}</h6>
                                <div class="dropdown">
                                    <button class="btn btn-sm btn-link text-dark p-0" type="button" data-bs-toggle="dropdown" aria-expanded="false" style="text-decoration: none;">
                                        ⋮
                                    </button>
                                    <ul class="dropdown-menu dropdown-menu-end">
                                        <li><a class="dropdown-item" href="#" @click.prevent="abrirModalEdicao(item)">✏️ Editar Progresso</a></li>
                                        <li><a class="dropdown-item" href="#" @click.prevent="alterarStatus(item, 'CONCLUIDO')">✅ Marcar como Lido</a></li>
                                        <li><a class="dropdown-item" href="#" @click.prevent="alterarStatus(item, 'ABANDONADO')">⏹️ Marcar como Abandonado</a></li>
                                        <li><hr class="dropdown-divider"></li>
                                        <li><a class="dropdown-item text-danger" href="#" @click.prevent="excluirItem(item)">🗑️ Excluir</a></li>
                                    </ul>
                                </div>
                            </div>
                            <div class="d-flex align-items-center gap-2">
                                <span class="badge" :class="{'bg-success': item.status === 'CONCLUIDO', 'bg-warning text-dark': item.status === 'LENDO', 'bg-secondary': item.status === 'ABANDONADO'}">{{ item.status }}</span>
                                <small class="text-muted">{{ item.autor }}</small>
                            </div>
                            
                            <!-- Barra de Progresso -->
                            <div class="progress mt-2" style="height: 10px;">
                                <div class="progress-bar" role="progressbar" :style="{width: (item.paginasLidas / item.paginasTotal * 100) + '%'}" :class="{'bg-success': item.status === 'CONCLUIDO', 'bg-warning': item.status === 'LENDO', 'bg-secondary': item.status === 'ABANDONADO'}"></div>
                            </div>
                            <div class="d-flex justify-content-between mt-1 small text-muted">
                                <span>{{ item.paginasLidas }} / {{ item.paginasTotal }} pág</span>
                                <span>{{ Math.round(item.paginasLidas / item.paginasTotal * 100) }}%</span>
                            </div>

                            <!-- Botão Atualizar Progresso -->
                            <div v-if="item.status === 'LENDO'" class="mt-2 text-end">
                                <button @click="abrirModalProgresso(item)" class="btn btn-sm btn-outline-primary">Atualizar Progresso</button>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>

        <!-- Coluna da Direita: Ranking -->
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-header bg-dark text-white">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <h5 class="mb-0">🏆 Ranking</h5>
                        <button @click="carregarRanking" class="btn btn-sm btn-outline-light">Atualizar</button>
                    </div>
                    <!-- Filtro de Ranking -->
                    <div class="d-flex gap-2">
                        <select v-model="filtroRanking" @change="carregarRanking" class="form-select form-select-sm bg-secondary text-white border-0">
                            <option :value="null">🌍 Global</option>
                            <option v-for="clube in meusClubes" :key="clube.id" :value="clube.id">👥 {{ clube.nome }}</option>
                        </select>
                        <select v-model="filtroPeriodo" @change="carregarRanking" class="form-select form-select-sm bg-secondary text-white border-0" style="width: 120px;">
                            <option value="SEMPRE">Sempre</option>
                            <option value="MES">Mês</option>
                            <option value="SEMANA">Semana</option>
                            <option value="DIA">Dia</option>
                        </select>
                    </div>
                </div>
                <div class="card-body p-0">
                    <table class="table table-striped mb-0 align-middle">
                        <thead class="table-light"><tr><th>#</th><th>Usuário</th><th>Pontos</th></tr></thead>
                        <tbody>
                            <tr v-for="(user, index) in ranking" :key="user.id" :class="{'table-warning': user.id === usuario.id}">
                                <td class="fw-bold ps-3">{{ index + 1 }}º</td>
                                <td>
                                    <a href="#" @click.prevent="$emit('ver-perfil', user.id)" class="text-decoration-none text-dark d-flex align-items-center">
                                        <img :src="user.foto" class="rounded-circle me-2" width="30" height="30" style="object-fit: cover;">
                                        {{ user.nome }} <span v-if="index === 0" class="ms-1">👑</span>
                                    </a>
                                </td>
                                <td class="fw-bold">{{ user.pontos }}</td>
                            </tr>
                            <tr v-if="ranking.length === 0">
                                <td colspan="3" class="text-center text-muted py-3">Nenhum usuário encontrado.</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Modais (Progresso e Edição) -->
    <div v-if="modalProgressoAberto" class="modal-overlay">
        <div class="modal-content-custom shadow">
            <h5>📖 Atualizar Leitura</h5>
            <p class="text-muted">{{ itemSelecionado.titulo }}</p>
            <div class="mb-3">
                <label class="form-label">Quantas páginas você leu hoje?</label>
                <input type="number" v-model.number="paginasLidasInput" class="form-control" min="1" :max="itemSelecionado.paginasTotal - itemSelecionado.paginasLidas">
            </div>
            <div class="d-flex justify-content-end gap-2">
                <button @click="modalProgressoAberto = false" class="btn btn-secondary">Cancelar</button>
                <button @click="salvarProgresso" class="btn btn-primary">Salvar</button>
            </div>
        </div>
    </div>

    <div v-if="modalEdicaoAberto" class="modal-overlay">
        <div class="modal-content-custom shadow">
            <h5>✏️ Editar Progresso Total</h5>
            <p class="text-muted">{{ itemSelecionado.titulo }}</p>
            <div class="alert alert-warning small">
                Atenção: Alterar o progresso total recalculará seus pontos (ganhando ou perdendo a diferença).
            </div>
            <div class="mb-3">
                <label class="form-label">Página atual (Total lido)</label>
                <input type="number" v-model.number="paginasEdicaoInput" class="form-control" min="0" :max="itemSelecionado.paginasTotal">
                <small class="text-muted">Total do livro: {{ itemSelecionado.paginasTotal }}</small>
            </div>
            <div class="d-flex justify-content-end gap-2">
                <button @click="modalEdicaoAberto = false" class="btn btn-secondary">Cancelar</button>
                <button @click="salvarEdicao" class="btn btn-primary">Salvar</button>
            </div>
        </div>
    </div>
    `,
    data() { return { 
        estante: [], 
        ranking: [], 
        buscaLivro: '', 
        sugestoes: [], 
        mostrarFormNovo: false,
        modalProgressoAberto: false,
        modalEdicaoAberto: false,
        itemSelecionado: null,
        paginasLidasInput: 0,
        paginasEdicaoInput: 0,
        filtroRanking: null, // null = Global, ID = Clube
        filtroPeriodo: 'SEMPRE',
        meusClubes: []
    } },
    mounted() { 
        this.carregarEstante(); 
        this.carregarMeusClubes(); // Carrega os clubes antes do ranking
        this.carregarRanking(); 
    },
    methods: {
        async carregarEstante() { try { const res = await fetch(`${API_BASE_URL}/api/estante/${this.usuario.id}`); this.estante = await res.json(); } catch (e) { console.error(e); } },
        
        async carregarMeusClubes() {
            // Usa o endpoint de perfil para pegar os clubes do usuário logado de forma rápida
            // Idealmente teria um endpoint dedicado /api/meus-clubes, mas vamos reutilizar
            try {
                const res = await fetch(`${API_BASE_URL}/api/perfil/${this.usuario.id}`);
                if (res.ok) {
                    const perfil = await res.json();
                    this.meusClubes = perfil.clubes || [];
                }
            } catch (e) { console.error(e); }
        },

        async carregarRanking() { 
            try { 
                let url = `${API_BASE_URL}/api/ranking`;
                const params = new URLSearchParams();
                if (this.filtroRanking) params.append('clubeId', this.filtroRanking);
                if (this.filtroPeriodo) params.append('periodo', this.filtroPeriodo);
                
                const queryString = params.toString();
                if (queryString) url += `?${queryString}`;

                const res = await fetch(url); 
                this.ranking = await res.json(); 
            } catch (e) { console.error(e); } 
        },
        
        async buscarLivros() {
            if (this.buscaLivro.length < 3) { this.sugestoes = []; return; }
            try { const res = await fetch(`${API_BASE_URL}/api/livros?q=${this.buscaLivro}`); this.sugestoes = await res.json(); } catch (e) { console.error(e); }
        },
        async adicionarAEstante(livro) {
            try {
                const res = await fetch(`${API_BASE_URL}/api/estante`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ usuarioId: this.usuario.id, livroId: livro.id, titulo: livro.titulo, autor: livro.autor, paginasTotal: livro.paginas }) });
                if (res.ok) { this.mostrarFormNovo = false; this.buscaLivro = ''; this.sugestoes = []; this.carregarEstante(); }
            } catch (e) { alert("Erro ao adicionar livro."); }
        },
        abrirModalProgresso(item) { this.itemSelecionado = item; this.paginasLidasInput = 0; this.modalProgressoAberto = true; },
        async salvarProgresso() {
            if (this.paginasLidasInput <= 0) return;
            try {
                const res = await fetch(`${API_BASE_URL}/api/estante/${this.itemSelecionado.id}/progresso?paginas=${this.paginasLidasInput}`, { method: 'PUT' });
                if (res.ok) { 
                    this.modalProgressoAberto = false; 
                    this.carregarEstante(); 
                    this.$emit('atualizar-usuario'); // Atualiza pontos no header
                    this.carregarRanking(); // Atualiza ranking
                } else if (res.status === 404) {
                    alert("Este livro não está mais na sua estante (talvez o banco tenha sido resetado).");
                    this.modalProgressoAberto = false;
                    this.carregarEstante();
                } else {
                    const msg = await res.text();
                    alert("Erro: " + msg);
                }
            } catch (e) { alert("Erro de conexão."); }
        },
        abrirModalEdicao(item) { this.itemSelecionado = item; this.paginasEdicaoInput = item.paginasLidas; this.modalEdicaoAberto = true; },
        async salvarEdicao() {
            try {
                const res = await fetch(`${API_BASE_URL}/api/estante/${this.itemSelecionado.id}/editar?paginas=${this.paginasEdicaoInput}`, { method: 'PUT' });
                if (res.ok) { 
                    this.modalEdicaoAberto = false; 
                    this.carregarEstante(); 
                    this.$emit('atualizar-usuario'); 
                    this.carregarRanking();
                } else {
                    const msg = await res.text();
                    alert("Erro: " + msg);
                }
            } catch (e) { alert("Erro de conexão."); }
        },
        async alterarStatus(item, novoStatus) {
            if (!confirm(`Deseja marcar como ${novoStatus}?`)) return;
            try {
                const res = await fetch(`${API_BASE_URL}/api/estante/${item.id}/status?status=${novoStatus}`, { method: 'PUT' });
                if (res.ok) { 
                    this.carregarEstante(); 
                    if (novoStatus === 'CONCLUIDO') {
                        this.$emit('atualizar-usuario');
                        this.carregarRanking();
                    }
                }
            } catch (e) { alert("Erro de conexão."); }
        },
        async excluirItem(item) {
            if (!confirm("Tem certeza? Você perderá os pontos ganhos com este livro!")) return;
            try {
                const res = await fetch(`${API_BASE_URL}/api/estante/${item.id}`, { method: 'DELETE' });
                if (res.ok) { 
                    this.carregarEstante(); 
                    this.$emit('atualizar-usuario');
                    this.carregarRanking();
                }
            } catch (e) { alert("Erro de conexão."); }
        }
    }
};
