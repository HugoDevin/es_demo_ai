const jsonHeaders = { 'Content-Type': 'application/json' };

const el = (id) => document.getElementById(id);
const tokenBox = el('tokenBox');

function prettyPrint(targetId, payload) {
    el(targetId).textContent = typeof payload === 'string' ? payload : JSON.stringify(payload, null, 2);
}

async function request(url, options = {}) {
    const response = await fetch(url, options);
    const text = await response.text();
    let data;
    try {
        data = text ? JSON.parse(text) : {};
    } catch (e) {
        data = { raw: text };
    }
    if (!response.ok) {
        throw data;
    }
    return data;
}

function authHeader() {
    const token = tokenBox.value.trim();
    return token ? { Authorization: `Bearer ${token}` } : {};
}

el('fillSellerBtn').addEventListener('click', () => {
    el('username').value = window.demoUsers.seller.username;
    el('password').value = window.demoUsers.seller.password;
});

el('fillBuyerBtn').addEventListener('click', () => {
    el('username').value = window.demoUsers.buyer.username;
    el('password').value = window.demoUsers.buyer.password;
});

el('loginForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    try {
        const result = await request('/api/v1/auth/login', {
            method: 'POST',
            headers: jsonHeaders,
            body: JSON.stringify({
                username: el('username').value.trim(),
                password: el('password').value
            })
        });
        tokenBox.value = result.data.token;
        prettyPrint('productsResult', { message: '登入成功，JWT 已寫入上方 Token 欄位。', tokenPreview: result.data.token.slice(0, 40) + '...' });
    } catch (error) {
        prettyPrint('productsResult', error);
    }
});

el('copyTokenBtn').addEventListener('click', async () => {
    await navigator.clipboard.writeText(tokenBox.value);
});

el('logoutBtn').addEventListener('click', async () => {
    try {
        const result = await request('/api/v1/auth/logout', {
            method: 'POST',
            headers: { ...authHeader() }
        });
        prettyPrint('productsResult', result);
        tokenBox.value = '';
    } catch (error) {
        prettyPrint('productsResult', error);
    }
});

async function loadProducts() {
    try {
        const keyword = el('keyword').value.trim();
        const query = keyword ? `?keyword=${encodeURIComponent(keyword)}` : '';
        const result = await request(`/api/v1/products${query}`);
        prettyPrint('productsResult', result);
    } catch (error) {
        prettyPrint('productsResult', error);
    }
}

el('loadProductsBtn').addEventListener('click', loadProducts);
el('searchProductsBtn').addEventListener('click', loadProducts);

el('createProductForm').addEventListener('submit', async (event) => {
    event.preventDefault();
    try {
        const result = await request('/api/v1/products', {
            method: 'POST',
            headers: { ...jsonHeaders, ...authHeader() },
            body: JSON.stringify({
                name: el('productName').value.trim(),
                description: el('productDescription').value.trim(),
                price: Number(el('productPrice').value),
                stock: Number(el('productStock').value)
            })
        });
        prettyPrint('createProductResult', result);
        loadProducts();
    } catch (error) {
        prettyPrint('createProductResult', error);
    }
});

el('loadOrdersBtn').addEventListener('click', async () => {
    try {
        const result = await request('/api/v1/orders', {
            headers: { ...authHeader() }
        });
        prettyPrint('ordersResult', result);
    } catch (error) {
        prettyPrint('ordersResult', error);
    }
});

loadProducts();
