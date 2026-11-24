const API_BASE = "/api";

let currentEntity = "pedidos";
let editingId = null;
let lookupCache = { categorias: [], produtos: [], clientes: [], mesas: [] };

const ENTITIES = {
    pedidos: {
        label: "Pedidos",
        endpoint: "/pedidos",
        columns: ["ID", "Cliente", "Mesa", "Produto", "Qtd", "Total", "Status", "Ações"],
        rowMapper: (p) => [
            p.id,
            p.cliente ? p.cliente.nome : "-",
            p.mesa ? `Mesa ${p.mesa.numero}` : "-",
            p.produto ? p.produto.nome : "-",
            p.quantidade,
            `R$ ${Number(p.valorTotal).toFixed(2)}`,
            p.status
        ],
        fields: () => [
            { name: "clienteId", label: "Cliente", type: "select", getVal: (i) => i.cliente?.id, options: lookupCache.clientes.map(c => ({ value: c.id, text: `${c.nome} (${c.pontosFidelidade} pts)` })) },
            { name: "mesaId", label: "Mesa", type: "select", getVal: (i) => i.mesa?.id, options: lookupCache.mesas.map(m => ({ value: m.id, text: `Mesa ${m.numero} (${m.status})` })) },
            { name: "produtoId", label: "Produto", type: "select", getVal: (i) => i.produto?.id, options: lookupCache.produtos.map(pr => ({ value: pr.id, text: `${pr.nome} - R$ ${Number(pr.preco).toFixed(2)}` })) },
            { name: "quantidade", label: "Quantidade", type: "number", min: 1, default: 1 },
            { name: "observacoes", label: "Observações", type: "text", placeholder: "Ex: Sem açúcar" },
            { name: "status", label: "Status", type: "select", options: ["ABERTO", "EM_PREPARO", "ENTREGUE", "CANCELADO"].map(s => ({ value: s, text: s })) }
        ],
        parsePayload: (fd) => ({
            cliente: fd.get("clienteId") ? { id: Number(fd.get("clienteId")) } : null,
            mesa: fd.get("mesaId") ? { id: Number(fd.get("mesaId")) } : null,
            produto: fd.get("produtoId") ? { id: Number(fd.get("produtoId")) } : null,
            quantidade: fd.get("quantidade") ? Number(fd.get("quantidade")) : null,
            observacoes: fd.get("observacoes"),
            status: fd.get("status")
        })
    },
    produtos: {
        label: "Produtos",
        endpoint: "/produtos",
        columns: ["ID", "Nome", "Categoria", "Preço", "Disponível", "Ações"],
        rowMapper: (p) => [
            p.id, p.nome, p.categoria ? p.categoria.nome : "-",
            `R$ ${Number(p.preco).toFixed(2)}`, p.disponivel ? "Sim" : "Não"
        ],
        fields: () => [
            { name: "nome", label: "Nome do Produto", type: "text" },
            { name: "descricao", label: "Descrição", type: "text" },
            { name: "preco", label: "Preço (R$)", type: "number", step: "0.01", min: "0.01" },
            { name: "categoriaId", label: "Categoria", type: "select", getVal: (i) => i.categoria?.id, options: lookupCache.categorias.map(c => ({ value: c.id, text: c.nome })) },
            { name: "disponivel", label: "Disponível para venda", type: "checkbox", default: true }
        ],
        parsePayload: (fd, form) => ({
            nome: fd.get("nome"),
            descricao: fd.get("descricao"),
            preco: fd.get("preco") ? Number(fd.get("preco")) : null,
            categoria: fd.get("categoriaId") ? { id: Number(fd.get("categoriaId")) } : null,
            disponivel: form.querySelector('[name="disponivel"]').checked
        })
    },
    categorias: {
        label: "Categorias",
        endpoint: "/categorias",
        columns: ["ID", "Nome", "Descrição", "Ativa", "Ações"],
        rowMapper: (c) => [c.id, c.nome, c.descricao || "-", c.ativa ? "Sim" : "Não"],
        fields: () => [
            { name: "nome", label: "Nome da Categoria", type: "text" },
            { name: "descricao", label: "Descrição", type: "text" },
            { name: "ativa", label: "Categoria ativa", type: "checkbox", default: true }
        ],
        parsePayload: (fd, form) => ({
            nome: fd.get("nome"),
            descricao: fd.get("descricao"),
            ativa: form.querySelector('[name="ativa"]').checked
        })
    },
    clientes: {
        label: "Clientes",
        endpoint: "/clientes",
        columns: ["ID", "Nome", "Telefone", "E-mail", "Pontos", "Ações"],
        rowMapper: (c) => [c.id, c.nome, c.telefone, c.email, c.pontosFidelidade],
        fields: () => [
            { name: "nome", label: "Nome Completo", type: "text" },
            { name: "telefone", label: "Telefone", type: "text", placeholder: "(11) 99999-9999" },
            { name: "email", label: "E-mail", type: "email" },
            { name: "pontosFidelidade", label: "Pontos Fidelidade", type: "number", min: 0, default: 0 }
        ],
        parsePayload: (fd) => ({
            nome: fd.get("nome"),
            telefone: fd.get("telefone"),
            email: fd.get("email"),
            pontosFidelidade: Number(fd.get("pontosFidelidade") || 0)
        })
    },
    mesas: {
        label: "Mesas",
        endpoint: "/mesas",
        columns: ["ID", "Número", "Capacidade", "Status", "Ações"],
        rowMapper: (m) => [m.id, `Mesa ${m.numero}`, `${m.capacidade} lugares`, m.status],
        fields: () => [
            { name: "numero", label: "Número da Mesa", type: "number", min: 1 },
            { name: "capacidade", label: "Capacidade (lugares)", type: "number", min: 1, default: 2 },
            { name: "status", label: "Status", type: "select", options: ["LIVRE", "OCUPADA", "RESERVADA"].map(s => ({ value: s, text: s })) }
        ],
        parsePayload: (fd) => ({
            numero: fd.get("numero") ? Number(fd.get("numero")) : null,
            capacidade: fd.get("capacidade") ? Number(fd.get("capacidade")) : null,
            status: fd.get("status")
        })
    }
};

const feedbackBox = document.getElementById("feedback-box");
const formEl = document.getElementById("entity-form");
const formTitle = document.getElementById("form-title");
const listTitle = document.getElementById("list-title");
const btnCancelEdit = document.getElementById("btn-cancel-edit");
const loadingState = document.getElementById("loading-state");
const emptyState = document.getElementById("empty-state");
const tableHead = document.getElementById("table-head");
const tableBody = document.getElementById("table-body");

function showFeedback(message, type = "success") {
    feedbackBox.textContent = message;
    feedbackBox.className = `feedback ${type}`;
}

function clearFeedback() {
    feedbackBox.className = "feedback hidden";
}

async function apiRequest(path, options = {}) {
    try {
        const res = await fetch(`${API_BASE}${path}`, {
            headers: { "Content-Type": "application/json" },
            ...options
        });
        if (res.status === 204) return null;
        const text = await res.text();
        if (!res.ok) {
            throw new Error(text || "Erro na operação.");
        }
        return text ? JSON.parse(text) : null;
    } catch (err) {
        if (err instanceof TypeError) {
            throw new Error("Falha de conexão com o servidor. Verifique se a API está rodando.");
        }
        throw err;
    }
}

async function refreshLookups() {
    const [categorias, produtos, clientes, mesas] = await Promise.all([
        apiRequest("/categorias"),
        apiRequest("/produtos"),
        apiRequest("/clientes"),
        apiRequest("/mesas")
    ]);
    lookupCache = { categorias, produtos, clientes, mesas };
}

function renderForm(item = null) {
    const config = ENTITIES[currentEntity];
    editingId = item ? item.id : null;
    formTitle.textContent = editingId ? `Editar ${config.label} #${editingId}` : `Cadastrar em ${config.label}`;
    btnCancelEdit.classList.toggle("hidden", !editingId);

    formEl.innerHTML = "";
    config.fields().forEach(field => {
        const group = document.createElement("div");
        group.className = "form-group";

        if (field.type === "checkbox") {
            group.classList.add("checkbox-row");
            const checked = item ? Boolean(item[field.name]) : Boolean(field.default);
            group.innerHTML = `<input type="checkbox" id="f-${field.name}" name="${field.name}" ${checked ? "checked" : ""}>
                               <label for="f-${field.name}">${field.label}</label>`;
        } else if (field.type === "select") {
            const currentVal = item ? (field.getVal ? field.getVal(item) : item[field.name]) : "";
            const opts = field.options.map(o =>
                `<option value="${o.value}" ${String(o.value) === String(currentVal) ? "selected" : ""}>${o.text}</option>`
            ).join("");
            group.innerHTML = `<label for="f-${field.name}">${field.label}</label>
                               <select id="f-${field.name}" name="${field.name}">${opts}</select>`;
        } else {
            const val = item && item[field.name] !== null ? item[field.name] : (field.default ?? "");
            group.innerHTML = `<label for="f-${field.name}">${field.label}</label>
                               <input type="${field.type}" id="f-${field.name}" name="${field.name}"
                                      value="${val}" placeholder="${field.placeholder || ""}"
                                      ${field.min !== undefined ? `min="${field.min}"` : ""}
                                      ${field.step ? `step="${field.step}"` : ""}>`;
        }
        formEl.appendChild(group);
    });

    const submitBtn = document.createElement("button");
    submitBtn.type = "submit";
    submitBtn.className = "btn-primary";
    submitBtn.textContent = editingId ? "Salvar Alterações" : "Cadastrar";
    formEl.appendChild(submitBtn);
}

function renderTable(items) {
    const config = ENTITIES[currentEntity];
    tableHead.innerHTML = `<tr>${config.columns.map(c => `<th>${c}</th>`).join("")}</tr>`;
    tableBody.innerHTML = "";

    if (!items || items.length === 0) {
        emptyState.classList.remove("hidden");
        return;
    }
    emptyState.classList.add("hidden");

    items.forEach(item => {
        const tr = document.createElement("tr");
        const cells = config.rowMapper(item).map(val => `<td>${val}</td>`).join("");
        tr.innerHTML = `${cells}
            <td class="actions-cell">
                <button type="button" class="btn-edit">Editar</button>
                <button type="button" class="btn-delete">Excluir</button>
            </td>`;

        tr.querySelector(".btn-edit").addEventListener("click", () => {
            clearFeedback();
            renderForm(item);
        });

        tr.querySelector(".btn-delete").addEventListener("click", async () => {
            if (!confirm(`Deseja realmente excluir o registro #${item.id}?`)) return;
            try {
                await apiRequest(`${config.endpoint}/${item.id}`, { method: "DELETE" });
                showFeedback(`Registro #${item.id} excluído com sucesso!`, "success");
                await loadEntityData();
            } catch (err) {
                showFeedback(err.message, "error");
            }
        });

        tableBody.appendChild(tr);
    });
}

async function loadEntityData(singleId = null) {
    const config = ENTITIES[currentEntity];
    listTitle.textContent = `${config.label} Cadastrados`;
    loadingState.classList.remove("hidden");
    emptyState.classList.add("hidden");
    tableBody.innerHTML = "";

    try {
        await refreshLookups();
        renderForm(null);

        if (singleId) {
            const item = await apiRequest(`${config.endpoint}/${singleId}`);
            renderTable([item]);
        } else {
            const list = await apiRequest(config.endpoint);
            renderTable(list);
        }
    } catch (err) {
        showFeedback(err.message, "error");
    } finally {
        loadingState.classList.add("hidden");
    }
}

formEl.addEventListener("submit", async (e) => {
    e.preventDefault();
    clearFeedback();
    const config = ENTITIES[currentEntity];
    const payload = config.parsePayload(new FormData(formEl), formEl);

    try {
        if (editingId) {
            await apiRequest(`${config.endpoint}/${editingId}`, {
                method: "PUT",
                body: JSON.stringify(payload)
            });
            showFeedback(`Registro #${editingId} atualizado com sucesso!`, "success");
        } else {
            await apiRequest(config.endpoint, {
                method: "POST",
                body: JSON.stringify(payload)
            });
            showFeedback("Registro cadastrado com sucesso!", "success");
        }
        await loadEntityData();
    } catch (err) {
        showFeedback(err.message, "error");
    }
});

btnCancelEdit.addEventListener("click", () => {
    clearFeedback();
    renderForm(null);
});

document.getElementById("btn-search").addEventListener("click", () => {
    clearFeedback();
    const id = document.getElementById("search-id").value;
    if (!id) {
        showFeedback("Informe um identificador numérico para consultar.", "error");
        return;
    }
    loadEntityData(id);
});

document.getElementById("btn-reset-search").addEventListener("click", () => {
    clearFeedback();
    document.getElementById("search-id").value = "";
    loadEntityData();
});

document.querySelectorAll(".tab-btn").forEach(btn => {
    btn.addEventListener("click", () => {
        document.querySelectorAll(".tab-btn").forEach(b => b.classList.remove("active"));
        btn.classList.add("active");
        currentEntity = btn.dataset.entity;
        document.getElementById("search-id").value = "";
        clearFeedback();
        loadEntityData();
    });
});

loadEntityData();
