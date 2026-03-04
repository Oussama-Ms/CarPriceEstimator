import axios from 'axios';

const API_URL = import.meta.env.VITE_API_BASE_URL || '/api';

const api = axios.create({
    baseURL: API_URL, // Use env var for hosting, fallback to proxy for local
    headers: {
        'Content-Type': 'application/json',
    },
});

export const predictPrice = async (vehiculeData) => {
    try {
        const response = await axios.post(`${API_URL}/predict`, vehiculeData);
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error.message;
    }
};

export default api;
