import axios from 'axios';

const api = axios.create({
    baseURL: '/api', // Proxy in vite.config.js handles the rest
    headers: {
        'Content-Type': 'application/json',
    },
});

export const predictPrice = async (vehiculeData) => {
    try {
        const response = await api.post('/predict', vehiculeData);
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error.message;
    }
};

export default api;
