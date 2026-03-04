import axios from 'axios';

// By using a relative path, the browser talks to Vercel (no CORS issues!). 
// Vercel's server then secretly forwards it to Hugging Face.
const api = axios.create({
    baseURL: '/api',
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