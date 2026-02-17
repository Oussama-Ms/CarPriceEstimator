import React, { useState, useEffect } from 'react';
import { predictPrice } from '../services/api';
import './PredictionForm.css';

const PredictionForm = () => {
    const [formData, setFormData] = useState({
        marque: 'DACIA',
        modele: '',
        annee: 2019,
        kilometrage: 100000,
        carburant: 'DIESEL',
        boiteVitesse: 'MANUELLE'
    });

    const [availableModels, setAvailableModels] = useState([]);
    const [prediction, setPrediction] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const carData = {
        "DACIA": ["Logan", "Sandero", "Duster", "Dokker", "Lodgy"],
        "RENAULT": ["Clio", "Megane", "Kangoo"],
        "PEUGEOT": ["206", "208", "Partner"],
        "VOLKSWAGEN": ["T-Roc", "Tiguan", "Touareg"],
        "HYUNDAI": ["i10", "Accent", "Tucson"],
        "TOYOTA": ["Yaris", "Corolla", "RAV4"],
        "MERCEDES-BENZ": ["Classe A", "Classe C", "Classe E"],
        "BMW": ["Série 1", "Série 3", "Série 5"]
    };

    useEffect(() => {
        const models = carData[formData.marque] || [];
        setAvailableModels(models);
        setFormData(prev => ({ ...prev, modele: models[0] || '' }));
    }, [formData.marque]);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setPrediction(null);

        try {
            const result = await predictPrice(formData);
            setPrediction(result);
        } catch (err) {
            setError("Failed to get prediction. Connection error?");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="form-container">
            <div className="hud-panel">
                <h2 className="hud-header">VEHICLE CONFIGURATOR</h2>

                <form onSubmit={handleSubmit}>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '20px' }}>
                        <div className="form-group">
                            <label className="hud-label">Select Brand</label>
                            <select name="marque" value={formData.marque} onChange={handleChange} className="hud-select">
                                {Object.keys(carData).map(brand => (
                                    <option key={brand} value={brand}>{brand}</option>
                                ))}
                            </select>
                        </div>

                        <div className="form-group">
                            <label className="hud-label">Select Model</label>
                            <select name="modele" value={formData.modele} onChange={handleChange} className="hud-select">
                                {availableModels.map(model => (
                                    <option key={model} value={model}>{model}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px', marginBottom: '20px' }}>
                        <div className="form-group">
                            <label className="hud-label">Production Year</label>
                            <input
                                type="number"
                                name="annee"
                                value={formData.annee}
                                onChange={handleChange}
                                className="hud-input"
                                min="1990" max="2025"
                            />
                        </div>
                        <div className="form-group">
                            <label className="hud-label">Mileage (KM)</label>
                            <input
                                type="number"
                                name="kilometrage"
                                value={formData.kilometrage}
                                onChange={handleChange}
                                className="hud-input"
                            />
                        </div>
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                        <div className="form-group">
                            <label className="hud-label">Fuel Type</label>
                            <select name="carburant" value={formData.carburant} onChange={handleChange} className="hud-select">
                                <option value="DIESEL">Diesel</option>
                                <option value="ESSENCE">Essence</option>
                                <option value="HYBRIDE">Hybride</option>
                                <option value="ELECTRIQUE">Electric</option>
                            </select>
                        </div>
                        <div className="form-group">
                            <label className="hud-label">Transmission</label>
                            <select name="boiteVitesse" value={formData.boiteVitesse} onChange={handleChange} className="hud-select">
                                <option value="MANUELLE">Manual</option>
                                <option value="AUTOMATIQUE">Automatic</option>
                            </select>
                        </div>
                    </div>

                    <button type="submit" disabled={loading} className="hud-btn">
                        {loading ? 'CALCULATING...' : 'ESTIMATE VALUE'}
                    </button>
                </form>

                {prediction && (
                    <div className="result-panel">
                        <span className="result-label">ESTIMATED MARKET VALUE</span>
                        <div className="result-value">{prediction}</div>
                    </div>
                )}

                {error && <div style={{ color: '#ff003c', marginTop: '20px', textAlign: 'center' }}>{error}</div>}
            </div>
        </div>
    );
};

export default PredictionForm;
