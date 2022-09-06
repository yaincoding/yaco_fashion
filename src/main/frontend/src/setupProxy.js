const {createProxyMiddleware} = require('http-proxy-middleware')

const api_url = window.location.origin;

module.exports = (app) => {
    app.use('/api',
        createProxyMiddleware({
            target: api_url,
            changeOrigin: true
        })
    );
};